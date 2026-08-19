package com.cry.manage.data.repository

import androidx.room.withTransaction
import com.cry.manage.data.AppDatabase
import com.cry.manage.data.model.Transaction
import com.cry.manage.data.model.Wallet
import com.cry.manage.data.model.XanhSettingsVersion
import com.cry.manage.data.model.XanhTrip
import com.cry.manage.feature.xanh.XanhSmRules
import kotlinx.coroutines.flow.Flow

class XanhTripRepository(
    private val database: AppDatabase
) {
    private val tripDao = database.xanhTripDao()
    private val walletDao = database.walletDao()
    private val transactionDao = database.transactionDao()
    private val settingsDao = database.xanhSettingsDao()

    fun getTrips(): Flow<List<XanhTrip>> = tripDao.getAll()
    fun getWallets(): Flow<List<Wallet>> = walletDao.getAllWallets()
    fun getSettings(): Flow<XanhSettingsVersion?> = settingsDao.getLatest()

    suspend fun saveSettings(settings: XanhSettingsVersion) {
        validateSettings(settings)
        settingsDao.insert(
            settings.copy(
                id = 0,
                effectiveFrom = System.currentTimeMillis()
            )
        )
    }

    suspend fun addTrip(
        serviceType: String,
        revenue: Double,
        netIncome: Double,
        tips: Double,
        promotion: Double,
        foodCost: Double,
        paymentType: String,
        timeSlot: String,
        note: String,
        driverWallet: Wallet,
        receiveWallet: Wallet?,
        occurredAt: Long
    ) {
        validateTrip(
            serviceType = serviceType,
            revenue = revenue,
            netIncome = netIncome,
            tips = tips,
            promotion = promotion,
            foodCost = foodCost,
            paymentType = paymentType,
            timeSlot = timeSlot
        )

        val settings = settingsDao.getLatestOnce() ?: XanhSmRules.defaultSettings
        val discountAmount = revenue - netIncome
        val discountPercent = if (revenue > 0) discountAmount / revenue * 100.0 else 0.0
        val points = XanhSmRules.pointsFor(settings, serviceType, timeSlot)

        database.withTransaction {
            val driver = walletDao.getWalletById(driverWallet.id)
                ?: error("Ví tài xế không còn tồn tại")

            val targetWallet = when (paymentType) {
                XanhTrip.PAYMENT_WALLET_CARD -> driver
                else -> {
                    val requested = receiveWallet ?: error("Cần chọn ví nhận tiền")
                    walletDao.getWalletById(requested.id)
                        ?: error("Ví nhận tiền không còn tồn tại")
                }
            }

            val realEarning = netIncome + tips
            val transactionId = transactionDao.insert(
                Transaction(
                    walletId = targetWallet.id,
                    walletName = targetWallet.name,
                    type = Transaction.TYPE_INCOME,
                    amount = realEarning,
                    note = buildString {
                        append("[Xanh SM] ")
                        append(serviceLabel(serviceType))
                        append(" - Khách trả ")
                        append(revenue.toLong())
                        append(" (Ròng ")
                        append(netIncome.toLong())
                        if (tips > 0) {
                            append(" + Tips ")
                            append(tips.toLong())
                        }
                        append(")")
                    },
                    occurredAt = occurredAt
                )
            )

            walletDao.updateBalance(
                id = targetWallet.id,
                balance = targetWallet.balance + realEarning
            )

            tripDao.insert(
                XanhTrip(
                    serviceType = serviceType,
                    revenue = revenue,
                    netIncome = netIncome,
                    tips = tips,
                    promotion = promotion,
                    foodCost = foodCost,
                    discountAmount = discountAmount,
                    discountPercent = discountPercent,
                    paymentType = paymentType,
                    timeSlot = timeSlot,
                    points = points,
                    note = note.trim(),
                    driverWalletId = driver.id,
                    driverWalletName = driver.name,
                    receiveWalletId = targetWallet.id,
                    receiveWalletName = targetWallet.name,
                    syncedTransactionIds = transactionId.toString(),
                    occurredAt = occurredAt
                )
            )
        }
    }

    suspend fun deleteTrip(trip: XanhTrip) {
        database.withTransaction {
            val targetWalletId = trip.receiveWalletId ?: trip.driverWalletId
            val targetWallet = walletDao.getWalletById(targetWalletId)
                ?: error("Ví liên kết không còn tồn tại")

            val rollbackAmount = trip.netIncome + trip.tips
            walletDao.updateBalance(
                id = targetWallet.id,
                balance = targetWallet.balance - rollbackAmount
            )

            val linkedTransactionIds = trip.syncedTransactionIds
                .split(',')
                .mapNotNull { it.trim().toLongOrNull() }

            if (linkedTransactionIds.isNotEmpty()) {
                transactionDao.deleteByIds(linkedTransactionIds)
            }

            tripDao.delete(trip)
        }
    }

    private fun validateSettings(settings: XanhSettingsVersion) {
        fun validTime(value: String): Boolean = Regex("^(?:[01]\\d|2[0-3]):[0-5]\\d$").matches(value)
        fun minuteOfDay(value: String): Int {
            val parts = value.split(':')
            return parts[0].toInt() * 60 + parts[1].toInt()
        }

        val rawTimes = listOf(
            settings.morningStart, settings.morningEnd,
            settings.noonStart, settings.noonEnd,
            settings.afternoonStart, settings.afternoonEnd
        )
        require(rawTimes.all(::validTime)) { "Khung giờ phải đúng định dạng HH:mm" }

        val ranges = listOf(
            minuteOfDay(settings.morningStart) to minuteOfDay(settings.morningEnd),
            minuteOfDay(settings.noonStart) to minuteOfDay(settings.noonEnd),
            minuteOfDay(settings.afternoonStart) to minuteOfDay(settings.afternoonEnd)
        )
        require(ranges.all { it.first < it.second }) { "Giờ bắt đầu phải nhỏ hơn giờ kết thúc" }
        val sortedRanges = ranges.sortedBy { it.first }
        require(sortedRanges.zipWithNext().all { (a, b) -> a.second <= b.first }) {
            "Các khung giờ cao điểm không được chồng lấn"
        }

        val allPoints = listOf(
            settings.morningBike, settings.morningFast, settings.morning2h, settings.morningFood,
            settings.noonBike, settings.noonFast, settings.noon2h, settings.noonFood,
            settings.afternoonBike, settings.afternoonFast, settings.afternoon2h, settings.afternoonFood,
            settings.offpeakBike, settings.offpeakFast, settings.offpeak2h, settings.offpeakFood
        )
        require(allPoints.all { it >= 0 }) { "Điểm dịch vụ không được âm" }

        val milestones = listOf(
            settings.milestone1Points to settings.milestone1Reward,
            settings.milestone2Points to settings.milestone2Reward,
            settings.milestone3Points to settings.milestone3Reward
        )
        require(milestones.all { it.first > 0 && it.second >= 0 }) { "Mốc thưởng không hợp lệ" }
        require(milestones.map { it.first }.distinct().size == milestones.size) { "Mốc điểm không được trùng" }
    }

    private fun validateTrip(
        serviceType: String,
        revenue: Double,
        netIncome: Double,
        tips: Double,
        promotion: Double,
        foodCost: Double,
        paymentType: String,
        timeSlot: String
    ) {
        require(serviceType in setOf(
            XanhTrip.SERVICE_BIKE,
            XanhTrip.SERVICE_EXPRESS_FAST,
            XanhTrip.SERVICE_EXPRESS_2H,
            XanhTrip.SERVICE_FOOD
        )) { "Loại dịch vụ không hợp lệ" }
        require(revenue > 0) { "Doanh số phải lớn hơn 0" }
        require(netIncome >= 0 && netIncome <= revenue) { "Thu nhập ròng không hợp lệ" }
        require(tips >= 0) { "Tips không hợp lệ" }
        require(promotion >= 0 && promotion <= revenue) { "Khuyến mãi không hợp lệ" }
        require(foodCost >= 0) { "Tiền ứng Food không hợp lệ" }
        if (serviceType != XanhTrip.SERVICE_FOOD) {
            require(foodCost == 0.0) { "Tiền ứng Food chỉ áp dụng cho dịch vụ Food" }
        }
        require(paymentType in setOf(
            XanhTrip.PAYMENT_CASH,
            XanhTrip.PAYMENT_BANK,
            XanhTrip.PAYMENT_WALLET_CARD
        )) { "Kiểu thanh toán không hợp lệ" }
        require(timeSlot in setOf(
            XanhTrip.SLOT_MORNING,
            XanhTrip.SLOT_NOON,
            XanhTrip.SLOT_AFTERNOON,
            XanhTrip.SLOT_OFFPEAK
        )) { "Khung giờ không hợp lệ" }
    }

    private fun serviceLabel(serviceType: String): String = when (serviceType) {
        XanhTrip.SERVICE_BIKE -> "Bike"
        XanhTrip.SERVICE_EXPRESS_FAST -> "Express Siêu Tốc"
        XanhTrip.SERVICE_EXPRESS_2H -> "Express 2H"
        XanhTrip.SERVICE_FOOD -> "Food"
        else -> serviceType
    }
}
