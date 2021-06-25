package com.tamirvs.plugins.reminders

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin


@CapacitorPlugin(name = "Reminders")
class RemindersPlugin : Plugin() {
    override fun load() {
//        val pluginConfig = config
//        val clientId = pluginConfig.configJSON.getString("clientId")
        createNotificationChannel()
    }

    companion object {
        const val CHANNEL_ID = "com.tamirvs.plugins.reminders.DEFAULT"
        const val CHANNEL_NAME = "Default"
        const val CHANNEL_DESC = "Reminders notifications"
    }

    override fun handleOnNewIntent(intent: Intent) {
        super.handleOnNewIntent(intent)

        if (intent.action != Intent.ACTION_VIEW || !intent.hasExtra("reminderId")) {
            return
        }

        val url = intent.data
        val id = intent.getIntExtra("reminderId", -1)

        if (url != null) {
            val ret = JSObject()
            ret.put("id", id)
            ret.put("url", url.toString())
            notifyListeners("reminderOpened", ret, true)
        }
    }

    @PluginMethod
    fun schedule(call: PluginCall) {
        val data = call.data
        val timestamp = data.getLong("timestamp")
        val id = data.getInt("id")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, RemindersReceiver::class.java)
        alarmIntent.putExtra("data", data.toString())
        val pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
        }

        call.resolve()
    }

    @PluginMethod
    fun cancel(call: PluginCall) {
        val data = call.data
        val id = data.getInt("id")
        val alarmIntent = Intent(context, RemindersReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)

        call.resolve()
    }

    @PluginMethod
    fun isAvailable(call: PluginCall) {
        val ret = JSObject()
        ret.put("result", true)
        call.resolve(ret)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = CHANNEL_DESC
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}