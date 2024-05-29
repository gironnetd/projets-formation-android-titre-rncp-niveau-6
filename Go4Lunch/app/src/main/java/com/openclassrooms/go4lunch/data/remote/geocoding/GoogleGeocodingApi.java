package com.openclassrooms.go4lunch.data.remote.geocoding;

import com.openclassrooms.go4lunch.data.model.api.geocoding.GeocodingSearchResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit interface for Google Geocoding Api
 */
public interface GoogleGeocodingApi {

    @GET("json?")
    Call<GeocodingSearchResponse> deviceLocation(@Query("latlng") String latitudeLongitude,
                                                 @Query("location_type") String locationType,
                                                 @Query("result_type") String resultType,
                                                 @Query("key") String key) throws IOException;
}
