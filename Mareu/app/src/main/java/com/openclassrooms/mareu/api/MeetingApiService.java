package com.openclassrooms.mareu.api;

import com.openclassrooms.mareu.model.Meeting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Api that manage the meetings
 */
public class MeetingApiService implements ApiService {

    /**
     * list of meetings
     */
    List<Meeting> meetings = new ArrayList<>(); // Meeting.MeetingGenerator.generateMeetings(); //

    /**
     * @return all meetings
     */
    @Override
    public List<Meeting> getMeetings() {
        return meetings;
    }

    /**
     * @param meeting to create
     * @return true if the meeting is added
     */
    @Override
    public boolean createMeeting(Meeting meeting) {
        return meetings.add(meeting);
    }

    /**
     * @param meetings to create
     * @return true if the meetings have been added
     */
    @Override
    public boolean createMeetings(List<Meeting> meetings) {
        return this.meetings.addAll(meetings);
    }

    /**
     * @param meeting to delete
     * @return true if the meeting have been deleted
     */
    @Override
    public boolean deleteMeeting(Meeting meeting) {
        return meetings.remove(meeting);
    }

    /**
     * @param meetings to delete
     * @return true if the meetings have been deleted
     */
    @Override
    public boolean deleteMeetings(List<Meeting> meetings) {
        return this.meetings.removeAll(meetings);
    }

    /**
     * @return true if all the meetings have been deleted
     */
    @Override
    public boolean deleteAllMeetings() {
        meetings.clear();
        return true;
    }

    /**
     * filter meetings by their place
     *
     * @param placeName
     */
    @Override
    public List<Meeting> filterMeetingsByPlace(String placeName) {
        List<Meeting> meetingsFilteredByPlace = new ArrayList<>();

        for(int index = 0; index < meetings.size(); index++) {
            if(meetings.get(index).getPlace().getName().equals(placeName)) {
                meetingsFilteredByPlace.add(meetings.get(index));
            }
        }
        return meetingsFilteredByPlace;
    }

    /**
     * filter meetings by their start time
     *
     * @param calendar
     */
    @Override
    public List<Meeting> filterMeetingsByDate(GregorianCalendar calendar) {
        List<Meeting> meetingsFilteredByDate = new ArrayList<>();

        for(int index = 0; index < meetings.size(); index++) {
            if(meetings.get(index).getSlot().getStartTime().get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) &&
                    meetings.get(index).getSlot().getStartTime().get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                    meetings.get(index).getSlot().getStartTime().get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                meetingsFilteredByDate.add(meetings.get(index));
            }
        }
        return meetingsFilteredByDate;
    }
}
