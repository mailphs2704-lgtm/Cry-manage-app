package com.cry.manage.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cry.manage.data.model.XanhTrip
import kotlinx.coroutines.flow.Flow

@Dao
interface XanhTripDao {
    @Query("SELECT * FROM xanh_trips ORDER BY occurredAt DESC, id DESC")
    fun getAll(): Flow<List<XanhTrip>>

    @Insert
    suspend fun insert(trip: XanhTrip): Long

    @Update
    suspend fun update(trip: XanhTrip)

    @Delete
    suspend fun delete(trip: XanhTrip)
}
