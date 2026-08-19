package com.cry.manage.feature.planned

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cry.manage.data.model.PlannedItem
import com.cry.manage.data.model.Wallet
import com.cry.manage.ui.components.ProjectBackground
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

private val CryRed = Color(0xFFC9233B)
private val CrySoft = Color(0xFFFFEEF1)
private val CryText = Color(0xFF28242A)
private val CryMuted = Color(0xFF766C70)
private val IncomeGreen = Color(0xFF17865C)
private val IncomeSoft = Color(0xFFE8F6F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannedScreen(
    onBack: () -> Unit,
    viewModel: PlannedViewModel = viewModel()
) {
    val items by viewModel.items.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var approvingItem by remember { mutableStateOf<PlannedItem?>(null) }

    val totalExpense = items.filter { it.type == PlannedItem.TYPE_EXPENSE }.sumOf { it.amount }
    val totalIncome = items.filter { it.type == PlannedItem.TYPE_INCOME }.sumOf { it.amount }

    ProjectBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = CryRed)
                        }
                    },
                    title = {
                        Column {
                            Text("Dự chi / Dự thu", color = CryText, fontWeight = FontWeight.Bold, fontSize = 21.sp)
                            Text("Theo dõi các khoản sắp tới", color = CryMuted, fontSize = 12.sp)
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = CryRed,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm khoản dự kiến")
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
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryCard(Modifier.weight(1f), "Tổng dự chi", totalExpense, CryRed, CrySoft)
                        SummaryCard(Modifier.weight(1f), "Tổng dự thu", totalIncome, IncomeGreen, IncomeSoft)
                    }
                }

                item {
                    Text(
                        text = "Danh sách chờ duyệt",
                        color = CryText,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (items.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Chưa có dự chi hoặc dự thu", color = CryText, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(5.dp))
                                Text("Nhấn + để tạo khoản đầu tiên.", color = CryMuted)
                            }
                        }
                    }
                } else {
                    items(items, key = { it.id }) { item ->
                        PlannedItemCard(
                            item = item,
                            onApprove = { approvingItem = item },
                            onDelete = { viewModel.delete(item) }
                        )
                    }
                }

                item { Spacer(Modifier.height(88.dp)) }
            }
        }
    }

    if (showAddDialog) {
        AddPlannedDialog(
            wallets = wallets.filter { it.isAvailable },
            onDismiss = { showAddDialog = false },
            onConfirm = { type, dueDate, amount, wallet, note ->
                viewModel.add(type, dueDate, amount, wallet, note)
                showAddDialog = false
            }
        )
    }

    approvingItem?.let { item ->
        ApproveDialog(
            item = item,
            onDismiss = { approvingItem = null },
            onConfirm = { actualAmount ->
                viewModel.approve(item, actualAmount)
                approvingItem = null
            }
        )
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier,
    title: String,
    amount: Double,
    accent: Color,
    soft: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Payments, contentDescription = null, tint = accent)
            }
            Spacer(Modifier.height(8.dp))
            Text(title, color = CryMuted, fontSize = 12.sp)
            Text(formatCurrency(amount), color = accent, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        }
    }
}

@Composable
private fun PlannedItemCard(
    item: PlannedItem,
    onApprove: () -> Unit,
    onDelete: () -> Unit
) {
    val isExpense = item.type == PlannedItem.TYPE_EXPENSE
    val accent = if (isExpense) CryRed else IncomeGreen
    val days = daysUntil(item.dueDate)
    val requiredPerDay = if (isExpense) item.amount / max(days, 1) else 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isExpense) "Dự chi" else "Dự thu",
                        color = accent,
                        fontWeight = FontWeight.Bold
                    )
                    Text(item.walletName, color = CryMuted, fontSize = 12.sp)
                }
                Text(formatCurrency(item.amount), color = accent, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = CryMuted, modifier = Modifier.size(18.dp))
                Text(
                    text = "  Đến hạn ${formatDate(item.dueDate)}" + if (days < 0) " • Quá hạn" else " • còn $days ngày",
                    color = if (days < 0) CryRed else CryMuted,
                    fontSize = 12.sp
                )
            }

            if (isExpense) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Thu nhập yêu cầu/ngày: ${formatCurrency(requiredPerDay)}",
                    color = CryText,
                    fontSize = 12.sp
                )
            }

            if (item.note.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(item.note, color = CryMuted, fontSize = 12.sp)
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = accent)
                ) {
                    Text(if (isExpense) "DUYỆT CHI" else "DUYỆT THU")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = CryRed)
                }
            }
        }
    }
}

@Composable
private fun AddPlannedDialog(
    wallets: List<Wallet>,
    onDismiss: () -> Unit,
    onConfirm: (String, Long, Double, Wallet, String) -> Unit
) {
    var type by remember { mutableStateOf(PlannedItem.TYPE_EXPENSE) }
    var selectedWallet by remember(wallets) { mutableStateOf(wallets.firstOrNull()) }
    var walletExpanded by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(formatDate(System.currentTimeMillis())) }
    var note by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo khoản dự kiến", color = CryText, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { type = PlannedItem.TYPE_EXPENSE },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == PlannedItem.TYPE_EXPENSE) CryRed else CrySoft,
                            contentColor = if (type == PlannedItem.TYPE_EXPENSE) Color.White else CryRed
                        )
                    ) { Text("Dự chi") }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { type = PlannedItem.TYPE_INCOME },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == PlannedItem.TYPE_INCOME) IncomeGreen else IncomeSoft,
                            contentColor = if (type == PlannedItem.TYPE_INCOME) Color.White else IncomeGreen
                        )
                    ) { Text("Dự thu") }
                }

                if (wallets.isEmpty()) {
                    Text("Cần có ít nhất một ví khả dụng.", color = CryRed)
                } else {
                    Text("Ví liên kết", color = CryMuted, fontSize = 12.sp)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { walletExpanded = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CrySoft)
                    ) {
                        Text(selectedWallet?.name ?: "Chọn ví", modifier = Modifier.padding(12.dp))
                    }
                    DropdownMenu(
                        expanded = walletExpanded,
                        onDismissRequest = { walletExpanded = false }
                    ) {
                        wallets.forEach { wallet ->
                            DropdownMenuItem(
                                text = { Text(wallet.name) },
                                onClick = {
                                    selectedWallet = wallet
                                    walletExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Số tiền") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Ngày đến hạn (dd/MM/yyyy)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ghi chú") },
                    modifier = Modifier.fillMaxWidth()
                )
                error?.let { Text(it, color = CryRed, fontSize = 12.sp) }
            }
        },
        confirmButton = {
            TextButton(
                enabled = wallets.isNotEmpty(),
                onClick = {
                    val wallet = selectedWallet
                    val amount = amountText.replace(",", "").replace(".", "").toDoubleOrNull()
                    val dueDate = parseDate(dateText)
                    when {
                        wallet == null -> error = "Hãy chọn ví."
                        amount == null || amount <= 0 -> error = "Số tiền không hợp lệ."
                        dueDate == null -> error = "Ngày đến hạn không hợp lệ."
                        else -> onConfirm(type, dueDate, amount, wallet, note)
                    }
                }
            ) { Text("LƯU", color = CryRed, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("HỦY", color = CryMuted) }
        }
    )
}

@Composable
private fun ApproveDialog(
    item: PlannedItem,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amountText by remember(item) { mutableStateOf(formatRawAmount(item.amount)) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (item.type == PlannedItem.TYPE_EXPENSE) "Duyệt chi" else "Duyệt thu",
                color = CryText,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Bạn có thể thay đổi số tiền thực tế trước khi duyệt.", color = CryMuted)
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Số tiền thực tế") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                error?.let { Text(it, color = CryRed, fontSize = 12.sp) }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = amountText.replace(",", "").replace(".", "").toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        error = "Số tiền không hợp lệ."
                    } else {
                        onConfirm(amount)
                    }
                }
            ) { Text("XÁC NHẬN", color = CryRed, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("HỦY", color = CryMuted) }
        }
    )
}

private fun daysUntil(timestamp: Long): Int {
    val day = 24L * 60L * 60L * 1000L
    return ((timestamp - System.currentTimeMillis()) / day).toInt()
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    formatter.maximumFractionDigits = 0
    return "${formatter.format(amount)} đ"
}

private fun formatRawAmount(amount: Double): String = amount.toLong().toString()

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).format(Date(timestamp))

private fun parseDate(value: String): Long? = try {
    SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).apply {
        isLenient = false
    }.parse(value)?.time
} catch (_: Exception) {
    null
}
