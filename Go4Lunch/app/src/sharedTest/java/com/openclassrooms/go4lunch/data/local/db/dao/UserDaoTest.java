package com.openclassrooms.go4lunch.data.local.db.dao;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.openclassrooms.go4lunch.data.local.db.AppDatabase;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.model.db.factory.UserFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class UserDaoTest {

    AppDatabase database;

    @Before
    public void initDataBase() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() {
        database.clearAllTables();
        database.close();
    }

    @Test
    public void save_user() {
        User user = UserFactory.makeUser();
        user.setAuthenticated(true);
        database.userDao().saveUser(user);

        assertThat(database.userDao().loadCurrentUser()).isNotNull();
    }

    @Test
    public void load_current_user() {
        User user = UserFactory.makeUser();
        user.setAuthenticated(true);
        database.userDao().saveUser(user);

        User currentUser = database.userDao().loadCurrentUser();
        assertThat(user).isEqualTo(currentUser);
    }

    @Test
    public void update_user() {
        User user = UserFactory.makeUser();
        user.setAuthenticated(true);
        database.userDao().saveUser(user);

        user.setMiddayRestaurantId(UUID.randomUUID().toString());
        database.userDao().updateUser(user);

        User currentUser = database.userDao().loadCurrentUser();

        assertThat(user.getMiddayRestaurantId()).isEqualTo(currentUser.getMiddayRestaurantId());
        assertThat(user).isEqualTo(currentUser);
    }

    @Test
    public void delete_user() {
        User user = UserFactory.makeUser();
        user.setAuthenticated(true);
        database.userDao().saveUser(user);
        assertThat(database.userDao().loadCurrentUser()).isNotNull();

        database.userDao().deleteUser(user);
        assertThat(database.userDao().loadCurrentUser()).isNull();
    }
}
