package com.cry.manage.data.repository

import com.cry.manage.data.dao.WalletDao
import com.cry.manage.data.model.Wallet
import kotlinx.coroutines.flow.Flow

class WalletRepository(
    private val walletDao: WalletDao
) {

    // Lấy toàn bộ ví
    fun getAllWallets(): Flow<List<Wallet>> {
        return walletDao.getAllWallets()
    }

    // Lấy một ví theo ID
    suspend fun getWalletById(id: Long): Wallet? {
        return walletDao.getWalletById(id)
    }

    // Thêm ví
    suspend fun insertWallet(wallet: Wallet) {
        walletDao.insertWallet(wallet)
    }

    // Cập nhật ví
    suspend fun updateWallet(wallet: Wallet) {
        walletDao.updateWallet(wallet)
    }

    // Xóa ví
    suspend fun deleteWallet(wallet: Wallet) {
        walletDao.deleteWallet(wallet)
    }

    // Cập nhật số dư
    suspend fun updateBalance(
        id: Long,
        balance: Double
    ) {
        walletDao.updateBalance(id, balance)
    }

    // Bật / tắt khả dụng
    suspend fun updateAvailability(
        id: Long,
        isAvailable: Boolean
    ) {
        walletDao.updateAvailability(id, isAvailable)
    }
}