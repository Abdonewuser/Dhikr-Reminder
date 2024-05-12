package com.saius.dhikrreminder.activities

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.saius.dhikrreminder.R
import com.saius.dhikrreminder.database.AdhkarDatabase
import com.saius.dhikrreminder.database.AdhkarModel
import com.saius.dhikrreminder.database.DAO
import com.saius.dhikrreminder.databinding.ActivityDailyAdhkarBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DailyAdhkarActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDailyAdhkarBinding
    private val db = AdhkarDatabase
    private lateinit var dao : DAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily_adhkar)

        dao = db.invoke(this).getDao()

        dao.getAdhkarData().observe(this, Observer {
            updateSwitchStatus(it)
        })

        binding.switch1.setOnCheckedChangeListener{_, isChecked ->
            CoroutineScope(Dispatchers.IO).launch{
                if (isChecked){
                    dao.update(AdhkarModel(1,"Astagfirullah", true))
                }else{
                    dao.update(AdhkarModel(1,"Astagfirullah", false))
                }
            }
        }
        binding.switch2.setOnCheckedChangeListener{_, isChecked ->
            CoroutineScope(Dispatchers.IO).launch{
                if (isChecked){
                    dao.update(AdhkarModel(2,"Subhanallah", true))
                }else{
                    dao.update(AdhkarModel(2,"Subhanallah", false))
                }
            }
        }
        binding.switch3.setOnCheckedChangeListener{_, isChecked ->
            CoroutineScope(Dispatchers.IO).launch{
                if (isChecked){
                    dao.update(AdhkarModel(3,"Alhamdulilah", true))
                }else{
                    dao.update(AdhkarModel(3,"Alhamdulilah", false))
                }
            }
        }
        binding.switch4.setOnCheckedChangeListener{_, isChecked ->
            CoroutineScope(Dispatchers.IO).launch{
                if (isChecked){
                    dao.update(AdhkarModel(4,"Allahuakbar", true))
                }else{
                    dao.update(AdhkarModel(4,"Allahuakbar", false))
                }
            }
        }
        binding.switch5.setOnCheckedChangeListener{_, isChecked ->
            CoroutineScope(Dispatchers.IO).launch{
                if (isChecked){
                    dao.update(AdhkarModel(5,"Subhanallah wabi hamdi", true))
                }else{
                    dao.update(AdhkarModel(5,"Subhanallah wabi hamdi", false))
                }
            }
        }
        binding.switch6.setOnCheckedChangeListener{_, isChecked ->
            CoroutineScope(Dispatchers.IO).launch{
                if (isChecked){
                    dao.update(AdhkarModel(6,"La ilaha illa Allah", true))
                }else{
                    dao.update(AdhkarModel(6,"La ilaha illa Allah", false))
                }
            }
        }
        binding.switch7.setOnCheckedChangeListener{_, isChecked ->
            CoroutineScope(Dispatchers.IO).launch{
                if (isChecked){
                    dao.update(AdhkarModel(7,"La haula walaquata illabillah", true))
                }else{
                    dao.update(AdhkarModel(7,"La haula walaquata illabillah", false))
                }
            }
        }

        binding.play1.setOnClickListener{
            val mp = MediaPlayer.create(this, R.raw.astagfirullah)
            mp.start()
        }
        binding.play2.setOnClickListener{
            val mp = MediaPlayer.create(this, R.raw.subhanallah)
            mp.start()
        }
        binding.play3.setOnClickListener{
            val mp = MediaPlayer.create(this, R.raw.alhamdulillah)
            mp.start()
        }
        binding.play4.setOnClickListener{
            val mp = MediaPlayer.create(this, R.raw.allahu_akbar)
            mp.start()
        }
        binding.play5.setOnClickListener{
            val mp = MediaPlayer.create(this, R.raw.subhanallah_wabi_hamdi)
            mp.start()
        }
        binding.play6.setOnClickListener{
            val mp = MediaPlayer.create(this, R.raw.la_ilaha_illa_allah)
            mp.start()
        }
        binding.play7.setOnClickListener{
            val mp = MediaPlayer.create(this, R.raw.la_haula)
            mp.start()
        }

    }

    private fun updateSwitchStatus(arr: List<AdhkarModel>) {
        binding.switch1.isChecked= arr[0].adhkarStatus
        binding.switch2.isChecked= arr[1].adhkarStatus
        binding.switch3.isChecked= arr[2].adhkarStatus
        binding.switch4.isChecked= arr[3].adhkarStatus
        binding.switch5.isChecked= arr[4].adhkarStatus
        binding.switch6.isChecked= arr[5].adhkarStatus
        binding.switch7.isChecked= arr[6].adhkarStatus
    }


}