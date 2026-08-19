package com.cry.manage.data.repository

import androidx.room.withTransaction
import com.cry.manage.data.AppDatabase
import com.cry.manage.data.model.PlannedItem
import com.cry.manage.data.model.Transaction
import com.cry.manage.data.model.Wallet
import kotlinx.coroutines.flow.Flow

class PlannedRepository(
    private val database: AppDatabase
) {
    private val plannedDao = database.plannedItemDao()
    private val walletDao = database.walletDao()
    private val transactionDao = database.transactionDao()

    fun getAll(): Flow<List<PlannedItem>> = plannedDao.getAll()
    fun getWallets(): Flow<List<Wallet>> = walletDao.getAllWallets()

    suspend fun add(item: PlannedItem) {
        plannedDao.insert(item)
    }

    suspend fun delete(item: PlannedItem) {
        plannedDao.delete(item)
    }

    suspend fun approve(item: PlannedItem, actualAmount: Double) {
        require(actualAmount > 0)

        database.withTransaction {
            val wallet = walletDao.getWalletById(item.walletId)
                ?: error("Ví đã chọn không còn tồn tại")

            val transactionType = if (item.type == PlannedItem.TYPE_EXPENSE) {
                Transaction.TYPE_EXPENSE
            } else {
                Transaction.TYPE_INCOME
            }

            transactionDao.insert(
                Transaction(
                    walletId = wallet.id,
                    walletName = wallet.name,
                    type = transactionType,
                    amount = actualAmount,
                    note = if (item.note.isBlank()) "Duyệt dự chi/dự thu" else item.note,
                    occurredAt = System.currentTimeMillis()
                )
            )

            val newBalance = if (transactionType == Transaction.TYPE_INCOME) {
                wallet.balance + actualAmount
            } else {
                wallet.balance - actualAmount
            }

            walletDao.updateBalance(wallet.id, newBalance)
            plannedDao.delete(item)
        }
    }
}
