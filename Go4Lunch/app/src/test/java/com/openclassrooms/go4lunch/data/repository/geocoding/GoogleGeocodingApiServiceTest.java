package com.openclassrooms.go4lunch.data.repository.geocoding;

import android.content.Context;
import android.location.Location;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.openclassrooms.go4lunch.data.local.prefs.AppPreferences;
import com.openclassrooms.go4lunch.data.remote.geocoding.GoogleGeocodingApi;
import com.openclassrooms.go4lunch.repository.geocoding.MockGoogleGeocodingApi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class GoogleGeocodingApiServiceTest {

    private GoogleGeocodingApiService geocodingApiService;

    private MockRetrofit mockRetrofit;
    private Retrofit retrofit;
    private Context instrumentationContext;
    private AppPreferences appPreferences;

    private String ACTUAL_CODE_POSTAL = "78150";
    private String NEW_CODE_POSTAL = "75001";

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

        BehaviorDelegate<GoogleGeocodingApi> delegate = mockRetrofit.create(GoogleGeocodingApi.class);

        MockGoogleGeocodingApi mockGoogleGeocodingApi = new MockGoogleGeocodingApi(delegate, instrumentationContext);

        geocodingApiService = new GoogleGeocodingApiService(instrumentationContext, mockGoogleGeocodingApi);
        appPreferences = AppPreferences.preferences(instrumentationContext);
    }

    @After
    public void tearDown() throws Exception {
        retrofit = null;
        mockRetrofit = null;
        geocodingApiService = null;
    }

    @Test
    public void is_new_postal_code() {

        Boolean isNewPostalCode = geocodingApiService.isNewPostalCode(new Location("")).blockingGet();
        assertThat(isNewPostalCode).isTrue();

        appPreferences.setPrefKeyDeviceLocationPostalCode(ACTUAL_CODE_POSTAL);

        isNewPostalCode = geocodingApiService.isNewPostalCode(new Location("")).blockingGet();
        assertThat(isNewPostalCode).isFalse();

        appPreferences.setPrefKeyDeviceLocationPostalCode(NEW_CODE_POSTAL);

        isNewPostalCode = geocodingApiService.isNewPostalCode(new Location("")).blockingGet();
        assertThat(isNewPostalCode).isTrue();
    }
}