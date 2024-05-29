package com.cleanup.gironnetd.todoc.viewmodel.task;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.cleanup.gironnetd.todoc.repository.TaskRepository;

public class TaskViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private TaskRepository taskRepository;

    public TaskViewModelFactory(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TaskViewModel(taskRepository);
    }
}
