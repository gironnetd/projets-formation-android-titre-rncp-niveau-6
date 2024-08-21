package com.openclassrooms.realestatemanager.util

import android.os.IBinder
import android.view.WindowManager
import androidx.test.espresso.Root
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class ToastMatcher: TypeSafeMatcher<Root>() {
    override fun describeTo(description: Description) {
        description.appendText("is toast");
    }

    override fun matchesSafely(root: Root): Boolean {
        val type: Int = root.windowLayoutParams.get().type
        if (type == WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY) {
            val windowToken: IBinder = root.decorView.windowToken
            val appToken: IBinder = root.decorView.applicationWindowToken
            if (windowToken === appToken) {
                return true
                //means this window isn't contained by any other windows.
            }
        }
        return false
    }
}
