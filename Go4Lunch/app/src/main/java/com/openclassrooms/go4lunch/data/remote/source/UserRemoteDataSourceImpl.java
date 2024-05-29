package com.openclassrooms.go4lunch.data.remote.source;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.source.user.UserRemoteDataSource;
import com.openclassrooms.go4lunch.utilities.HelperClass;

import java.util.List;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.Single;

import static com.openclassrooms.go4lunch.data.model.db.Place.FIELD_PLACE_ID;
import static com.openclassrooms.go4lunch.data.model.db.Place.RESTAURANT_COLLECTION;
import static com.openclassrooms.go4lunch.data.model.db.User.FIELD_WORKMATE_ID;
import static com.openclassrooms.go4lunch.data.model.db.User.WORKMATE_COLLECTION;

/**
 * Implementation for User Remote Data Source Interface
 */
public class UserRemoteDataSourceImpl implements UserRemoteDataSource {

    private static UserRemoteDataSourceImpl instance;

    private final FirebaseAuth mFirebaseAuth;
    private final FirebaseFirestore mFirebaseFirestore;

    public UserRemoteDataSourceImpl(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore) {
        this.mFirebaseAuth = firebaseAuth;
        this.mFirebaseFirestore = firebaseFirestore;
    }

    public static UserRemoteDataSourceImpl instance(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore) {
        synchronized (UserRemoteDataSourceImpl.class) {
            if(instance == null) {
                instance = new UserRemoteDataSourceImpl(firebaseAuth, firebaseFirestore);
            }
            return instance;
        }
    }

    @Override
    public Completable saveUser(User user) {
        return Completable.create(emitter -> mFirebaseFirestore.collection(WORKMATE_COLLECTION).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> workmates = queryDocumentSnapshots.toObjects(User.class);
                    WriteBatch batch = mFirebaseFirestore.batch();
                    if (!isUserAlreadyRegister(workmates, user)) {
                        DocumentReference reference = mFirebaseFirestore.collection(WORKMATE_COLLECTION).document();
                        batch.set(reference, user);
                    }

                    batch.commit().addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            emitter.onComplete();
                        }
                    });
                }));
    }

    private boolean isUserAlreadyRegister(List<User> workmates, User user) {
        return workmates.contains(user);
    }

    public Single<User> findCurrentUser() {
        return Single.create(emitter -> {
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                User user = new User(firebaseUser);
                user.setAuthenticated(true);
                emitter.onSuccess(user);
            } else {
                emitter.onError(new Throwable("Current User is Null"));
            }
        });
    }

    @Override
    public Completable updateUser(User user, Place place) {
        return Completable.create(emitter -> mFirebaseFirestore.collection(WORKMATE_COLLECTION)
                .whereEqualTo(FIELD_WORKMATE_ID, user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    User userToUpdate = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                    if (place != null) {
                        userToUpdate.setMiddayRestaurantId(place.getPlaceId());
                    } else {
                        userToUpdate.setMiddayRestaurantId(null);
                    }

                    // to remove recursivity to database firestore
                    if (place != null && place.getWorkmates() != null) {
                        int index = place.getWorkmates().indexOf(user);
                        if (index != -1) {
                            place.getWorkmates().get(index).setMiddayRestaurantId(null);
                            place.getWorkmates().get(index).setMiddayRestaurant(null);
                        }
                    }

                    userToUpdate.setMiddayRestaurant(place);
                    WriteBatch batch = mFirebaseFirestore.batch();
                    batch.set(queryDocumentSnapshots.getDocuments().get(0).getReference(), userToUpdate);
                    batch.commit().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            emitter.onComplete();
                        }
                    });
                }));
    }

    public Single<Query> queryAllWorkmates() {
        return Single.create(emitter -> {
            Query query = mFirebaseFirestore.collection(WORKMATE_COLLECTION);
            emitter.onSuccess(query);
        });
    }

    public Single<Query> queryWorkmatesByRestaurant(String restaurantId) {
        return Single.create(emitter -> mFirebaseFirestore.collection(RESTAURANT_COLLECTION)
                .whereEqualTo(FIELD_PLACE_ID, restaurantId).get()
                .addOnSuccessListener(querySnapshots -> {
                    DocumentReference reference = querySnapshots.getDocuments().get(0).getReference();
                    Query query = reference.collection(WORKMATE_COLLECTION);
                    emitter.onSuccess(query);
                }));
    }

    @Override
    public Single<User> findUserById(String userId) {
        return Single.create(emitter -> {
            mFirebaseFirestore.collection(WORKMATE_COLLECTION)
                    .whereEqualTo(FIELD_WORKMATE_ID, userId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        User user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                        if (user != null) {
                            emitter.onSuccess(user);
                        } else {
                            emitter.onError(new Throwable());
                        }
                    });
        });
    }

    @Override
    public Completable deleteUser(User user, boolean logout) {
        return Completable.create(emitter -> mFirebaseFirestore.collection(WORKMATE_COLLECTION)
                .whereEqualTo(FIELD_WORKMATE_ID, user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = mFirebaseFirestore.batch();
                    batch.delete(queryDocumentSnapshots.getDocuments().get(0).getReference());
                    batch.commit().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (logout) {
                                mFirebaseAuth.signOut();
                                LoginManager.getInstance().logOut();
                            }
                            emitter.onComplete();
                        }
                    });
                }));
    }

    public Single<User> firebaseSignInWithCredential(AuthCredential authCredential) {
        return Single.create(emitter -> mFirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    User user = new User(firebaseUser);
                    user.setAuthenticated(true);
                    emitter.onSuccess(user);
                }
            } else {
                HelperClass.logErrorMessage(Objects.requireNonNull(authTask.getException()).getMessage());
                emitter.onError(authTask.getException());
            }
        }));
    }

    public Single<User> createUserWithEmailAndPassword(String email, String password) {
        return Single.create(emitter -> mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    User user = new User(firebaseUser);
                    user.setAuthenticated(true);
                    emitter.onSuccess(user);
                }
            } else {
                HelperClass.logErrorMessage(Objects.requireNonNull(authTask.getException()).getMessage());
                emitter.onError(authTask.getException());
            }
        }));
    }

    public Single<User> signInWithEmailAndPassword(String email, String password) {
        return Single.create(emitter -> mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    User user = new User(firebaseUser);
                    user.setAuthenticated(true);
                    emitter.onSuccess(user);
                }
            } else {
                HelperClass.logErrorMessage(Objects.requireNonNull(authTask.getException()).getMessage());
                emitter.onError(authTask.getException());
            }
        }));
    }
}
