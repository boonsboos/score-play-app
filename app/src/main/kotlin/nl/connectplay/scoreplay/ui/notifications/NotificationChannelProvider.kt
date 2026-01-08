package nl.connectplay.scoreplay.ui.notifications

import android.content.Context
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

// this object is responsible for creating and registering the notification channel on the android device
// the object wil be create only once for the global app
object NotificationChannelProvider {
    const val CHANNEL_ID =
        "scoreplay_notifications" // unique id for this notification channel
    private const val CHANNEL_NAME = "Score-Play Notifications"
    private const val CHANNEL_DESCRIPTION = "Notifications about FriendRequest and Highscores"

    fun create(context: Context) {
        // get the androids notification system, this is required to register the notification channels before showing notifications
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel =
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        notificationChannel.description =
            CHANNEL_DESCRIPTION // The description is visible to the user in the system settings

        notificationManager.createNotificationChannel(notificationChannel) // makes the notification channel available for notifications
    }
}