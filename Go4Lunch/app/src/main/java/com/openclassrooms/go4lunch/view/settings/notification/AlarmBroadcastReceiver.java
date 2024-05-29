package com.openclassrooms.go4lunch.view.settings.notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.data.local.prefs.AppPreferences;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;

import java.util.Calendar;
import java.util.Random;

import static android.os.Build.VERSION.SDK_INT;
import static com.openclassrooms.go4lunch.data.model.db.Place.FIELD_PLACE_ID;
import static com.openclassrooms.go4lunch.data.model.db.Place.RESTAURANT_COLLECTION;
import static com.openclassrooms.go4lunch.data.model.db.User.FIELD_WORKMATE_ID;
import static com.openclassrooms.go4lunch.data.model.db.User.WORKMATE_COLLECTION;

/**
 * Manage notification for the chosen midday restaurant
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "123456";
    public static boolean isAlarmStarted = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Schedule alarm on BOOT_COMPLETED
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            scheduleAlarm(context);
        } else {
            AppPreferences appPreferences = AppPreferences.preferences(context);
            String currentUserUuid = appPreferences.getPrefKeyCurrentUserUuid();
            boolean isNotificationEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notifications", true);

            if(currentUserUuid != null && isNotificationEnabled) {
                FirebaseFirestore.getInstance().collection(WORKMATE_COLLECTION)
                        .whereEqualTo(FIELD_WORKMATE_ID, currentUserUuid)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            Place middayRestaurant = queryDocumentSnapshots.toObjects(User.class).get(0).getMiddayRestaurant();

                            if(middayRestaurant != null) {

                                StringBuilder bigText = new StringBuilder();
                                bigText.append(middayRestaurant.getName() + "\n");

                                for (String string : middayRestaurant.getAddress().split(",")) {
                                    bigText.append(string + "\n");
                                }

                                bigText.append("with workmates : \n");

                                FirebaseFirestore.getInstance().collection(RESTAURANT_COLLECTION)
                                        .whereEqualTo(FIELD_PLACE_ID, middayRestaurant.getPlaceId())
                                        .get()
                                        .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                            Place restaurant = queryDocumentSnapshots1.getDocuments().get(0).toObject(Place.class);

                                            for (User workmate : restaurant.getWorkmates()) {
                                                bigText.append("\t - " + workmate.getDisplayName() + "\n");
                                            }

                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.ic_notification_icon)
                                                    .setColor(context.getResources().getColor(R.color.colorPrimary))
                                                    .setContentTitle("Your restaurant for this noon")
                                                    .setContentText(middayRestaurant.getName())
                                                    .setStyle(new NotificationCompat.BigTextStyle()
                                                            .bigText(bigText))
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                    // Set the intent that will fire when the user taps the notification
                                                    .setAutoCancel(true);

                                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                                            NotificationManager mNotificationManager =
                                                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                NotificationChannel channel = new NotificationChannel(
                                                        CHANNEL_ID,
                                                        "Channel human readable title",
                                                        NotificationManager.IMPORTANCE_HIGH);
                                                mNotificationManager.createNotificationChannel(channel);
                                                builder.setChannelId(CHANNEL_ID);
                                            }
                                            // notificationId is a unique int for each notification that you must define
                                            notificationManager.notify(new Random().nextInt(), builder.build());
                                        });
                            }
                        });
            }
            scheduleAlarm(context);
        }
    }

    /* Schedule the alarm based on user preferences */
    public static void scheduleAlarm(Context context) {
        AlarmManager manager = AlarmManagerProvider.getAlarmManager(context);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent operation = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar actualTime = Calendar.getInstance();
        Calendar startTime = Calendar.getInstance();
        startTime.setTimeInMillis(System.currentTimeMillis());
        if (android.text.format.DateFormat.is24HourFormat(context)) {
            startTime.set(Calendar.HOUR_OF_DAY, 12);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.SECOND, 0);
        } else {
            startTime.set(Calendar.HOUR, 12);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.SECOND, 0);
            startTime.set(Calendar.AM_PM, Calendar.AM);
        }

        if(!isAlarmStarted && actualTime.getTime().after(startTime.getTime())) {
            isAlarmStarted = true;
            startTime.add(Calendar.DATE, 1);
        } else if(!isAlarmStarted && actualTime.getTime().before(startTime.getTime())) {
            isAlarmStarted = true;
        } else if(isAlarmStarted) {
            startTime.add(Calendar.DATE, 1);
        }

        if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
            manager.setExact(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), operation);
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), operation);
        }
    }
}
