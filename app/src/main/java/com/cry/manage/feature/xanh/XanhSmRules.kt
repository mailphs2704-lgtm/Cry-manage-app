package com.cry.manage.feature.xanh

import com.cry.manage.data.model.XanhSettingsVersion
import com.cry.manage.data.model.XanhTrip

data class WeeklyMilestone(
    val points: Int,
    val reward: Double
)

object XanhSmRules {
    val defaultSettings = XanhSettingsVersion(effectiveFrom = 0)

    fun pointsFor(
        settings: XanhSettingsVersion,
        serviceType: String,
        timeSlot: String
    ): Int = when (timeSlot) {
        XanhTrip.SLOT_MORNING -> when (serviceType) {
            XanhTrip.SERVICE_BIKE -> settings.morningBike
            XanhTrip.SERVICE_EXPRESS_FAST -> settings.morningFast
            XanhTrip.SERVICE_EXPRESS_2H -> settings.morning2h
            XanhTrip.SERVICE_FOOD -> settings.morningFood
            else -> 0
        }
        XanhTrip.SLOT_NOON -> when (serviceType) {
            XanhTrip.SERVICE_BIKE -> settings.noonBike
            XanhTrip.SERVICE_EXPRESS_FAST -> settings.noonFast
            XanhTrip.SERVICE_EXPRESS_2H -> settings.noon2h
            XanhTrip.SERVICE_FOOD -> settings.noonFood
            else -> 0
        }
        XanhTrip.SLOT_AFTERNOON -> when (serviceType) {
            XanhTrip.SERVICE_BIKE -> settings.afternoonBike
            XanhTrip.SERVICE_EXPRESS_FAST -> settings.afternoonFast
            XanhTrip.SERVICE_EXPRESS_2H -> settings.afternoon2h
            XanhTrip.SERVICE_FOOD -> settings.afternoonFood
            else -> 0
        }
        XanhTrip.SLOT_OFFPEAK -> when (serviceType) {
            XanhTrip.SERVICE_BIKE -> settings.offpeakBike
            XanhTrip.SERVICE_EXPRESS_FAST -> settings.offpeakFast
            XanhTrip.SERVICE_EXPRESS_2H -> settings.offpeak2h
            XanhTrip.SERVICE_FOOD -> settings.offpeakFood
            else -> 0
        }
        else -> 0
    }

    fun milestones(settings: XanhSettingsVersion): List<WeeklyMilestone> = listOf(
        WeeklyMilestone(settings.milestone1Points, settings.milestone1Reward),
        WeeklyMilestone(settings.milestone2Points, settings.milestone2Reward),
        WeeklyMilestone(settings.milestone3Points, settings.milestone3Reward)
    ).sortedBy { it.points }

    fun nextMilestone(settings: XanhSettingsVersion, totalPoints: Int): WeeklyMilestone? =
        milestones(settings).firstOrNull { totalPoints < it.points }

    fun achievedReward(settings: XanhSettingsVersion, totalPoints: Int): Double =
        milestones(settings).lastOrNull { totalPoints >= it.points }?.reward ?: 0.0

    fun slotLabel(settings: XanhSettingsVersion, slot: String): String = when (slot) {
        XanhTrip.SLOT_MORNING -> "Sáng ${settings.morningStart}-${settings.morningEnd}"
        XanhTrip.SLOT_NOON -> "Trưa ${settings.noonStart}-${settings.noonEnd}"
        XanhTrip.SLOT_AFTERNOON -> "Chiều ${settings.afternoonStart}-${settings.afternoonEnd}"
        XanhTrip.SLOT_OFFPEAK -> "Ngoài giờ"
        else -> slot
    }
}
