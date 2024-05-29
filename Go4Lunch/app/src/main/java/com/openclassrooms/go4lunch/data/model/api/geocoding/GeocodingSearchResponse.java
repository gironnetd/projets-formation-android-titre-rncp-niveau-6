package com.openclassrooms.go4lunch.data.model.api.geocoding;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Geocoding Search Response Class from Google Geocoding Api
 */
public class GeocodingSearchResponse {

    @SerializedName("results")
    @Expose
    private List<GeocodingPlace> geocodingPlaces = null;

    public List<GeocodingPlace> getGeocodingPlaces() {
        return geocodingPlaces;
    }
}
