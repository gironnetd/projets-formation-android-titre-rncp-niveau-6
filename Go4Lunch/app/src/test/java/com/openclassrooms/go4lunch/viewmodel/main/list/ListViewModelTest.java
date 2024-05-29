package com.openclassrooms.go4lunch.viewmodel.main.list;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.openclassrooms.go4lunch.data.local.FakePlaceRepository;
import com.openclassrooms.go4lunch.data.local.FakeUserRepository;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.utilities.RxImmediateSchedulerRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static com.openclassrooms.go4lunch.utilities.LiveDataTestUtil.getValue;


public class ListViewModelTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    private ListViewModel listViewModel;

    private FakeUserRepository userRepository;
    private FakePlaceRepository placeRepository;

    private final int PLACES_COUNT = 10;

    @Before
    public void setUpViewModel() throws Exception {
        placeRepository = new FakePlaceRepository();
        listViewModel = new ListViewModel(userRepository, placeRepository);
    }

    @After
    public void tearDown() throws Exception {
        placeRepository = null;
        listViewModel = null;
    }

    @Test
    public void search_places() throws InterruptedException {
        List<String> placeIds = new ArrayList<>();

        for (int index = 0; index < PLACES_COUNT; index++) {
            placeIds.add(UUID.randomUUID().toString());
        }

        listViewModel.searchPlaces(placeIds);

        List<Place> expectedPlaces = (List<Place>) getValue(listViewModel.places());

        assertThat(expectedPlaces).isNotNull();
        assertThat(expectedPlaces).hasSize(PLACES_COUNT);

        for (int index = 0; index < expectedPlaces.size(); index++) {
            assertThat(expectedPlaces.get(index).getPlaceId()).isEqualTo(placeIds.get(index));
        }
    }
}