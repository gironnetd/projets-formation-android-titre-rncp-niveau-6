package com.openclassrooms.mareu.utils;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import com.openclassrooms.mareu.R;

import org.hamcrest.Matcher;

public class ClickViewAction implements ViewAction {
    @Override
    public Matcher<View> getConstraints() {
        return null;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public void perform(UiController uiController, View view) {
        // Maybe check for null
        view.performClick();
    }
}
