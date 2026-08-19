package com.cry.manage.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val walletId: Long,
    val walletName: String,
    val type: String,
    val amount: Double,
    val note: String = "",
    val occurredAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val TYPE_INCOME = "INCOME"
        const val TYPE_EXPENSE = "EXPENSE"
    }
}
