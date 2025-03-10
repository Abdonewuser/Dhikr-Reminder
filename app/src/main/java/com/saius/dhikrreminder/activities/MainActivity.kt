package com.saius.dhikrreminder.activities

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.saius.dhikrreminder.R
import com.saius.dhikrreminder.database.AdhkarDatabase
import com.saius.dhikrreminder.database.AdhkarModel
import com.saius.dhikrreminder.database.DAO
import com.saius.dhikrreminder.databinding.ActivityMainBinding
import com.saius.dhikrreminder.receiver.ReminderReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// TODO: Fix the issue so that alarm can be set after a restart when button is switched on
/**
 * Main activity responsible for the home screen of the application.
 * Allows users to navigate to the daily adhkar screen and set reminders.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val db = AdhkarDatabase
    private lateinit var dao : DAO

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     * Note: Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        dao = db.invoke(this).getDao()

        checkBatteryOptimization()

        val sharedPreferences = getSharedPreferences("ReminderData", MODE_PRIVATE)
        val reMinute = sharedPreferences.getString("MINUTE", "")
        val reSwitch = sharedPreferences.getBoolean("ON_AND_OFF", false)

        // To insert data for the first time
        val sp = getSharedPreferences("FLAG", MODE_PRIVATE)
        var insertDataVar = sp.getInt("INSERT_FLAG", 0)
        if (insertDataVar == 0){
            insertDataVar = 1

            val spGet = getSharedPreferences("FLAG", MODE_PRIVATE).edit()
            spGet.putInt("INSERT_FLAG", insertDataVar)
            spGet.apply()

            insertData()
        }

        if (reSwitch){
            if (reMinute != null) {
                setAlarm(reMinute.toInt())
                Toast.makeText(this, "Reminder has been reactivated", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for navigating to DailyAdhkarActivity
        binding.cardViewDhikr.setOnClickListener{
            val intent = Intent(this, DailyAdhkarActivity::class.java)
            startActivity(intent)
        }
        // Set click listener for navigating to ReminderOptionsActivity
        binding.cardViewReminderOptions.setOnClickListener{
            val intent = Intent(this, ReminderOptionsActivity::class.java)
            startActivityForResult(intent, 100)
        }
        // Set click listener on help button
        binding.imgBtnHelp.setOnClickListener{
            val intent = Intent(this, )
        }

        updateTxtMinAndTxtAdhkar()


    }

    private fun updateTxtMinAndTxtAdhkar() {
        val sharedPreferences = getSharedPreferences("ReminderData", MODE_PRIVATE)
        val reMinute = sharedPreferences.getString("MINUTE", "")
        CoroutineScope(Dispatchers.IO).launch {
            val list = dao.getAdhkarDataInList()
            val listSize = list.size.toString()

            withContext(Dispatchers.Main){
                binding.txtAdhkar.setText("$listSize Adhkar are active")
//                binding.txtMins.setText("Set to remind every $reMinute minutes")
            }
        }
        binding.txtMins.setText("Set to remind every $reMinute minutes")
    }

    private fun checkBatteryOptimization() {
        if (!isIgnoringBatteryOptimization()){

            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Battery Optimization")
                .setMessage("Turn off battery optimization to receive timely reminders")
                .setPositiveButton("Ok"){_,_ ->
                    openBatteryOptimizationSetting()
                }
            alertDialog.show()
        }
    }

    private fun openBatteryOptimizationSetting() {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        startActivity(intent)
    }

    private fun isIgnoringBatteryOptimization(): Boolean {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }

    /**
     * Deprecated callback method when activity is returning to receive results from the ReminderOptionsActivity.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing
     * you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data types can be attached as
     * extras to the Intent).
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK){

            val min = data?.getStringExtra("MINUTES")
            val switchStatus = data?.getBooleanExtra("ON_AND_OFF", false)
            val minutes = min?.toInt()

            Log.d("TAGY MainActivity", "onActivityResult: $minutes and $switchStatus")

            if (switchStatus == true){
                Log.d("TAGY MainActivity", "onActivityResult: Alarm has been set")
                setAlarm(minutes!!)
            } else{
                Log.d("TAGY MainActivity", "onActivityResult: Alarm has been cancelled")
                cancelAlarm()
            }
        }
    }

    /**
     * Cancels the alarm that was previously set.
     */
    private fun cancelAlarm() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1000, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Sets an alarm to trigger the ReminderReceiver with the specified interval.
     *
     * @param minutes The interval (in minutes) at which the alarm should repeat.
     */
    private fun setAlarm(minutes: Int) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intervals = minutes * 60 * 1000
        val timeForTest = 60000

        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1000, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeForTest, intervals.toLong(), pendingIntent)
    }

    private fun insertData() {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(AdhkarModel(1,"Astagfirullah", true))
            dao.insert(AdhkarModel(2,"Subhanallah", true))
            dao.insert(AdhkarModel(3,"Alhamdulilah", true))
            dao.insert(AdhkarModel(4,"Allahuakbar", true))
            dao.insert(AdhkarModel(5,"Subhanallah wabi hamdi", true))
            dao.insert(AdhkarModel(6,"La ilaha illa Allah", true))
            dao.insert(AdhkarModel(7,"La haula walaquata illabillah", true)) }
    }

    override fun onResume() {
        super.onResume()
        updateTxtMinAndTxtAdhkar()
    }
}