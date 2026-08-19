package com.cry.manage.data

import android.content.Context
import com.cry.manage.data.model.PlannedItem
import com.cry.manage.data.model.Transaction
import com.cry.manage.data.model.Wallet
import com.cry.manage.data.model.XanhTrip
import kotlinx.coroutines.flow.first
import java.util.Calendar

object DemoDataSeeder {
    private const val PREFS = "cry_manage_demo"
    private const val KEY_SEEDED = "demo_seeded_v1"

    suspend fun seedIfNeeded(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_SEEDED, false)) return

        val db = AppDatabase.getDatabase(context)
        val walletDao = db.walletDao()
        val transactionDao = db.transactionDao()
        val plannedDao = db.plannedItemDao()
        val tripDao = db.xanhTripDao()

        var wallets = walletDao.getAllWallets().first()

        suspend fun ensureWallet(name: String, description: String, balance: Double, available: Boolean = true) {
            if (wallets.none { it.name == name }) {
                walletDao.insertWallet(
                    Wallet(
                        name = name,
                        description = description,
                        balance = balance,
                        isAvailable = available
                    )
                )
                wallets = walletDao.getAllWallets().first()
            }
        }

        ensureWallet("Ví tài xế", "Ví Xanh SM Bike", 420_000.0)
        ensureWallet("Tiền mặt", "Tiền mặt đang giữ", 1_250_000.0)
        ensureWallet("Ngân hàng", "Tài khoản nhận chuyển khoản", 3_800_000.0)
        ensureWallet("Tiết kiệm", "Ví không khả dụng để test", 5_000_000.0, available = false)

        val driver = wallets.first { it.name == "Ví tài xế" }
        val cash = wallets.first { it.name == "Tiền mặt" }
        val bank = wallets.first { it.name == "Ngân hàng" }

        val today = Calendar.getInstance()
        fun dayOffset(days: Int): Long {
            val c = today.clone() as Calendar
            c.add(Calendar.DAY_OF_YEAR, days)
            c.set(Calendar.HOUR_OF_DAY, 12)
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)
            return c.timeInMillis
        }

        val existingTransactions = transactionDao.getAllTransactions().first()
        if (existingTransactions.none { it.note.startsWith("[DEMO]") }) {
            val demoTransactions = listOf(
                Transaction(walletId = cash.id, walletName = cash.name, type = Transaction.TYPE_EXPENSE, amount = 85_000.0, note = "[DEMO] Ăn uống", occurredAt = dayOffset(-1)),
                Transaction(walletId = bank.id, walletName = bank.name, type = Transaction.TYPE_INCOME, amount = 650_000.0, note = "[DEMO] Thu nhập ngoài", occurredAt = dayOffset(-2)),
                Transaction(walletId = cash.id, walletName = cash.name, type = Transaction.TYPE_EXPENSE, amount = 120_000.0, note = "[DEMO] Xăng xe", occurredAt = dayOffset(-3)),
                Transaction(walletId = bank.id, walletName = bank.name, type = Transaction.TYPE_EXPENSE, amount = 350_000.0, note = "[DEMO] Mua sắm", occurredAt = dayOffset(-8)),
                Transaction(walletId = cash.id, walletName = cash.name, type = Transaction.TYPE_INCOME, amount = 300_000.0, note = "[DEMO] Thu tiền mặt", occurredAt = dayOffset(-15))
            )
            demoTransactions.forEach { transactionDao.insert(it) }
        }

        val existingPlanned = plannedDao.getAll().first()
        if (existingPlanned.none { it.note.startsWith("[DEMO]") }) {
            plannedDao.insert(
                PlannedItem(
                    type = PlannedItem.TYPE_EXPENSE,
                    dueDate = dayOffset(3),
                    amount = 900_000.0,
                    walletId = bank.id,
                    walletName = bank.name,
                    note = "[DEMO] Tiền nhà"
                )
            )
            plannedDao.insert(
                PlannedItem(
                    type = PlannedItem.TYPE_EXPENSE,
                    dueDate = dayOffset(7),
                    amount = 450_000.0,
                    walletId = cash.id,
                    walletName = cash.name,
                    note = "[DEMO] Bảo dưỡng xe"
                )
            )
            plannedDao.insert(
                PlannedItem(
                    type = PlannedItem.TYPE_INCOME,
                    dueDate = dayOffset(2),
                    amount = 700_000.0,
                    walletId = bank.id,
                    walletName = bank.name,
                    note = "[DEMO] Dự thu công việc"
                )
            )
            plannedDao.insert(
                PlannedItem(
                    type = PlannedItem.TYPE_EXPENSE,
                    dueDate = dayOffset(-2),
                    amount = 180_000.0,
                    walletId = cash.id,
                    walletName = cash.name,
                    note = "[DEMO] Khoản quá hạn để test"
                )
            )
        }

        val existingTrips = tripDao.getAll().first()
        if (existingTrips.isEmpty()) {
            val trips = listOf(
                demoTrip(120_000.0, 96_000.0, 20_000.0, XanhTrip.PAYMENT_CASH, "06:00 - 08:00", 8, driver, cash, dayOffset(0)),
                demoTrip(185_000.0, 148_000.0, 15_000.0, XanhTrip.PAYMENT_BANK, "08:00 - 11:00", 10, driver, bank, dayOffset(0)),
                demoTrip(95_000.0, 76_000.0, 0.0, XanhTrip.PAYMENT_WALLET_CARD, "11:00 - 14:00", 6, driver, null, dayOffset(-1)),
                demoTrip(210_000.0, 168_000.0, 30_000.0, XanhTrip.PAYMENT_CASH, "17:00 - 20:00", 12, driver, cash, dayOffset(-1)),
                demoTrip(160_000.0, 132_800.0, 10_000.0, XanhTrip.PAYMENT_BANK, "20:00 - 23:00", 9, driver, bank, dayOffset(-2)),
                demoTrip(135_000.0, 108_000.0, 25_000.0, XanhTrip.PAYMENT_CASH, "06:00 - 09:00", 7, driver, cash, dayOffset(-4)),
                demoTrip(250_000.0, 205_000.0, 20_000.0, XanhTrip.PAYMENT_WALLET_CARD, "17:00 - 21:00", 15, driver, null, dayOffset(-6)),
                demoTrip(175_000.0, 140_000.0, 0.0, XanhTrip.PAYMENT_BANK, "09:00 - 12:00", 10, driver, bank, dayOffset(-9))
            )
            trips.forEach { tripDao.insert(it) }
        }

        prefs.edit().putBoolean(KEY_SEEDED, true).apply()
    }

    private fun demoTrip(
        revenue: Double,
        netIncome: Double,
        promotion: Double,
        paymentType: String,
        timeSlot: String,
        points: Int,
        driver: Wallet,
        receive: Wallet?,
        occurredAt: Long
    ): XanhTrip {
        val discount = revenue - netIncome
        return XanhTrip(
            revenue = revenue,
            netIncome = netIncome,
            promotion = promotion,
            discountAmount = discount,
            discountPercent = if (revenue > 0) discount / revenue * 100.0 else 0.0,
            paymentType = paymentType,
            timeSlot = timeSlot,
            points = points,
            driverWalletId = driver.id,
            driverWalletName = driver.name,
            receiveWalletId = receive?.id,
            receiveWalletName = receive?.name,
            occurredAt = occurredAt
        )
    }
}
