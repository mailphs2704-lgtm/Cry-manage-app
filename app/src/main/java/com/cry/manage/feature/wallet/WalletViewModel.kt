package com.cry.manage.feature.wallet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cry.manage.data.AppDatabase
import com.cry.manage.data.model.Wallet
import com.cry.manage.data.repository.WalletRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WalletViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)

    private val repository = WalletRepository(
        database.walletDao()
    )

    val wallets: StateFlow<List<Wallet>> =
        repository.getAllWallets()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun addWallet(
        name: String,
        description: String,
        balance: Double,
        isAvailable: Boolean
    ) {
        viewModelScope.launch {

            val wallet = Wallet(
                name = name,
                description = description,
                balance = balance,
                isAvailable = isAvailable
            )

            repository.insertWallet(wallet)
        }
    }

    fun updateWallet(wallet: Wallet) {
        viewModelScope.launch {
            repository.updateWallet(wallet)
        }
    }

    fun deleteWallet(wallet: Wallet) {
        viewModelScope.launch {
            repository.deleteWallet(wallet)
        }
    }

    fun updateBalance(
        id: Long,
        balance: Double
    ) {
        viewModelScope.launch {
            repository.updateBalance(
                id = id,
                balance = balance
            )
        }
    }

    fun updateAvailability(
        id: Long,
        isAvailable: Boolean
    ) {
        viewModelScope.launch {
            repository.updateAvailability(
                id = id,
                isAvailable = isAvailable
            )
        }
    }
}