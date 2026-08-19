package com.cry.manage.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cry.manage.data.model.XanhTrip
import kotlinx.coroutines.flow.Flow

@Dao
interface XanhTripDao {
    @Query("SELECT * FROM xanh_trips ORDER BY occurredAt DESC, id DESC")
    fun getAll(): Flow<List<XanhTrip>>

    @Insert
    suspend fun insert(trip: XanhTrip): Long
}
