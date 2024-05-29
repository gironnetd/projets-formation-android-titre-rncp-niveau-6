package com.openclassrooms.go4lunch.data.model.db.mapper;

import com.openclassrooms.go4lunch.data.model.api.places.details.PlaceDetails;
import com.openclassrooms.go4lunch.data.model.api.places.nearbysearch.NearbySearchPlace;
import com.openclassrooms.go4lunch.data.model.db.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to regroup Nearby Search and Details data in Place entity
 */
public class PlaceMapper {

    public List<Place> mapToCached(List<NearbySearchPlace> nearbySearchPlaces,
                                   List<PlaceDetails> placeDetails) {
        List<Place> places = new ArrayList<>();
        int count = nearbySearchPlaces.size();

        for(int index = 0; index < count; index++) {
            places.add(new Place(nearbySearchPlaces.get(index),
                    placeDetails.get(index)));
        }
        return  places;
    }
}
