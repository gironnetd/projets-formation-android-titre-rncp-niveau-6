package com.cleanup.gironnetd.todoc.data;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.cleanup.gironnetd.todoc.data.AppDatabase;
import com.cleanup.gironnetd.todoc.model.Project;
import com.cleanup.gironnetd.todoc.model.Task;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TaskDaoTest {

    AppDatabase database;
    private final int ITEM_COUNT = 4;

    private List<Task> tasks;

    @Before
    public void initDataBase() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                database.projectDao().insertAll(Arrays.asList(Project.getAllProjects()));
                            }
                        });
                    }
                })
                .allowMainThreadQueries()
                .build();

        tasks = new ArrayList<>();
        for(int index = 0; index < ITEM_COUNT; index++) {
            tasks.add(new Task(index + 1, index, "test task " + (index + 1), new Date().getTime()));
            database.taskDao().insert(tasks.get(index));
        }
    }

    @After
    public void closeDb() {
        database.taskDao().deleteAll(database.taskDao().getAll().blockingGet());
        database.clearAllTables();
        database.close();
        tasks = null;
    }

    @Test
    public void get_all_tasks() {
        database.taskDao().getAll().subscribe(new SingleObserver<List<Task>>() {
            @Override
            public void onSubscribe(Disposable d) {}
            @Override
            public void onSuccess(List<Task> tasks) {
                for(int index = 0; index < tasks.size(); index++) {
                    Assert.assertEquals(index + 1, tasks.get(index).getId());
                    Assert.assertEquals("test task " + (index + 1), tasks.get(index).getName());
                }
            }
            @Override
            public void onError(Throwable e) {}
        });
    }

    @Test
    public void add_tasks() {
        Task task = new Task(ITEM_COUNT + 1, "test task " + (ITEM_COUNT + 1));
        database.taskDao().insert(task);
        database.taskDao().getAll().subscribe(new SingleObserver<List<Task>>() {
            @Override
            public void onSubscribe(Disposable d) {}
            @Override
            public void onSuccess(List<Task> tasks) {
                Assert.assertEquals(ITEM_COUNT + 1, tasks.size());
            }
            @Override
            public void onError(Throwable e) {}
        });
    }

    @Test
    public void remove_tasks() {
        database.taskDao().delete(tasks.get(2));
        database.taskDao().getAll().subscribe(new SingleObserver<List<Task>>() {
            @Override
            public void onSubscribe(Disposable d) {}
            @Override
            public void onSuccess(List<Task> tasks) {
                Assert.assertEquals(ITEM_COUNT - 1, tasks.size());
            }
            @Override
            public void onError(Throwable e) {}
        });
    }
}
