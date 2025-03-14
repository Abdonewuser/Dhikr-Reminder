package com.saius.dhikrreminder.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AdhkarModel::class], version = 4)
abstract class AdhkarDatabase : RoomDatabase() {

    abstract fun getDao(): DAO

    companion object{
        @Volatile
        private var instance: AdhkarDatabase? = null

        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also{
                instance = it
            }
        }
        private fun createDatabase(context: Context) = Room.databaseBuilder(context.applicationContext, AdhkarDatabase::class.java, "adhkarDb")
            .fallbackToDestructiveMigration()
            .build()

    }
}