package com.saius.dhikrreminder.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface DAO {

    @Insert
    suspend fun insert(adhkarModel: AdhkarModel)

    @Update
    fun update(adhkarModel: AdhkarModel)

    @Query("Select * from Adhkar")
    fun getAdhkarData() : LiveData<List<AdhkarModel>>

    @Query("Select * from Adhkar where adhkarStatus = 1")
    fun getAdhkarDataInList() : List<AdhkarModel>
}