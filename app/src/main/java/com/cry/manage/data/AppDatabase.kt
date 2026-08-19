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
import com.cry.manage.data.dao.XanhSettingsDao
import com.cry.manage.data.dao.XanhTripDao
import com.cry.manage.data.model.PlannedItem
import com.cry.manage.data.model.Transaction
import com.cry.manage.data.model.Wallet
import com.cry.manage.data.model.XanhSettingsVersion
import com.cry.manage.data.model.XanhTrip

@Database(
    entities = [Wallet::class, Transaction::class, PlannedItem::class, XanhTrip::class, XanhSettingsVersion::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun walletDao(): WalletDao
    abstract fun transactionDao(): TransactionDao
    abstract fun plannedItemDao(): PlannedItemDao
    abstract fun xanhTripDao(): XanhTripDao
    abstract fun xanhSettingsDao(): XanhSettingsDao

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

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE xanh_trips ADD COLUMN serviceType TEXT NOT NULL DEFAULT 'BIKE'")
                db.execSQL("ALTER TABLE xanh_trips ADD COLUMN tips REAL NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE xanh_trips ADD COLUMN foodCost REAL NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE xanh_trips ADD COLUMN note TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE xanh_trips ADD COLUMN syncedTransactionIds TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS xanh_settings_versions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        effectiveFrom INTEGER NOT NULL,
                        morningStart TEXT NOT NULL,
                        morningEnd TEXT NOT NULL,
                        noonStart TEXT NOT NULL,
                        noonEnd TEXT NOT NULL,
                        afternoonStart TEXT NOT NULL,
                        afternoonEnd TEXT NOT NULL,
                        morningBike INTEGER NOT NULL,
                        morningFast INTEGER NOT NULL,
                        morning2h INTEGER NOT NULL,
                        morningFood INTEGER NOT NULL,
                        noonBike INTEGER NOT NULL,
                        noonFast INTEGER NOT NULL,
                        noon2h INTEGER NOT NULL,
                        noonFood INTEGER NOT NULL,
                        afternoonBike INTEGER NOT NULL,
                        afternoonFast INTEGER NOT NULL,
                        afternoon2h INTEGER NOT NULL,
                        afternoonFood INTEGER NOT NULL,
                        offpeakBike INTEGER NOT NULL,
                        offpeakFast INTEGER NOT NULL,
                        offpeak2h INTEGER NOT NULL,
                        offpeakFood INTEGER NOT NULL,
                        milestone1Points INTEGER NOT NULL,
                        milestone1Reward REAL NOT NULL,
                        milestone2Points INTEGER NOT NULL,
                        milestone2Reward REAL NOT NULL,
                        milestone3Points INTEGER NOT NULL,
                        milestone3Reward REAL NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO xanh_settings_versions (
                        effectiveFrom, morningStart, morningEnd, noonStart, noonEnd, afternoonStart, afternoonEnd,
                        morningBike, morningFast, morning2h, morningFood,
                        noonBike, noonFast, noon2h, noonFood,
                        afternoonBike, afternoonFast, afternoon2h, afternoonFood,
                        offpeakBike, offpeakFast, offpeak2h, offpeakFood,
                        milestone1Points, milestone1Reward, milestone2Points, milestone2Reward, milestone3Points, milestone3Reward
                    ) VALUES (
                        0, '06:00', '09:00', '11:00', '13:30', '16:30', '19:30',
                        3, 4, 2, 2,
                        2, 3, 2, 4,
                        3, 4, 2, 2,
                        1, 2, 1, 1,
                        50, 50000, 100, 150000, 150, 300000
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
