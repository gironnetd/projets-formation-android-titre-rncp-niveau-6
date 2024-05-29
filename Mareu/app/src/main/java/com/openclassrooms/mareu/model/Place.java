package com.openclassrooms.mareu.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.openclassrooms.mareu.model.MeetingTime.MeetingTimeGenerator.generateMeetingTimes;

/**
 * Place class representing the place where meetings take place
 */
public class Place {

    /**
     * identifier of the place
     */
    private long id;

    /**
     * name of the place
     */
    private String name;

    /**
     * list of the meeting times of the place
     */
    private List<MeetingTime> meetingTimes;

    public String getName() {
        return name;
    }

    public List<MeetingTime> getMeetingTimes() {
        return meetingTimes;
    }

    public Place(long id, String name, List<MeetingTime> meetingTimes) {
        this.id = id;
        this.name = name;
        this.meetingTimes = meetingTimes;
    }

    /**
     * Class to generate places where the meetings take place
     */
    public static class PlaceGenerator {

        public static List<String> PLACE_NAMES = Arrays.asList(
                "Peach", "Mario", "Luigi", "Toad", "Yoshi", "Daisy"
        );

        public static List<Place> generatePlaces() {
            List<Place> places = new ArrayList<>();

            for(int index = 0; index < PLACE_NAMES.size(); index++) {
                places.add(new Place(index, PLACE_NAMES.get(index), generateMeetingTimes()));
            }
            return places;
        }

        static List<Place> PLACES_LIST = Arrays.asList(
                new Place(1L, "Peach", generateMeetingTimes()),
                new Place(2L, "Mario", generateMeetingTimes()),
                new Place(3L, "Luigi", generateMeetingTimes()),
                new Place(4L, "Toad", generateMeetingTimes()),
                new Place(5L, "Yoshi", generateMeetingTimes()),
                new Place(6L, "Daisy", generateMeetingTimes())
        );
    }
}
