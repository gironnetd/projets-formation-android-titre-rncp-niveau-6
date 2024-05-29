package com.openclassrooms.go4lunch.data.repository.geocoding;

import android.content.Context;
import android.location.Location;

import com.openclassrooms.go4lunch.BuildConfig;
import com.openclassrooms.go4lunch.data.local.prefs.AppPreferences;
import com.openclassrooms.go4lunch.data.model.api.geocoding.AddressComponent;
import com.openclassrooms.go4lunch.data.model.api.geocoding.GeocodingSearchResponse;
import com.openclassrooms.go4lunch.data.remote.geocoding.GoogleGeocodingApi;

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

/**
 * Google Geocoding Api Service implementation
 */
public class GoogleGeocodingApiService {

    private static GoogleGeocodingApiService instance;

    private Context mContext;
    private GoogleGeocodingApi geocodingApi;

    public GoogleGeocodingApiService(Context mContext, GoogleGeocodingApi geocodingApi) {
        this.mContext = mContext;
        this.geocodingApi = geocodingApi;
    }

    public static GoogleGeocodingApiService instance(Context context) {
        synchronized (GoogleGeocodingApiService.class) {
            if(instance == null) {
                GoogleGeocodingApi geocodingApi = new Retrofit.Builder()
                        .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                        .create(GoogleGeocodingApi.class);
                instance = new GoogleGeocodingApiService(context, geocodingApi);
            }
            return instance;
        }
    }

    public Single<Boolean> isNewPostalCode(Location location) {
        return Single.create(emitter -> {
            String latitudeLongitude = location.getLatitude() + "," + location.getLongitude();
            String key = BuildConfig.google_maps_key;

            String locationType = "ROOFTOP";
            String resultType = "street_address";
            Call<GeocodingSearchResponse> call = geocodingApi.deviceLocation(latitudeLongitude, locationType, resultType, key);

            if (!isRunningInstrumentedTest()) {
                GeocodingSearchResponse geocodingSearchResponse = call.execute().body();

                if (geocodingSearchResponse == null) {
                    emitter.onSuccess(true);
                } else {
                    List<AddressComponent> addressComponents = Objects.requireNonNull(geocodingSearchResponse).getGeocodingPlaces().get(0).getAddressComponents();
                    String actualPostalCode = AppPreferences.preferences(mContext).getPrefKeyDeviceLocationPostalCode();

                    for (AddressComponent addressComponent : addressComponents) {
                        if (addressComponent.getTypes().contains("postal_code")) {
                            if (actualPostalCode == null) {
                                AppPreferences.preferences(mContext).setPrefKeyDeviceLocationPostalCode(addressComponent.getLongName());

                                emitter.onSuccess(true);
                            } else {
                                if (addressComponent.getLongName().equals(actualPostalCode)) {
                                    emitter.onSuccess(false);
                                } else {
                                    AppPreferences.preferences(mContext).setPrefKeyDeviceLocationPostalCode(addressComponent.getLongName());
                                    emitter.onSuccess(true);
                                }
                            }
                        }
                    }
                    emitter.onSuccess(true);
                }
            }

            if (isRunningInstrumentedTest()) {
                call.enqueue(new Callback<GeocodingSearchResponse>() {
                    @Override
                    public void onResponse(Call<GeocodingSearchResponse> call, Response<GeocodingSearchResponse> response) {
                        GeocodingSearchResponse geocodingSearchResponse = response.body();
                        if (geocodingSearchResponse == null) {
                            emitter.onSuccess(true);
                        } else {
                            List<AddressComponent> addressComponents = Objects.requireNonNull(geocodingSearchResponse).getGeocodingPlaces().get(0).getAddressComponents();
                            String actualPostalCode = AppPreferences.preferences(mContext).getPrefKeyDeviceLocationPostalCode();

                            for (AddressComponent addressComponent : addressComponents) {
                                if (addressComponent.getTypes().contains("postal_code")) {
                                    if (actualPostalCode == null) {
                                        AppPreferences.preferences(mContext).setPrefKeyDeviceLocationPostalCode(addressComponent.getLongName());

                                        emitter.onSuccess(true);
                                    } else {
                                        if (addressComponent.getLongName().equals(actualPostalCode)) {
                                            emitter.onSuccess(false);
                                        } else {
                                            AppPreferences.preferences(mContext).setPrefKeyDeviceLocationPostalCode(addressComponent.getLongName());
                                            emitter.onSuccess(true);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<GeocodingSearchResponse> call, Throwable t) {
                    }
                });
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
