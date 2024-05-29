package com.openclassrooms.go4lunch.data.model.api.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Geometry Class from both Google Place Nearby Search Api and Place Details Api
 */
public class Geometry {

    @SerializedName("location")
    @Expose
    private Location location;

    public Geometry() {}

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
