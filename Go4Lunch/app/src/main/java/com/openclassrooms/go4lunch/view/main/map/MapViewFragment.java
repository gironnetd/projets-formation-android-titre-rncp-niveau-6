package com.openclassrooms.go4lunch.view.main.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.data.local.prefs.AppPreferences;
import com.openclassrooms.go4lunch.data.model.api.places.Location;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.utilities.Constants;
import com.openclassrooms.go4lunch.utilities.EspressoIdlingResource;
import com.openclassrooms.go4lunch.view.detail.DetailActivity;
import com.openclassrooms.go4lunch.view.main.BaseFragment;
import com.openclassrooms.go4lunch.view.main.MainActivity;
import com.openclassrooms.go4lunch.view.main.map.marker.FloatingMarkerTitlesOverlay;
import com.openclassrooms.go4lunch.view.main.map.marker.MarkerInfo;
import com.openclassrooms.go4lunch.viewmodel.main.map.MapViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.openclassrooms.go4lunch.utilities.HelperClass.logErrorMessage;

/**
 * Fragment to display map with restaurants around the user
 */
public class MapViewFragment extends BaseFragment implements OnMapReadyCallback,
        EventListener<QuerySnapshot>, SearchListAdapter.SearchListener {

    private GoogleMap mMap;

    private Query mQuery;
    private ListenerRegistration mRegistration;

    private final ArrayList<DocumentSnapshot> mSnapshots = new ArrayList<>();

    @BindView(R.id.map_floating_markers_overlay)
    FloatingMarkerTitlesOverlay floatingMarkersOverlay;

    private int lastMarkerDisplayedPosition = -1;

    private final LatLng mDefaultLocation = new LatLng(48.8226, 2.1232);
    private static final int DEFAULT_ZOOM = 15;

    private Location mLastKnownLocation;
    private ArrayList<Place> places;
    private final ArrayList<Place> restaurants = new ArrayList<>();

    @BindView(R.id.floating_action_button_my_location)
    FloatingActionButton fabMyLocation;

    private MapViewModel mapViewModel;

    private SearchListAdapter adapter;

    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    List<AutocompletePrediction> predictions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map_view, container, false);
        ButterKnife.bind(this, root);

        initMapViewModel();

        fabMyLocation.setOnClickListener(view -> {
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM);
            mMap.animateCamera(location);
        });

        adapter = new SearchListAdapter();
        adapter.setCallBack(this);
        initAutocompletePredictions();
        return root;
    }

    private void initAutocompletePredictions() {
        ((MainActivity) requireContext()).searchView.setQueryHint(getResources().getString(R.string.search_restaurant_title));

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
                    predictions = response.getAutocompletePredictions();

                    List<SpannableString> primaryText = new ArrayList<>();
                    for(AutocompletePrediction prediction : predictions) {
                        if(prediction.getPlaceTypes().
                                contains(com.google.android.libraries.places.api.model.Place.Type.RESTAURANT)) {
                            primaryText.add(prediction.getFullText(STYLE_BOLD));
                        }
                    }

                    ((MainActivity) requireContext()).searchResultList.setVisibility(View.VISIBLE);
                    adapter.setmData(primaryText);
                    adapter.setmStringFilterList(primaryText);
                    ((MainActivity) requireContext()).searchResultList.setAdapter(adapter);
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
                    predictions = response.getAutocompletePredictions();

                    List<SpannableString> primaryText = new ArrayList<>();
                    for(AutocompletePrediction prediction : predictions) {
                        if(prediction.getPlaceTypes().
                                contains(com.google.android.libraries.places.api.model.Place.Type.RESTAURANT)) {
                            primaryText.add(prediction.getFullText(STYLE_BOLD));
                        }
                    }

                    ((MainActivity) requireContext()).searchResultList.setVisibility(View.VISIBLE);
                    adapter.setmData(primaryText);
                    adapter.setmStringFilterList(primaryText);
                    ((MainActivity) requireContext()).searchResultList.setAdapter(adapter);
                });

                return false;
            }
        });

        if(((MainActivity) requireContext()).searchView.getQuery().length() != 0) {
            ((MainActivity) requireContext()).searchResultList.setVisibility(View.VISIBLE);
            ((MainActivity) requireContext()).searchView.setQuery(((((MainActivity) requireContext()).searchView.getQuery())), true);
        }

        ImageView closeButton = ((MainActivity) requireContext()).searchView.findViewById(R.id.search_close_btn);
        // Set on click listener
        closeButton.setOnClickListener(v -> ((MainActivity) requireContext()).searchCardView.setVisibility(View.INVISIBLE));
    }

    private void initMapViewModel() {

        mapViewModel = (MapViewModel) obtainViewModel(MapViewModel.class);
        mapViewModel.findCurrentUser();
        mapViewModel.currentUser().observe(getViewLifecycleOwner(), user -> {
            mapViewModel.queryAllRestaurants(user.getUid());
            mapViewModel.queryAllRestaurants().observe(getViewLifecycleOwner(), query -> {
                mQuery = query;
                startListening();

                // Build the map.
                SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                        .findFragmentById(R.id.map);
                Objects.requireNonNull(mapFragment).getMapAsync(this);

                mapViewModel.findPlaces();
                mapViewModel.places().observe(getViewLifecycleOwner(), places -> {
                    this.places = (ArrayList<Place>) places;
                });
            });
        });
    }

    private void initMarker(int index, DocumentSnapshot snapshot) {
        Place restaurant = snapshot.toObject(Place.class);
        restaurants.add(restaurant);

        LatLng latLng = new LatLng(Objects.requireNonNull(restaurant).getLatitude(),
                restaurant.getLongitude());

        // Add a marker for the selected place, with an info window
        // showing information about that place.
        MarkerOptions markerOptions = null;
        if(restaurant.getWorkmates() != null && !restaurant.getWorkmates().isEmpty()) {
            markerOptions = new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(requireContext(),
                            R.drawable.ic_marker_restaurant_selected))
                    .position(latLng);
        } else {
            markerOptions = new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(requireContext(),
                            R.drawable.ic_marker_restaurant_not_selected))
                    .position(latLng);
        }

        if(mMap != null) {
            Marker marker =  mMap.addMarker(markerOptions);
            marker.setTag(index);
        }
    }

    private void initMarker(Place place) {
        LatLng latLng = new LatLng(Objects.requireNonNull(place).getLatitude(),
                Objects.requireNonNull(place).getLongitude());

        // Add a marker for the selected place, with an info window
        // showing information about that place.
        MarkerOptions markerOptions = null;

        markerOptions = new MarkerOptions()
                .icon(bitmapDescriptorFromVector(requireContext(),
                        R.drawable.ic_marker_restaurant_not_selected))
                .position(latLng);

        if(mMap != null) {
            Marker marker =  mMap.addMarker(markerOptions);
            marker.setTag(place);
        }
    }

    private void initMarker(DocumentSnapshot snapshot) {
        Place restaurant = snapshot.toObject(Place.class);

        LatLng latLng = new LatLng(Objects.requireNonNull(restaurant).getLatitude(),
                restaurant.getLongitude());

        // Add a marker for the selected place, with an info window
        // showing information about that place.
        MarkerOptions markerOptions = null;
        if(restaurant.getWorkmates() != null && !restaurant.getWorkmates().isEmpty()) {
            markerOptions = new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(requireContext(),
                            R.drawable.ic_marker_restaurant_selected))
                    .position(latLng);
        } else {
            markerOptions = new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(requireContext(),
                            R.drawable.ic_marker_restaurant_not_selected))
                    .position(latLng);
        }

        if(mMap != null) {
            Marker marker =  mMap.addMarker(markerOptions);
            marker.setTag(restaurants.indexOf(restaurant));
        }

        if(!floatingMarkersOverlay.getMarkerInfoList().isEmpty() &&
                floatingMarkersOverlay.getMarkerInfoList().get(0).isVisible()) {
            if(restaurant.getWorkmates() != null && !restaurant.getWorkmates().isEmpty()) {
                floatingMarkersOverlay.getMarkerInfoList().get(0)
                        .setColor(getResources().getColor(R.color.colorAccent));
            } else {
                floatingMarkersOverlay.getMarkerInfoList().get(0)
                        .setColor(getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        floatingMarkersOverlay.clearMarkers();
        stopListening();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        EspressoIdlingResource.increment();
        mMap = googleMap;

        if (!isRunningInstrumentedTest()) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    requireActivity(), R.raw.styled_map));
        } else {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    requireActivity(), R.raw.styled_map_for_testing));
        }

        floatingMarkersOverlay.setSource(mMap);
        floatingMarkersOverlay.setMaxFloatingTitlesCount(60);
        floatingMarkersOverlay.setSetMaxNewMarkersCheckPerFrame(60);

        if (!AppPreferences.preferences(requireContext()).getPrefKeyDeviceLocationLatitudeLongitude().isEmpty()) {
            mLastKnownLocation = new Location();
            mLastKnownLocation.setLatitude(AppPreferences.preferences(requireContext()).getPrefKeyDeviceLocationLatitude());
            mLastKnownLocation.setLongitude(AppPreferences.preferences(requireContext()).getPrefKeyDeviceLocationLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        mMap.setOnMarkerClickListener(marker -> {
            if(marker.getTag() instanceof Place) {
                Place place = (Place)  marker.getTag();
                LatLng latLng = new LatLng(Objects.requireNonNull(place).getLatitude(),
                        Objects.requireNonNull(place).getLongitude());
                MarkerInfo markerInfo = new MarkerInfo(latLng,
                        place.getName(),
                        getResources().getColor(R.color.colorPrimary));
                markerInfo.setVisible(true);

                floatingMarkersOverlay.addMarker(1, markerInfo);
            }

            if(marker.getTag() instanceof Integer) {
                int index = (int) marker.getTag() ;
                if(lastMarkerDisplayedPosition == -1 || lastMarkerDisplayedPosition != index) {
                    LatLng latLng = new LatLng(Objects.requireNonNull(places.get(index)).getLatitude(),
                            places.get(index).getLongitude());

                    MarkerInfo markerInfo = null;
                    if (restaurants.get(index).getWorkmates() != null &&
                            !restaurants.get(index).getWorkmates().isEmpty()) {
                        markerInfo = new MarkerInfo(latLng,
                                places.get(index).getName(),
                                getResources().getColor(R.color.colorAccent));
                        markerInfo.setVisible(true);
                    } else {
                        markerInfo = new MarkerInfo(latLng,
                                places.get(index).getName(),
                                getResources().getColor(R.color.colorPrimary));
                        markerInfo.setVisible(true);
                    }

                    floatingMarkersOverlay.addMarker(index, markerInfo);

                    if(lastMarkerDisplayedPosition != -1) {
                        floatingMarkersOverlay.removeMarker(lastMarkerDisplayedPosition);
                    }
                    lastMarkerDisplayedPosition = index;
                } else {
                    Place restaurant = places.get((int) marker.getTag());
                    Intent intent = new Intent(requireActivity(), DetailActivity.class);
                    intent.putExtra(Constants.RESTAURANT_DETAIL_ID, restaurant.getPlaceId());
                    startActivity(intent);
                }
            }
            return true;
        });
        if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
            EspressoIdlingResource.decrement(); // Set app as idle.
        }
    }

    private static BitmapDescriptor bitmapDescriptorFromVector(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(Objects.requireNonNull(drawable))).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(Objects.requireNonNull(drawable).getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            fabMyLocation.setVisibility(View.VISIBLE);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e)  {
            logErrorMessage(e.getMessage());
        }
    }

    public void startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(this);
        }
    }

    public void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }

        mSnapshots.clear();
    }

    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        if (e != null) {
            Timber.w(e, "onEvent:error");
            onError(e);
            return;
        }

        // Dispatch the event
        Timber.d("onEvent:numChanges:" + documentSnapshots.getDocumentChanges().size());
        for (int index = 0; index < documentSnapshots.getDocumentChanges().size(); index++) {
            switch (documentSnapshots.getDocumentChanges().get(index).getType()) {
                case ADDED:
                    onDocumentAdded(index, documentSnapshots.getDocumentChanges().get(index));
                    break;
                case MODIFIED:
                    onDocumentModified(documentSnapshots.getDocumentChanges().get(index));
                    break;
            }
        }
    }

    protected void onError(FirebaseFirestoreException e) {
        Timber.w(e, "onError");
    }

    protected void onDocumentAdded(int index, DocumentChange change) {

        mSnapshots.add(change.getNewIndex(), change.getDocument());
        EspressoIdlingResource.increment();
        initMarker(index, change.getDocument());
        EspressoIdlingResource.decrement();
    }

    protected void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            mSnapshots.set(change.getOldIndex(), change.getDocument());
        } else {
            // Item changed and changed position
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument());
        }
        EspressoIdlingResource.increment();
        initMarker(change.getDocument());
        EspressoIdlingResource.decrement();
    }

    @Override
    public void onSearchItemClick(int position) {
        mapViewModel.searchPlace(predictions.get(position).getPlaceId());
        mapViewModel.place().observe(getViewLifecycleOwner(), place -> {
            initMarker(place);
            ((MainActivity) requireContext()).searchResultList.setVisibility(View.GONE);
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(place.getLatitude(),
                            place.getLongitude()), DEFAULT_ZOOM);
            mMap.animateCamera(location);
        });
    }
}