package com.cry.manage.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.cry.manage.data.model.PlannedItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannedItemDao {
    @Query("SELECT * FROM planned_items ORDER BY dueDate ASC, id ASC")
    fun getAll(): Flow<List<PlannedItem>>

    @Insert
    suspend fun insert(item: PlannedItem): Long

    @Delete
    suspend fun delete(item: PlannedItem)
}
