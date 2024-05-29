package com.openclassrooms.go4lunch.data.source.user;

import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class FakeUserLocalDataSource implements UserLocalDataSource {

    public List<User> users;

    public FakeUserLocalDataSource(List<User> users) {
        this.users = users;
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
            } else {
                userToUpdate.setMiddayRestaurantId(null);
            }
            users.set(index, userToUpdate);
            emitter.onComplete();
        });
    }

    @Override
    public Completable deleteUser(User user) {
        return Completable.create(emitter -> {
            users.remove(user);
            emitter.onComplete();
        });
    }

    @Override
    public Single<User> findCurrentUser() {
        return Single.create(emitter -> {
            User user = users.stream().filter(User::isAuthenticated).findFirst().orElse(null);

            if (user != null) {
                emitter.onSuccess(user);
            } else {
                emitter.onError(new Throwable());
            }
        });
    }
}
