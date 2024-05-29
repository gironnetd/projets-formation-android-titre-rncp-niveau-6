package com.openclassrooms.go4lunch.data.source.user;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.model.db.factory.UserFactory;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class FakeUserRemoteDataSource implements UserRemoteDataSource {

    public List<User> users;

    public FakeUserRemoteDataSource(List<User> users) {
        this.users = users;
    }

    @Override
    public Single<User> firebaseSignInWithCredential(AuthCredential authCredential) {
        return Single.create(emitter -> {
            User user = UserFactory.makeUser();
            user.setAuthenticated(true);
            users.add(user);
            emitter.onSuccess(user);
        });
    }

    @Override
    public Single<User> createUserWithEmailAndPassword(String email, String password) {
        return Single.create(emitter -> {
            User user = UserFactory.makeUser();
            user.setEmail(email);
            user.setAuthenticated(true);
            users.add(user);
            emitter.onSuccess(user);
        });
    }

    @Override
    public Single<User> signInWithEmailAndPassword(String email, String password) {
        return Single.create(emitter -> {
            User userToFind = users.stream().filter(user -> user.getEmail().equals(email)).findFirst().orElse(null);
            if (userToFind != null) {
                emitter.onSuccess(userToFind);
            } else {
                emitter.onError(new Throwable());
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
    public Single<User> findUserById(String userId) {
        return Single.create(emitter -> {
            User userToFind = users.stream().filter(user -> user.getUid().equals(userId)).findFirst().orElse(null);
            if (userToFind != null) {
                emitter.onSuccess(userToFind);
            } else {
                emitter.onError(new Throwable());
            }
        });
    }

    @Override
    public Completable saveUser(User user) {
        return Completable.create(emitter -> {
            users.add(user);
            emitter.onComplete();
        });
    }

    @Override
    public Completable updateUser(User user, Place place) {
        return Completable.create(emitter -> {
            int index = users.indexOf(user);
            User userToUpdate = users.get(index);

            if(place != null) {
                userToUpdate.setMiddayRestaurantId(place.getPlaceId());
                userToUpdate.setMiddayRestaurant(place);
            } else {
                userToUpdate.setMiddayRestaurantId(null);
                userToUpdate.setMiddayRestaurant(null);
            }
            users.set(index, userToUpdate);
            emitter.onComplete();
        });
    }

    @Override
    public Completable deleteUser(User user, boolean logout) {
        return Completable.create(emitter -> {
            users.remove(user);
            emitter.onComplete();
        });
    }

    @Override
    public Single<User> findCurrentUser() {
        return Single.create(emitter -> {
            User user = users.stream().filter(User::isAuthenticated).findFirst().orElse(null);
            emitter.onSuccess(user);
        });
    }
}
