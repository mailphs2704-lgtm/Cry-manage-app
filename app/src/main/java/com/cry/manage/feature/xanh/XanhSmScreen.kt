package com.cry.manage.feature.xanh

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
import androidx.compose.material.icons.filled.DirectionsBike
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cry.manage.data.model.Wallet
import com.cry.manage.data.model.XanhTrip
import com.cry.manage.ui.components.ProjectBackground
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CryRed = Color(0xFFC9233B)
private val CryText = Color(0xFF28242A)
private val CryMuted = Color(0xFF766C70)
private val XanhGreen = Color(0xFF008C72)
private val XanhSoft = Color(0xFFE7F6F2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XanhSmScreen(
    onBack: () -> Unit,
    viewModel: XanhSmViewModel = viewModel()
) {
    val trips by viewModel.trips.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val totalRevenue = trips.sumOf { it.revenue }
    val totalNet = trips.sumOf { it.netIncome }
    val totalDiscount = trips.sumOf { it.discountAmount }
    val totalPoints = trips.sumOf { it.points }

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
                            Text("Xanh SM Bike", color = CryText, fontWeight = FontWeight.Bold, fontSize = 21.sp)
                            Text("Quản lý chuyến xe và thu nhập", color = CryMuted, fontSize = 12.sp)
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = XanhGreen,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm chuyến xe")
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(Modifier.weight(1f), "Doanh số", formatCurrency(totalRevenue), XanhGreen)
                        MetricCard(Modifier.weight(1f), "Thu nhập ròng", formatCurrency(totalNet), CryRed)
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(Modifier.weight(1f), "Chiết khấu", formatCurrency(totalDiscount), CryRed)
                        MetricCard(Modifier.weight(1f), "Điểm", totalPoints.toString(), XanhGreen)
                    }
                }
                item {
                    Text(
                        text = "Lịch sử chuyến xe",
                        color = CryText,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                if (trips.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Chưa có chuyến xe", color = CryText, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text("Nhấn + để ghi nhận chuyến đầu tiên.", color = CryMuted)
                            }
                        }
                    }
                } else {
                    items(trips, key = { it.id }) { trip ->
                        TripCard(trip)
                    }
                }
                item { Spacer(Modifier.height(88.dp)) }
            }
        }
    }

    if (showAddDialog) {
        AddTripDialog(
            wallets = wallets,
            onDismiss = { showAddDialog = false },
            onConfirm = { revenue, netIncome, promotion, paymentType, timeSlot, points, driverWallet, receiveWallet, occurredAt ->
                viewModel.addTrip(
                    revenue = revenue,
                    netIncome = netIncome,
                    promotion = promotion,
                    paymentType = paymentType,
                    timeSlot = timeSlot,
                    points = points,
                    driverWallet = driverWallet,
                    receiveWallet = receiveWallet,
                    occurredAt = occurredAt
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun MetricCard(modifier: Modifier, title: String, value: String, accent: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(title, color = CryMuted, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, color = accent, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        }
    }
}

@Composable
private fun TripCard(trip: XanhTrip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(46.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.DirectionsBike, contentDescription = null, tint = XanhGreen)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(formatPaymentType(trip.paymentType), color = CryText, fontWeight = FontWeight.Bold)
                    Text("${formatDate(trip.occurredAt)} • ${trip.timeSlot.ifBlank { "Không ghi khung giờ" }}", color = CryMuted, fontSize = 12.sp)
                }
                Text(formatCurrency(trip.netIncome), color = XanhGreen, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Doanh số ${formatCurrency(trip.revenue)}", modifier = Modifier.weight(1f), color = CryMuted, fontSize = 12.sp)
                Text("CK ${String.format(Locale.US, "%.1f", trip.discountPercent)}%", color = CryRed, fontSize = 12.sp)
            }
            if (trip.promotion > 0) {
                Spacer(Modifier.height(4.dp))
                Text("Khuyến mãi: ${formatCurrency(trip.promotion)}", color = CryMuted, fontSize = 12.sp)
            }
            if (trip.points > 0) {
                Spacer(Modifier.height(4.dp))
                Text("Điểm: ${trip.points}", color = XanhGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun AddTripDialog(
    wallets: List<Wallet>,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double, Double, String, String, Int, Wallet, Wallet?, Long) -> Unit
) {
    var revenueText by remember { mutableStateOf("") }
    var netIncomeText by remember { mutableStateOf("") }
    var promotionText by remember { mutableStateOf("0") }
    var pointsText by remember { mutableStateOf("0") }
    var timeSlot by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(formatDate(System.currentTimeMillis())) }
    var paymentType by remember { mutableStateOf(XanhTrip.PAYMENT_CASH) }
    var driverWallet by remember(wallets) { mutableStateOf(wallets.firstOrNull()) }
    var receiveWallet by remember(wallets) { mutableStateOf(wallets.firstOrNull()) }
    var driverExpanded by remember { mutableStateOf(false) }
    var receiveExpanded by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val revenuePreview = parseMoney(revenueText) ?: 0.0
    val netPreview = parseMoney(netIncomeText) ?: 0.0
    val discountPreview = (revenuePreview - netPreview).coerceAtLeast(0.0)
    val discountPercent = if (revenuePreview > 0) discountPreview / revenuePreview * 100.0 else 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ghi nhận chuyến xe", color = CryText, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        PaymentButton("Tiền mặt", XanhTrip.PAYMENT_CASH, paymentType) { paymentType = it }
                        PaymentButton("Ngân hàng", XanhTrip.PAYMENT_BANK, paymentType) { paymentType = it }
                        PaymentButton("Thẻ ví", XanhTrip.PAYMENT_WALLET_CARD, paymentType) { paymentType = it }
                    }
                }
                item { OutlinedTextField(revenueText, { revenueText = it }, label = { Text("Doanh số") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(netIncomeText, { netIncomeText = it }, label = { Text("Thu nhập ròng") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(promotionText, { promotionText = it }, label = { Text("Khuyến mãi") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = XanhSoft)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Chiết khấu tự tính", color = CryMuted, fontSize = 12.sp)
                            Text("${formatCurrency(discountPreview)} • ${String.format(Locale.US, "%.1f", discountPercent)}%", color = XanhGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                item {
                    WalletPicker("Ví tài xế", driverWallet, wallets, driverExpanded, { driverExpanded = it }) { driverWallet = it }
                }
                if (paymentType != XanhTrip.PAYMENT_WALLET_CARD) {
                    item {
                        WalletPicker(
                            if (paymentType == XanhTrip.PAYMENT_CASH) "Ví tiền mặt" else "Ví ngân hàng",
                            receiveWallet,
                            wallets,
                            receiveExpanded,
                            { receiveExpanded = it }
                        ) { receiveWallet = it }
                    }
                }
                item { OutlinedTextField(timeSlot, { timeSlot = it }, label = { Text("Khung giờ") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(pointsText, { pointsText = it }, label = { Text("Điểm") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(dateText, { dateText = it }, label = { Text("Ngày (dd/MM/yyyy)") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                errorText?.let { item { Text(it, color = CryRed, fontSize = 12.sp) } }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val revenue = parseMoney(revenueText)
                    val net = parseMoney(netIncomeText)
                    val promo = parseMoney(promotionText) ?: 0.0
                    val points = pointsText.toIntOrNull() ?: 0
                    val date = parseDate(dateText)
                    when {
                        wallets.isEmpty() -> errorText = "Bạn cần tạo ví trước."
                        revenue == null || revenue <= 0 -> errorText = "Doanh số không hợp lệ."
                        net == null || net < 0 || net > revenue -> errorText = "Thu nhập ròng không hợp lệ."
                        promo < 0 || promo > revenue -> errorText = "Khuyến mãi không hợp lệ."
                        driverWallet == null -> errorText = "Hãy chọn ví tài xế."
                        paymentType != XanhTrip.PAYMENT_WALLET_CARD && receiveWallet == null -> errorText = "Hãy chọn ví nhận tiền."
                        date == null -> errorText = "Ngày không hợp lệ."
                        else -> onConfirm(revenue, net, promo, paymentType, timeSlot, points, driverWallet!!, if (paymentType == XanhTrip.PAYMENT_WALLET_CARD) null else receiveWallet, date)
                    }
                }
            ) { Text("LƯU", color = XanhGreen, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("HỦY", color = CryMuted) } }
    )
}

@Composable
private fun PaymentButton(label: String, value: String, selected: String, onSelect: (String) -> Unit) {
    Button(
        onClick = { onSelect(value) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected == value) XanhGreen else XanhSoft,
            contentColor = if (selected == value) Color.White else XanhGreen
        ),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Text(label, fontSize = 11.sp)
    }
}

@Composable
private fun WalletPicker(
    label: String,
    selectedWallet: Wallet?,
    wallets: List<Wallet>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (Wallet) -> Unit
) {
    Column {
        Text(label, color = CryMuted, fontSize = 12.sp)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandedChange(true) },
            colors = CardDefaults.cardColors(containerColor = XanhSoft)
        ) {
            Text(selectedWallet?.name ?: "Chọn ví", modifier = Modifier.padding(12.dp), color = CryText)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            wallets.forEach { wallet ->
                DropdownMenuItem(
                    text = { Text(wallet.name) },
                    onClick = {
                        onSelect(wallet)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

private fun parseMoney(value: String): Double? = value
    .replace(".", "")
    .replace(",", "")
    .trim()
    .toDoubleOrNull()

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    formatter.maximumFractionDigits = 0
    return "${formatter.format(amount)} đ"
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).format(Date(timestamp))

private fun parseDate(value: String): Long? = try {
    SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).apply { isLenient = false }.parse(value)?.time
} catch (_: Exception) {
    null
}

private fun formatPaymentType(type: String): String = when (type) {
    XanhTrip.PAYMENT_CASH -> "Tiền mặt"
    XanhTrip.PAYMENT_BANK -> "Chuyển khoản ngân hàng"
    XanhTrip.PAYMENT_WALLET_CARD -> "Thẻ ví"
    else -> type
}
