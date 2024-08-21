package com.openclassrooms.realestatemanager.util


/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 - Nathan Barraille
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.hamcrest.Matcher


/**
 * An Espresso ViewAction that changes the orientation of the screen
 */
class OrientationChangeAction private constructor(private val orientation: Int) : ViewAction {


    override fun getConstraints(): Matcher<View> {
        return isRoot()
    }

    override fun getDescription(): String {
        return "change orientation to $orientation"
    }

    override fun perform(uiController: UiController, view: View) {
        uiController.loopMainThreadUntilIdle()
        activity.requestedOrientation = orientation
        val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
        if (resumedActivities.isEmpty()) {
            throw RuntimeException("Could not change orientation")
        }
    }

    companion object {
        lateinit var activity : Activity
        fun orientationLandscape(activity: Activity): ViewAction {
            this.activity = activity
            return OrientationChangeAction(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        }

        fun orientationPortrait(activity: Activity): ViewAction {
            this.activity = activity
            return OrientationChangeAction(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
    }
}