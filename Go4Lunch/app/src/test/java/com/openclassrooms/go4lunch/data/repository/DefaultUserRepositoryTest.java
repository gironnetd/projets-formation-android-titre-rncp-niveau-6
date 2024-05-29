package com.openclassrooms.go4lunch.data.repository;

import com.google.firebase.auth.AuthCredential;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.model.db.factory.PlaceFactory;
import com.openclassrooms.go4lunch.data.model.db.factory.UserFactory;
import com.openclassrooms.go4lunch.data.source.user.FakeUserLocalDataSource;
import com.openclassrooms.go4lunch.data.source.user.FakeUserRemoteDataSource;
import com.openclassrooms.go4lunch.utilities.RxImmediateSchedulerRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;

public class DefaultUserRepositoryTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    private DefaultUserRepository userRepository;

    private FakeUserLocalDataSource userLocalDataSource;
    private FakeUserRemoteDataSource userRemoteDataSource;

    private User newUser;

    private List<User> remoteUsers = new ArrayList<>();
    private List<User> localUsers = new ArrayList<>();
    private List<User> newUsers = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        User user1 = UserFactory.makeUser();
        User user2 = UserFactory.makeUser();
        User user3 = UserFactory.makeUser();
        newUser = UserFactory.makeUser();

        remoteUsers.add(user1);
        remoteUsers.add(user2);
        remoteUsers.add(user3);

        newUsers.add(user3);

        userRemoteDataSource = new FakeUserRemoteDataSource(remoteUsers);
        userLocalDataSource = new FakeUserLocalDataSource(localUsers);

        userRepository = new DefaultUserRepository(userLocalDataSource, userRemoteDataSource);
    }

    @After
    public void tearDown() throws Exception {
        userRepository = null;
        userLocalDataSource = null;
        userRemoteDataSource = null;
        remoteUsers = null;
        localUsers = null;
        newUsers = null;
    }

    @Test
    public void check_if_user_is_authenticated() {

        newUser = UserFactory.makeUser();
        newUser.setAuthenticated(true);

        userLocalDataSource.users.add(newUser);


        User user = userRepository.isUserAuthenticated().blockingGet();

        assertThat(user.isAuthenticated()).isTrue();
    }

    @Test(expected = RuntimeException.class)
    public void check_if_user_is_not_authenticated() {
        userRepository.isUserAuthenticated().doOnError(throwable -> {
            assertThat(throwable).isNotNull();
        }).blockingGet();
    }

    @Test
    public void firebase_sign_in_with_credential() {
        AuthCredential authCredential = mock(AuthCredential.class);

        User user = userRepository.firebaseSignInWithCredential(authCredential).blockingGet();

        assertThat(user).isNotNull();
        assertThat(user.isAuthenticated()).isTrue();
    }

    @Test
    public void create_user_with_email_and_password() {
        User expectedUser = userRepository.createUserWithEmailAndPassword(UUID.randomUUID().toString(),
                UUID.randomUUID().toString()).blockingGet();

        assertThat(expectedUser.isAuthenticated()).isTrue();
        assertThat(userRemoteDataSource.users).contains(expectedUser);
    }

    @Test
    public void sign_in_with_email_and_password() {
        newUser.setAuthenticated(true);
        userRemoteDataSource.users.add(newUser);

        User expectedUser = userRepository.signInWithEmailAndPassword(newUser.getEmail()
                , UUID.randomUUID().toString()).blockingGet();

        assertThat(expectedUser).isNotNull();
        assertThat(expectedUser).isEqualTo(newUser);
    }

    @Test
    public void save_user() {
        userRepository.saveUser(newUser).blockingGet();

        assertThat(userLocalDataSource.users).contains(newUser);
        assertThat(userRemoteDataSource.users).contains(newUser);
    }

    @Test
    public void update_user() {
        Place place = PlaceFactory.makePlace();

        userRepository.saveUser(newUser).blockingGet();
        userRepository.updateUser(newUser, place).blockingGet();

        int index = userLocalDataSource.users.indexOf(newUser);
        User expectedUser = userLocalDataSource.users.get(index);

        assertThat(expectedUser.getMiddayRestaurantId()).isEqualTo(place.getPlaceId());

        index = userRemoteDataSource.users.indexOf(newUser);
        expectedUser = userRemoteDataSource.users.get(index);

        assertThat(expectedUser.getMiddayRestaurantId()).isEqualTo(place.getPlaceId());
        assertThat(expectedUser.getMiddayRestaurant()).isEqualTo(place);
    }

    @Test
    public void delete_user() {
        userRepository.saveUser(newUser).blockingGet();

        assertThat(userLocalDataSource.users).contains(newUser);
        assertThat(userRemoteDataSource.users).contains(newUser);

        userRepository.deleteUser(newUser, false).blockingGet();

        assertThat(userLocalDataSource.users).doesNotContain(newUser);
        assertThat(userRemoteDataSource.users).doesNotContain(newUser);
    }

    @Test
    public void find_current_user() {
        newUser.setAuthenticated(true);
        userRepository.saveUser(newUser).blockingGet();

        assertThat(userRepository.isUserAuthenticated().blockingGet()).isEqualTo(newUser);
    }
}