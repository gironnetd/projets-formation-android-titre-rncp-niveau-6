package com.openclassrooms.mareu;

import com.openclassrooms.mareu.api.MeetingApiService;
import com.openclassrooms.mareu.di.Injection;
import com.openclassrooms.mareu.model.Meeting;
import com.openclassrooms.mareu.model.Place;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static com.openclassrooms.mareu.model.Collaborator.CollaboratorGenerator.generateCollaborators;
import static com.openclassrooms.mareu.model.Meeting.MeetingGenerator.generateMeetings;
import static com.openclassrooms.mareu.model.Place.PlaceGenerator.PLACE_NAMES;
import static com.openclassrooms.mareu.model.Place.PlaceGenerator.generatePlaces;
import static com.openclassrooms.mareu.model.Meeting.MeetingGenerator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Unit test on Meeting service
 */
@RunWith(JUnit4.class)

public class MeetingServiceTest {

    private MeetingApiService service;

    @Before
    public void setup() {
        service = Injection.getNewInstanceApiService();
        service.createMeetings(generateMeetings());
    }

    @Test
    public void get_Meetings_With_Success() {
        List<Meeting> meetings = service.getMeetings();
        List<Meeting> expectedMeetings = MeetingGenerator.MEETINGS_LIST;
        assertThat(meetings, IsIterableContainingInAnyOrder.containsInAnyOrder(expectedMeetings.toArray()));
    }

    @Test
    public void delete_Meeting_With_Success() {
        Meeting meetingToDelete = service.getMeetings().get(0);
        service.deleteMeeting(meetingToDelete);
        assertFalse(service.getMeetings().contains(meetingToDelete));
    }

    @Test
    public void delete_Meetings_With_Success() {
        List<Meeting> meetingsToDelete = service.getMeetings();
        service.deleteMeetings(meetingsToDelete.subList(0, 3));
        assertFalse(service.getMeetings().contains(meetingsToDelete));
    }

    @Test
    public void delete_AllMeetings_With_Success() {
        service.deleteAllMeetings();
        assertTrue(service.getMeetings().isEmpty());
    }

    @Test
    public void add_Meeting_With_Success() {
        int MeetingsNumber = service.getMeetings().size();
        Place place = generatePlaces().get(0);
        Meeting newMeeting = new Meeting(MeetingsNumber + 1,
                "Add a Meeting",
                place.getMeetingTimes().get(2) ,
                generateCollaborators(),
                place);
        service.createMeeting(newMeeting);
        assertTrue(service.getMeetings().size() == MeetingsNumber + 1);
    }

    @Test
    public void add_Meetings_With_Success() {
        service.deleteAllMeetings();
        List<Meeting> meetingsToAdd = generateMeetings();
        service.createMeetings(meetingsToAdd);
        assertTrue(service.getMeetings().containsAll(meetingsToAdd));
    }

    @Test
    public void filter_Meetings_by_Date_With_Success() {

        // choose day of month between 1, 2 or 4
        final int DAY_OF_MONTH = 1;

        List<Meeting> allMeetings = service.getMeetings();
        List<Meeting> meetingsByDate = new ArrayList<>();

        GregorianCalendar dateExpected = new GregorianCalendar();

        for(int index = 0; index < allMeetings.size(); index++) {
            if(allMeetings.get(index).getSlot().getStartTime().get(Calendar.DAY_OF_MONTH) == DAY_OF_MONTH) {
               meetingsByDate.add(allMeetings.get(index));
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        dateExpected.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), DAY_OF_MONTH);

        assertEquals(meetingsByDate, service.filterMeetingsByDate(dateExpected));
    }

    @Test
    public void filter_Meetings_by_Place_With_Success() {

        final String PLACE_NAME = PLACE_NAMES.get(3);

        List<Meeting> allMeetings = service.getMeetings();
        List<Meeting> meetingsByPlace = new ArrayList<>();

        for(int index = 0; index < allMeetings.size(); index++) {
            if(allMeetings.get(index).getPlace().getName().equals(PLACE_NAME)) {
                meetingsByPlace.add(allMeetings.get(index));
            }
        }

        assertEquals(meetingsByPlace, service.filterMeetingsByPlace(PLACE_NAME));
    }
}
