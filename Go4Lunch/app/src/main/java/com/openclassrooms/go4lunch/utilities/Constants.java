package com.openclassrooms.go4lunch.utilities;

/**
 * Constants class for application
 */
public class Constants {
    public static final int GOOGLE_SIGN_IN = 123;
    public static final int AUTHENTICATION_REQUEST_CODE = 1234;
    public static final String CURRENT_USER_AUTHENTICATED = "CURRENT_USER_AUTHENTICATED";
    public static final String NEARBY_SEARCH_RANK_BY = "distance";
    public static final String NEARBY_SEARCH_TYPE = "restaurant";
    public static final String PLACES = "places";
    public static final String SHARED_PREFERENCES_NAME_FILE = "shared_prefs";
    // number normally between 0 and 60
    public static int PLACES_RESULT_NUMBER = 20;
    public static final String PLACE_DETAILS_REQUEST_FIELDS = "formatted_phone_number," +
            "name," +
            "place_id," +
            "geometry," +
            "rating," +
            "international_phone_number," +
            "formatted_address," +
            "opening_hours," +
            "photos," +
            "opening_hours," +
            "vicinity," +
            "website";
    public static final String RESTAURANT_DETAIL_ID = "RESTAURANT_DETAIL_ID";
    public static final String IS_AUTHENTICATION_ACTIVITY_LAUNCH_FROM_MAIN_ACTIVITY = "IS_AUTHENTICATION_ACTIVITY_LAUNCH_FROM_MAIN_ACTIVITY";
}
