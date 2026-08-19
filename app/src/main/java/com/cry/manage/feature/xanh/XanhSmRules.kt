package com.cry.manage.feature.xanh

import com.cry.manage.data.model.XanhTrip

data class WeeklyMilestone(
    val points: Int,
    val reward: Double
)

object XanhSmRules {
    val weeklyMilestones = listOf(
        WeeklyMilestone(points = 50, reward = 50_000.0),
        WeeklyMilestone(points = 100, reward = 150_000.0),
        WeeklyMilestone(points = 150, reward = 300_000.0)
    )

    fun pointsFor(serviceType: String, timeSlot: String): Int = when (timeSlot) {
        XanhTrip.SLOT_MORNING -> when (serviceType) {
            XanhTrip.SERVICE_BIKE -> 3
            XanhTrip.SERVICE_EXPRESS_FAST -> 4
            XanhTrip.SERVICE_EXPRESS_2H -> 2
            XanhTrip.SERVICE_FOOD -> 2
            else -> 0
        }
        XanhTrip.SLOT_NOON -> when (serviceType) {
            XanhTrip.SERVICE_BIKE -> 2
            XanhTrip.SERVICE_EXPRESS_FAST -> 3
            XanhTrip.SERVICE_EXPRESS_2H -> 2
            XanhTrip.SERVICE_FOOD -> 4
            else -> 0
        }
        XanhTrip.SLOT_AFTERNOON -> when (serviceType) {
            XanhTrip.SERVICE_BIKE -> 3
            XanhTrip.SERVICE_EXPRESS_FAST -> 4
            XanhTrip.SERVICE_EXPRESS_2H -> 2
            XanhTrip.SERVICE_FOOD -> 2
            else -> 0
        }
        XanhTrip.SLOT_OFFPEAK -> when (serviceType) {
            XanhTrip.SERVICE_BIKE -> 1
            XanhTrip.SERVICE_EXPRESS_FAST -> 2
            XanhTrip.SERVICE_EXPRESS_2H -> 1
            XanhTrip.SERVICE_FOOD -> 1
            else -> 0
        }
        else -> 0
    }

    fun nextMilestone(totalPoints: Int): WeeklyMilestone? =
        weeklyMilestones.firstOrNull { totalPoints < it.points }

    fun achievedReward(totalPoints: Int): Double =
        weeklyMilestones.lastOrNull { totalPoints >= it.points }?.reward ?: 0.0
}
