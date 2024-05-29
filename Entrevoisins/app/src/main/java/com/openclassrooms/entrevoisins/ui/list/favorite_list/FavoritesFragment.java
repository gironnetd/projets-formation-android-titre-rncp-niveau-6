package com.openclassrooms.entrevoisins.ui.list.favorite_list;

import com.openclassrooms.entrevoisins.R;
import com.openclassrooms.entrevoisins.events.RemoveNeighbourFromFavoriteEvent;
import com.openclassrooms.entrevoisins.ui.list.BaseFragment;
import com.openclassrooms.entrevoisins.ui.list.neighbour_list.MyNeighbourRecyclerViewAdapter;
import com.openclassrooms.entrevoisins.ui.list.neighbour_list.NeighbourFragment;

import org.greenrobot.eventbus.Subscribe;

public class FavoritesFragment extends BaseFragment {

    /**
     * Create and return a new instance
     * @return @{@link NeighbourFragment}
     */
    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        return fragment;
    }

    @Override
    public int getResourceLayout() {
        return R.layout.fragment_favorite_neighbour_list;
    }

    /**
     * Init the List of favorite neighbours
     */
    public void initList() {
        mNeighbours = mApiService.getFavoriteNeighbours();
        mRecyclerView.setAdapter(new MyNeighbourRecyclerViewAdapter(this.getClass().getName(), mNeighbours));
    }

    /**
     * Fired if the user clicks on a delete button
     * @param event
     */
    @Subscribe
    public void onRemoveNeighbourFromFavorite(RemoveNeighbourFromFavoriteEvent event) {
        mApiService.toggleToFavorite(event.neighbour);
        initList();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            initList();
        }
    }
}
