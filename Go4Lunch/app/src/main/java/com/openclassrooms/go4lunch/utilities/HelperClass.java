package com.openclassrooms.go4lunch.utilities;

import timber.log.Timber;

/**
 * Helper class to log application with Timber
 */
public class HelperClass {
    public static void logErrorMessage(String errorMessage) {
        Timber.d(errorMessage);
    }
}