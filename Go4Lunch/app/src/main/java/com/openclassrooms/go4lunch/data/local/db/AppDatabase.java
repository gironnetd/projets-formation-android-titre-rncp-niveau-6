package com.openclassrooms.go4lunch.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.openclassrooms.go4lunch.data.local.db.dao.PlaceDao;
import com.openclassrooms.go4lunch.data.local.db.dao.UserDao;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Room Database Class for application
 */
@Database(entities = {Place.class, User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "go4lunch.db";
    private static AppDatabase instance;

    // For Singleton instantiation
    private static final Object LOCK = new Object();

    public abstract PlaceDao placeDao();

    public abstract UserDao userDao();

    public static AppDatabase database(Context context) {
        synchronized (LOCK) {
            if (instance == null) {
                if (!isRunningInstrumentedTest()) {
                    instance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                            .build();
                }

                if (isRunningInstrumentedTest()) {
                    instance = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                            .allowMainThreadQueries()
                            .build();
                }
            }
            return instance;
        }
    }

    private static AtomicBoolean isRunningInstrumentedTest;

    public static synchronized boolean isRunningInstrumentedTest() {
        if (null == isRunningInstrumentedTest) {
            boolean istest;

            try {
                Class.forName("androidx.test.espresso.Espresso");
                istest = true;
            } catch (ClassNotFoundException e) {
                istest = false;
            }
            isRunningInstrumentedTest = new AtomicBoolean(istest);
        }
        return isRunningInstrumentedTest.get();
    }
}
