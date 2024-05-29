package com.openclassrooms.go4lunch.viewmodel.main.workmate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * View model for the workmate fragment in main activity
 */
public class WorkmateViewModel extends ViewModel {

    private final UserRepository userRepository;

    private MutableLiveData<Query> _queryAllWorkmates;
    private CompositeDisposable compositeDisposable;

    public WorkmateViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        _queryAllWorkmates = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public void queryAllWorkmates() {
        Disposable disposable = userRepository.queryAllWorkmates()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(query -> _queryAllWorkmates.setValue(query));
        compositeDisposable.add(disposable);
    }

    public LiveData<Query> queryWorkmates() {
        return _queryAllWorkmates;
    }
}
