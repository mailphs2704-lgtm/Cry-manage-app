package com.cry.manage.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planned_items")
data class PlannedItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val dueDate: Long,
    val amount: Double,
    val walletId: Long,
    val walletName: String,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val TYPE_EXPENSE = "EXPENSE"
        const val TYPE_INCOME = "INCOME"
    }
}
