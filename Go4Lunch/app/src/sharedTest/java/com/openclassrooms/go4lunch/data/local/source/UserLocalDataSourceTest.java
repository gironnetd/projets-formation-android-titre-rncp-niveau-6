package com.openclassrooms.go4lunch.data.local.source;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.openclassrooms.go4lunch.data.local.db.AppDatabase;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.model.db.factory.PlaceFactory;
import com.openclassrooms.go4lunch.data.model.db.factory.UserFactory;
import com.openclassrooms.go4lunch.utilities.RxImmediateSchedulerRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class UserLocalDataSourceTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    private AppDatabase database;
    private UserLocalDataSourceImpl userLocalDataSource;

    @Before
    public void setUp() throws Exception {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        userLocalDataSource = new UserLocalDataSourceImpl(database.userDao());
    }

    @After
    public void tearDown() throws Exception {
        userLocalDataSource = null;
        database.clearAllTables();
        database.close();
    }

    @Test
    public void save_user() {
        User user = UserFactory.makeUser();
        user.setAuthenticated(true);
        userLocalDataSource.saveUser(user).blockingGet();

        User expectedUser = userLocalDataSource.findCurrentUser().blockingGet();
        assertThat(expectedUser).isNotNull();
    }

    @Test
    public void find_current_user() {
        User user = UserFactory.makeUser();
        user.setAuthenticated(true);
        userLocalDataSource.saveUser(user).blockingGet();

        User currentUser = userLocalDataSource.findCurrentUser().blockingGet();
        assertThat(currentUser).isNotNull();
    }

    @Test
    public void update_user() {
        User user = UserFactory.makeUser();
        user.setAuthenticated(true);
        userLocalDataSource.saveUser(user).blockingGet();

        User currentUser = userLocalDataSource.findCurrentUser().blockingGet();
        currentUser.setDisplayName(UUID.randomUUID().toString());

        userLocalDataSource.updateUser(currentUser, null).blockingGet();
        User actualUser = userLocalDataSource.findCurrentUser().blockingGet();

        assertThat(currentUser.getDisplayName()).isEqualTo(actualUser.getDisplayName());

        Place place = PlaceFactory.makePlace();
        userLocalDataSource.updateUser(user, place).blockingGet();
        actualUser = userLocalDataSource.findCurrentUser().blockingGet();

        assertThat(actualUser.getMiddayRestaurantId()).isEqualTo(place.getPlaceId());
    }

    @Test(expected = RuntimeException.class)
    public void delete_user() {
        User user = UserFactory.makeUser();
        user.setAuthenticated(true);
        userLocalDataSource.saveUser(user).blockingGet();

        User currentUser = userLocalDataSource.findCurrentUser().blockingGet();
        assertThat(currentUser).isNotNull();

        userLocalDataSource.deleteUser(user).blockingGet();

        userLocalDataSource.findCurrentUser().doOnError(throwable -> {
            assertThat(throwable).isNotNull();
            assertThat(throwable.getMessage()).isEqualTo("Current User is not Found");
        }).blockingGet();
    }
}