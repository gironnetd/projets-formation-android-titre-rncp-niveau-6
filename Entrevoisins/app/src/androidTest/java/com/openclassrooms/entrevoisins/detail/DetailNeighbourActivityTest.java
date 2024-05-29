package com.openclassrooms.entrevoisins.detail;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.openclassrooms.entrevoisins.R;
import com.openclassrooms.entrevoisins.ui.detail.DetailNeighbourActivity;
import com.openclassrooms.entrevoisins.ui.list.neighbour_list.ListNeighbourActivity;
import com.openclassrooms.entrevoisins.utils.ClickViewAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Test class for detail of neighbour
 */
@RunWith(AndroidJUnit4.class)
public class DetailNeighbourActivityTest {

    private ListNeighbourActivity mActivity;

    @Rule
    public IntentsTestRule<ListNeighbourActivity> mActivityRule =
            new IntentsTestRule(ListNeighbourActivity.class);

    @Before
    public void setUp()  {
        mActivity = mActivityRule.getActivity();
        assertThat(mActivity, notNullValue());
    }

    /**
     * When we click on an item of list neighbour
     * the detail neighbour activity is displayed
     */
    @Test
    public void myNeighboursList_clickAction_shouldStartDetailActivity() {

        // When : a neighbour in the list is clicked
        onView(ViewMatchers.withId(R.id.list_neighbours))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, new ClickViewAction()));

        // Then : the Detail activity is Launched
        intended(hasComponent(DetailNeighbourActivity.class.getName()));
    }

    /**
     * When a detail of a neighbour is displayed the name
     * of the corresponding neighbour is displayed
     */
    @Test
    public void detailActivity_nameTextView_ShouldDisplaySomething() {

        // Given : a neighbour on the list is clicked
        onView(ViewMatchers.withId(R.id.list_neighbours))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, new ClickViewAction()));

        // When : the Detail activity is launched
        intended(hasComponent(DetailNeighbourActivity.class.getName()));

        // Then : the name of this neighbour is displayed in the neighbour name field
        onView(ViewMatchers.withId(R.id.tv_detail_neighbour_name)).check(matches(withText("Jack")));
    }
}
