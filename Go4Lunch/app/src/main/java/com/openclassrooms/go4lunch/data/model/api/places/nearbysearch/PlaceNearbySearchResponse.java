package com.openclassrooms.go4lunch.data.model.api.places.nearbysearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Nearby Search Place Response Class from Google Place Nearby Search Api
 */
public class PlaceNearbySearchResponse {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;

    @SerializedName("next_page_token")
    @Expose
    private String nextPageToken;

    @SerializedName("results")
    @Expose
    private List<NearbySearchPlace> places = null;

    public PlaceNearbySearchResponse() {}

    public String getNextPageToken() {
        return nextPageToken;
    }

    public List<NearbySearchPlace> getPlaces() {
        return places;
    }

    public void setPlaces(List<NearbySearchPlace> places) {
        this.places = places;
    }
}
