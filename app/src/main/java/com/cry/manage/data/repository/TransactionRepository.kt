package com.cry.manage.data.repository

import androidx.room.withTransaction
import com.cry.manage.data.AppDatabase
import com.cry.manage.data.model.Transaction
import com.cry.manage.data.model.Wallet
import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val database: AppDatabase
) {
    private val transactionDao = database.transactionDao()
    private val walletDao = database.walletDao()

    fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions()

    fun getAllWallets(): Flow<List<Wallet>> =
        walletDao.getAllWallets()

    suspend fun addTransaction(
        wallet: Wallet,
        type: String,
        amount: Double,
        note: String,
        occurredAt: Long
    ) {
        require(amount > 0) { "Số tiền phải lớn hơn 0" }
        require(type == Transaction.TYPE_INCOME || type == Transaction.TYPE_EXPENSE) {
            "Loại giao dịch không hợp lệ"
        }

        database.withTransaction {
            transactionDao.insert(
                Transaction(
                    walletId = wallet.id,
                    walletName = wallet.name,
                    type = type,
                    amount = amount,
                    note = note,
                    occurredAt = occurredAt
                )
            )

            val newBalance = if (type == Transaction.TYPE_INCOME) {
                wallet.balance + amount
            } else {
                wallet.balance - amount
            }

            walletDao.updateBalance(
                id = wallet.id,
                balance = newBalance
            )
        }
    }
}
