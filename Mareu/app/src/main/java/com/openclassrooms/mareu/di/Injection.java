package com.openclassrooms.mareu.di;

import com.openclassrooms.mareu.api.ApiService;
import com.openclassrooms.mareu.api.MeetingApiService;

/**
 * Injection class that allows to inject a singleton of ApiService
 */
public class Injection {

    private static ApiService service = new MeetingApiService();

    /**
     * Get an instance on @{@link ApiService}
     * @return
     */
    public static ApiService getMeetingApiService() {
        return service;
    }

    /**
     * Get always a new instance on @{@link MeetingApiService}. Useful for tests, so we ensure the context is clean.
     * @return
     */
    public static MeetingApiService getNewInstanceApiService() {
        return new MeetingApiService();
    }
}
