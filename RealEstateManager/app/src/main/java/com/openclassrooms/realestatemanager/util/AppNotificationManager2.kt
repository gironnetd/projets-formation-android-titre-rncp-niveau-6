package com.openclassrooms.realestatemanager.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.property.Property
import com.openclassrooms.realestatemanager.ui.MainActivity
import java.util.ArrayList

class AppNotificationManager2(var mContext: Context) {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels: MutableList<NotificationChannel> = ArrayList()
            channels.add(createAppNotificationChanel(
                PROPERTIES_CHANEL_ID,
                mContext.getString(R.string.notification_channel_properties_name),
                mContext.getString(R.string.notification_channel_properties_description),
                NotificationManagerCompat.IMPORTANCE_HIGH))
            channels.add(createAppNotificationChanel(
                APP_CHANEL_ID,
                mContext.getString(R.string.notification_channel_app_name),
                mContext.getString(R.string.notification_channel_app_description),
                NotificationManagerCompat.IMPORTANCE_DEFAULT))
            val notificationManager =
                mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(channels)
        }
    }

    private fun showNotification(@NonNull notification: Notification, notificationId: Int) {
        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(mContext)
        notificationManager.notify(notificationId, notification)
    }

    private fun createCustomNotification(
        action: NotificationCompat.Action?,
        message: String,
        contentIntent: PendingIntent?,
    ): Notification {
        return Builder(mContext, PROPERTIES_CHANEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(mContext.getString(R.string.notification_title))
            .setContentText(message)
            .setAutoCancel(true)
            //.setContentIntent(contentIntent)
            //.addAction(action)
            .setGroup(GROUP_KEY_PROPERTIES)
            .build()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createAppNotificationChanel(
        chanelId: String,
        chanelName: String,
        chanelDescription: String,
        chanelImportance: Int,
    ): NotificationChannel {
        val channel = NotificationChannel(chanelId, chanelName, chanelImportance)
        channel.description = chanelDescription
        return channel
    }

    fun showDetailsNotificationWithAllPropertiesAction(property: Property?, message: String) {
        val allCitiesIntent = Intent(mContext, MainActivity::class.java)
        val notificationId = (BASE_NOTIFICATION_ID.toInt() + property.hashCode())
//        allCitiesIntent.putExtra(Companion.EXTRA_NOTIFICATION_ID, notificationId)
//        val allCitiesPendingIntent = PendingIntent.getActivity(
//            mContext,
//            notificationId,
//            allCitiesIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT)
//        val detailCityIntent = Intent(mContext, DetailActivity::class.java)
//        detailCityIntent.putExtra(DetailActivity.CITY_ID, city.getId())
//        val detailPendingIntent = PendingIntent.getActivity(
//            mContext,
//            notificationId,
//            null,
//            //detailCityIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT)
//        val allCitiesAction: NotificationCompat.Action = NotificationCompat.Action(
//            R.drawable.ic_notification,
//            mContext.getString(R.string.notification_action_all_cities),
//            allCitiesPendingIntent)
        val notification = createCustomNotification(
            //allCitiesAction,
            null,
            message,
            null
            //detailPendingIntent
        )
        showNotification(notification, notificationId)
    }

    fun showBundleNotification(notificationCount: Int) {
        val summaryNotification: Notification = Builder(mContext, PROPERTIES_CHANEL_ID)
            .setContentText("$notificationCount cities")
            .setSmallIcon(R.drawable.ic_notification)
            .setStyle(NotificationCompat.InboxStyle())
            .setGroup(GROUP_KEY_PROPERTIES)
            .setGroupSummary(true)
            .build()
        showNotification(summaryNotification, BASE_NOTIFICATION_ID.toInt())
    }

    fun hideNotification(@Nullable intent: Intent?) {
        val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (intent != null) {
            val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, INVALID_NOTIFICATION_ID)
            notificationManager.cancel(notificationId)
        }
    }

    companion object {
        private const val EXTRA_NOTIFICATION_ID = "notification_id"
        private const val APP_PACKAGE = "com.openclassrooms.realestatemanager"
        private const val PROPERTIES_CHANEL_ID = "$APP_PACKAGE.CITIES_CHANNEL"
        private const val APP_CHANEL_ID = "$APP_PACKAGE.APP_CHANNEL"
        private const val GROUP_KEY_PROPERTIES = "$APP_CHANEL_ID.CITIES_GROUP"
        private const val BASE_NOTIFICATION_ID = 100L
        private const val INVALID_NOTIFICATION_ID = -1
    }

}

