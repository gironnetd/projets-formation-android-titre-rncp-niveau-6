package com.openclassrooms.go4lunch.data.remote.source;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.model.db.factory.PlaceFactory;
import com.openclassrooms.go4lunch.data.model.db.factory.UserFactory;
import com.openclassrooms.go4lunch.utilities.RxImmediateSchedulerRule;

import net.andreinc.mockneat.MockNeat;
import net.andreinc.mockneat.types.enums.NameType;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import static com.google.common.truth.Truth.assertThat;
import static com.openclassrooms.go4lunch.data.model.db.User.FIELD_WORKMATE_ID;
import static com.openclassrooms.go4lunch.data.model.db.User.WORKMATE_COLLECTION;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class UserRemoteDataSourceTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    private UserRemoteDataSourceImpl userRemoteDataSource;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private final String EMAIL = "gironnetd@yahoo.se";
    private final String PASSWORD = "Gironn050580";
    private final String RESTAURANT_ID = "ChIJT7JaMQh95kcRjXxh_whEo_8";

    @Before
    public void setUp() throws Exception {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userRemoteDataSource = new UserRemoteDataSourceImpl(firebaseAuth,
                firebaseFirestore);
    }

    @After
    public void tearDown() throws Exception {
        firebaseAuth = null;
        firebaseFirestore = null;
        userRemoteDataSource = null;
    }

    @Test
    public void save_user() throws ExecutionException, InterruptedException {
        // make sure user is authenticated
        userRemoteDataSource.signInWithEmailAndPassword(EMAIL, PASSWORD).blockingGet();

        User user = UserFactory.makeUser();
        MockNeat mockNeat = MockNeat.secure();
        String displayName = mockNeat.names().type(NameType.FIRST_NAME).val()
                + " " + mockNeat.names().type(NameType.LAST_NAME).val();
        user.setDisplayName(displayName);
        user.setMiddayRestaurantId(null);
        user.setMiddayRestaurant(null);
        user.setAuthenticated(true);
        userRemoteDataSource.saveUser(user).blockingGet();

        QuerySnapshot snapshot = Tasks.await(firebaseFirestore.collection(WORKMATE_COLLECTION)
                .whereEqualTo(FIELD_WORKMATE_ID, user.getUid())
                .get()
        );

        User currentUser = snapshot.getDocuments().get(0).toObject(User.class);
        assertThat(currentUser).isNotNull();
        assertThat(currentUser).isEqualTo(user);
        assertThat(currentUser.getUid()).isEqualTo(user.getUid());

        userRemoteDataSource.deleteUser(user, false).blockingGet();
    }

    @Test(expected = RuntimeException.class)
    public void find_current_user() throws RuntimeException {
        // make sure user is authenticated
        userRemoteDataSource.signInWithEmailAndPassword(EMAIL, PASSWORD).blockingGet();

        User currentUser = userRemoteDataSource.findCurrentUser().blockingGet();
        assertThat(currentUser).isNotNull();

        userRemoteDataSource.saveUser(currentUser).blockingGet();
        userRemoteDataSource.deleteUser(currentUser, true).blockingGet();

        userRemoteDataSource.findCurrentUser().doOnError(throwable -> {
            assertThat(throwable).isNotNull();
            assertThat(throwable.getMessage()).isEqualTo("Current User is Null");
        }).blockingGet();

        // make sure user is authenticated
        userRemoteDataSource.signInWithEmailAndPassword(EMAIL, PASSWORD).blockingGet();
    }

    @Test
    public void update_user() {
        // make sure user is authenticated
        userRemoteDataSource.signInWithEmailAndPassword(EMAIL, PASSWORD).blockingGet();

        User user = UserFactory.makeUser();
        MockNeat mockNeat = MockNeat.secure();
        String displayName = mockNeat.names().type(NameType.FIRST_NAME).val()
                + " " + mockNeat.names().type(NameType.LAST_NAME).val();
        user.setDisplayName(displayName);
        user.setMiddayRestaurantId(null);
        user.setMiddayRestaurant(null);
        user.setAuthenticated(true);
        userRemoteDataSource.saveUser(user).blockingGet();

        userRemoteDataSource.updateUser(user, null).blockingGet();

        user = userRemoteDataSource.findUserById(user.getUid()).blockingGet();

        assertThat(user.getMiddayRestaurantId()).isNull();
        assertThat(user.getMiddayRestaurant()).isNull();

        Place place = PlaceFactory.makePlace();

        userRemoteDataSource.updateUser(user, place).blockingGet();

        user = userRemoteDataSource.findUserById(user.getUid()).blockingGet();

        assertThat(user.getMiddayRestaurantId()).isEqualTo(place.getPlaceId());
        assertThat(user.getMiddayRestaurant()).isEqualTo(place);

        userRemoteDataSource.deleteUser(user, false).blockingGet();
    }

    @Test
    public void delete_user() throws ExecutionException, InterruptedException {
        // make sure user is authenticated
        userRemoteDataSource.signInWithEmailAndPassword(EMAIL, PASSWORD).blockingGet();

        User user = UserFactory.makeUser();
        MockNeat mockNeat = MockNeat.secure();
        String displayName = mockNeat.names().type(NameType.FIRST_NAME).val()
                + " " + mockNeat.names().type(NameType.LAST_NAME).val();
        user.setDisplayName(displayName);
        user.setMiddayRestaurantId(null);
        user.setMiddayRestaurant(null);
        user.setAuthenticated(true);
        userRemoteDataSource.saveUser(user).blockingGet();

        QuerySnapshot snapshot = Tasks.await(firebaseFirestore.collection(WORKMATE_COLLECTION)
                .whereEqualTo(FIELD_WORKMATE_ID, user.getUid())
                .get()
        );

        User currentUser = snapshot.getDocuments().get(0).toObject(User.class);
        assertThat(currentUser).isNotNull();
        assertThat(currentUser).isEqualTo(user);
        assertThat(currentUser.getUid()).isEqualTo(user.getUid());

        userRemoteDataSource.deleteUser(currentUser, false).blockingGet();

        snapshot = Tasks.await(firebaseFirestore.collection(WORKMATE_COLLECTION)
                .whereEqualTo(FIELD_WORKMATE_ID, currentUser.getUid())
                .get()
        );
        assertThat(snapshot.getDocuments()).isEmpty();
    }

    @Test
    public void query_all_workmates() {
        // make sure user is authenticated
        userRemoteDataSource.signInWithEmailAndPassword(EMAIL, PASSWORD).blockingGet();

        Query query = userRemoteDataSource.queryAllWorkmates().blockingGet();
        assertThat(query).isNotNull();
    }

    @Test
    public void query_workmates_by_restaurant() {
        // make sure user is authenticated
        userRemoteDataSource.signInWithEmailAndPassword(EMAIL, PASSWORD).blockingGet();

        Query query = userRemoteDataSource.queryWorkmatesByRestaurant(RESTAURANT_ID).blockingGet();
        assertThat(query).isNotNull();
    }

    @Test
    public void create_user_with_email_and_password() {
        MockNeat mockNeat = MockNeat.secure();
        String email = mockNeat.emails().val();
        userRemoteDataSource.createUserWithEmailAndPassword(email, PASSWORD).blockingGet();

        User userAuthenticated = userRemoteDataSource.findCurrentUser().blockingGet();

        assertThat(userAuthenticated).isNotNull();
        assertThat(userAuthenticated.isAuthenticated()).isTrue();
    }

    @Test
    public void sign_in_with_email_and_password() {
        User userAuthenticated = userRemoteDataSource.signInWithEmailAndPassword(EMAIL, PASSWORD).blockingGet();

        assertThat(userAuthenticated).isNotNull();
        assertThat(userAuthenticated.isAuthenticated()).isTrue();
    }
}