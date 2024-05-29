package com.openclassrooms.go4lunch.repository.geocoding;

import android.content.Context;

import com.google.gson.Gson;
import com.openclassrooms.go4lunch.data.model.api.geocoding.GeocodingSearchResponse;
import com.openclassrooms.go4lunch.data.remote.geocoding.GoogleGeocodingApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;

public class MockGoogleGeocodingApi implements GoogleGeocodingApi {

    private BehaviorDelegate<GoogleGeocodingApi> delegate;
    private Context context;

    public MockGoogleGeocodingApi(BehaviorDelegate<GoogleGeocodingApi> delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    public Call<GeocodingSearchResponse> deviceLocation(String latitudeLongitude, String locationType, String resultType, String key) throws IOException {

        InputStream inputStream = context.getAssets().open("geocode.json");
        String geocodeJson = convertStreamToString(inputStream);

        Gson gson = new Gson();
        GeocodingSearchResponse response = gson.fromJson(geocodeJson, GeocodingSearchResponse.class);

        return delegate.returningResponse(response).deviceLocation(latitudeLongitude, locationType, resultType, key);
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
