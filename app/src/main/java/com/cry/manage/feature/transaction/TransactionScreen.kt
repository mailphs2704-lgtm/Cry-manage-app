package com.cry.manage.feature.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cry.manage.data.model.Transaction
import com.cry.manage.data.model.Wallet
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CryRed = Color(0xFFC9233B)
private val CrySoft = Color(0xFFFFEEF1)
private val CryText = Color(0xFF28242A)
private val CryMuted = Color(0xFF766C70)
private val IncomeGreen = Color(0xFF17865C)
private val IncomeSoft = Color(0xFFE8F6F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    onBack: () -> Unit,
    viewModel: TransactionViewModel = viewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val totalIncome = transactions
        .filter { it.type == Transaction.TYPE_INCOME }
        .sumOf { it.amount }
    val totalExpense = transactions
        .filter { it.type == Transaction.TYPE_EXPENSE }
        .sumOf { it.amount }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, CrySoft)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = CryRed
                            )
                        }
                    },
                    title = {
                        Column {
                            Text(
                                text = "Quản lý thu chi",
                                color = CryText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp
                            )
                            Text(
                                text = "Theo dõi dòng tiền thực tế",
                                color = CryMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = CryRed,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 5.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm giao dịch")
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(Modifier.height(2.dp)) }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Tổng thu",
                            amount = totalIncome,
                            positive = true
                        )
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Tổng chi",
                            amount = totalExpense,
                            positive = false
                        )
                    }
                }

                item {
                    Text(
                        text = "Lịch sử giao dịch",
                        color = CryText,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (transactions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Chưa có giao dịch nào",
                                    color = CryText,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(5.dp))
                                Text(
                                    text = "Nhấn nút + để nhập khoản thu hoặc chi.",
                                    color = CryMuted,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                } else {
                    items(transactions, key = { it.id }) { transaction ->
                        TransactionItem(transaction)
                    }
                }

                item { Spacer(Modifier.height(88.dp)) }
            }
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            wallets = wallets,
            onDismiss = { showAddDialog = false },
            onConfirm = { wallet, type, amount, note, occurredAt ->
                viewModel.addTransaction(wallet, type, amount, note, occurredAt)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier,
    title: String,
    amount: Double,
    positive: Boolean
) {
    val accent = if (positive) IncomeGreen else CryRed
    val soft = if (positive) IncomeSoft else CrySoft

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(soft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (positive) Icons.Default.SouthWest else Icons.Default.NorthEast,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(21.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = title,
                color = CryMuted,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = formatCurrency(amount),
                color = accent,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    val isIncome = transaction.type == Transaction.TYPE_INCOME
    val accent = if (isIncome) IncomeGreen else CryRed
    val soft = if (isIncome) IncomeSoft else CrySoft

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(soft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncome) Icons.Default.SouthWest else Icons.Default.NorthEast,
                    contentDescription = null,
                    tint = accent
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = if (isIncome) "Khoản thu" else "Khoản chi",
                    color = CryText,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.walletName,
                    color = CryMuted,
                    style = MaterialTheme.typography.bodySmall
                )
                if (transaction.note.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = transaction.note,
                        color = CryMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (isIncome) "+" else "-") + formatCurrency(transaction.amount),
                    color = accent,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = formatDate(transaction.occurredAt),
                    color = CryMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun AddTransactionDialog(
    wallets: List<Wallet>,
    onDismiss: () -> Unit,
    onConfirm: (Wallet, String, Double, String, Long) -> Unit
) {
    var type by remember { mutableStateOf(Transaction.TYPE_EXPENSE) }
    var selectedWallet by remember(wallets) { mutableStateOf(wallets.firstOrNull()) }
    var walletMenuExpanded by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(formatDate(System.currentTimeMillis())) }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Thêm giao dịch",
                color = CryText,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { type = Transaction.TYPE_INCOME },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == Transaction.TYPE_INCOME) IncomeGreen else IncomeSoft,
                            contentColor = if (type == Transaction.TYPE_INCOME) Color.White else IncomeGreen
                        )
                    ) { Text("Thu") }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { type = Transaction.TYPE_EXPENSE },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == Transaction.TYPE_EXPENSE) CryRed else CrySoft,
                            contentColor = if (type == Transaction.TYPE_EXPENSE) Color.White else CryRed
                        )
                    ) { Text("Chi") }
                }

                if (wallets.isEmpty()) {
                    Text(
                        text = "Bạn cần tạo ít nhất một ví trước khi nhập giao dịch.",
                        color = CryRed
                    )
                } else {
                    Column {
                        Text(
                            text = "Ví phát sinh",
                            color = CryMuted,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(5.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { walletMenuExpanded = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CrySoft)
                        ) {
                            Text(
                                text = selectedWallet?.name ?: "Chọn ví",
                                color = CryText,
                                modifier = Modifier.padding(13.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = walletMenuExpanded,
                            onDismissRequest = { walletMenuExpanded = false }
                        ) {
                            wallets.forEach { wallet ->
                                DropdownMenuItem(
                                    text = { Text(wallet.name) },
                                    onClick = {
                                        selectedWallet = wallet
                                        walletMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Số tiền") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Ngày (dd/MM/yyyy)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ghi chú") },
                    modifier = Modifier.fillMaxWidth()
                )
                errorText?.let {
                    Text(text = it, color = CryRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = wallets.isNotEmpty(),
                onClick = {
                    val wallet = selectedWallet
                    val amount = amountText.replace(",", "").replace(".", "").toDoubleOrNull()
                    val occurredAt = parseDate(dateText)
                    when {
                        wallet == null -> errorText = "Hãy chọn ví."
                        amount == null || amount <= 0 -> errorText = "Số tiền không hợp lệ."
                        occurredAt == null -> errorText = "Ngày không hợp lệ."
                        else -> onConfirm(wallet, type, amount, note.trim(), occurredAt)
                    }
                }
            ) {
                Text("LƯU", color = CryRed, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("HỦY", color = CryMuted)
            }
        }
    )
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    formatter.maximumFractionDigits = 0
    return "${formatter.format(amount)} đ"
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).format(Date(timestamp))

private fun parseDate(value: String): Long? = try {
    SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).apply {
        isLenient = false
    }.parse(value)?.time
} catch (_: Exception) {
    null
}
