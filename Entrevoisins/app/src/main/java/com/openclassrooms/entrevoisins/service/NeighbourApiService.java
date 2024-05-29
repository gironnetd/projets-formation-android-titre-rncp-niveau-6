package com.openclassrooms.entrevoisins.service;

import com.openclassrooms.entrevoisins.model.Neighbour;

import java.util.List;


/**
 * Neighbour API client
 */
public interface NeighbourApiService {

    /**
     * Get all my Neighbours
     * @return {@link List}
     */
    List<Neighbour> getNeighbours();

    /**
     *
     * @param neighbourId Get neighbour by id
     * @return
     */
    Neighbour getNeighbourById(int neighbourId);

    /**
     * Get all favorite Neighbours
     * @return {@link List}
     *
     */
    List<Neighbour> getFavoriteNeighbours();

    /**
     * Deletes a neighbour
     * @param neighbour
     */
    void deleteNeighbour(Neighbour neighbour);

    /**
     * Create a neighbour
     * @param neighbour
     */
    void createNeighbour(Neighbour neighbour);

    /**
     *
     * @param neighbour
     */
    void toggleToFavorite(Neighbour neighbour);
}
