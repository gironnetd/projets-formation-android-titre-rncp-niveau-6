package com.cleanup.gironnetd.todoc.repository;

import com.cleanup.gironnetd.todoc.data.dao.TaskDao;
import com.cleanup.gironnetd.todoc.model.Task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TaskRepositoryUnitTest {

    private TaskRepository repository;

    @Mock
    private TaskDao taskDao;

    private ArrayList<Task> tasks;

    Task task1 ;
    Task task2 ;
    Task task3 ;

    @Before
    public void createRepository() {
        MockitoAnnotations.initMocks(this);
        repository = new TaskRepository(taskDao);

        task1 = new Task(1, 1, "aaa", 123);
        task2 = new Task(2, 2, "zzz", 124);
        task3 = new Task(3, 3, "hhh", 125);

        tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
    }

    @Test
    public void get_all_tasks_from_repository() {
        when(taskDao.getAll()).thenReturn(Single.just(tasks));

        List<Task> expectedTasks = repository.getAllTasks().blockingGet();

        for(int index = 0; index < expectedTasks.size(); index++) {
            assertEquals(expectedTasks.get(index).getId(), tasks.get(index).getId());
            assertEquals(expectedTasks.get(index).getName(), tasks.get(index).getName());
            assertEquals(expectedTasks.get(index).getCreationTimestamp(), tasks.get(index).getCreationTimestamp());
        }
    }

    @Test
    public void insert_Task_from_Repository() {

        Task newTask = new Task(tasks.size(), 3, "www", 126);

        tasks.add(newTask);
        repository.insertTask(newTask);

        verify(taskDao).insert(newTask);

        when(taskDao.getAll()).thenReturn(Single.just(tasks));

        List<Task> actualTasks = repository.getAllTasks().blockingGet();

        assertSame(tasks, actualTasks);
    }

    @Test
    public void delete_Task_from_repository() {

        final int tasksSize = tasks.size();

        repository.deleteTask(task2);

        verify(taskDao).delete(task2);

        tasks.remove(task2);

        when(taskDao.getAll()).thenReturn(Single.just(tasks));

        List<Task> actualTasks = repository.getAllTasks().blockingGet();

        assertEquals(actualTasks.size(), tasksSize - 1);
    }
}
