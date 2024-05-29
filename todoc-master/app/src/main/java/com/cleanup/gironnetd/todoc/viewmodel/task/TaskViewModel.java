package com.cleanup.gironnetd.todoc.viewmodel.task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cleanup.gironnetd.todoc.model.Task;
import com.cleanup.gironnetd.todoc.repository.TaskRepository;
import com.cleanup.gironnetd.todoc.utilities.EspressoIdlingResource;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TaskViewModel extends ViewModel {

    private TaskRepository taskRepository;
    private MutableLiveData<List<Task>> _tasks;

    public TaskViewModel(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        _tasks = new MutableLiveData<>();
        loadTasks();
    }

    private void loadTasks() {
        EspressoIdlingResource.increment();

        Disposable disposable = taskRepository.getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                        EspressoIdlingResource.decrement(); // Set app as idle.
                    }
                })
                .subscribe(tasks ->  {
                    _tasks.setValue(tasks);
                });
    }

    public LiveData<List<Task>> tasks() {
        return _tasks;
    }

    public void insertTask(Task task) {
        taskRepository.insertTask(task);
    }

    public void deleteTask(Task task) {
        taskRepository.deleteTask(task);
    }
}
