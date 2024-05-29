package com.openclassrooms.go4lunch.data.model.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

/**
 * User Entity for local and remote database
 */
@Entity(tableName = "user")
public class User implements Parcelable {

    public static final String WORKMATE_COLLECTION = "workmates";
    public static final String FIELD_WORKMATE_ID = "uid";
    public static final String FIELD_MIDDAY_RESTAURANT_ID = "middayRestaurantId";
    public static final String FIELD_MIDDAY_RESTAURANT = "middayRestaurant";

    @PrimaryKey
    @NonNull
    private String uid;

    @ColumnInfo(name = "display_name")
    private String displayName;

    private String email;

    @ColumnInfo(name = "photo_url")
    private String photoUrl;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    private boolean authenticated;

    @ColumnInfo(name = "midday_restaurant_id")
    private String middayRestaurantId;

    @Ignore
    private Place middayRestaurant;

    public User() {}

    @Ignore
    public User(@NonNull String uid, String displayName, String email, String photoUrl,
                String phoneNumber, boolean authenticated, String middayRestaurantId) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
        this.authenticated = authenticated;
        this.middayRestaurantId = middayRestaurantId;
    }

    @Ignore
    public User(@NonNull String uid, String displayName, String email, String photoUrl,
                String phoneNumber, boolean authenticated, String middayRestaurantId, Place middayRestaurant) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
        this.authenticated = authenticated;
        this.middayRestaurantId = middayRestaurantId;
        this.middayRestaurant = middayRestaurant;
    }

    @Ignore
    public User(FirebaseUser firebaseUser) {
        this.uid = firebaseUser.getUid();
        if(firebaseUser.getDisplayName() == null) {
            this.displayName = firebaseUser.getEmail();
        } else {
            this.displayName = firebaseUser.getDisplayName();
        }
        this.email = firebaseUser.getEmail();
        if(firebaseUser.getPhotoUrl() != null) {
            this.photoUrl = Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString();
        }
        this.phoneNumber = firebaseUser.getPhoneNumber();
    }

    @Ignore
    protected User(Parcel in) {
        uid = in.readString();
        displayName = in.readString();
        email = in.readString();
        photoUrl = in.readString();
        phoneNumber = in.readString();
        authenticated = in.readByte() != 0;
        middayRestaurantId = in.readString();
        middayRestaurant = in.readParcelable(Place.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getMiddayRestaurantId() {
        return middayRestaurantId;
    }

    public void setMiddayRestaurantId(String middayRestaurantId) {
        this.middayRestaurantId = middayRestaurantId;
    }

    public Place getMiddayRestaurant() {
        return middayRestaurant;
    }

    public void setMiddayRestaurant(Place middayRestaurant) {
        this.middayRestaurant = middayRestaurant;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(displayName);
        parcel.writeString(email);
        parcel.writeString(photoUrl);
        parcel.writeString(phoneNumber);
        parcel.writeByte((byte) (authenticated ? 1 : 0));
        parcel.writeString(middayRestaurantId);
        parcel.writeParcelable(middayRestaurant, i);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return uid.equals(user.uid) &&
                displayName.equals(user.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, displayName);
    }
}
