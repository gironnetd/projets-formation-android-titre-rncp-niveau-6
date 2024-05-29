package com.openclassrooms.entrevoisins.list.favorite_list;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.openclassrooms.entrevoisins.R;
import com.openclassrooms.entrevoisins.di.DI;
import com.openclassrooms.entrevoisins.model.Neighbour;
import com.openclassrooms.entrevoisins.service.NeighbourApiService;
import com.openclassrooms.entrevoisins.ui.list.neighbour_list.ListNeighbourActivity;
import com.openclassrooms.entrevoisins.utils.ClickViewAction;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;
import static com.openclassrooms.entrevoisins.utils.RecyclerViewItemCountAssertion.withItemCount;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Test class for list of favorite neighbours
 */
@RunWith(AndroidJUnit4.class)
public class FavoriteNeighboursListTest {

    private ListNeighbourActivity mActivity;

    @Rule
    public ActivityTestRule<ListNeighbourActivity> mActivityRule =
            new ActivityTestRule(ListNeighbourActivity.class);

    @Before
    public void setUp() {
        mActivity = mActivityRule.getActivity();
        assertThat(mActivity, notNullValue());
    }

    @Test
    public void myNeighboursList_favoritesTab_ShouldDisplayFavoritesOnly() {
        // Given : Add 3 neighbours to favorites
        onView(withId(R.id.list_neighbours))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, new ClickViewAction()));
        onView(withId(R.id.fab_favorite)).perform(click());
        onView(withId(R.id.fab_back)).perform(click());
        onView(withId(R.id.list_neighbours))
                .perform(RecyclerViewActions.actionOnItemAtPosition(3, new ClickViewAction()));
        onView(withId(R.id.fab_favorite)).perform(click());
        onView(withId(R.id.fab_back)).perform(click());
        onView(withId(R.id.list_neighbours))
                .perform(RecyclerViewActions.actionOnItemAtPosition(5, new ClickViewAction()));
        onView(withId(R.id.fab_favorite)).perform(click());
        onView(withId(R.id.fab_back)).perform(click());

        // When : Select the favorites tab
        Espresso.onView(withText(R.string.tab_favorites_title)).perform(click());

        // Then :  the favorites list contains 3 neighbours
        onView(ViewMatchers.withId(R.id.list_favorite_neighbours)).check(withItemCount(3));
    }
}
