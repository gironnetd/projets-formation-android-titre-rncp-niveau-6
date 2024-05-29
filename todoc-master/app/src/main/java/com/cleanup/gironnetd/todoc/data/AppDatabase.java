package com.cleanup.gironnetd.todoc.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cleanup.gironnetd.todoc.data.dao.ProjectDao;
import com.cleanup.gironnetd.todoc.data.dao.TaskDao;
import com.cleanup.gironnetd.todoc.model.Project;
import com.cleanup.gironnetd.todoc.model.Task;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Database(entities = {Project.class, Task.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "todoc.db";
    private static AppDatabase instance;

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AtomicBoolean isRunningTest;

    public static AppDatabase database(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (!isRunningTest()) {
                    instance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            instance.projectDao().insertAll(Arrays.asList(Project.getAllProjects()));
                                        }
                                    });
                                }
                            }).build();
                } else {
                    instance = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            instance.projectDao().insertAll(Arrays.asList(Project.getAllProjects()));
                                        }
                                    });
                                }
                            }).build();
                }
            }
        }
        return instance;
    }

    public static synchronized boolean isRunningTest () {
        if (null == isRunningTest) {
            boolean istest;

            try {
                Class.forName ("androidx.test.espresso.Espresso");
                istest = true;
            } catch (ClassNotFoundException e) {
                istest = false;
            }

            isRunningTest = new AtomicBoolean (istest);
        }

        return isRunningTest.get();
    }

    public abstract TaskDao taskDao();
    public abstract ProjectDao projectDao();
}
