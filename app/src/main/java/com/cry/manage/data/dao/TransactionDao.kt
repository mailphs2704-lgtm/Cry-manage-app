package com.cry.manage.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cry.manage.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY occurredAt DESC, id DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = :type AND occurredAt BETWEEN :start AND :end")
    fun getTotalByTypeBetween(type: String, start: Long, end: Long): Flow<Double>
}
