package com.cry.manage.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cry.manage.data.dao.PlannedItemDao
import com.cry.manage.data.dao.TransactionDao
import com.cry.manage.data.dao.WalletDao
import com.cry.manage.data.dao.XanhTripDao
import com.cry.manage.data.model.PlannedItem
import com.cry.manage.data.model.Transaction
import com.cry.manage.data.model.Wallet
import com.cry.manage.data.model.XanhTrip

@Database(
    entities = [Wallet::class, Transaction::class, PlannedItem::class, XanhTrip::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun walletDao(): WalletDao
    abstract fun transactionDao(): TransactionDao
    abstract fun plannedItemDao(): PlannedItemDao
    abstract fun xanhTripDao(): XanhTripDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS transactions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        walletId INTEGER NOT NULL,
                        walletName TEXT NOT NULL,
                        type TEXT NOT NULL,
                        amount REAL NOT NULL,
                        note TEXT NOT NULL,
                        occurredAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS planned_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        type TEXT NOT NULL,
                        dueDate INTEGER NOT NULL,
                        amount REAL NOT NULL,
                        walletId INTEGER NOT NULL,
                        walletName TEXT NOT NULL,
                        note TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS xanh_trips (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        revenue REAL NOT NULL,
                        netIncome REAL NOT NULL,
                        promotion REAL NOT NULL,
                        discountAmount REAL NOT NULL,
                        discountPercent REAL NOT NULL,
                        paymentType TEXT NOT NULL,
                        timeSlot TEXT NOT NULL,
                        points INTEGER NOT NULL,
                        driverWalletId INTEGER NOT NULL,
                        driverWalletName TEXT NOT NULL,
                        receiveWalletId INTEGER,
                        receiveWalletName TEXT,
                        occurredAt INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cry_manage_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
