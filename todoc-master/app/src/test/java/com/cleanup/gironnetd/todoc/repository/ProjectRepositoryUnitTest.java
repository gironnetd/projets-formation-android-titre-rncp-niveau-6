package com.cleanup.gironnetd.todoc.repository;

import com.cleanup.gironnetd.todoc.data.dao.ProjectDao;
import com.cleanup.gironnetd.todoc.model.Project;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ProjectRepositoryUnitTest {

    private ProjectRepository repository;

    @Mock
    private ProjectDao projectDao;

    @Before
    public void createRepository() {
        MockitoAnnotations.initMocks(this);
        repository = new ProjectRepository(projectDao);
    }

    @Test
    public void get_all_projects_from_repository() {
        when(projectDao.getAll()).thenReturn(Single.just(Arrays.asList(Project.getAllProjects())));

        List<Project> actualProjects = Arrays.asList(Project.getAllProjects());
        List<Project> expectedProjects = repository.getAllProjects().blockingGet();

        for(int index = 0; index < expectedProjects.size(); index++) {
            assertEquals(expectedProjects.get(index).getId(), actualProjects.get(index).getId());
            assertEquals(expectedProjects.get(index).getName(), actualProjects.get(index).getName());
            assertEquals(expectedProjects.get(index).getColor(), actualProjects.get(index).getColor());
        }
    }
}
