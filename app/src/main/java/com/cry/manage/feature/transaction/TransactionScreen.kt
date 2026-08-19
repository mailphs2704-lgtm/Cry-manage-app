package com.cry.manage.feature.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cry.manage.data.model.Transaction
import com.cry.manage.data.model.Wallet
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    onBack: () -> Unit,
    viewModel: TransactionViewModel = viewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                title = { Text("Quản lý thu chi") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm giao dịch")
            }
        }
    ) { paddingValues ->
        if (transactions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text("Chưa có giao dịch nào.")
                Spacer(Modifier.height(6.dp))
                Text(
                    "Nhấn nút + để nhập khoản thu hoặc chi.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { Spacer(Modifier.height(4.dp)) }
                items(transactions, key = { it.id }) { transaction ->
                    TransactionItem(transaction)
                }
                item { Spacer(Modifier.height(80.dp)) }
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
private fun TransactionItem(transaction: Transaction) {
    val isIncome = transaction.type == Transaction.TYPE_INCOME
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isIncome) "Thu" else "Chi",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = transaction.walletName,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = (if (isIncome) "+" else "-") + formatCurrency(transaction.amount),
                    fontWeight = FontWeight.Bold
                )
            }
            if (transaction.note.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(transaction.note)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = formatDate(transaction.occurredAt),
                style = MaterialTheme.typography.bodySmall
            )
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
        title = { Text("Thêm giao dịch") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { type = Transaction.TYPE_INCOME },
                        enabled = type != Transaction.TYPE_INCOME
                    ) { Text("Thu") }
                    Button(
                        onClick = { type = Transaction.TYPE_EXPENSE },
                        enabled = type != Transaction.TYPE_EXPENSE
                    ) { Text("Chi") }
                }

                if (wallets.isEmpty()) {
                    Text("Bạn cần tạo ít nhất một ví trước khi nhập giao dịch.")
                } else {
                    Column {
                        Text("Ví phát sinh")
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { walletMenuExpanded = true }
                        ) {
                            Text(
                                text = selectedWallet?.name ?: "Chọn ví",
                                modifier = Modifier.padding(12.dp)
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
                errorText?.let { Text(it) }
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
            ) { Text("LƯU") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("HỦY") }
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
