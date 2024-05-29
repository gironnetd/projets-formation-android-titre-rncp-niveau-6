package com.cleanup.gironnetd.todoc.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.cleanup.gironnetd.todoc.model.Task;
import com.cleanup.gironnetd.todoc.repository.TaskRepository;
import com.cleanup.gironnetd.todoc.utilities.RxImmediateSchedulerRule;
import com.cleanup.gironnetd.todoc.viewmodel.task.TaskViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Single;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertSame;


public class TaskViewModelUnitTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private TaskRepository taskRepository;

    private TaskViewModel taskViewModel;

    private ArrayList<Task> tasks;

    Task task1 ;
    Task task2 ;
    Task task3 ;

    @Before
    public void setupTaskViewModel() {
        MockitoAnnotations.initMocks(this);

        tasks = new ArrayList<>();

        task1 = new Task(1, 1, "aaa", 123);
        task2 = new Task(2, 2, "zzz", 124);
        task3 = new Task(3, 3, "hhh", 125);

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);

        when(taskRepository.getAllTasks()).thenReturn(Single.just(tasks));
        taskViewModel = new TaskViewModel(taskRepository);
    }

    @Test
    public void get_all_tasks() {

        when(taskRepository.getAllTasks()).thenReturn(Single.just(tasks));
        List<Task> actualTasks = taskViewModel.tasks().getValue();

        assertSame(tasks, actualTasks);
    }

    @Test
    public void insert_task_with_success() {
        Task newTask = new Task(tasks.size(), 3, "www", 126);

        tasks.add(newTask);
        taskViewModel.insertTask(newTask);

        verify(taskRepository).insertTask(newTask);

        when(taskRepository.getAllTasks()).thenReturn(Single.just(tasks));

        List<Task> actualTasks = taskViewModel.tasks().getValue();

        assertSame(tasks, actualTasks);
    }

    @Test
    public void delete_task_with_success() {

        final int tasksSize = tasks.size();

        taskViewModel.deleteTask(task2);

        verify(taskRepository).deleteTask(task2);

        tasks.remove(task2);

        when(taskRepository.getAllTasks()).thenReturn(Single.just(tasks));

        List<Task> actualTasks = taskViewModel.tasks().getValue();

        assertEquals(Objects.requireNonNull(actualTasks).size(), tasksSize - 1);
    }

}
