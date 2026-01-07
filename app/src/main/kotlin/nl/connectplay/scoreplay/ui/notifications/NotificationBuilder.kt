package nl.connectplay.scoreplay.ui.notifications

import android.content.Context
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import nl.connectplay.scoreplay.R

object NotificationBuilder {
    fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = NotificationManagerCompat.from(context)

        // checks if the app allowed to send notifications
        if (!notificationManager.areNotificationsEnabled()) return

        val notification =
            NotificationCompat.Builder(
                context,
                NotificationChannelProvider.CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO() change the Icon to Notification Icon
                .setContentTitle(title)
                .setContentText(message)
                // set the importance level so the notifcations will be shown as normal alert
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // removes notifications when the user clicks on it
                .setAutoCancel(true)

        // triggers the notification to be shown to the user (notify)
        // this unique notification id is required so multiple notifications can exist at the same time (System.currentTimeMillis().toInt())
        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notification.build()
        )

    }
}