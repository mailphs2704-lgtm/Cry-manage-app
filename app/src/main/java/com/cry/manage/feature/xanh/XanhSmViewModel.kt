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
        viewModelScope.launch {
            repository.addTrip(
                serviceType = serviceType,
                revenue = revenue,
                netIncome = netIncome,
                tips = tips,
                promotion = promotion,
                foodCost = foodCost,
                paymentType = paymentType,
                timeSlot = timeSlot,
                note = note,
                driverWallet = driverWallet,
                receiveWallet = receiveWallet,
                occurredAt = occurredAt
            )
        }
    }

    fun deleteTrip(trip: XanhTrip) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }
}
