package com.saius.dhikrreminder.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.saius.dhikrreminder.R
import com.saius.dhikrreminder.database.AdhkarDatabase
import com.saius.dhikrreminder.database.AdhkarModel
import com.saius.dhikrreminder.database.DAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *
 * BroadcastReceiver responsible for receiving reminders to play adhkar (remembrance phrases).
 * Upon receiving a reminder, it fetches the adhkar data from the database and plays the respective audio.
 * It also manages the index of the current adhkar being played to ensure that each adhkar is played in sequence.
 *
 * @constructor Creates an instance of ReminderReceiver.
 */
class ReminderReceiver : BroadcastReceiver() {

    // Database instance to access adhkar data
    private val db = AdhkarDatabase

    // Data Access Object for accessing adhkar data from the database
    private lateinit var dao: DAO

    // List of adhkar models fetched from the database
    private var list = listOf<AdhkarModel>()

    // Index of the current adhkar being played
    private var currentItemIndex = 0

    /**
     * Method called when a reminder is received.
     *
     * @param context The context in which the receiver is running.
     * @param intent The Intent being received.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAGY Reminder Receiver", "onReceive: YES I HAVE RECEIVED")

        // Retrieve the index of the current adhkar from SharedPreferences
        val sharedPreferences = context?.getSharedPreferences("INDEX", AppCompatActivity.MODE_PRIVATE)
        if (sharedPreferences != null) {
            currentItemIndex = sharedPreferences.getInt("INDEX_NUMBER", 0)
        }

        // Fetch adhkar data from the database and handle playing adhkar
        if (context != null) {
            dao = db.invoke(context.applicationContext).getDao()

            CoroutineScope(Dispatchers.IO).launch {
                list = dao.getAdhkarDataInList()
                handleItem(context)
            }
        }
    }

    /**
     * Method to handle playing adhkar based on the current index.
     *
     * @param context The context in which the receiver is running.
     */
    private fun handleItem(context: Context?) {
        if (list.isNotEmpty()) {
            // Update the index in SharedPreferences
            val sharedPreferences = context!!.getSharedPreferences("INDEX", AppCompatActivity.MODE_PRIVATE).edit()
            // To check if the list has become smaller than the index number after turning on and off adhakr switch
            if (currentItemIndex >= list.size) {
                currentItemIndex = 0
                sharedPreferences.putInt("INDEX_NUMBER", currentItemIndex)
                sharedPreferences.apply()
            }
            val currentItem = list[currentItemIndex]


            // Play the respective adhkar audio based on the current adhkar ID
            when (currentItem.id) {
                1 -> playAudio(context, R.raw.astagfirullah)
                2 -> playAudio(context, R.raw.subhanallah)
                3 -> playAudio(context, R.raw.alhamdulillah)
                4 -> playAudio(context, R.raw.allahu_akbar)
                5 -> playAudio(context, R.raw.subhanallah_wabi_hamdi)
                6 -> playAudio(context, R.raw.la_ilaha_illa_allah)
                7 -> playAudio(context, R.raw.la_haula)
            }

            notify(context, currentItem.adhkar)

            // Reset the index to 0 if it reaches the end of the adhkar list
            currentItemIndex += 1
            sharedPreferences.putInt("INDEX_NUMBER", currentItemIndex)
            sharedPreferences.apply()

            Log.d("FINAL BOSS AFTER", "handleItem: ${currentItem.adhkar} and index is $currentItemIndex and list size ${list.size}")
        }
    }

    /**
     * Method to play adhkar audio.
     *
     * @param context The context in which the receiver is running.
     * @param audioResource The resource ID of the audio to be played.
     */
    private fun playAudio(context: Context, audioResource: Int) {
        val mediaPlayer = MediaPlayer.create(context, audioResource)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
        }
    }

    private fun notify(context: Context, adhkar: String) {

        val channelId = "ADHKAR"
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notifications_processed)
            .setContentText(adhkar)
            .setContentTitle("Dhikr Reminder")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSilent(true)

        val channel = NotificationChannel(channelId, "Adhkar", NotificationManager.IMPORTANCE_HIGH)
        nm.createNotificationChannel(channel)

        nm.notify(0, notificationBuilder.build())


    }
}
