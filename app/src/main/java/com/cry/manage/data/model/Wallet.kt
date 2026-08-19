package com.cry.manage.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallets")
data class Wallet(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Tên ví
    val name: String,

    // Mô tả ví
    val description: String = "",

    // Số dư hiện tại
    val balance: Double = 0.0,

    // Ví có đang khả dụng hay không
    val isAvailable: Boolean = true
)