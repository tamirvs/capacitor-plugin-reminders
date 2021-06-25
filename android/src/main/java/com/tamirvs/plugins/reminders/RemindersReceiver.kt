package com.tamirvs.plugins.reminders

import android.R.attr.action
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.getcapacitor.JSObject
import com.getcapacitor.plugin.util.AssetUtil
import android.net.Uri


class RemindersReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
//        android.util.Log.d("timee", "received")
        val jsData = intent.getStringExtra("data")
        val data = JSObject(jsData)
        val id = data.getInt("id")
        val url = data.getString("url")

        val packageName = context.packageName
        val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return

        intent.action = Intent.ACTION_VIEW
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("reminderId", id)
        intent.data = Uri.parse(url)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val resId = AssetUtil.getResourceID(context, data.getString("smallIcon"), "drawable")
        var builder = NotificationCompat.Builder(context, RemindersPlugin.CHANNEL_ID)
            .setSmallIcon(resId)
            .setContentTitle(data.getString("title"))
            .setContentText(data.getString("body"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(id, builder.build())
        }
    }
}