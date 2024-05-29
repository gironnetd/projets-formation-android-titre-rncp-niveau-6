package com.openclassrooms.go4lunch.view.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.openclassrooms.go4lunch.BuildConfig;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.data.local.prefs.AppPreferences;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.view.BaseActivity;
import com.openclassrooms.go4lunch.view.detail.DetailActivity;
import com.openclassrooms.go4lunch.view.settings.SettingsActivity;
import com.openclassrooms.go4lunch.view.splash.SplashActivity;
import com.openclassrooms.go4lunch.viewmodel.main.MainViewModel;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.openclassrooms.go4lunch.utilities.Constants.RESTAURANT_DETAIL_ID;

/**
 * Main Activity class implementation
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.nav_view)
    BottomNavigationView navView;

    NavController navController;
    AppBarConfiguration appBarConfiguration;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    ImageView currentUserPhoto;
    TextView currentUserDisplayName;
    TextView currentUserEmail;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.cv_search_view)
    public CardView searchCardView;

    @BindView(R.id.search_view)
    public SearchView searchView;

    @BindView(R.id.search_view_results)
    public ListView searchResultList;

    @BindView(R.id.container)
    ConstraintLayout container;

    MainViewModel mainViewModel;

    private User currentUser;
    public PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map_view, R.id.navigation_list_view, R.id.navigation_workmates)
                .setDrawerLayout(drawerLayout)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        setSupportActionBar(toolbar);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        TooltipCompat.setTooltipText(navView.getRootView(), null);
        setNavigationViewListener();

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),
                    BuildConfig.google_maps_key);
        }
        placesClient = Places.createClient(this);
    }

    private void initMainViewModel() {
        mainViewModel = (MainViewModel) obtainViewModel(MainViewModel.class);

        mainViewModel.findCurrentUser();
        mainViewModel.currentUser().observe(this, user -> {
            currentUser = user;
            initNavigationView();
        });
    }

    private void initNavigationView() {
        View headerView = navigationView.getHeaderView(0);
        currentUserPhoto = headerView.findViewById(R.id.iv_current_user_photo);
        if(Objects.requireNonNull(currentUser).getPhotoUrl() != null) {
            Glide.with(this)
                    .load(Objects.requireNonNull(currentUser).getPhotoUrl())
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(currentUserPhoto);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_group_white_background_24dp)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(currentUserPhoto);
        }

        currentUserDisplayName = headerView.findViewById(R.id.tv_current_user_display_name);
        currentUserDisplayName.setText(currentUser.getDisplayName());

        currentUserEmail = headerView.findViewById(R.id.tv_current_user_email);
        currentUserEmail.setText(currentUser.getEmail());
    }

    private void setNavigationViewListener() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMainViewModel();

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        EditText searchEditText = searchView.findViewById(R.id.search_src_text);
        // Set search text color
        searchEditText.setTextColor(getResources().getColor(android.R.color.black));
        // Set search hints color
        searchEditText.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);

        searchItem.setOnMenuItemClickListener(menuItem -> {
            if(searchCardView.getVisibility() == View.INVISIBLE) {
                searchCardView.setVisibility(View.VISIBLE);
            }
            return true;
        });
        ViewCompat.setTranslationZ(searchCardView, 8);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(searchCardView.getVisibility() == View.VISIBLE) {
            searchCardView.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.your_lunch:
                if(currentUser.getMiddayRestaurant() != null) {
                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.putExtra(RESTAURANT_DETAIL_ID, currentUser.getMiddayRestaurant().getPlaceId());
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Toast.makeText(this, R.string.midday_restaurant_not_chosen, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.log_out:
                mainViewModel.isCurrentUserLogOut().observe(this, aBoolean -> {
                    if(aBoolean) {
                        AppPreferences.preferences(getApplicationContext()).setPrefKeyCurrentUserUuid(null);
                        Intent splashActivityIntent = new Intent(this, SplashActivity.class);
                        startActivity(splashActivityIntent);
                        finish();
                    }
                });
                mainViewModel.logoutCurrentUser(currentUser);
                return true;
        }
        return true;
    }
}
