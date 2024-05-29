package com.openclassrooms.go4lunch.data.model.api.geocoding;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Geocoding Place Class from Google Geocoding Api
 */
public class GeocodingPlace {

    @SerializedName("address_components")
    @Expose
    private List<AddressComponent> addressComponents = null;

    public List<AddressComponent> getAddressComponents() {
        return addressComponents;
    }
}
