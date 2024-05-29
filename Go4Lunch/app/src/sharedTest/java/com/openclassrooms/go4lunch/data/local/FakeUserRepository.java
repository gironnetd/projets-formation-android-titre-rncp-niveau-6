package com.openclassrooms.go4lunch.data.local;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.model.db.factory.UserFactory;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class FakeUserRepository implements UserRepository {

    private List<User> userServiceData = new ArrayList<>();

    @Override
    public Single<User> isUserAuthenticated() {
        return findCurrentUser();
    }

    @Override
    public Single<User> firebaseSignInWithCredential(AuthCredential authCredential) {
        return Single.create(emitter -> {
            User user = UserFactory.makeUser();
            user.setAuthenticated(true);
            userServiceData.add(user);
            emitter.onSuccess(user);
        });
    }

    @Override
    public Single<User> createUserWithEmailAndPassword(String email, String password) {
        return Single.create(emitter -> {
            User user = UserFactory.makeUser();
            user.setEmail(email);
            user.setAuthenticated(true);
            userServiceData.add(user);
            emitter.onSuccess(user);
        });
    }

    @Override
    public Single<User> signInWithEmailAndPassword(String email, String password) {
        return Single.create(emitter -> {
            User userToFind = userServiceData.stream().filter(user -> user.getEmail().equals(email)).findFirst().orElse(null);
            if (userToFind != null) {
                emitter.onSuccess(userToFind);
            } else {
                Throwable throwable = new Throwable("SignInWithEmailAndPassword : User is Null");
                emitter.onError(throwable);
            }
        });
    }

    @Override
    public Single<Query> queryAllWorkmates() {
        return null;
    }

    @Override
    public Single<Query> queryWorkmatesByRestaurant(String restaurantId) {
        return null;
    }

    @Override
    public Single<User> findCurrentUser() {
        return Single.create(emitter -> {
            User userAuthenticated = userServiceData.stream().filter(user -> user.isAuthenticated()).findFirst().orElse(null);
            if (userAuthenticated != null) {
                emitter.onSuccess(userAuthenticated);
            } else {
                emitter.onError(new Throwable());
            }
        });
    }

    @Override
    public Completable saveUser(User user) {
        return Completable.create(emitter -> {
            userServiceData.add(user);
            emitter.onComplete();
        });
    }

    @Override
    public Completable updateUser(User user, Place place) {
        return Completable.create(emitter -> {
            int index = userServiceData.indexOf(user);
            User userToUpdate = userServiceData.get(index);

            if (place != null) {
                userToUpdate.setMiddayRestaurantId(place.getPlaceId());
                userToUpdate.setMiddayRestaurant(place);
            } else {
                userToUpdate.setMiddayRestaurantId(null);
                userToUpdate.setMiddayRestaurant(null);
            }
            userServiceData.set(index, userToUpdate);
            emitter.onComplete();
        });
    }

    @Override
    public Completable deleteUser(User user, boolean logout) {
        return Completable.create(emitter -> {
            userServiceData.remove(user);
            emitter.onComplete();
        });
    }
}
