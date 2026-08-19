package com.cry.manage.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "xanh_trips")
data class XanhTrip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serviceType: String = "BIKE",
    val revenue: Double,
    val netIncome: Double,
    val tips: Double = 0.0,
    val promotion: Double = 0.0,
    val foodCost: Double = 0.0,
    val discountAmount: Double,
    val discountPercent: Double,
    val paymentType: String,
    val timeSlot: String,
    val points: Int,
    val note: String = "",
    val driverWalletId: Long,
    val driverWalletName: String,
    val receiveWalletId: Long? = null,
    val receiveWalletName: String? = null,
    val syncedTransactionIds: String = "",
    val occurredAt: Long,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val SERVICE_BIKE = "BIKE"
        const val SERVICE_EXPRESS_FAST = "EXPRESS_FAST"
        const val SERVICE_EXPRESS_2H = "EXPRESS_2H"
        const val SERVICE_FOOD = "FOOD"

        const val SLOT_MORNING = "MORNING"
        const val SLOT_NOON = "NOON"
        const val SLOT_AFTERNOON = "AFTERNOON"
        const val SLOT_OFFPEAK = "OFFPEAK"

        const val PAYMENT_CASH = "CASH"
        const val PAYMENT_BANK = "BANK"
        const val PAYMENT_WALLET_CARD = "WALLET_CARD"
    }
}
