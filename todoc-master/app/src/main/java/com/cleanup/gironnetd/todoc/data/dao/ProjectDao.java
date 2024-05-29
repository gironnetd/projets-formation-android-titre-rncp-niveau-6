package com.cleanup.gironnetd.todoc.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cleanup.gironnetd.todoc.model.Project;

import java.util.List;


import io.reactivex.Single;

@Dao
public interface ProjectDao {

    @Query("SELECT * FROM project")
    Single<List<Project>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Project> projects);
}
