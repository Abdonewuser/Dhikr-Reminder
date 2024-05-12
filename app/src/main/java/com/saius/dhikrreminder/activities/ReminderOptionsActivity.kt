package com.saius.dhikrreminder.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.saius.dhikrreminder.R
import com.saius.dhikrreminder.databinding.ActivityReminderOptionsBinding

class ReminderOptionsActivity : AppCompatActivity() {

    lateinit var binding: ActivityReminderOptionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder_options)


        val sharedPreferences = getSharedPreferences("ReminderData", MODE_PRIVATE)
        var switchStatus : Boolean = sharedPreferences.getBoolean("SWITCH_STATUS", false)
        val minute : String? = sharedPreferences.getString("MINUTE", "")

        // Initialize
        binding.switchButton.isChecked = switchStatus
        binding.editTextNumber.setText(minute)

        binding.switchButton.setOnCheckedChangeListener{_, isChecked ->
            switchStatus = isChecked
        }

        binding.btnSave.setOnClickListener{
            // To make sure that user fills the minute field
            if (binding.editTextNumber.text.toString() == ""){
                Toast.makeText(this, "Put in the number of minutes", Toast.LENGTH_SHORT).show()
            }else{
                val minutes = binding.editTextNumber.text.toString().trim()

                val sp = getSharedPreferences("ReminderData", MODE_PRIVATE).edit()
                sp.putBoolean("SWITCH_STATUS", switchStatus)
                sp.putString("MINUTE", minutes)
                sp.apply()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("ON_AND_OFF", switchStatus)
                intent.putExtra("MINUTES", minutes)

                if (switchStatus){
                    val m = binding.editTextNumber.text.toString().trim()
                    Toast.makeText(this, "Reminder is set to remind every $m minutes", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this, "Reminder is off", Toast.LENGTH_SHORT).show()
                }

                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}