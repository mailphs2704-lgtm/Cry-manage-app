package com.cry.manage.feature.planned

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cry.manage.data.AppDatabase
import com.cry.manage.data.model.PlannedItem
import com.cry.manage.data.model.Wallet
import com.cry.manage.data.repository.PlannedRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlannedViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PlannedRepository(
        AppDatabase.getDatabase(application)
    )

    val items: StateFlow<List<PlannedItem>> = repository.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val wallets: StateFlow<List<Wallet>> = repository.getWallets().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun add(
        type: String,
        dueDate: Long,
        amount: Double,
        wallet: Wallet,
        note: String
    ) {
        viewModelScope.launch {
            repository.add(
                PlannedItem(
                    type = type,
                    dueDate = dueDate,
                    amount = amount,
                    walletId = wallet.id,
                    walletName = wallet.name,
                    note = note.trim()
                )
            )
        }
    }

    fun delete(item: PlannedItem) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun approve(item: PlannedItem, actualAmount: Double) {
        viewModelScope.launch {
            repository.approve(item, actualAmount)
        }
    }
}
