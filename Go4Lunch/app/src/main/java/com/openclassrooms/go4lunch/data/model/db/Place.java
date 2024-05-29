package com.openclassrooms.go4lunch.data.model.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.openclassrooms.go4lunch.data.model.api.places.details.PlaceDetails;
import com.openclassrooms.go4lunch.data.model.api.places.nearbysearch.NearbySearchPlace;
import com.openclassrooms.go4lunch.data.model.db.utilities.Converters;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Place Entity for local and remote database
 */
@Entity(tableName = "place")
@IgnoreExtraProperties
public class Place implements Parcelable {

    public static final String RESTAURANT_COLLECTION = "restaurants";
    public static final String FIELD_PLACE_ID = "placeId";
    public static final String FIELD_LIKES = "likes";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_WORKMATE_IDS = "workmateIds";
    public static final String FIELD_WORKMATES = "workmates";

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "place_id")
    private String placeId;

    private String name;

    private String address;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "website_uri")
    private String websiteUri;

    @ColumnInfo(name = "photo_reference")
    private String photoReference;

    @TypeConverters(Converters.class)
    @ColumnInfo(name = "weekday_text")
    private List<String> weekdayText = null;

    private double rating;

    private String vicinity;

    @ColumnInfo(name = "workmate_id")
    private String workmateId;

    @Ignore
    private List<String> workmateIds;

    @Ignore
    private List<User> workmates;

    @ColumnInfo(defaultValue = "0")
    private int likes;

    public Place() {}

    public Place(PlaceDetails placeDetails) {
        this.placeId = placeDetails.getPlaceId();
        this.name = placeDetails.getName();
        this.address = placeDetails.getFormattedAddress();
        this.latitude = placeDetails.getGeometry().getLocation().getLatitude();
        this.longitude = placeDetails.getGeometry().getLocation().getLongitude();
        this.phoneNumber = placeDetails.getInternationalPhoneNumber();
        this.websiteUri = placeDetails.getWebsite();

        if(placeDetails.getPhotos() != null && !placeDetails.getPhotos().isEmpty()) {
            this.photoReference = placeDetails.getPhotos().get(0).getPhotoReference();
        }
        if(placeDetails.getOpeningHours() != null && placeDetails.getOpeningHours().getWeekdayText() != null) {
            weekdayText = placeDetails.getOpeningHours().getWeekdayText();
        }
        this.rating = placeDetails.getRating();
        this.vicinity = placeDetails.getVicinity();
    }

    @Ignore
    public Place(String placeId, String name, String address, double latitude,
                 double longitude, String phoneNumber, String websiteUri,
                 String photoReference, List<String> weekdayText,
                 double rating, String vicinity, List<String> workmateIds,
                 List<User> workmates, int likes) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
        this.websiteUri = websiteUri;
        this.photoReference = photoReference;
        this.weekdayText = weekdayText;
        this.rating = rating;
        this.vicinity = vicinity;
        this.workmateIds = workmateIds;
        this.workmates = workmates;
        this.likes = likes;
    }

    @Ignore
    public Place(NearbySearchPlace nearbySearchPlace, PlaceDetails placeDetails) {
        placeId = nearbySearchPlace.getPlaceId();
        name = nearbySearchPlace.getName();
        address = placeDetails.getFormattedAddress();
        latitude = nearbySearchPlace.getGeometry().getLocation().getLatitude();
        longitude = nearbySearchPlace.getGeometry().getLocation().getLongitude();
        if(placeDetails.getInternationalPhoneNumber() != null) {
            phoneNumber = placeDetails.getInternationalPhoneNumber();
        } else {
            phoneNumber = placeDetails.getFormattedPhoneNumber();
        }
        websiteUri = placeDetails.getWebsite();
        if(nearbySearchPlace.getPhotos() != null && !nearbySearchPlace.getPhotos().isEmpty()) {
            photoReference = nearbySearchPlace.getPhotos().get(0).getPhotoReference();
        }

        if(placeDetails.getOpeningHours() != null && placeDetails.getOpeningHours().getWeekdayText() != null) {
            weekdayText = placeDetails.getOpeningHours().getWeekdayText();
        }
        rating = nearbySearchPlace.getRating();
        vicinity = nearbySearchPlace.getVicinity();
    }

    /**
     * Comparator to sort task from A to Z
     */
    public static class PlaceAZComparator implements Comparator<Place> {
        @Override
        public int compare(Place left, Place right) {
            return left.name.compareTo(right.name);
        }
    }


    protected Place(Parcel in) {
        placeId = in.readString();
        name = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        phoneNumber = in.readString();
        websiteUri = in.readString();
        photoReference = in.readString();
        weekdayText = in.createStringArrayList();
        rating = in.readDouble();
        vicinity = in.readString();
        workmateId = in.readString();
        workmateIds = in.createStringArrayList();
        workmates = in.createTypedArrayList(User.CREATOR);
        likes = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeId);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(phoneNumber);
        dest.writeString(websiteUri);
        dest.writeString(photoReference);
        dest.writeStringList(weekdayText);
        dest.writeDouble(rating);
        dest.writeString(vicinity);
        dest.writeString(workmateId);
        dest.writeStringList(workmateIds);
        dest.writeTypedList(workmates);
        dest.writeInt(likes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    @NonNull
    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(@NonNull String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(String websiteUri) {
        this.websiteUri = websiteUri;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public List<String> getWeekdayText() {
        return weekdayText;
    }

    public void setWeekdayText(List<String> weekdayText) {
        this.weekdayText = weekdayText;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    @Exclude
    public String getWorkmateId() {
        return workmateId;
    }

    public void setWorkmateId(String workmateId) {
        this.workmateId = workmateId;
    }

    public List<String> getWorkmateIds() {
        return workmateIds;
    }

    public void setWorkmateIds(List<String> workmateIds) {
        this.workmateIds = workmateIds;
    }

    public List<User> getWorkmates() {
        return workmates;
    }

    public void setWorkmates(List<User> workmates) {
        this.workmates = workmates;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place that = (Place) o;
        return placeId.equals(that.placeId) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, name);
    }
}
