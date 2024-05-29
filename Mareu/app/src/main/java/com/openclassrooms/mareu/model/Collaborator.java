package com.openclassrooms.mareu.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Collaborator class representing the Collaborator who take part of meetings
 */
public class Collaborator {

    /**
     * identifier of the participant
     */
    private long id;

    /**
     * first name of the participant
     * @return
     */
    private String firstName;

    /**
     * last name of the participant
     */
    private String lastName;

    /**
     * email of the participant
     */
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Collaborator(long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Class to generate list of employees of the society that can participate to meetings
     */
    public static class CollaboratorGenerator {

        public static List<Collaborator> generateCollaborators() {
            return new ArrayList<>(collaboratorsList);
        }

        static List<Collaborator> collaboratorsList = Arrays.asList(
                new Collaborator(1L,"Francis","Dupont", "francis@lamzone.com"),
                new Collaborator(2L, "Alexandra", "Ricci","alexandra@lamzone.com"),
                new Collaborator(3L, "Alain", "Gonzales","alain@lamzone.com"),
                new Collaborator(4L, "Steeve", "Delaunay","steeve@lamzone.com"),
                new Collaborator(5L, "Jean", "Le Québel","jean@lamzone.com"),
                new Collaborator(6L, "Frank", "Marechal","frank@lamzone.com"),
                new Collaborator(7L, "Stéphane", "Mendoza","stéphane@lamzone.com"),
                new Collaborator(8L, "Sylvain", "Brown","sylvain@lamzone.com"),
                new Collaborator(9L, "Jérôme", "Abbas","jérôme@lamzone.com"),
                new Collaborator(10L, "Cynthia", "Abergel","cynthia@lamzone.com"),
                new Collaborator(11L, "Pierre", "Alekseï","pierre@lamzone.com"),
                new Collaborator(12L, "Cécile", "Lemaitre","cécile@lamzone.com")
        );
    }
}
