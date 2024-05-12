package com.saius.dhikrreminder.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Adhkar")
data class AdhkarModel(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val adhkar : String,
    val adhkarStatus : Boolean)