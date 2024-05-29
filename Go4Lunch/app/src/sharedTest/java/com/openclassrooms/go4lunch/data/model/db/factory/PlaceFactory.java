package com.openclassrooms.go4lunch.data.model.db.factory;

import com.openclassrooms.go4lunch.data.model.db.Place;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PlaceFactory {

    public static Place makePlace() {
        return new Place(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                new Random().nextDouble(), new Random().nextDouble(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), new ArrayList<>(),
                ThreadLocalRandom.current().nextDouble(0, 5), UUID.randomUUID().toString(),
                new ArrayList<>(), new ArrayList<>(), new Random().nextInt());
    }

    public static Place makePlace(String placeId) {
        return new Place(placeId, UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                new Random().nextDouble(), new Random().nextDouble(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), new ArrayList<>(),
                ThreadLocalRandom.current().nextDouble(0, 5), UUID.randomUUID().toString(),
                new ArrayList<>(), new ArrayList<>(), new Random().nextInt());
    }
}
