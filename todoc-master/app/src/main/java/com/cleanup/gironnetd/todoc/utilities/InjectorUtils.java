package com.cleanup.gironnetd.todoc.utilities;

import android.content.Context;

import com.cleanup.gironnetd.todoc.data.AppDatabase;
import com.cleanup.gironnetd.todoc.repository.ProjectRepository;
import com.cleanup.gironnetd.todoc.repository.TaskRepository;
import com.cleanup.gironnetd.todoc.viewmodel.project.ProjectViewModelFactory;
import com.cleanup.gironnetd.todoc.viewmodel.task.TaskViewModelFactory;

public class InjectorUtils {

    static public TaskViewModelFactory provideTaskViewModelFactory(Context context) {
        TaskRepository taskRepository = getTaskRepository(context);
        return new TaskViewModelFactory(taskRepository);
    }

    static private TaskRepository getTaskRepository(Context context) {
        return TaskRepository.getInstance(AppDatabase.database(context).taskDao());
    }

    static public ProjectViewModelFactory provideProjectViewModelFactory(Context context) {
        ProjectRepository projectRepository = getProjectRepository(context);
        return new ProjectViewModelFactory(projectRepository);
    }

    static private ProjectRepository getProjectRepository(Context context) {
        return ProjectRepository.getInstance(AppDatabase.database(context).projectDao());
    }
}
