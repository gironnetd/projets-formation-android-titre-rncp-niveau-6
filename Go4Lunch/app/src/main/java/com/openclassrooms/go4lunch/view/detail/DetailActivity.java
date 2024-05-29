package com.openclassrooms.go4lunch.view.detail;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.trusted.TrustedWebActivityIntentBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.androidbrowserhelper.trusted.TwaLauncher;
import com.openclassrooms.go4lunch.BuildConfig;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.utilities.Constants;
import com.openclassrooms.go4lunch.utilities.EspressoIdlingResource;
import com.openclassrooms.go4lunch.view.BaseActivity;
import com.openclassrooms.go4lunch.viewmodel.detail.DetailViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Detail Activity class implementation
 */
public class DetailActivity extends BaseActivity {

    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 1;

    private Place restaurant;

    @BindView(R.id.detail_image)
    ImageView detailImage;

    @BindView(R.id.tv_restaurant_name)
    TextView restaurantName;

    @BindView(R.id.iv_first_star)
    ImageView firstStar;
    @BindView(R.id.iv_second_star)
    ImageView secondStar;
    @BindView(R.id.iv_third_star)
    ImageView thirdStar;

    @BindView(R.id.tv_restaurant_address)
    TextView restaurantAddress;

    @BindView(R.id.workmate_recycler_view)
    RecyclerView workmateRecyclerView;

    private WorkmateDetailAdapter mAdapter;

    @BindView(R.id.iv_call)
    ImageView callImageView;

    @BindView(R.id.tv_call_label)
    TextView callLabel;

    @BindView(R.id.iv_like)
    ImageView likeImageView;

    @BindView(R.id.tv_like_label)
    TextView likeLabel;

    @BindView(R.id.iv_website)
    ImageView websiteImageView;

    @BindView(R.id.tv_website_label)
    TextView websiteLabel;

    @BindView(R.id.fab_restaurant_joining)
    FloatingActionButton restaurantJoiningButton;

    private DetailViewModel detailViewModel;

    private boolean isRestaurantJoining = false;

    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        initDetailViewModel();

        if (restaurant == null && getIntent().getExtras() != null) {
            String restaurantId = getIntent().getStringExtra(Constants.RESTAURANT_DETAIL_ID);
            initRestaurant(restaurantId);
        }
    }

    public void initRestaurant(String restaurantId) {
        detailViewModel.findPlaceById(restaurantId);
        detailViewModel.restaurant().observe(this, place -> {
            restaurant = place;

            if (restaurant.getPhotoReference() != null) {
                String placeApiPhotoRequest = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400"
                        + "&photoreference=" + restaurant.getPhotoReference()
                        + "&key=" + BuildConfig.google_maps_key;
                Glide.with(this).load(placeApiPhotoRequest).into(detailImage);
            }

            initWorkmates();
            initCallFeature();
            initWebsiteFeature();
            initRatingToValue(restaurant.getRating());

            restaurantName.setText(restaurant.getName());
            restaurantAddress.setText(restaurant.getAddress());
            initLikes();
            initRestaurantJoiningButton();
        });
    }

    private void initLikes() {
        initLikeLabel();
        likeImageView.setOnClickListener(view -> addLikes());
        likeLabel.setOnClickListener(view -> addLikes());
    }

    private void initLikeLabel() {
        if(restaurant.getLikes() == 0 || restaurant.getLikes() == 1) {
            likeLabel.setText(restaurant.getLikes() + " LIKE");
        } else {
            likeLabel.setText(restaurant.getLikes() + " LIKES");
        }
    }

    private void addLikes() {
        int likes = restaurant.getLikes();
        likes++;
        restaurant.setLikes(likes);
        detailViewModel.incrementLikes(restaurant);
        Toast.makeText(this, String.format(getString(R.string.toast_likes_count), restaurant.getName(), restaurant.getLikes()), Toast.LENGTH_SHORT).show();
        initLikeLabel();
    }

    private void initDetailViewModel() {
        detailViewModel = (DetailViewModel) obtainViewModel(DetailViewModel.class);

        detailViewModel.findCurrentUser();
        detailViewModel.currentUser().observe(this, user -> {
            currentUser = user;
        });
    }

    private void initWorkmates() {
        detailViewModel.queryWorkmateByRestaurant(restaurant.getPlaceId());
        detailViewModel.workmateByRestaurant().observeForever( query -> {

            mAdapter = new WorkmateDetailAdapter(query);
            workmateRecyclerView.setAdapter(mAdapter);

            if (isRunningInstrumentedTest()) {
                workmateRecyclerView.setItemAnimator(null);
            }
            mAdapter.startListening();
            workmateRecyclerView.setItemViewCacheSize(15);
        });

        detailViewModel.isWorkmateAdded().observe(this, isAdded -> {
            if(isAdded) {
                showSnackBar(this, getString(R.string.restaurant_chosen));
                EspressoIdlingResource.decrement();
            }
        });

        detailViewModel.isWorkmateRemoved().observe(this, isRemoved -> {
            if(isRemoved) {
                showSnackBar(this, getString(R.string.restaurant_changed));
                EspressoIdlingResource.decrement();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    private void showSnackBar(Activity activity, String message){
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    private void initRestaurantJoiningButton() {
        if(restaurant.getWorkmates() != null && restaurant.getWorkmates().contains(currentUser)) {
            isRestaurantJoining = true;
        }

        if(!isRestaurantJoining) {
            DrawableCompat.setTintList(DrawableCompat.wrap(restaurantJoiningButton.getDrawable()),
                    ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
            restaurantJoiningButton.setSupportBackgroundTintList
                    (ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
        } else {
            DrawableCompat.setTintList(DrawableCompat.wrap(restaurantJoiningButton.getDrawable()),
                    ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
            restaurantJoiningButton.setSupportBackgroundTintList
                    (ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        }

        restaurantJoiningButton.setOnClickListener(view -> {
            EspressoIdlingResource.increment();
            if(!isRestaurantJoining) {
                if (!isRunningInstrumentedTest()) {
                    DrawableCompat.setTintList(DrawableCompat.wrap(restaurantJoiningButton.getDrawable()),
                            ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                    DrawableCompat.setTintList(DrawableCompat.wrap(restaurantJoiningButton.getBackground()),
                            ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

                }

                if (currentUser.getMiddayRestaurant() == null) {
                    currentUser.setMiddayRestaurantId(restaurant.getPlaceId());
                    currentUser.setMiddayRestaurant(restaurant);
                    detailViewModel.addUser(currentUser);
                } else {
                    detailViewModel.changeMiddayRestaurant(currentUser, restaurant);
                }
            } else {
                if (!isRunningInstrumentedTest()) {
                    DrawableCompat.setTintList(DrawableCompat.wrap(restaurantJoiningButton.getDrawable()),
                            ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
                    DrawableCompat.setTintList(DrawableCompat.wrap(restaurantJoiningButton.getBackground()),
                            ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                }
                detailViewModel.removeUser(currentUser);
            }
            isRestaurantJoining = !isRestaurantJoining;
        });
    }

    private void initCallFeature() {
        if (restaurant.getPhoneNumber() == null) {
            callImageView.setClickable(false);
            callLabel.setClickable(false);
            callLabel.setTextColor(getResources().getColor(android.R.color.darker_gray));
            callImageView.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                    android.R.color.darker_gray),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            callImageView.setOnClickListener(view -> callPhonePermission());
            callLabel.setOnClickListener(view -> callPhonePermission());
        }
    }

    private void callPhonePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            callRestaurant();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    PERMISSIONS_REQUEST_CALL_PHONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CALL_PHONE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callRestaurant();
            }
        }
    }

    private void callRestaurant() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + restaurant.getPhoneNumber()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initWebsiteFeature() {
        if (restaurant.getWebsiteUri() == null) {
            websiteImageView.setClickable(false);
            websiteLabel.setClickable(false);
            websiteLabel.setTextColor(getResources().getColor(android.R.color.darker_gray));
            websiteImageView.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                    android.R.color.darker_gray),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            websiteImageView.setOnClickListener(view -> launchWebsite());
            websiteLabel.setOnClickListener(view -> launchWebsite());
        }
    }

    private void launchWebsite() {
        TrustedWebActivityIntentBuilder builder = new TrustedWebActivityIntentBuilder(Uri.parse(restaurant.getWebsiteUri()))
                .setNavigationBarColor(getResources().getColor(R.color.colorPrimary))
                .setToolbarColor(getResources().getColor(R.color.colorPrimary));
        TwaLauncher launcher = new TwaLauncher(this);
        launcher.launch(builder, null, null);
    }

    private void initRatingToValue(double closer) {
        if(closer == 0) {
            firstStar.setImageResource(R.drawable.ic_star_border_24dp);
            secondStar.setImageResource(R.drawable.ic_star_border_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_border_24dp);
        } else if(closer == 0.5) {
            firstStar.setImageResource(R.drawable.ic_star_half_24dp);
            secondStar.setImageResource(R.drawable.ic_star_border_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_border_24dp);
        } else if(closer == 1) {
            firstStar.setImageResource(R.drawable.ic_star_24dp);
            secondStar.setImageResource(R.drawable.ic_star_border_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_border_24dp);
        } else if(closer == 1.5) {
            firstStar.setImageResource(R.drawable.ic_star_24dp);
            secondStar.setImageResource(R.drawable.ic_star_half_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_border_24dp);
        } else if(closer == 2) {
            firstStar.setImageResource(R.drawable.ic_star_24dp);
            secondStar.setImageResource(R.drawable.ic_star_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_border_24dp);
        } else if(closer == 2.5) {
            firstStar.setImageResource(R.drawable.ic_star_24dp);
            secondStar.setImageResource(R.drawable.ic_star_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_half_24dp);
        } else if(closer == 3) {
            firstStar.setImageResource(R.drawable.ic_star_24dp);
            secondStar.setImageResource(R.drawable.ic_star_24dp);
            thirdStar.setImageResource(R.drawable.ic_star_24dp);
        }
    }
}
