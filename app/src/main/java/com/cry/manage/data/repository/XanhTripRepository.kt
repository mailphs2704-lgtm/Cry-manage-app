package com.cry.manage.data.repository

import androidx.room.withTransaction
import com.cry.manage.data.AppDatabase
import com.cry.manage.data.model.Transaction
import com.cry.manage.data.model.Wallet
import com.cry.manage.data.model.XanhTrip
import kotlinx.coroutines.flow.Flow

class XanhTripRepository(
    private val database: AppDatabase
) {
    private val tripDao = database.xanhTripDao()
    private val walletDao = database.walletDao()
    private val transactionDao = database.transactionDao()

    fun getTrips(): Flow<List<XanhTrip>> = tripDao.getAll()
    fun getWallets(): Flow<List<Wallet>> = walletDao.getAllWallets()

    suspend fun addTrip(
        revenue: Double,
        netIncome: Double,
        promotion: Double,
        paymentType: String,
        timeSlot: String,
        points: Int,
        driverWallet: Wallet,
        receiveWallet: Wallet?,
        occurredAt: Long
    ) {
        require(revenue > 0) { "Doanh số phải lớn hơn 0" }
        require(netIncome >= 0 && netIncome <= revenue) { "Thu nhập ròng không hợp lệ" }
        require(promotion >= 0 && promotion <= revenue) { "Khuyến mãi không hợp lệ" }
        require(paymentType in setOf(
            XanhTrip.PAYMENT_CASH,
            XanhTrip.PAYMENT_BANK,
            XanhTrip.PAYMENT_WALLET_CARD
        )) { "Kiểu thanh toán không hợp lệ" }

        if (paymentType != XanhTrip.PAYMENT_WALLET_CARD) {
            require(receiveWallet != null) { "Cần chọn ví nhận tiền" }
        }

        val discountAmount = revenue - netIncome
        val discountPercent = if (revenue == 0.0) 0.0 else discountAmount / revenue * 100.0

        database.withTransaction {
            val driver = walletDao.getWalletById(driverWallet.id)
                ?: error("Ví tài xế không còn tồn tại")
            val receiver = receiveWallet?.let {
                walletDao.getWalletById(it.id)
                    ?: error("Ví nhận tiền không còn tồn tại")
            }

            tripDao.insert(
                XanhTrip(
                    revenue = revenue,
                    netIncome = netIncome,
                    promotion = promotion,
                    discountAmount = discountAmount,
                    discountPercent = discountPercent,
                    paymentType = paymentType,
                    timeSlot = timeSlot.trim(),
                    points = points,
                    driverWalletId = driver.id,
                    driverWalletName = driver.name,
                    receiveWalletId = receiver?.id,
                    receiveWalletName = receiver?.name,
                    occurredAt = occurredAt
                )
            )

            val deltas = linkedMapOf<Long, Double>()
            fun addDelta(walletId: Long, amount: Double) {
                deltas[walletId] = (deltas[walletId] ?: 0.0) + amount
            }

            when (paymentType) {
                XanhTrip.PAYMENT_CASH,
                XanhTrip.PAYMENT_BANK -> {
                    val customerPaid = revenue - promotion
                    if (customerPaid > 0 && receiver != null) {
                        addDelta(receiver.id, customerPaid)
                        transactionDao.insert(
                            Transaction(
                                walletId = receiver.id,
                                walletName = receiver.name,
                                type = Transaction.TYPE_INCOME,
                                amount = customerPaid,
                                note = "Xanh SM - khách thanh toán",
                                occurredAt = occurredAt
                            )
                        )
                    }

                    if (promotion > 0) {
                        addDelta(driver.id, promotion)
                        transactionDao.insert(
                            Transaction(
                                walletId = driver.id,
                                walletName = driver.name,
                                type = Transaction.TYPE_INCOME,
                                amount = promotion,
                                note = "Xanh SM - hoàn khuyến mãi",
                                occurredAt = occurredAt
                            )
                        )
                    }
                }

                XanhTrip.PAYMENT_WALLET_CARD -> {
                    addDelta(driver.id, revenue)
                    transactionDao.insert(
                        Transaction(
                            walletId = driver.id,
                            walletName = driver.name,
                            type = Transaction.TYPE_INCOME,
                            amount = revenue,
                            note = "Xanh SM - doanh số thẻ ví",
                            occurredAt = occurredAt
                        )
                    )
                }
            }

            if (discountAmount > 0) {
                addDelta(driver.id, -discountAmount)
                transactionDao.insert(
                    Transaction(
                        walletId = driver.id,
                        walletName = driver.name,
                        type = Transaction.TYPE_EXPENSE,
                        amount = discountAmount,
                        note = "Xanh SM - chiết khấu chuyến",
                        occurredAt = occurredAt
                    )
                )
            }

            for ((walletId, delta) in deltas) {
                val wallet = walletDao.getWalletById(walletId)
                    ?: error("Ví không còn tồn tại")
                walletDao.updateBalance(
                    id = walletId,
                    balance = wallet.balance + delta
                )
            }
        }
    }
}
