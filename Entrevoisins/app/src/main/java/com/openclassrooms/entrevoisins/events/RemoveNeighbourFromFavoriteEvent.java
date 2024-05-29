package com.openclassrooms.entrevoisins.events;

import com.openclassrooms.entrevoisins.model.Neighbour;

public class RemoveNeighbourFromFavoriteEvent {

    /**
     * Neighbour to remove
     */
    public Neighbour neighbour;

    /**
     * Constructor.
     * @param neighbour
     */
    public RemoveNeighbourFromFavoriteEvent(Neighbour neighbour) {
        this.neighbour = neighbour;
    }
}
