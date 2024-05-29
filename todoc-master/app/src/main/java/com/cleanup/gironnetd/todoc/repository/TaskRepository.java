package com.cleanup.gironnetd.todoc.repository;

import com.cleanup.gironnetd.todoc.data.dao.TaskDao;
import com.cleanup.gironnetd.todoc.model.Task;

import java.util.List;

import io.reactivex.Single;

public class TaskRepository {

    private TaskDao taskDao;
    private static TaskRepository instance;

    public TaskRepository(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public Single<List<Task>> getAllTasks() {
        return taskDao.getAll();
    }

    public void insertTask(Task task) {
        taskDao.insert(task);
    }

    public void deleteTask(Task task) {
        taskDao.delete(task);
    }

    public static TaskRepository getInstance(TaskDao taskDao) {
        if(instance == null) {
            synchronized (TaskRepository.class) {
                instance = new TaskRepository(taskDao);
            }
        }
        return instance;
    }
}
