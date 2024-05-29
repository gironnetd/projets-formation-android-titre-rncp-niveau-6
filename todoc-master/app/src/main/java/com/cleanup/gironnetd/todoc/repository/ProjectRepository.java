package com.cleanup.gironnetd.todoc.repository;

import com.cleanup.gironnetd.todoc.data.dao.ProjectDao;
import com.cleanup.gironnetd.todoc.model.Project;

import java.util.List;

import io.reactivex.Single;

public class ProjectRepository {

    private ProjectDao projectDao;
    private static ProjectRepository instance;

    public ProjectRepository(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    public Single<List<Project>> getAllProjects() { return projectDao.getAll(); }

    public static ProjectRepository getInstance(ProjectDao projectDao) {
        if(instance == null) {
            synchronized (ProjectRepository.class) {
                instance = new ProjectRepository(projectDao);
            }
        }
        return instance;
    }
}
