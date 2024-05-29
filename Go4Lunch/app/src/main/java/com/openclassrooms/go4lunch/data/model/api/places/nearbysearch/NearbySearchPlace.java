package com.openclassrooms.go4lunch.data.model.api.places.nearbysearch;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.openclassrooms.go4lunch.data.model.api.places.Geometry;

import java.util.List;

/**
 * Nearby Search Place Class from Google Place Nearby Search Api
 */
public class NearbySearchPlace {

    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("photos")
    @Expose
    private List<Photo> photos = null;

    @SerializedName("rating")
    @Expose
    private double rating;

    @SerializedName("vicinity")
    @Expose
    private String vicinity;

    @NonNull
    public String getPlaceId() {
        return placeId;
    }

    public Geometry getGeometry() {
        return geometry;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
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

    public String getVicinity() {
        return vicinity;
    }
}
