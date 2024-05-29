package com.openclassrooms.go4lunch.repository.places;

import android.content.Context;

import com.google.gson.Gson;
import com.openclassrooms.go4lunch.data.model.api.places.details.PlaceDetailsSearchResponse;
import com.openclassrooms.go4lunch.data.model.api.places.nearbysearch.PlaceNearbySearchResponse;
import com.openclassrooms.go4lunch.data.remote.places.GooglePlacesApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;

public class MockGooglePlacesApi implements GooglePlacesApi {

    BehaviorDelegate<GooglePlacesApi> delegate;
    private Context context;

    public MockGooglePlacesApi(BehaviorDelegate<GooglePlacesApi> delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    public Call<PlaceNearbySearchResponse> nearbySearch(String location, String rankby, String type, String key) throws IOException {

        InputStream inputStream = context.getAssets().open("nearbysearch.json");
        String nearbySearchJson = convertStreamToString(inputStream);

        Gson gson = new Gson();
        PlaceNearbySearchResponse response = gson.fromJson(nearbySearchJson, PlaceNearbySearchResponse.class);

        return delegate.returningResponse(response).nearbySearch(location, rankby, type, key);
    }

    @Override
    public Call<PlaceNearbySearchResponse> nearbySearchWithPageToken(String location, String rankby, String type, String key, String pageToken) {
        return null;
    }

    @Override
    public Call<PlaceDetailsSearchResponse> placeDetails(String placeId, String fields, String key) throws IOException {
        List<String> detailsJson = new ArrayList<>();

        InputStream inputStream = context.getAssets().open("detail_ChIJd75k9Qh95kcRNfywwPTpybI.json");
        detailsJson.add(0, convertStreamToString(inputStream));

        inputStream = context.getAssets().open("detail_ChIJGyCD9Ah95kcRzM4_Unb9JlU.json");
        detailsJson.add(1, convertStreamToString(inputStream));

        Gson gson = new Gson();
        PlaceDetailsSearchResponse response = null;

        if (placeId.equals("ChIJd75k9Qh95kcRNfywwPTpybI")) {
            response = gson.fromJson(detailsJson.get(0), PlaceDetailsSearchResponse.class);
        }

        if (placeId.equals("ChIJGyCD9Ah95kcRzM4_Unb9JlU")) {
            response = gson.fromJson(detailsJson.get(1), PlaceDetailsSearchResponse.class);
        }
        return delegate.returningResponse(response).placeDetails(placeId, fields, key);
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
