package com.openclassrooms.go4lunch.data.model.db.factory;

import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;

import java.util.UUID;

public class UserFactory {

    public static User makeUser() {
        Place place = PlaceFactory.makePlace();
        return new User(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                null, UUID.randomUUID().toString(),
                false, place.getPlaceId(), place);
    }
}
