package com.openclassrooms.go4lunch.data.repository.places;

import android.content.Context;

import com.openclassrooms.go4lunch.BuildConfig;
import com.openclassrooms.go4lunch.data.local.prefs.AppPreferences;
import com.openclassrooms.go4lunch.data.model.api.places.details.PlaceDetails;
import com.openclassrooms.go4lunch.data.model.api.places.details.PlaceDetailsSearchResponse;
import com.openclassrooms.go4lunch.data.model.api.places.nearbysearch.NearbySearchPlace;
import com.openclassrooms.go4lunch.data.model.api.places.nearbysearch.PlaceNearbySearchResponse;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.mapper.PlaceMapper;
import com.openclassrooms.go4lunch.data.remote.places.GooglePlacesApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.openclassrooms.go4lunch.utilities.Constants.NEARBY_SEARCH_RANK_BY;
import static com.openclassrooms.go4lunch.utilities.Constants.NEARBY_SEARCH_TYPE;
import static com.openclassrooms.go4lunch.utilities.Constants.PLACES_RESULT_NUMBER;
import static com.openclassrooms.go4lunch.utilities.Constants.PLACE_DETAILS_REQUEST_FIELDS;

/**
 * Google Place Api Service implementation
 */
public class GooglePlaceApiService {

    private static GooglePlaceApiService instance;

    private String googleApiKey ;
    private GooglePlacesApi placesApi;
    private Context context;

    public GooglePlaceApiService(Context context, String googleApiKey, GooglePlacesApi placesApi) {
        this.googleApiKey = googleApiKey;
        this.placesApi = placesApi;
        this.context = context;
    }

    public static GooglePlaceApiService instance(Context context) {
        synchronized (GooglePlaceApiService.class) {
            if(instance == null) {
                GooglePlacesApi placesApi = new Retrofit.Builder()
                        .baseUrl("https://maps.googleapis.com/maps/api/place/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                        .create(GooglePlacesApi.class);
                instance = new GooglePlaceApiService(context, BuildConfig.google_maps_key, placesApi);
            }
            return instance;
        }
    }

    private final List<Place> places = new ArrayList<>();
    private final int placesResultNumber = PLACES_RESULT_NUMBER;

    public Single<List<Place>> findPlaces() {
        return loadNearbySearchPlaces()
                .flatMap(nearbySearchPlaces -> loadDetailPlaces(nearbySearchPlaces.subList(0, placesResultNumber))
                        .flatMap(placeDetails -> {
                            places.addAll(new PlaceMapper().mapToCached(
                                    nearbySearchPlaces.subList(0, placesResultNumber),
                                    placeDetails));
                            Collections.sort(places, new Place.PlaceAZComparator());
                            return Single.just(places);
                        }));
    }

    private Single<List<NearbySearchPlace>> loadNearbySearchPlaces() {
        return Single.create(emitter -> {
            AppPreferences appPreferences = AppPreferences.preferences(context);
            String latitudeLongitude = appPreferences.getPrefKeyDeviceLocationLatitudeLongitude();
            Call<PlaceNearbySearchResponse> nearbySearchCall = placesApi.nearbySearch(latitudeLongitude, NEARBY_SEARCH_RANK_BY,
                    NEARBY_SEARCH_TYPE, googleApiKey);

            if (!isRunningInstrumentedTest()) {
                PlaceNearbySearchResponse placeNearbySearchResponse = nearbySearchCall.execute().body();
                emitter.onSuccess(Objects.requireNonNull(placeNearbySearchResponse).getPlaces());
            }

            if (isRunningInstrumentedTest()) {
                nearbySearchCall.enqueue(new Callback<PlaceNearbySearchResponse>() {
                    @Override
                    public void onResponse(Call<PlaceNearbySearchResponse> call, Response<PlaceNearbySearchResponse> response) {
                        emitter.onSuccess(Objects.requireNonNull(response.body()).getPlaces());
                    }

                    @Override
                    public void onFailure(Call<PlaceNearbySearchResponse> call, Throwable t) {
                    }
                });
            }
        });
    }

    private Single<List<PlaceDetails>> loadDetailPlaces(List<NearbySearchPlace> nearbySearchPlaces) {
        return Single.create(emitter -> {
            List<PlaceDetails> placeDetails = new ArrayList<>();
            for (int index = 0; index < nearbySearchPlaces.size(); index++) {
                Call<PlaceDetailsSearchResponse> placeDetailsCall =
                        placesApi.placeDetails(nearbySearchPlaces.get(index).getPlaceId(),
                                PLACE_DETAILS_REQUEST_FIELDS,
                                googleApiKey);

                if (!isRunningInstrumentedTest()) {
                    PlaceDetailsSearchResponse detailsSearchResponse = placeDetailsCall.execute().body();
                    placeDetails.add(Objects.requireNonNull(detailsSearchResponse).getPlaceDetails());

                    if (placeDetails.size() == placesResultNumber) {
                        emitter.onSuccess(placeDetails);
                    }
                }

                if (isRunningInstrumentedTest()) {
                    placeDetailsCall.enqueue(new Callback<PlaceDetailsSearchResponse>() {
                        @Override
                        public void onResponse(Call<PlaceDetailsSearchResponse> call, Response<PlaceDetailsSearchResponse> response) {
                            placeDetails.add(Objects.requireNonNull(response.body()).getPlaceDetails());
                            if (placeDetails.size() == placesResultNumber) {
                                emitter.onSuccess(placeDetails);
                            }
                        }

                        @Override
                        public void onFailure(Call<PlaceDetailsSearchResponse> call, Throwable t) {
                        }
                    });
                }
            }
        });
    }

    public Single<Place> searchPlace(String placeId) {
        return Single.create(emitter -> {
            Call<PlaceDetailsSearchResponse> placeDetailsCall =
                    placesApi.placeDetails(placeId,
                            PLACE_DETAILS_REQUEST_FIELDS,
                            googleApiKey);

            if (!isRunningInstrumentedTest()) {
                PlaceDetailsSearchResponse detailsSearchResponse = placeDetailsCall.execute().body();
                if (detailsSearchResponse.getPlaceDetails() != null) {
                    emitter.onSuccess(new Place(detailsSearchResponse.getPlaceDetails()));
                } else {
                    emitter.onError(new Throwable());
                }
            }

            if (isRunningInstrumentedTest()) {
                placeDetailsCall.enqueue(new Callback<PlaceDetailsSearchResponse>() {
                    @Override
                    public void onResponse(Call<PlaceDetailsSearchResponse> call, Response<PlaceDetailsSearchResponse> response) {
                        if (response.body().getPlaceDetails() != null) {
                            emitter.onSuccess(new Place(response.body().getPlaceDetails()));
                        } else {
                            emitter.onError(new Throwable());
                        }
                    }

                    @Override
                    public void onFailure(Call<PlaceDetailsSearchResponse> call, Throwable t) {
                    }
                });
            }

        });
    }

    public Single<List<Place>> searchPlaces(List<String> placeIds) {
        return Single.create(emitter -> {
            List<Place> places = new ArrayList<>();
            for(int index = 0; index < placeIds.size(); index++) {
                Call<PlaceDetailsSearchResponse> placeDetailsCall =
                        placesApi.placeDetails(placeIds.get(index),
                                PLACE_DETAILS_REQUEST_FIELDS,
                                googleApiKey);

                if (!isRunningInstrumentedTest()) {
                    PlaceDetailsSearchResponse detailsSearchResponse = placeDetailsCall.execute().body();
                    places.add(new Place(Objects.requireNonNull(detailsSearchResponse).getPlaceDetails()));
                    if (places.size() == placeIds.size()) {
                        emitter.onSuccess(places);
                    }
                }

                if (isRunningInstrumentedTest()) {
                    placeDetailsCall.enqueue(new Callback<PlaceDetailsSearchResponse>() {
                        @Override
                        public void onResponse(Call<PlaceDetailsSearchResponse> call, Response<PlaceDetailsSearchResponse> response) {
                            places.add(new Place(Objects.requireNonNull(response.body()).getPlaceDetails()));
                            if (places.size() == placeIds.size()) {
                                emitter.onSuccess(places);
                            }
                        }

                        @Override
                        public void onFailure(Call<PlaceDetailsSearchResponse> call, Throwable t) {
                        }
                    });
                }
            }
        });
    }

    private static AtomicBoolean isRunningInstrumentedTest;

    public static synchronized boolean isRunningInstrumentedTest() {
        if (null == isRunningInstrumentedTest) {
            boolean istest;
            try {
                Class.forName("androidx.test.espresso.Espresso");
                istest = true;
            } catch (ClassNotFoundException e) {
                istest = false;
            }
            isRunningInstrumentedTest = new AtomicBoolean(istest);
        }
        return isRunningInstrumentedTest.get();
    }
}
