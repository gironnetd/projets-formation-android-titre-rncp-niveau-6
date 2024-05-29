package com.openclassrooms.go4lunch.view.main.list.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.BuildConfig;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.data.local.prefs.AppPreferences;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.view.detail.DetailActivity;
import com.openclassrooms.go4lunch.view.main.MainActivity;
import com.openclassrooms.go4lunch.view.shared.FirestoreAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.openclassrooms.go4lunch.utilities.Constants.RESTAURANT_DETAIL_ID;

/**
 * Adapter to display the list of restaurants around the user
 */
public class RestaurantAdapter extends FirestoreAdapter<RestaurantAdapter.ViewHolder> {
    private final  List<Place> places;
    private Context context;
    private double actualLatitude;
    private double actualLongitude;
    private Calendar calendar;
    private GregorianCalendar actualHour;
    private int dayOfWeek = -1;

    public RestaurantAdapter(Query query, List<Place> places) {
        super(query);
        this.places = places;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context == null) { context = parent.getContext(); }
        if(actualLatitude == 0) {
            actualLatitude = AppPreferences.preferences(context).getPrefKeyDeviceLocationLatitude();
        }
        if(actualLongitude == 0) {
            actualLongitude = AppPreferences.preferences(context).getPrefKeyDeviceLocationLongitude();
        }
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        actualHour = new GregorianCalendar();
        actualHour.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        actualHour.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        if(dayOfWeek == -1) {
            dayOfWeek = findDayOfWeek();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_view,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Place snapshotRestaurant = getSnapshot(position).toObject(Place.class);

        if(snapshotRestaurant.getWorkmates() != null) {
            holder.workmateCount.setText(String.format(Locale.FRENCH, "(%d)", snapshotRestaurant.getWorkmates().size()));
        }

        Place restaurant = places.get(position);

        assert restaurant != null;
        holder.restaurantName.setText(restaurant.getName());
        StringBuilder stringBuilder = new StringBuilder();
        String[] addressText = restaurant.getAddress().split(",");

        if(addressText.length > 1) {
            for(int index = 0; index < addressText.length - 1; index++) {
                if(index < addressText.length - 2) {
                    stringBuilder.append(addressText[index].trim()).append("\n");
                } else {
                    stringBuilder.append(addressText[index].trim());
                }
            }
        } else {
            stringBuilder.append(restaurant.getAddress().trim()) ;
        }
        holder.restaurantAddress.setText(stringBuilder.toString());

        if(restaurant.getPhotoReference() != null ) {
            String placeApiPhotoRequest = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400"
                    + "&photoreference=" + restaurant.getPhotoReference()
                    + "&key=" + BuildConfig.google_maps_key;
            Glide.with(context).load(placeApiPhotoRequest).into(holder.restaurantPhoto);
        }

        float[] distance = new float[1];
        Location.distanceBetween(actualLatitude, actualLongitude,
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                distance);

        holder.restaurantDistance.setText(String.format(Locale.FRENCH,"%d m", (int) distance[0]));
        initRatingToValue(restaurant.getRating(), holder);

        if(restaurant.getWeekdayText() == null ) {
            holder.isOpen = context.getString(R.string.no_opening_hours_communicated);
        } else if(restaurant.getWeekdayText() != null &&
                restaurant.getWeekdayText().get(dayOfWeek).contains(context.getString(R.string.closed))) {
            holder.isOpen = context.getString(R.string.closed);
        } else if(restaurant.getWeekdayText() != null) {
            String[] strings = restaurant.getWeekdayText().get(dayOfWeek).split(",");

            String regex = "";
            if(strings.length == 2) {
                regex = "^(.*): (.*)–(.*),(.*)–(.*)";
            } else if(strings.length == 1) {
                regex = "^(.*): (.*)–(.*)";
            }
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(restaurant.getWeekdayText().get(dayOfWeek));

            if (matcher.find()) {
                if(matcher.groupCount() == 5) {
                    int[] morningHours = new int[2];
                    int[] eveningHours = new int[2];

                    morningHours[0] = groupToDateInInteger(Objects.requireNonNull(matcher.group(2)).trim());
                    morningHours[1] = groupToDateInInteger(Objects.requireNonNull(matcher.group(3)).trim());
                    eveningHours[0] = groupToDateInInteger(Objects.requireNonNull(matcher.group(4)).trim() + " PM");
                    eveningHours[1] = groupToDateInInteger(Objects.requireNonNull(matcher.group(5)).trim());
                    holder.isOpen = isRestaurantOpen(matcher, morningHours, eveningHours);
                }
                if(matcher.groupCount() == 3) {
                    int[] openingHours = new int[2];
                    openingHours[0] = groupToDateInInteger(Objects.requireNonNull(matcher.group(2)).trim() + " PM");
                    openingHours[1] = groupToDateInInteger(Objects.requireNonNull(matcher.group(3)).trim());
                    holder.isOpen = isRestaurantOpen(matcher, openingHours);
                }
            }
        }
        holder.restaurantOpeningHours.setText(holder.isOpen);
        if(holder.isOpen.equals(context.getString(R.string.closed_soon))) {
            holder.restaurantOpeningHours.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.restaurantOpeningHours.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(RESTAURANT_DETAIL_ID, restaurant.getPlaceId());
            ((MainActivity) context).startActivity(intent);
        });
    }

    private int groupToDateInInteger(String group) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.FRENCH);
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a", Locale.FRENCH);
        Date date = null;
        if(group.equals("12:00")) {
            group = "12:00 AM";
        }
        try {
            date = parseFormat.parse(group);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        String[] dateInNumber = displayFormat.format(date).split(":");
        int result = Integer.parseInt(dateInNumber[0] + dateInNumber[1]);

        return result != 0 ? result : 1200;
    }

    private String isRestaurantOpen(Matcher matcher, int[] openingHours ) {
        int time = actualHour.get(Calendar.HOUR_OF_DAY) * 100 + actualHour.get(Calendar.MINUTE);
        String isOpen = "";

        if (isBetween(time, openingHours[0], openingHours[1])) {
            if (isClosedSoon(openingHours[1])) {
                isOpen = context.getString(R.string.closed_soon);
            } else {
                isOpen = context.getString(R.string.open_util) + " " + matcher.group(3);
            }
        } else {
            if (time < openingHours[0]) {
                isOpen = context.getString(R.string.open_at) + " " +  Objects.requireNonNull(matcher.group(2)).trim();
            }
            if (time > openingHours[1]) {
                isOpen = context.getString(R.string.closed_since) + " " + Objects.requireNonNull(matcher.group(3)).trim();
            }
        }
        return isOpen;
    }

    private String isRestaurantOpen(Matcher matcher, int[] morningHours, int[] eveningHours ) {
        int time = actualHour.get(Calendar.HOUR_OF_DAY) * 100 + actualHour.get(Calendar.MINUTE);
        String isOpen = "";
        if (isBetween(time, morningHours[0], morningHours[1])) {
            if (isClosedSoon(morningHours[1])) {
                isOpen = context.getString(R.string.closed_soon);
            } else {
                isOpen = context.getString(R.string.open_util) + " " + Objects.requireNonNull(matcher.group(3)).trim();
            }
        } else {
            if (isBetween(time, morningHours[1], eveningHours[0])) {
                isOpen =  context.getString(R.string.open_at) + " " + matcher.group(4);
            } else {
                if (isBetween(time, eveningHours[0], eveningHours[1])) {
                    if (isClosedSoon(eveningHours[1])) {
                        isOpen =  context.getString(R.string.closed_soon);
                    } else {
                        isOpen =  context.getString(R.string.open_util) + matcher.group(5);
                    }
                } else {
                    if(time < morningHours[0]) {
                        isOpen =  context.getString(R.string.open_at) + " " + Objects.requireNonNull(matcher.group(2)).trim();
                    }
                    if(time > morningHours[1]) {
                        isOpen =  context.getString(R.string.closed_since) + Objects.requireNonNull(matcher.group(5)).trim();
                    }
                }
            }
        }
        return isOpen;
    }

    private boolean isBetween(int time, int from, int to) {
        return to > from && time >= from && time <= to || to < from && (time >= from || time <= to);
    }

    private boolean isClosedSoon(int until) {
        calendar.setTime(actualHour.getTime());

        String to = Integer.toString(until);

        if(to.length() == 3) {
            to = "0" + to;
        }

        int hour = Integer.parseInt(to.substring(0, 2));
        int minute = Integer.parseInt(to.substring(2, 4));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        long periodSeconds = (calendar.getTimeInMillis() - actualHour.getTimeInMillis()) ;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(periodSeconds);

        return minutes > 0 && minutes <= 15;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_restaurant_name)
        TextView restaurantName;
        @BindView(R.id.tv_restaurant_address)
        TextView restaurantAddress;
        @BindView(R.id.tv_restaurant_opening_hours)
        TextView restaurantOpeningHours;
        @BindView(R.id.iv_restaurant_photo)
        ImageView restaurantPhoto;
        @BindView(R.id.tv_restaurant_distance)
        TextView restaurantDistance;
        @BindView(R.id.iv_first_star)
        ImageView firstStar;
        @BindView(R.id.iv_second_star)
        ImageView secondStar;
        @BindView(R.id.iv_third_star)
        ImageView thirdStar;
        @BindView(R.id.tv_workmate_count)
        TextView workmateCount;

        String isOpen = "";
        final View itemView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }

    private int findDayOfWeek() {
        switch(calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            case Calendar.SATURDAY:
                return 5;
            case Calendar.SUNDAY:
                return 6;
            default:
                return -1;
        }
    }

    private void initRatingToValue(double closer, ViewHolder holder) {
        if(closer == 0) {
            holder.firstStar.setImageResource(R.drawable.ic_star_border_24dp);
            holder.secondStar.setImageResource(R.drawable.ic_star_border_24dp);
            holder.thirdStar.setImageResource(R.drawable.ic_star_border_24dp);
        } else if(closer == 0.5) {
            holder.firstStar.setImageResource(R.drawable.ic_star_half_24dp);
            holder.secondStar.setImageResource(R.drawable.ic_star_border_24dp);
            holder.thirdStar.setImageResource(R.drawable.ic_star_border_24dp);
        } else if(closer == 1) {
            holder.firstStar.setImageResource(R.drawable.ic_star_24dp);
            holder.secondStar.setImageResource(R.drawable.ic_star_border_24dp);
            holder.thirdStar.setImageResource(R.drawable.ic_star_border_24dp);
        } else if(closer == 1.5) {
            holder.firstStar.setImageResource(R.drawable.ic_star_24dp);
            holder.secondStar.setImageResource(R.drawable.ic_star_half_24dp);
            holder.thirdStar.setImageResource(R.drawable.ic_star_border_24dp);
        } else if(closer == 2) {
            holder.firstStar.setImageResource(R.drawable.ic_star_24dp);
            holder.secondStar.setImageResource(R.drawable.ic_star_24dp);
            holder.thirdStar.setImageResource(R.drawable.ic_star_border_24dp);
        } else if(closer == 2.5) {
            holder.firstStar.setImageResource(R.drawable.ic_star_24dp);
            holder.secondStar.setImageResource(R.drawable.ic_star_24dp);
            holder.thirdStar.setImageResource(R.drawable.ic_star_half_24dp);
        } else if(closer == 3) {
            holder.firstStar.setImageResource(R.drawable.ic_star_24dp);
            holder.secondStar.setImageResource(R.drawable.ic_star_24dp);
            holder.thirdStar.setImageResource(R.drawable.ic_star_24dp);
        }
    }

}
