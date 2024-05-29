package com.openclassrooms.go4lunch.data.model.api.places.details;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.openclassrooms.go4lunch.data.model.api.places.Geometry;
import com.openclassrooms.go4lunch.data.model.api.places.nearbysearch.Photo;

import java.util.List;

/**
 * Place Details Class from Google Place Details Api
 */
public class PlaceDetails {

    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;

    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours = null;

    @SerializedName("international_phone_number")
    @Expose
    private String internationalPhoneNumber;

    @SerializedName("formatted_phone_number")
    @Expose
    private String formattedPhoneNumber;

    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    private double rating;

    @SerializedName("photos")
    @Expose
    private List<Photo> photos = null;

    private String vicinity;

    @NonNull
    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public double getRating() {
        float newRating = (float) Math.round(((rating * 3 / 5)) * 10) / 10;

        double closer = 0;
        if(newRating < 0.5) {
            closer = isCloserTo(newRating, 0, 0.5);
        } else if(newRating >= 0.5 && newRating < 1 ) {
            closer = isCloserTo(newRating, 0.5, 1);
        } else if(newRating >= 1 && newRating < 1.5 ) {
            closer = isCloserTo(newRating, 1, 1.5);
        } else if(newRating >= 1.5 && newRating < 2 ) {
            closer = isCloserTo(newRating, 1.5, 2);
        } else if(newRating >= 2 && newRating < 2.5 ) {
            closer = isCloserTo(newRating, 2, 2.5);
        } else if(newRating >= 2.5 && newRating <= 3 ) {
            closer = isCloserTo(newRating, 2.5, 3);
        }
        return closer;
    }

    private double isCloserTo(double rating, double first, double second) {
        return (Math.abs(rating - first) < Math.abs(rating - second)) ? first : second;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
}
