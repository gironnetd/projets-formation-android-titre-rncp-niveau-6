package com.openclassrooms.go4lunch.data.repository.places;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.remote.places.GooglePlacesApi;
import com.openclassrooms.go4lunch.repository.places.MockGooglePlacesApi;
import com.openclassrooms.go4lunch.utilities.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class GooglePlaceApiServiceTest {

    private GooglePlaceApiService placesApiService;

    private MockRetrofit mockRetrofit;
    private Retrofit retrofit;
    private Context instrumentationContext;

    @Before
    public void setUp() throws Exception {
        retrofit = new Retrofit.Builder().baseUrl("http://test.com")
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();

        mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();
        instrumentationContext = InstrumentationRegistry.getInstrumentation().getContext();

        BehaviorDelegate<GooglePlacesApi> delegate = mockRetrofit.create(GooglePlacesApi.class);

        MockGooglePlacesApi mockGooglePlacesApi = new MockGooglePlacesApi(delegate, instrumentationContext);

        Constants.PLACES_RESULT_NUMBER = 2;

        placesApiService = new GooglePlaceApiService(instrumentationContext, null, mockGooglePlacesApi);
    }

    @After
    public void tearDown() throws Exception {
        placesApiService = null;
        instrumentationContext = null;
        mockRetrofit = null;
        retrofit = null;
    }

    @Test
    public void find_places() throws IOException, InterruptedException {

        List<Place> places = placesApiService.findPlaces().blockingGet();

        assertThat(places).isNotNull();
        assertThat(places).isNotEmpty();
        assertThat(places).hasSize(2);
    }

    @Test
    public void search_place() {
        Place placeById = placesApiService.searchPlace("ChIJd75k9Qh95kcRNfywwPTpybI").blockingGet();

        assertThat(placeById).isNotNull();
        assertThat(placeById.getPlaceId()).isEqualTo("ChIJd75k9Qh95kcRNfywwPTpybI");

    }

    @Test
    public void search_places() {
        List<String> placeIds = Arrays.asList("ChIJd75k9Qh95kcRNfywwPTpybI", "ChIJGyCD9Ah95kcRzM4_Unb9JlU");

        List<Place> places = placesApiService.searchPlaces(placeIds).blockingGet();

        assertThat(places).isNotNull();
        assertThat(places).isNotEmpty();
        assertThat(places).hasSize(2);
    }
}