package com.openclassrooms.entrevoisins.events;

import com.openclassrooms.entrevoisins.model.Neighbour;

public class ToggleFavoriteNeighbourEvent {

    /**
     * Neighbour to toggle
     */
    public Neighbour neighbour;

    /**
     * Constructor.
     * @param neighbour
     */
    public ToggleFavoriteNeighbourEvent(Neighbour neighbour) {
        this.neighbour = neighbour;
    }
}
