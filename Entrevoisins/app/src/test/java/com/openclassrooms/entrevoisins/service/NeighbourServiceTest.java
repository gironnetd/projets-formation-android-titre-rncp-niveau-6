package com.openclassrooms.entrevoisins.service;

import com.openclassrooms.entrevoisins.di.DI;
import com.openclassrooms.entrevoisins.model.Neighbour;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Unit test on Neighbour service
 */
@RunWith(JUnit4.class)
public class NeighbourServiceTest {

    private NeighbourApiService service;
    private int initialNeighbourCount ;

    @Before
    public void setup() {
        service = DI.getNewInstanceApiService();
        initialNeighbourCount = service.getNeighbours().size();
    }

    @After
    public void tearDown() {
        service = null;
        initialNeighbourCount = -1;
    }

    @Test
    public void get_Neighbours_With_Success() {
        List<Neighbour> neighbours = service.getNeighbours();
        List<Neighbour> expectedNeighbours = DummyNeighbourGenerator.DUMMY_NEIGHBOURS;
        assertThat(neighbours, IsIterableContainingInAnyOrder.containsInAnyOrder(expectedNeighbours.toArray()));
    }

    @Test
    public void delete_Neighbour_With_Success() {
        Neighbour neighbourToDelete = service.getNeighbours().get(0);
        service.deleteNeighbour(neighbourToDelete);
        assertFalse(service.getNeighbours().contains(neighbourToDelete));
    }

    @Test
    public void add_Neighbour_With_Success() {
        int neighboursNumber = service.getNeighbours().size();
        Neighbour newNeighbour = new Neighbour(neighboursNumber + 1,
                "Jean",
                "",
                "",
                "",
                "",
                false);
        service.createNeighbour(newNeighbour);
        assertTrue(service.getNeighbours().size() == neighboursNumber + 1);

    }

    @Test
    public void get_Neighbour_By_Id_With_Success() {
        int NEIGHBOUR_ID = initialNeighbourCount + 1;
        Neighbour newNeighbour = new Neighbour(NEIGHBOUR_ID,
                "Jean",
                "https://i.pravatar.cc/150?u=a042581f4e29026704d",
                "24, square Dupont 78150 Le Chesnay",
                "062453649237",
                "Je suis le parcours Développeur d'applications - Android chez OpenClassrooms",
                false);
        service.createNeighbour(newNeighbour);
        Neighbour neighbourById = service.getNeighbourById(NEIGHBOUR_ID);
        assertEquals(newNeighbour.getId(), neighbourById.getId());
        assertEquals(newNeighbour.getName(), neighbourById.getName());
        assertEquals(newNeighbour.getAvatarUrl(), neighbourById.getAvatarUrl());
        assertEquals(newNeighbour.getAddress(), neighbourById.getAddress());
        assertEquals(newNeighbour.getPhoneNumber(), neighbourById.getPhoneNumber());
        assertEquals(newNeighbour.getAboutMe(), neighbourById.getAboutMe());
    }

    @Test
    public void get_Favorite_Neighbours_With_Success() {
        int favoriteNumber = 5;
        for(int i = 0; i < favoriteNumber; i++) {
            Neighbour neighbour = new Neighbour(i,
                    "",
                    "",
                    "",
                    "",
                    "",
                    true);
            service.createNeighbour(neighbour);
        }

        List<Neighbour> favoriteNeighbours = service.getFavoriteNeighbours();
        assertEquals(favoriteNumber,favoriteNeighbours.size());
    }

    @Test
    public void toggle_Neighbour_To_Favorite_With_Success() {
        int NEIGHBOUR_ID = initialNeighbourCount;
        Neighbour neighbour = service.getNeighbourById(NEIGHBOUR_ID);
        Boolean isFavorite = neighbour.getFavorite();
        service.toggleToFavorite(neighbour);

        Neighbour neighbourAfterToggle = service.getNeighbourById(NEIGHBOUR_ID);
        assertNotSame(isFavorite, neighbourAfterToggle.getFavorite());

        service.toggleToFavorite(neighbour);
    }
}
