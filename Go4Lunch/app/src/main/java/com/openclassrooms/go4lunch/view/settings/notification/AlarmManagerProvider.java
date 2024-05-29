package com.openclassrooms.go4lunch.view.settings.notification;

import android.app.AlarmManager;
import android.content.Context;

/**
 * Singleton to provide an unique alarm manager
 */
public class AlarmManagerProvider {

    private static AlarmManager sAlarmManager;

    static synchronized AlarmManager getAlarmManager(Context context) {
        if (sAlarmManager == null) {
            sAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        return sAlarmManager;
    }
}
