package com.openclassrooms.go4lunch.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.openclassrooms.go4lunch.data.model.db.User;
/**
 * Room Dao for User Entity
 */
@Dao
public interface UserDao {

    @Insert
    void saveUser(User user);

    @Query("SELECT * FROM user WHERE authenticated = 1")
    User loadCurrentUser();

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}
