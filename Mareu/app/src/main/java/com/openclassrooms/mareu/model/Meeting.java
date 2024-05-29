package com.openclassrooms.mareu.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.openclassrooms.mareu.model.Collaborator.CollaboratorGenerator.generateCollaborators;
import static com.openclassrooms.mareu.model.MeetingTime.MeetingTimeGenerator.MEETING_TIMES;
import static com.openclassrooms.mareu.model.MeetingTime.MeetingTimeGenerator.generateMeetingTimes;
import static com.openclassrooms.mareu.model.Place.PlaceGenerator.PLACES_LIST;
import static com.openclassrooms.mareu.model.Collaborator.CollaboratorGenerator.collaboratorsList;
import static com.openclassrooms.mareu.model.Place.PlaceGenerator.generatePlaces;

/**
 * Meeting class representing the Meeting object
 */
public class Meeting {

    /**
     * identifier of meeting
     */
    private long id;

    /**
     * subject of meeting
     */
    private String subject;

    /**
     * slot of the meeting
     */
    private MeetingTime slot;

    /**
     * list of participants of the meeting
     */
    private List<Collaborator> participants;

    /**
     * place of the meeting
     */
    private Place place;

    public String getSubject() {
        return subject;
    }

    public MeetingTime getSlot() {
        return slot;
    }

    public List<Collaborator> getParticipants() {
        return participants;
    }

    public Place getPlace() {
        return place;
    }

    public Meeting(long id) {
        this.id = id;
    }

    public Meeting(long id, String subject, MeetingTime slot, List<Collaborator> collaborators, Place place) {
        this.id = id;
        this.subject = subject;
        this.slot = slot;
        this.participants = collaborators;
        this.place = place;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeting meeting = (Meeting) o;
        return Objects.equals(id, meeting.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Class to generate random meetings
     */
    public static class MeetingGenerator {

        public static List<Meeting> generateMeetings() {
            List<Meeting> meetings = new ArrayList<>();
            int meetingsCount = 9;
            for(int index = 0; index < meetingsCount; index++) {
                Meeting meetingToAdd;

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                if(index % 3 == 0) {
                    meetingToAdd = new Meeting(index + 1, "Meeting " + (index + 1), generateMeetingTimes().get(10),
                            new ArrayList<>(generateCollaborators()),
                            generatePlaces().get(4));
                } else if(index % 3 == 1) {
                    meetingToAdd = new Meeting(index + 1, "Meeting " + (index + 1), generateMeetingTimes().get(1),
                            new ArrayList<>(generateCollaborators()),
                            generatePlaces().get(3));
                } else {
                    meetingToAdd = new Meeting(index + 1, "Meeting " + (index + 1), generateMeetingTimes().get(11),
                            new ArrayList<>(generateCollaborators()),
                            generatePlaces().get(0));
                }
                meetings.add(meetingToAdd);

            }
            return meetings;
        }

        public static List<Meeting> MEETINGS_LIST = Arrays.asList(
                new Meeting(1L, "A", generateMeetingTimes().get(11),
                        new ArrayList<>(generateCollaborators()),
                        generatePlaces().get(0)),
                new Meeting(2L, "B", generateMeetingTimes().get(1),
                        new ArrayList<>(generateCollaborators()),
                        generatePlaces().get(3)),
                new Meeting(3L, "C", generateMeetingTimes().get(10),
                        new ArrayList<>(generateCollaborators()),
                        generatePlaces().get(4)),
                new Meeting(4L, "AA", generateMeetingTimes().get(11),
                        new ArrayList<>(generateCollaborators()),
                        generatePlaces().get(0)),
                new Meeting(5L, "BB", generateMeetingTimes().get(1),
                        new ArrayList<>(generateCollaborators()),
                        generatePlaces().get(3)),
                new Meeting(6L, "CC", generateMeetingTimes().get(10),
                        new ArrayList<>(generateCollaborators()),
                        generatePlaces().get(4)),
                new Meeting(7L, "AAA", generateMeetingTimes().get(11),
                        new ArrayList<>(generateCollaborators()),
                        generatePlaces().get(0)),
                new Meeting(8L, "BBB", generateMeetingTimes().get(1),
                        new ArrayList<>(generateCollaborators()),
                        generatePlaces().get(3)),
                new Meeting(9L, "CCC", generateMeetingTimes().get(10),
                        new ArrayList<>(generateCollaborators()),
                        generatePlaces().get(4))
        );
    }
}
