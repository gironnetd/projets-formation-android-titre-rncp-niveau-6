package com.openclassrooms.mareu.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * MeetingTime class representing the MeetingTime of Places
 */
public class MeetingTime {

    /**
     * identifier of the meeting time
     */
    private long id ;

    /**
     * start time of the meeting
     */
    private GregorianCalendar startTime;

    /**
     * end time of the meeting
     */
    private GregorianCalendar endTime;

    /**
     * check if the meeting time in the place is reserved or not
     */
    private boolean reserved ;

    public GregorianCalendar getStartTime() {
        return startTime;
    }

    public GregorianCalendar getEndTime() {
        return endTime;
    }

    public boolean getReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public MeetingTime(long id, GregorianCalendar startTime, GregorianCalendar endTime, boolean reserved) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reserved = reserved;
    }

    /**
     * Class to generate  meetingTimes for each places
     */
    static class MeetingTimeGenerator {

        static List<MeetingTime> generateMeetingTimes() {
            List<MeetingTime> meetingTimes = new ArrayList<>();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            int hourInDay = 24;

            for(int index = 0; index < hourInDay; index++) {

                GregorianCalendar startTime = new GregorianCalendar();

                if(index % 3 == 0) {
                    startTime.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, index, 0);
                } else if(index % 3 == 1) {
                    startTime.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 2, index, 0);
                } else if(index % 3 == 2) {
                    startTime.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 4, index, 0);
                }
                meetingTimes.add(new MeetingTime(1L, startTime,
                        new GregorianCalendar(1900,0,0,index + 1,0), false));
            }
            return meetingTimes.subList(8, 22);
        }

        static List<MeetingTime> MEETING_TIMES = Arrays.asList(
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 0, 0),
                        new GregorianCalendar(1900,0,0,1,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 1, 0),
                        new GregorianCalendar(1900,0,0,2,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 2, 0),
                        new GregorianCalendar(1900,0,0,3,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 3, 0),
                        new GregorianCalendar(1900,0,0,4,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 4, 0),
                        new GregorianCalendar(1900,0,0,5,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 5, 0),
                        new GregorianCalendar(1900,0,0,6,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 6, 0),
                        new GregorianCalendar(1900,0,0,7,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 7, 0),
                        new GregorianCalendar(1900,0,0,8,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 8, 0),
                        new GregorianCalendar(1900,0,0,9,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 9, 0),
                        new GregorianCalendar(1900,0,0,10,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 10, 0),
                        new GregorianCalendar(1900,0,0,11,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 11, 0),
                        new GregorianCalendar(1900,0,0,12,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 12, 0),
                        new GregorianCalendar(1900,0,0,13,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 13, 0),
                        new GregorianCalendar(1900,0,0,14,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 14, 0),
                        new GregorianCalendar(1900,0,0,15,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 15, 0),
                        new GregorianCalendar(1900,0,0,16,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 16, 0),
                        new GregorianCalendar(1900,0,0,17,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 17, 0),
                        new GregorianCalendar(1900,0,0,18,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 18, 0),
                        new GregorianCalendar(1900,0,0,19,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 19, 0),
                        new GregorianCalendar(1900,0,0,20,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 20, 0),
                        new GregorianCalendar(1900,0,0,21,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 21, 0),
                        new GregorianCalendar(1900,0,0,22,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 22, 0),
                        new GregorianCalendar(1900,0,0,23,0), false),
                new MeetingTime(1L, new GregorianCalendar( 1900, 0, 0, 23, 0),
                        new GregorianCalendar(1900,0,0,24,0), false)
        );
    }
}
