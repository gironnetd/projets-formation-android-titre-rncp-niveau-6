package com.openclassrooms.go4lunch.data.model.api.geocoding;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Address Component Class from Google Geocoding Api
 */
public class AddressComponent {

    @SerializedName("long_name")
    @Expose
    private String longName;

    @SerializedName("short_name")
    @Expose
    private String shortName;

    @SerializedName("types")
    @Expose
    private List<String> types = null;

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

}
