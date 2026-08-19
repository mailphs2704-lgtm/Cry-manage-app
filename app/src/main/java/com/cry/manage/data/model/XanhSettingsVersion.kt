package com.cry.manage.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "xanh_settings_versions")
data class XanhSettingsVersion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val effectiveFrom: Long = System.currentTimeMillis(),

    val morningStart: String = "06:00",
    val morningEnd: String = "09:00",
    val noonStart: String = "11:00",
    val noonEnd: String = "13:30",
    val afternoonStart: String = "16:30",
    val afternoonEnd: String = "19:30",

    val morningBike: Int = 3,
    val morningFast: Int = 4,
    val morning2h: Int = 2,
    val morningFood: Int = 2,

    val noonBike: Int = 2,
    val noonFast: Int = 3,
    val noon2h: Int = 2,
    val noonFood: Int = 4,

    val afternoonBike: Int = 3,
    val afternoonFast: Int = 4,
    val afternoon2h: Int = 2,
    val afternoonFood: Int = 2,

    val offpeakBike: Int = 1,
    val offpeakFast: Int = 2,
    val offpeak2h: Int = 1,
    val offpeakFood: Int = 1,

    // Giữ lại 3 mốc cũ để migration và dữ liệu lịch sử tương thích.
    val milestone1Points: Int = 50,
    val milestone1Reward: Double = 50_000.0,
    val milestone2Points: Int = 100,
    val milestone2Reward: Double = 150_000.0,
    val milestone3Points: Int = 150,
    val milestone3Reward: Double = 300_000.0,

    // Danh sách mốc động, ví dụ: "50:50000|100:150000|150:300000|200:500000".
    // Cấu hình cũ có giá trị rỗng sẽ tự fallback về 3 mốc phía trên.
    val milestonesData: String = ""
)
