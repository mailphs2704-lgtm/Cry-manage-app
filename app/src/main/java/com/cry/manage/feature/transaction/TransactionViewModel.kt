package com.cry.manage.feature.transaction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cry.manage.data.AppDatabase
import com.cry.manage.data.model.Transaction
import com.cry.manage.data.model.Wallet
import com.cry.manage.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TransactionRepository(
        AppDatabase.getDatabase(application)
    )

    val transactions: StateFlow<List<Transaction>> =
        repository.getAllTransactions().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val wallets: StateFlow<List<Wallet>> =
        repository.getAllWallets().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addTransaction(
        wallet: Wallet,
        type: String,
        amount: Double,
        note: String,
        occurredAt: Long
    ) {
        viewModelScope.launch {
            repository.addTransaction(
                wallet = wallet,
                type = type,
                amount = amount,
                note = note,
                occurredAt = occurredAt
            )
        }
    }
}
