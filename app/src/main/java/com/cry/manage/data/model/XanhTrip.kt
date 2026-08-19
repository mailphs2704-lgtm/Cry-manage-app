package com.cry.manage.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "xanh_trips")
data class XanhTrip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val revenue: Double,
    val netIncome: Double,
    val promotion: Double,
    val discountAmount: Double,
    val discountPercent: Double,
    val paymentType: String,
    val timeSlot: String,
    val points: Int,
    val driverWalletId: Long,
    val driverWalletName: String,
    val receiveWalletId: Long? = null,
    val receiveWalletName: String? = null,
    val occurredAt: Long,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val PAYMENT_CASH = "CASH"
        const val PAYMENT_BANK = "BANK"
        const val PAYMENT_WALLET_CARD = "WALLET_CARD"
    }
}
