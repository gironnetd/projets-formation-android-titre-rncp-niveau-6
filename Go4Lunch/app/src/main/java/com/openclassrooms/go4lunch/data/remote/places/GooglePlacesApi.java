package com.openclassrooms.go4lunch.data.remote.places;

import com.openclassrooms.go4lunch.data.model.api.places.details.PlaceDetailsSearchResponse;
import com.openclassrooms.go4lunch.data.model.api.places.nearbysearch.PlaceNearbySearchResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit interface for Google Places Api
 */
public interface GooglePlacesApi {

    @GET("nearbysearch/json?")
    Call<PlaceNearbySearchResponse> nearbySearch(@Query("location") String location,
                                                 @Query("rankby") String rankby,
                                                 @Query("type") String type,
                                                 @Query("key") String key) throws IOException;

    @GET("nearbysearch/json?")
    Call<PlaceNearbySearchResponse> nearbySearchWithPageToken(@Query("location") String location,
                                                              @Query("rankby") String rankby,
                                                              @Query("type") String type,
                                                              @Query("key") String key,
                                                              @Query("pagetoken") String pageToken);

    @GET("details/json?")
    Call<PlaceDetailsSearchResponse> placeDetails(@Query("place_id") String placeId,
                                                  @Query("fields") String fields,
                                                  @Query("key") String key) throws IOException;
}
