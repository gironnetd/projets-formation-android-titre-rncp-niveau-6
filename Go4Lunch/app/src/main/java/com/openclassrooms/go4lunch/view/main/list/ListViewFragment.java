package com.openclassrooms.go4lunch.view.main.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.view.main.BaseFragment;
import com.openclassrooms.go4lunch.view.main.MainActivity;
import com.openclassrooms.go4lunch.view.main.list.adapter.RestaurantAdapter;
import com.openclassrooms.go4lunch.view.main.list.adapter.SearchPlaceAdapter;
import com.openclassrooms.go4lunch.viewmodel.main.list.ListViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment to display list of restaurants around the user
 */
public class ListViewFragment extends BaseFragment {

    @BindView(R.id.restaurant_recycler_view)
    RecyclerView restaurantsList;

    @BindView(R.id.search_restaurant_recycler_view)
    RecyclerView searchRestaurantsList;

    private RestaurantAdapter mAdapter;
    private ListViewModel listViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, root);

        if (((MainActivity) requireContext()).searchCardView.getVisibility() == View.VISIBLE) {
            searchRestaurantsList.setVisibility(View.VISIBLE);
            restaurantsList.setVisibility(View.GONE);
        } else {
            searchRestaurantsList.setVisibility(View.GONE);
            restaurantsList.setVisibility(View.VISIBLE);
        }
        initAutocompletePredictions();
        initListViewModel();
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchItem.setOnMenuItemClickListener(item -> {
            if (((MainActivity) requireContext()).searchCardView.getVisibility() == View.VISIBLE) {
                ((MainActivity) requireContext()).searchCardView.setVisibility(View.INVISIBLE);
                searchRestaurantsList.setVisibility(View.GONE);
                restaurantsList.setVisibility(View.VISIBLE);
            } else {
                ((MainActivity) requireContext()).searchCardView.setVisibility(View.VISIBLE);
                searchRestaurantsList.setVisibility(View.VISIBLE);
                restaurantsList.setVisibility(View.GONE);
            }
            return false;
        });
    }

    private void initAutocompletePredictions() {
        ((MainActivity) requireContext()).searchView.setQueryHint(getResources().getString(R.string.search_restaurant_title));
        ((MainActivity) requireContext()).searchResultList.setVisibility(View.GONE);

        EditText searchViewEditText = ((MainActivity) requireContext()).searchView.findViewById(R.id.search_src_text);
        searchViewEditText.setEnabled(true);
        ((MainActivity) requireContext()).searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FindAutocompletePredictionsRequest.Builder requestBuilder = FindAutocompletePredictionsRequest
                        .builder()
                        .setQuery(query);
                Task<FindAutocompletePredictionsResponse> task =
                        ((MainActivity) requireContext()).placesClient.findAutocompletePredictions(requestBuilder.build());

                task.addOnSuccessListener(response -> {
                    List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
                    List<String> placeIds = new ArrayList<>();
                    for (AutocompletePrediction prediction : predictions) {
                        if (prediction.getPlaceTypes().
                                contains(com.google.android.libraries.places.api.model.Place.Type.RESTAURANT)) {
                            placeIds.add(prediction.getPlaceId());
                        }
                    }
                    listViewModel.searchPlaces(placeIds);
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                FindAutocompletePredictionsRequest.Builder requestBuilder = FindAutocompletePredictionsRequest
                        .builder()
                        .setQuery(newText);
                Task<FindAutocompletePredictionsResponse> task =
                        ((MainActivity) requireContext()).placesClient.findAutocompletePredictions(requestBuilder.build());

                task.addOnSuccessListener(response -> {
                    List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
                    List<String> placeIds = new ArrayList<>();
                    for (AutocompletePrediction prediction : predictions) {
                        if (prediction.getPlaceTypes().
                                contains(com.google.android.libraries.places.api.model.Place.Type.RESTAURANT)) {
                            placeIds.add(prediction.getPlaceId());
                        }
                    }

                    listViewModel.searchPlaces(placeIds);
                });
                return false;
            }
        });

        if (((MainActivity) requireContext()).searchView.getQuery().length() != 0) {
            ((MainActivity) requireContext()).searchView.setQuery(((((MainActivity) requireContext()).searchView.getQuery())), true);
        }

        ImageView closeButton = ((MainActivity) requireContext()).searchView.findViewById(R.id.search_close_btn);
        // Set on click listener
        closeButton.setOnClickListener(v -> {
            ((MainActivity) requireContext()).searchCardView.setVisibility(View.INVISIBLE);
            searchRestaurantsList.setVisibility(View.GONE);
            restaurantsList.setVisibility(View.VISIBLE);
        });
    }

    private void initListViewModel() {
        listViewModel = (ListViewModel) obtainViewModel(ListViewModel.class);

        listViewModel.findCurrentUser();
        listViewModel.currentUser().observe(getViewLifecycleOwner(), user -> {
            listViewModel.queryAllRestaurants(user.getUid());
            listViewModel.queryAllRestaurants().observe(getViewLifecycleOwner(), query -> {
                listViewModel.findPlaces();
                listViewModel.places().observe(getViewLifecycleOwner(), places -> {
                    mAdapter = new RestaurantAdapter(query, places);
                    restaurantsList.setAdapter(mAdapter);
                    if (isRunningInstrumentedTest()) {
                        restaurantsList.setItemAnimator(null);
                    }
                    mAdapter.startListening();
                    restaurantsList.setItemViewCacheSize(places.size());
                });
            });
            listViewModel.searchPlaces().observe(getViewLifecycleOwner(), places -> {
                SearchPlaceAdapter mAdapter = new SearchPlaceAdapter(places);
                searchRestaurantsList.setAdapter(mAdapter);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
}
