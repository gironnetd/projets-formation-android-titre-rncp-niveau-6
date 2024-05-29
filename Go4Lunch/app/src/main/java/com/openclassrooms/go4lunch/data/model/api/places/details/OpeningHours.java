package com.openclassrooms.go4lunch.data.model.api.places.details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Opening Hours Class from Google Place Details Api
 */
public class OpeningHours {

    @SerializedName("weekday_text")
    @Expose
    private List<String> weekdayText = null;

    public OpeningHours() {
    }

    public List<String> getWeekdayText() {
        return weekdayText;
    }
}
