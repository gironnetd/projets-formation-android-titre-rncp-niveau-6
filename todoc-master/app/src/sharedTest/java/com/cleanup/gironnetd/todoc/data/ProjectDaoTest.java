package com.cleanup.gironnetd.todoc.data;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.cleanup.gironnetd.todoc.data.AppDatabase;
import com.cleanup.gironnetd.todoc.model.Project;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

@RunWith(AndroidJUnit4.class)

@SmallTest
public class ProjectDaoTest {

    AppDatabase database;

    @Before
    public void initDataBase() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        database.projectDao().insertAll(Arrays.asList(Project.getAllProjects()));
    }

    @After
    public void closeDb() {
        database.clearAllTables();
        database.close();
    }

    @Test
    public void get_all_projects() {
        List<Project> expectedProjects = new ArrayList<Project>(Arrays.asList(Project.getAllProjects()));
        database.projectDao().getAll().subscribe(new SingleObserver<List<Project>>() {
            @Override
            public void onSubscribe(Disposable d) {}
            @Override
            public void onSuccess(List<Project> allActualProjets) {
                for(int index = 0; index < expectedProjects.size(); index++) {
                    Assert.assertEquals(allActualProjets.get(index).getId(), expectedProjects.get(index).getId());
                    Assert.assertEquals(allActualProjets.get(index).getName(), expectedProjects.get(index).getName());
                    Assert.assertEquals(allActualProjets.get(index).getColor(), expectedProjects.get(index).getColor());
                }
            }
            @Override
            public void onError(Throwable e) {}
        });
    }
}
