package com.openclassrooms.go4lunch.data.model.db;

import com.openclassrooms.go4lunch.data.model.db.factory.PlaceFactory;
import com.openclassrooms.go4lunch.data.model.db.factory.UserFactory;

import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

public class UserUnitTest {

    @Test
    public void get_uid() {
        User user = UserFactory.makeUser();
        assertThat(user.getUid()).isNotNull();
    }

    @Test
    public void set_uid() {
        User user = UserFactory.makeUser();

        String uid = UUID.randomUUID().toString();
        user.setUid(uid);

        assertThat(uid).isEqualTo(user.getUid());
    }

    @Test
    public void get_display_name() {
        User user = UserFactory.makeUser();
        assertThat(user.getDisplayName()).isNotNull();
    }

    @Test
    public void set_display_name() {
        User user = UserFactory.makeUser();

        String displayName = UUID.randomUUID().toString();
        user.setDisplayName(displayName);

        assertThat(displayName).isEqualTo(user.getDisplayName());
    }

    @Test
    public void get_email() {
        User user = UserFactory.makeUser();
        assertThat(user.getEmail()).isNotNull();
    }

    @Test
    public void set_email() {
        User user = UserFactory.makeUser();

        String email = UUID.randomUUID().toString();
        user.setEmail(email);

        assertThat(email).isEqualTo(user.getEmail());
    }

    @Test
    public void get_photo_url() {
        User user = UserFactory.makeUser();
        user.setPhotoUrl(UUID.randomUUID().toString());
        assertThat(user.getPhotoUrl()).isNotNull();
    }

    @Test
    public void set_photo_url() {
        User user = UserFactory.makeUser();

        String photoUrl = UUID.randomUUID().toString();
        user.setPhotoUrl(photoUrl);

        assertThat(photoUrl).isEqualTo(user.getPhotoUrl());
    }

    @Test
    public void get_phone_number() {
        User user = UserFactory.makeUser();
        assertThat(user.getPhoneNumber()).isNotNull();
    }

    @Test
    public void set_phone_number() {
        User user = UserFactory.makeUser();

        String phoneNumber = UUID.randomUUID().toString();
        user.setPhoneNumber(phoneNumber);

        assertThat(phoneNumber).isEqualTo(user.getPhoneNumber());
    }

    @Test
    public void is_authenticated() {
        User user = UserFactory.makeUser();
        user.setAuthenticated(true);
        assertThat(user.isAuthenticated()).isNotNull();
        assertThat(user.isAuthenticated()).isTrue();
    }

    @Test
    public void set_authenticated() {
        User user = UserFactory.makeUser();

        boolean isAuthenticated = new Random().nextBoolean();
        user.setAuthenticated(isAuthenticated);

        assertThat(isAuthenticated).isEqualTo(user.isAuthenticated());
    }

    @Test
    public void get_midday_restaurant_id() {
        User user = UserFactory.makeUser();
        assertThat(user.getMiddayRestaurantId()).isNotNull();
    }

    @Test
    public void set_midday_restaurant_id() {
        User user = UserFactory.makeUser();

        String middayRestaurantId = UUID.randomUUID().toString();
        user.setMiddayRestaurantId(middayRestaurantId);

        assertThat(middayRestaurantId).isEqualTo(user.getMiddayRestaurantId());
    }

    @Test
    public void get_midday_restaurant() {
        User user = UserFactory.makeUser();
        assertThat(user.getMiddayRestaurant()).isNotNull();
    }

    @Test
    public void set_midday_restaurant() {
        User user = UserFactory.makeUser();

        Place middayRestaurant = PlaceFactory.makePlace();
        user.setMiddayRestaurant(middayRestaurant);

        assertThat(middayRestaurant).isEqualTo(user.getMiddayRestaurant());
    }
}