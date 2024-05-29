package com.openclassrooms.go4lunch.data.model.db;

import com.openclassrooms.go4lunch.data.model.db.factory.PlaceFactory;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;
import static com.google.common.truth.Truth.assertThat;

public class PlaceUnitTest {

    @Test
    public void get_placeId() {
        Place place = PlaceFactory.makePlace();
        assertThat(place.getPlaceId()).isNotNull();
    }

    @Test
    public void set_placeId() {
        Place place = PlaceFactory.makePlace();

        String placeId = UUID.randomUUID().toString();
        place.setPlaceId(placeId);
        assertThat(placeId).isEqualTo(place.getPlaceId());
    }

    @Test
    public void get_name() {
        Place place = PlaceFactory.makePlace();
        assertThat(place.getName()).isNotNull();
    }

    @Test
    public void set_name() {
        Place place = PlaceFactory.makePlace();

        String name = UUID.randomUUID().toString();
        place.setName(name);

        assertThat(name).isEqualTo(place.getName());
    }

    @Test
    public void get_address() {
        Place place = PlaceFactory.makePlace();
        assertThat(place.getAddress()).isNotNull();
    }

    @Test
    public void set_address() {
        Place place = PlaceFactory.makePlace();

        String address = UUID.randomUUID().toString();
        place.setAddress(address);

        assertThat(address).isEqualTo(place.getAddress());
    }

    @Test
    public void get_latitude() {
        Place place = PlaceFactory.makePlace();
        assertThat(place.getLatitude()).isNotNull();
    }

    @Test
    public void set_latitude() {
        Place place = PlaceFactory.makePlace();

        double latitude = new Random().nextDouble();
        place.setLatitude(latitude);

        assertThat(latitude).isEqualTo(place.getLatitude());
    }

    @Test
    public void get_longitude() {
        Place place = PlaceFactory.makePlace();
        assertThat(place.getLongitude()).isNotNull();
    }

    @Test
    public void set_longitude() {
        Place place = PlaceFactory.makePlace();

        double longitude = new Random().nextDouble();
        place.setLongitude(longitude);

        assertThat(longitude).isEqualTo(place.getLongitude());
    }

    @Test
    public void get_phone_number() {
        Place place = PlaceFactory.makePlace();
        assertThat(place.getPhoneNumber()).isNotNull();
    }

    @Test
    public void set_phone_number() {
        Place place = PlaceFactory.makePlace();

        String phoneNumber = UUID.randomUUID().toString();
        place.setPhoneNumber(phoneNumber);

        assertThat(phoneNumber).isEqualTo(place.getPhoneNumber());
    }

    @Test
    public void get_website_uri() {
        Place place = PlaceFactory.makePlace();
        assertThat(place.getWebsiteUri()).isNotNull();
    }

    @Test
    public void set_website_uri() {
        Place place = PlaceFactory.makePlace();

        String websiteUri = UUID.randomUUID().toString();
        place.setWebsiteUri(websiteUri);

        assertThat(websiteUri).isEqualTo(place.getWebsiteUri());
    }

    @Test
    public void get_photo_reference() {
        Place place = PlaceFactory.makePlace();
        assertThat(place.getPhotoReference()).isNotNull();
    }

    @Test
    public void set_photo_reference() {
        Place place = PlaceFactory.makePlace();

        String photoReference = UUID.randomUUID().toString();
        place.setPhotoReference(photoReference);

        assertThat(photoReference).isEqualTo(place.getPhotoReference());
    }

    @Test
    public void get_weekday_text() {
        Place place = PlaceFactory.makePlace();
        assertThat(place.getWeekdayText()).isNotNull();
    }

    @Test
    public void set_weekday_text() {
        Place place = PlaceFactory.makePlace();

        List<String> weekdayText = new ArrayList<>();
        for(int index = 0; index < 4; index++) {
            weekdayText.add(UUID.randomUUID().toString());
        }

        place.setWeekdayText(weekdayText);

        assertThat(weekdayText).isEqualTo(place.getWeekdayText());
    }

    @Test
    public void get_rating() {
        Place place = PlaceFactory.makePlace();
        assertThat(place.getRating()).isNotNull();
    }

    @Test
    public void set_rating() {
        Place place = PlaceFactory.makePlace();

        double rating = ThreadLocalRandom.current().nextDouble(0, 5);
        place.setRating(rating);

        assertThat(rating).isEqualTo(place.getRating());
    }

    @Test
    public void get_vicinity() {
        Place place = PlaceFactory.makePlace();
        assertThat(place.getVicinity()).isNotNull();
    }

    @Test
    public void set_vicinity() {
        Place place = PlaceFactory.makePlace();

        String vicinity = UUID.randomUUID().toString();
        place.setVicinity(vicinity);

        assertThat(vicinity).isEqualTo(place.getVicinity());
    }
}