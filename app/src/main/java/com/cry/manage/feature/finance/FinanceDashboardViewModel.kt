package com.cry.manage.feature.finance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cry.manage.data.AppDatabase
import com.cry.manage.data.model.PlannedItem
import com.cry.manage.data.model.Transaction
import com.cry.manage.data.model.Wallet
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import kotlin.math.ceil
import kotlin.math.max

class FinanceDashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)

    val state: StateFlow<FinanceDashboardState> = combine(
        database.walletDao().getAllWallets(),
        database.transactionDao().getAllTransactions(),
        database.plannedItemDao().getAll()
    ) { wallets, transactions, plannedItems ->
        buildState(wallets, transactions, plannedItems)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FinanceDashboardState()
    )

    private fun buildState(
        wallets: List<Wallet>,
        transactions: List<Transaction>,
        plannedItems: List<PlannedItem>
    ): FinanceDashboardState {
        val now = System.currentTimeMillis()
        val availableWallets = wallets.filter { it.isAvailable }
        val totalBalance = wallets.sumOf { it.balance }
        val availableBalance = availableWallets.sumOf { it.balance }

        val plannedExpenses = plannedItems.filter { it.type == PlannedItem.TYPE_EXPENSE }
        val plannedIncomes = plannedItems.filter { it.type == PlannedItem.TYPE_INCOME }
        val totalPlannedExpense = plannedExpenses.sumOf { it.amount }
        val totalPlannedIncome = plannedIncomes.sumOf { it.amount }
        val fundingGap = max(0.0, totalPlannedExpense - availableBalance - totalPlannedIncome)
        val dailyTarget = calculateDailyTarget(
            availableBalance = availableBalance,
            expenses = plannedExpenses,
            incomes = plannedIncomes,
            now = now
        )

        return FinanceDashboardState(
            wallets = wallets,
            availableWallets = availableWallets,
            totalBalance = totalBalance,
            availableBalance = availableBalance,
            totalPlannedExpense = totalPlannedExpense,
            totalPlannedIncome = totalPlannedIncome,
            fundingGap = fundingGap,
            dailyTarget = dailyTarget,
            day = calculatePeriodStats(transactions, startOfDay(now), now),
            week = calculatePeriodStats(transactions, startOfWeek(now), now),
            month = calculatePeriodStats(transactions, startOfMonth(now), now)
        )
    }

    private fun calculatePeriodStats(
        transactions: List<Transaction>,
        start: Long,
        end: Long
    ): PeriodStats {
        val periodTransactions = transactions.filter { it.occurredAt in start..end }
        return PeriodStats(
            income = periodTransactions
                .filter { it.type == Transaction.TYPE_INCOME }
                .sumOf { it.amount },
            expense = periodTransactions
                .filter { it.type == Transaction.TYPE_EXPENSE }
                .sumOf { it.amount }
        )
    }

    private fun calculateDailyTarget(
        availableBalance: Double,
        expenses: List<PlannedItem>,
        incomes: List<PlannedItem>,
        now: Long
    ): Double {
        if (expenses.isEmpty()) return 0.0

        val todayStart = startOfDay(now)
        val sortedExpenses = expenses.sortedBy { it.dueDate }
        var cumulativeExpense = 0.0
        var requiredDailyIncome = 0.0

        sortedExpenses.forEach { expense ->
            cumulativeExpense += expense.amount
            val incomeAvailableByDeadline = incomes
                .filter { it.dueDate <= expense.dueDate }
                .sumOf { it.amount }

            val deficitByDeadline = max(
                0.0,
                cumulativeExpense - availableBalance - incomeAvailableByDeadline
            )

            val daysUntilDeadline = max(
                1,
                ceil((expense.dueDate - todayStart).toDouble() / DAY_MILLIS).toInt() + 1
            )

            requiredDailyIncome = max(
                requiredDailyIncome,
                deficitByDeadline / daysUntilDeadline
            )
        }

        return requiredDailyIncome
    }

    private fun startOfDay(time: Long): Long = Calendar.getInstance().run {
        timeInMillis = time
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        timeInMillis
    }

    private fun startOfWeek(time: Long): Long = Calendar.getInstance().run {
        timeInMillis = time
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        timeInMillis
    }

    private fun startOfMonth(time: Long): Long = Calendar.getInstance().run {
        timeInMillis = time
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        timeInMillis
    }

    private companion object {
        const val DAY_MILLIS = 86_400_000.0
    }
}

data class FinanceDashboardState(
    val wallets: List<Wallet> = emptyList(),
    val availableWallets: List<Wallet> = emptyList(),
    val totalBalance: Double = 0.0,
    val availableBalance: Double = 0.0,
    val totalPlannedExpense: Double = 0.0,
    val totalPlannedIncome: Double = 0.0,
    val fundingGap: Double = 0.0,
    val dailyTarget: Double = 0.0,
    val day: PeriodStats = PeriodStats(),
    val week: PeriodStats = PeriodStats(),
    val month: PeriodStats = PeriodStats()
)

data class PeriodStats(
    val income: Double = 0.0,
    val expense: Double = 0.0
)
