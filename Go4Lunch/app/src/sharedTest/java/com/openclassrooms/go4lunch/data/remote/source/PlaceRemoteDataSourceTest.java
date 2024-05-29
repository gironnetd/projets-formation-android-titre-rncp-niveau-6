package com.openclassrooms.go4lunch.data.remote.source;

import android.content.Context;
import android.location.Location;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.go4lunch.data.local.prefs.AppPreferences;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.model.db.factory.PlaceFactory;
import com.openclassrooms.go4lunch.data.model.db.factory.UserFactory;
import com.openclassrooms.go4lunch.data.remote.geocoding.GoogleGeocodingApi;
import com.openclassrooms.go4lunch.data.remote.places.GooglePlacesApi;
import com.openclassrooms.go4lunch.data.repository.geocoding.GoogleGeocodingApiService;
import com.openclassrooms.go4lunch.data.repository.maps.GoogleMapApiService;
import com.openclassrooms.go4lunch.data.repository.places.GooglePlaceApiService;
import com.openclassrooms.go4lunch.repository.geocoding.MockGoogleGeocodingApi;
import com.openclassrooms.go4lunch.repository.places.MockGooglePlacesApi;
import com.openclassrooms.go4lunch.utilities.RxImmediateSchedulerRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static com.google.common.truth.Truth.assertThat;
import static com.openclassrooms.go4lunch.utilities.Constants.PLACES_RESULT_NUMBER;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class PlaceRemoteDataSourceTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    private PlaceRemoteDataSourceImpl placeRemoteDataSource;
    private UserRemoteDataSourceImpl userRemoteDataSource;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private GoogleGeocodingApiService geocodingApiService;
    private GoogleMapApiService mapApiService;
    private GooglePlaceApiService placesApiService;

    private final int PLACES_COUNT = 5;

    private String ACTUAL_CODE_POSTAL = "78150";
    private String NEW_CODE_POSTAL = "75001";

    private final String EMAIL = "gironnetd@yahoo.se";
    private final String PASSWORD = "Gironn050580";

    private Context instrumentationContext;

    @Before
    public void setUp() throws Exception {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://test.com")
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();

        MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();
        instrumentationContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        BehaviorDelegate<GoogleGeocodingApi> geocodingApiBehaviorDelegate = mockRetrofit.create(GoogleGeocodingApi.class);
        MockGoogleGeocodingApi mockGoogleGeocodingApi = new MockGoogleGeocodingApi(geocodingApiBehaviorDelegate, instrumentationContext);

        geocodingApiService = new GoogleGeocodingApiService(instrumentationContext, mockGoogleGeocodingApi);

        mapApiService = GoogleMapApiService.instance(instrumentationContext);

        BehaviorDelegate<GooglePlacesApi> placesApiBehaviorDelegate = mockRetrofit.create(GooglePlacesApi.class);
        MockGooglePlacesApi mockGooglePlacesApi = new MockGooglePlacesApi(placesApiBehaviorDelegate, instrumentationContext);
        PLACES_RESULT_NUMBER = 2;

        placesApiService = new GooglePlaceApiService(instrumentationContext, null, mockGooglePlacesApi);

        placeRemoteDataSource = new PlaceRemoteDataSourceImpl(firebaseFirestore,
                geocodingApiService,
                mapApiService,
                placesApiService);

        userRemoteDataSource = new UserRemoteDataSourceImpl(firebaseAuth,
                firebaseFirestore);

        // make sure user is authenticated
        userRemoteDataSource.signInWithEmailAndPassword(EMAIL, PASSWORD).blockingGet();
    }

    @After
    public void tearDown() throws Exception {
        placeRemoteDataSource = null;
        userRemoteDataSource = null;
        firebaseFirestore = null;
        geocodingApiService = null;
        mapApiService = null;
        placesApiService = null;
    }

    @Test
    public void save_places() throws ExecutionException, InterruptedException {
        User user = UserFactory.makeUser();
        List<Place> places = new ArrayList<>();
        for (int index = 0; index < PLACES_COUNT; index++) {
            Place place = PlaceFactory.makePlace();
            places.add(place);
        }
        Collections.sort(places, new Place.PlaceAZComparator());
        placeRemoteDataSource.savePlaces(user.getUid(), places).blockingGet();

        QuerySnapshot querySnapshot = Tasks.await(firebaseFirestore.collection(Place.RESTAURANT_COLLECTION)
                .whereArrayContains(Place.FIELD_WORKMATE_IDS, user.getUid())
                .orderBy(Place.FIELD_NAME, Query.Direction.ASCENDING)
                .get());

        List<Place> expectedPlaces = querySnapshot.toObjects(Place.class);

        assertThat(expectedPlaces).isNotNull();
        assertThat(expectedPlaces).hasSize(places.size());
        assertThat(expectedPlaces).isEqualTo(places);

        for (Place placeToDelete : places) {
            placeRemoteDataSource.deletePlace(placeToDelete.getPlaceId()).blockingGet();
        }
    }

    @Test
    public void find_place_by_id() {
        User user = UserFactory.makeUser();
        String placeId = UUID.randomUUID().toString();

        Place place = PlaceFactory.makePlace(placeId);
        List<Place> places = new ArrayList<>();
        places.add(place);
        for (int index = 0; index < PLACES_COUNT; index++) {
            places.add(PlaceFactory.makePlace());
        }

        placeRemoteDataSource.savePlaces(user.getUid(), places).blockingGet();

        Place expectedPlace = placeRemoteDataSource.findPlaceById(placeId).blockingGet();

        assertThat(expectedPlace).isNotNull();
        assertThat(expectedPlace).isEqualTo(place);

        for (Place placeToDelete : places) {
            placeRemoteDataSource.deletePlace(placeToDelete.getPlaceId()).blockingGet();
        }
    }

    @Test
    public void increment_likes() {
        String userId = UUID.randomUUID().toString();
        Place newPlace = PlaceFactory.makePlace();
        placeRemoteDataSource.savePlaces(userId, Arrays.asList(newPlace)).blockingGet();

        String placeId = newPlace.getPlaceId();
        int initialLikeCount = newPlace.getLikes();
        int likes = newPlace.getLikes();
        likes++;
        newPlace.setLikes(likes);

        placeRemoteDataSource.incrementLikes(newPlace).blockingGet();
        Place expectedPlace = placeRemoteDataSource.findPlaceById(placeId).blockingGet();

        assertThat(expectedPlace.getLikes()).isEqualTo(initialLikeCount + 1);

        placeRemoteDataSource.deletePlace(newPlace.getPlaceId()).blockingGet();
    }

    @Test
    public void remove_user() {
        User user = UserFactory.makeUser();
        Place newPlace = PlaceFactory.makePlace();
        user.setMiddayRestaurantId(newPlace.getPlaceId());
        user.setMiddayRestaurant(newPlace);

        placeRemoteDataSource.savePlaces(user.getUid(), Arrays.asList(newPlace)).blockingGet();
        placeRemoteDataSource.addUser(user).blockingGet();

        Place expectedPlace = placeRemoteDataSource.findPlaceById(newPlace.getPlaceId()).blockingGet();

        assertThat(expectedPlace.getWorkmates()).contains(user);

        placeRemoteDataSource.removeUser(user).blockingGet();
        expectedPlace = placeRemoteDataSource.findPlaceById(newPlace.getPlaceId()).blockingGet();

        assertThat(expectedPlace.getWorkmates()).doesNotContain(user);

        placeRemoteDataSource.deletePlace(newPlace.getPlaceId()).blockingGet();
    }

    @Test
    public void add_user() {
        User user = UserFactory.makeUser();
        Place newPlace = PlaceFactory.makePlace();
        user.setMiddayRestaurantId(newPlace.getPlaceId());
        user.setMiddayRestaurant(newPlace);

        placeRemoteDataSource.savePlaces(user.getUid(), Arrays.asList(newPlace)).blockingGet();
        placeRemoteDataSource.addUser(user).blockingGet();

        Place expectedPlace = placeRemoteDataSource.findPlaceById(newPlace.getPlaceId()).blockingGet();

        assertThat(expectedPlace.getWorkmates()).contains(user);

        placeRemoteDataSource.deletePlace(newPlace.getPlaceId()).blockingGet();
    }

    @Test
    public void change_place() {
        User user = UserFactory.makeUser();
        Place place = PlaceFactory.makePlace();
        Place newPlace = PlaceFactory.makePlace();
        user.setMiddayRestaurantId(place.getPlaceId());
        user.setMiddayRestaurant(place);

        placeRemoteDataSource.savePlaces(user.getUid(), Arrays.asList(newPlace, place)).blockingGet();
        placeRemoteDataSource.addUser(user).blockingGet();

        placeRemoteDataSource.changePlace(user, newPlace).blockingGet();
        placeRemoteDataSource.addUser(user).blockingGet();
        Place initialPlaceWithUser = placeRemoteDataSource.findPlaceById(place.getPlaceId()).blockingGet();

        assertThat(initialPlaceWithUser.getWorkmates()).isEmpty();

        Place newPlaceWithUser = placeRemoteDataSource.findPlaceById(newPlace.getPlaceId()).blockingGet();

        assertThat(newPlaceWithUser.getWorkmates()).contains(user);

        placeRemoteDataSource.deletePlace(place.getPlaceId()).blockingGet();
        placeRemoteDataSource.deletePlace(newPlace.getPlaceId()).blockingGet();

    }

    @Test
    public void query_all_restaurants() {
        User user = UserFactory.makeUser();
        Query query = placeRemoteDataSource.queryAllRestaurants(user.getUid()).blockingGet();
        assertThat(query).isNotNull();
    }

    @Test
    public void is_new_postal_code() {
        AppPreferences appPreferences = AppPreferences.preferences(instrumentationContext);
        appPreferences.setPrefKeyDeviceLocationPostalCode(null);

        Boolean isNewPostalCode = placeRemoteDataSource.isNewPostalCode(new Location("")).blockingGet();
        assertThat(isNewPostalCode).isTrue();

        appPreferences.setPrefKeyDeviceLocationPostalCode(ACTUAL_CODE_POSTAL);

        isNewPostalCode = placeRemoteDataSource.isNewPostalCode(new Location("")).blockingGet();
        assertThat(isNewPostalCode).isFalse();

        appPreferences.setPrefKeyDeviceLocationPostalCode(NEW_CODE_POSTAL);

        isNewPostalCode = placeRemoteDataSource.isNewPostalCode(new Location("")).blockingGet();
        assertThat(isNewPostalCode).isTrue();
    }

    @Test
    public void find_places() throws IOException, InterruptedException {
        List<Place> expectedPlaces = placeRemoteDataSource.findPlaces().blockingGet();

        assertThat(expectedPlaces).isNotNull();
        assertThat(expectedPlaces).hasSize(PLACES_RESULT_NUMBER);
    }

    @Test
    public void search_place() {
        Place placeById = placeRemoteDataSource.searchPlace("ChIJd75k9Qh95kcRNfywwPTpybI").blockingGet();

        assertThat(placeById).isNotNull();
        assertThat(placeById.getPlaceId()).isEqualTo("ChIJd75k9Qh95kcRNfywwPTpybI");
    }

    @Test
    public void search_places() {

        List<String> placeIds = new ArrayList<>();
        placeIds.add(0, "ChIJd75k9Qh95kcRNfywwPTpybI");
        placeIds.add(1, "ChIJGyCD9Ah95kcRzM4_Unb9JlU");

        List<Place> places = placeRemoteDataSource.searchPlaces(placeIds).blockingGet();

        assertThat(places).isNotNull();
        assertThat(places).isNotEmpty();
        assertThat(places).hasSize(2);
    }
}