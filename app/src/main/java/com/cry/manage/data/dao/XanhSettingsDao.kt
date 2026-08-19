package com.cry.manage.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cry.manage.data.model.XanhSettingsVersion
import kotlinx.coroutines.flow.Flow

@Dao
interface XanhSettingsDao {
    @Query("SELECT * FROM xanh_settings_versions ORDER BY effectiveFrom DESC, id DESC LIMIT 1")
    fun getLatest(): Flow<XanhSettingsVersion?>

    @Query("SELECT * FROM xanh_settings_versions ORDER BY effectiveFrom DESC, id DESC LIMIT 1")
    suspend fun getLatestOnce(): XanhSettingsVersion?

    @Insert
    suspend fun insert(settings: XanhSettingsVersion): Long
}
