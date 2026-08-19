package com.cry.manage.feature.xanh

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cry.manage.data.AppDatabase
import com.cry.manage.data.model.Wallet
import com.cry.manage.data.model.XanhTrip
import com.cry.manage.data.repository.XanhTripRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class XanhSmViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = XanhTripRepository(
        AppDatabase.getDatabase(application)
    )

    val trips: StateFlow<List<XanhTrip>> = repository.getTrips().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val wallets: StateFlow<List<Wallet>> = repository.getWallets().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun addTrip(
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
        viewModelScope.launch {
            repository.addTrip(
                revenue = revenue,
                netIncome = netIncome,
                promotion = promotion,
                paymentType = paymentType,
                timeSlot = timeSlot,
                points = points,
                driverWallet = driverWallet,
                receiveWallet = receiveWallet,
                occurredAt = occurredAt
            )
        }
    }
}
