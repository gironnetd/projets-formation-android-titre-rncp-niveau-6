package com.openclassrooms.mareu.api;

import com.openclassrooms.mareu.model.Meeting;

import java.util.GregorianCalendar;
import java.util.List;

/**
 * Api interface that perform features on list of meetings
 */
public interface ApiService {

    /**
     *
     * @return all meetings
     */
    List<Meeting> getMeetings();

    /**
     *
     * @param meeting to create
     * @return true if the meeting is added
     */
    boolean createMeeting(Meeting meeting);

    /**
     *
     * @param meetings to create
     * @return true if the meetings have been added
     */
    boolean createMeetings(List<Meeting> meetings);

    /**
     *
     * @param meeting to delete
     * @return true if the meeting have been deleted
     */
    boolean deleteMeeting(Meeting meeting);

    /**
     *
     * @param meetings to delete
     * @return true if the meetings have been deleted
     */
    boolean deleteMeetings(List<Meeting> meetings);

    /**
     *
     * @return true if all the meetings have been deleted
     */
    boolean deleteAllMeetings();

    /**
     * filter meetings by their place
     */
    List<Meeting> filterMeetingsByPlace(String placeName);

    /**
     * filter meetings by their start time
     */
    List<Meeting> filterMeetingsByDate(GregorianCalendar calendar);
}
