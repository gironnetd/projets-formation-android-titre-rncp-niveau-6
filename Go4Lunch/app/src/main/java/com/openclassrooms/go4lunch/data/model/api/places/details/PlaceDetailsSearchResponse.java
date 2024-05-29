package com.openclassrooms.go4lunch.data.model.api.places.details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Place Details Search Response Class from Google Place Details Api
 */
public class PlaceDetailsSearchResponse {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;

    @SerializedName("result")
    @Expose
    private PlaceDetails placeDetails;

    @SerializedName("status")
    @Expose
    private String status;

    public PlaceDetails getPlaceDetails() {
        return placeDetails;
    }
}
