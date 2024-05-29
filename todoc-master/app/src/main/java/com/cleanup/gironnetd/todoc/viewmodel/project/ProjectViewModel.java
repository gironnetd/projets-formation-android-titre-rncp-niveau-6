package com.cleanup.gironnetd.todoc.viewmodel.project;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cleanup.gironnetd.todoc.model.Project;
import com.cleanup.gironnetd.todoc.repository.ProjectRepository;
import com.cleanup.gironnetd.todoc.utilities.EspressoIdlingResource;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProjectViewModel extends ViewModel {

    private ProjectRepository  projectRepository;
    private MutableLiveData<List<Project>> _projects;

    public ProjectViewModel(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
        _projects = new MutableLiveData<>();
        // loadProjects();
    }

    private void loadProjects() {
        EspressoIdlingResource.increment();

        Disposable disposable = projectRepository.getAllProjects().toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                        EspressoIdlingResource.decrement(); // Set app as idle.
                    }
                })
                .subscribe(projects ->  {
                    _projects.setValue(projects);
                });
    }

    public LiveData<List<Project>> projects() {
        loadProjects();
        return _projects;
    }
}
