package com.openclassrooms.mareu.fragments.add_meeting;

import android.view.View;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.openclassrooms.mareu.R;
import com.openclassrooms.mareu.ui.activity.MeetingActivity;
import com.openclassrooms.mareu.utils.ClickViewAction;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.openclassrooms.mareu.utils.NestedScrollViewAction.nestedScrollTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddMeetingFragmentTest {

    MeetingActivity mActivity;

    @Rule
    public ActivityTestRule<MeetingActivity> mActivityRule = new ActivityTestRule(MeetingActivity.class);

    @Before
    public void setUp() {
        mActivity = mActivityRule.getActivity();
        assertThat(mActivity, notNullValue());
    }

    @Test
    public void verify_If_All_fields_To_Add_A_Meeting_Are_Completed() {
        // click on add floating action button if two panes screen
        clickOn_FloatingActionButton_If_Needed();

        // click on add meeting button
        clickOnAddMeetingMenuItem();

        onView(withText(R.string.no_subject_meeting_registered)).check(matches(isDisplayed()));

        onView(withId(R.id.text_input_edit_text_meeting_subject))
                .perform(typeText("Meeting " + 1), pressImeActionButton());

        // click on add meeting button
        clickOnAddMeetingMenuItem();

        onView(withText(R.string.no_place_meeting_selected)).check(matches(isDisplayed()));

        onView(withId(R.id.spinner_places)).perform(click());
        onView(withText("Mario")).perform(click());

        // click on add meeting button
        clickOnAddMeetingMenuItem();

        onView(withText(R.string.no_meeting_time_selected)).check(matches(isDisplayed()));

        onView(withId(R.id.meeting_date_picker)).perform(nestedScrollTo());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        onView(withId(R.id.meeting_date_picker)).perform(PickerActions.setDate(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));

        onView(withId(R.id.image_view_date_meeting)).perform(click());
        onView(withId(R.id.meeting_times_list))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(2, new ClickViewAction()));

        onView(withId(R.id.image_view_meeting_times)).perform(click());

        onView(withId(R.id.fragment_add_meeting))
                .perform(swipeDown());

        // click on add meeting button
        clickOnAddMeetingMenuItem();

        onView(withText(R.string.no_participants_selected)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.collaborators_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, new ClickViewAction()));

        onView(ViewMatchers.withId(R.id.collaborators_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(3, new ClickViewAction()));

        onView(withId(R.id.fragment_add_meeting))
                .perform(swipeDown());

        // click on add meeting button
        clickOnAddMeetingMenuItem();

        onView(withText(R.string.meeting_added)).check(matches(isDisplayed()));
    }

    /**
     * click on add meeting button
     */
    private void clickOnAddMeetingMenuItem() {
        onView(withId(R.id.add_meeting)).perform(click());
    }

    private void clickOn_FloatingActionButton_If_Needed() {
        // check if screen have two panes
        if(check_If_Is_TwoPanes_Screen()) {
            // then click on floating action button
            clickOn_AddMeeting_FoatingActionButton();
        }
    }

    private boolean check_If_Is_TwoPanes_Screen() {
        return mActivity.fabAddMeeting.getVisibility() == View.VISIBLE;
    }

    private void clickOn_AddMeeting_FoatingActionButton() {
        onView(ViewMatchers.withId(R.id.fab_add_meeting)).perform(ViewActions.click());
    }
}
