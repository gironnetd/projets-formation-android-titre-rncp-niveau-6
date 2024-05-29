package com.openclassrooms.go4lunch.viewmodel.main;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.openclassrooms.go4lunch.data.local.FakePlaceRepository;
import com.openclassrooms.go4lunch.data.local.FakeUserRepository;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.model.db.factory.UserFactory;
import com.openclassrooms.go4lunch.utilities.RxImmediateSchedulerRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;
import static com.openclassrooms.go4lunch.utilities.LiveDataTestUtil.getValue;

public class MainViewModelTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    private MainViewModel mainViewModel;

    private FakeUserRepository userRepository;
    private FakePlaceRepository placeRepository;

    @Before
    public void setUpViewModel() throws Exception {
        userRepository = new FakeUserRepository();
        placeRepository = new FakePlaceRepository();
        mainViewModel = new MainViewModel(userRepository, placeRepository);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected = RuntimeException.class)
    public void logout_current_user() throws InterruptedException {
        User currentUser = UserFactory.makeUser();
        currentUser.setAuthenticated(true);
        placeRepository.savePlaces(currentUser.getUid(), Collections.singletonList(currentUser.getMiddayRestaurant()))
                .blockingGet();
        userRepository.saveUser(currentUser).blockingGet();

        assertThat(userRepository.isUserAuthenticated().blockingGet()).isNotNull();

        mainViewModel.logoutCurrentUser(currentUser);

        Boolean isUserLogOut = (Boolean) getValue(mainViewModel.isCurrentUserLogOut());

        assertThat(isUserLogOut).isTrue();

        userRepository.isUserAuthenticated().doOnError(throwable -> {
            assertThat(throwable).isNotNull();
        }).blockingGet();
    }
}