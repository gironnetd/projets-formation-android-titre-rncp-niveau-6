package com.openclassrooms.mareu.activity;

import android.view.View;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.openclassrooms.mareu.R;
import com.openclassrooms.mareu.ui.activity.MeetingActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MeetingActivityTest {

    MeetingActivity mActivity;

    @Rule
    public ActivityTestRule<MeetingActivity> mActivityRule = new ActivityTestRule(MeetingActivity.class);

    @Before
    public void setUp() {
        mActivity = mActivityRule.getActivity();
        assertThat(mActivity, notNullValue());
    }

    @Test
    public void display_List_Meeting_Fragment_In_Launching() {
        onView(ViewMatchers.withId(R.id.fragment_list_meeting)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void display_Add_Meeting_Fragment_On_Click_Floating_Action_Button_When_Is_Visible() {
        if(mActivity.fabAddMeeting.getVisibility() == View.VISIBLE) {
            onView(ViewMatchers.withId(R.id.fab_add_meeting)).perform(ViewActions.click());
        }
         onView(ViewMatchers.withId(R.id.fragment_add_meeting)).
                check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
