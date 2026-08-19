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
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.mutableIntStateOf
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
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

private val CryRed = Color(0xFFC9233B)
private val CryText = Color(0xFF28242A)
private val CryMuted = Color(0xFF766C70)
private val XanhGreen = Color(0xFF008C72)
private val XanhSoft = Color(0xFFE7F6F2)
private val TipPurple = Color(0xFF6657D9)
private val PointGold = Color(0xFFD89A00)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XanhSmScreen(
    onBack: () -> Unit,
    viewModel: XanhSmViewModel = viewModel()
) {
    val trips by viewModel.trips.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var deletingTrip by remember { mutableStateOf<XanhTrip?>(null) }
    var weekOffset by remember { mutableIntStateOf(0) }

    val weekRange = remember(weekOffset) { weekRange(weekOffset) }
    val weekTrips = trips.filter { it.occurredAt in weekRange.first..weekRange.second }
    val totalRevenue = weekTrips.sumOf { it.revenue }
    val totalNet = weekTrips.sumOf { it.netIncome }
    val totalTips = weekTrips.sumOf { it.tips }
    val totalDiscount = weekTrips.sumOf { it.discountAmount }
    val totalPoints = weekTrips.sumOf { it.points }
    val avgDiscount = if (totalRevenue > 0) totalDiscount / totalRevenue * 100.0 else 0.0
    val nextMilestone = XanhSmRules.nextMilestone(totalPoints)
    val achievedReward = XanhSmRules.achievedReward(totalPoints)

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
                            Text("Xanh SM Companion", color = CryText, fontWeight = FontWeight.Bold, fontSize = 21.sp)
                            Text("Dòng tiền • chuyến xe • điểm thưởng", color = CryMuted, fontSize = 12.sp)
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
                item { WeekNavigation(weekOffset, weekRange) { weekOffset += it } }

                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(Modifier.weight(1f), "Chuyến", weekTrips.size.toString(), XanhGreen)
                        MetricCard(Modifier.weight(1f), "Doanh số", formatCurrency(totalRevenue), CryText)
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(Modifier.weight(1f), "Thực thu ròng", formatCurrency(totalNet), XanhGreen)
                        MetricCard(Modifier.weight(1f), "Tips", "+${formatCurrency(totalTips)}", TipPurple)
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(
                            Modifier.weight(1f),
                            "Chiết khấu",
                            "${formatCurrency(totalDiscount)} • ${String.format(Locale.US, "%.1f", avgDiscount)}%",
                            CryRed
                        )
                        MetricCard(Modifier.weight(1f), "Điểm tuần", totalPoints.toString(), PointGold)
                    }
                }

                item {
                    MilestoneCard(
                        totalPoints = totalPoints,
                        achievedReward = achievedReward,
                        nextMilestone = nextMilestone
                    )
                }

                item {
                    Text(
                        text = "Lịch sử theo ngày",
                        color = CryText,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                val grouped = weekTrips.groupBy { dayKey(it.occurredAt) }.toSortedMap(compareByDescending { it })
                if (grouped.isEmpty()) {
                    item { EmptyTripsCard() }
                } else {
                    grouped.forEach { (_, dayTrips) ->
                        item { DayHeader(dayTrips) }
                        items(dayTrips, key = { it.id }) { trip ->
                            TripCard(trip = trip, onDelete = { deletingTrip = trip })
                        }
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
            onConfirm = { serviceType, revenue, netIncome, tips, promotion, foodCost, paymentType, timeSlot, note, driverWallet, receiveWallet, occurredAt ->
                viewModel.addTrip(
                    serviceType = serviceType,
                    revenue = revenue,
                    netIncome = netIncome,
                    tips = tips,
                    promotion = promotion,
                    foodCost = foodCost,
                    paymentType = paymentType,
                    timeSlot = timeSlot,
                    note = note,
                    driverWallet = driverWallet,
                    receiveWallet = receiveWallet,
                    occurredAt = occurredAt
                )
                showAddDialog = false
            }
        )
    }

    deletingTrip?.let { trip ->
        AlertDialog(
            onDismissRequest = { deletingTrip = null },
            title = { Text("Xóa chuyến xe?") },
            text = {
                Text(
                    "Chuyến ${formatServiceType(trip.serviceType)} sẽ bị xóa và " +
                        "${formatCurrency(trip.netIncome + trip.tips)} sẽ được khấu trừ ngược khỏi ví ${trip.receiveWalletName ?: trip.driverWalletName}."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTrip(trip)
                        deletingTrip = null
                    }
                ) { Text("XÓA", color = CryRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { deletingTrip = null }) { Text("HỦY", color = CryMuted) }
            }
        )
    }
}

@Composable
private fun WeekNavigation(weekOffset: Int, range: Pair<Long, Long>, onMove: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f))
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(if (weekOffset == 0) "Tuần hiện tại" else "Tuần đã chọn", color = XanhGreen, fontWeight = FontWeight.Bold)
            Text("${formatDate(range.first)} - ${formatDate(range.second)}", color = CryMuted, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onMove(-1) },
                    colors = ButtonDefaults.buttonColors(containerColor = XanhSoft, contentColor = XanhGreen)
                ) { Text("← Tuần trước") }
                if (weekOffset != 0) {
                    Button(
                        onClick = { onMove(-weekOffset) },
                        colors = ButtonDefaults.buttonColors(containerColor = XanhGreen)
                    ) { Text("Hiện tại") }
                }
                Button(
                    onClick = { onMove(1) },
                    colors = ButtonDefaults.buttonColors(containerColor = XanhSoft, contentColor = XanhGreen)
                ) { Text("Tuần sau →") }
            }
        }
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
            Text(value, color = accent, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun MilestoneCard(totalPoints: Int, achievedReward: Double, nextMilestone: WeeklyMilestone?) {
    val target = nextMilestone?.points ?: XanhSmRules.weeklyMilestones.last().points
    val progress = (totalPoints.toFloat() / max(target, 1).toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tiến độ thưởng tuần", color = CryText, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("$totalPoints điểm • đã đạt ${formatCurrency(achievedReward)}", color = PointGold, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = PointGold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (nextMilestone == null) "Đã đạt mốc thưởng cao nhất"
                else "Còn ${nextMilestone.points - totalPoints} điểm để đạt ${formatCurrency(nextMilestone.reward)}",
                color = CryMuted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun DayHeader(trips: List<XanhTrip>) {
    val dayRevenue = trips.sumOf { it.revenue }
    val dayNet = trips.sumOf { it.netIncome }
    val dayTips = trips.sumOf { it.tips }
    Column(modifier = Modifier.padding(top = 4.dp)) {
        Text("${formatWeekday(trips.first().occurredAt)}, ${formatDate(trips.first().occurredAt)} • ${trips.size} chuyến", color = CryText, fontWeight = FontWeight.Bold)
        Text(
            "Doanh số ${formatCurrency(dayRevenue)} • Ròng ${formatCurrency(dayNet)}" +
                if (dayTips > 0) " • Tips +${formatCurrency(dayTips)}" else "",
            color = CryMuted,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun EmptyTripsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Chưa có chuyến trong tuần này", color = CryText, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Nhấn + để ghi nhận chuyến đầu tiên.", color = CryMuted)
        }
    }
}

@Composable
private fun TripCard(trip: XanhTrip, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.DirectionsBike, contentDescription = null, tint = XanhGreen)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(formatServiceType(trip.serviceType), color = CryText, fontWeight = FontWeight.Bold)
                    Text("${formatTimeSlot(trip.timeSlot)} • ${formatPaymentType(trip.paymentType)}", color = CryMuted, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(formatCurrency(trip.netIncome), color = XanhGreen, fontWeight = FontWeight.Bold)
                    if (trip.tips > 0) Text("+${formatCurrency(trip.tips)} tips", color = TipPurple, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa chuyến", tint = CryRed)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                Text("Gross ${formatCurrency(trip.revenue)}", modifier = Modifier.weight(1f), color = CryMuted, fontSize = 12.sp)
                Text("CK ${String.format(Locale.US, "%.1f", trip.discountPercent)}%", color = CryRed, fontSize = 12.sp)
                Text(" • ${trip.points}đ", color = PointGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            if (trip.promotion > 0 || trip.foodCost > 0) {
                Spacer(Modifier.height(4.dp))
                Text(
                    listOfNotNull(
                        trip.promotion.takeIf { it > 0 }?.let { "K.mãi ${formatCurrency(it)}" },
                        trip.foodCost.takeIf { it > 0 }?.let { "Ứng Food ${formatCurrency(it)}" }
                    ).joinToString(" • "),
                    color = CryMuted,
                    fontSize = 12.sp
                )
            }
            if (trip.note.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(trip.note, color = CryMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun AddTripDialog(
    wallets: List<Wallet>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Double, Double, Double, Double, String, String, String, Wallet, Wallet?, Long) -> Unit
) {
    var serviceType by remember { mutableStateOf(XanhTrip.SERVICE_BIKE) }
    var timeSlot by remember { mutableStateOf(XanhTrip.SLOT_MORNING) }
    var paymentType by remember { mutableStateOf(XanhTrip.PAYMENT_CASH) }
    var revenueText by remember { mutableStateOf("") }
    var netIncomeText by remember { mutableStateOf("") }
    var tipsText by remember { mutableStateOf("0") }
    var promotionText by remember { mutableStateOf("0") }
    var foodCostText by remember { mutableStateOf("0") }
    var note by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(formatDate(System.currentTimeMillis())) }
    var driverWallet by remember(wallets) { mutableStateOf(wallets.firstOrNull()) }
    var receiveWallet by remember(wallets) { mutableStateOf(wallets.firstOrNull()) }
    var driverExpanded by remember { mutableStateOf(false) }
    var receiveExpanded by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val revenuePreview = parseMoney(revenueText) ?: 0.0
    val netPreview = parseMoney(netIncomeText) ?: 0.0
    val tipsPreview = parseMoney(tipsText) ?: 0.0
    val discountPreview = (revenuePreview - netPreview).coerceAtLeast(0.0)
    val discountPercent = if (revenuePreview > 0) discountPreview / revenuePreview * 100.0 else 0.0
    val pointsPreview = XanhSmRules.pointsFor(serviceType, timeSlot)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ghi nhận chuyến Xanh SM", color = CryText, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item { Text("Dịch vụ", color = CryMuted, fontSize = 12.sp) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ChoiceButton("Bike", XanhTrip.SERVICE_BIKE, serviceType) { serviceType = it }
                        ChoiceButton("Siêu tốc", XanhTrip.SERVICE_EXPRESS_FAST, serviceType) { serviceType = it }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ChoiceButton("Express 2H", XanhTrip.SERVICE_EXPRESS_2H, serviceType) { serviceType = it }
                        ChoiceButton("Food", XanhTrip.SERVICE_FOOD, serviceType) { serviceType = it }
                    }
                }

                item { Text("Khung giờ • tự tính điểm", color = CryMuted, fontSize = 12.sp) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ChoiceButton("Sáng", XanhTrip.SLOT_MORNING, timeSlot) { timeSlot = it }
                        ChoiceButton("Trưa", XanhTrip.SLOT_NOON, timeSlot) { timeSlot = it }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ChoiceButton("Chiều", XanhTrip.SLOT_AFTERNOON, timeSlot) { timeSlot = it }
                        ChoiceButton("Ngoài giờ", XanhTrip.SLOT_OFFPEAK, timeSlot) { timeSlot = it }
                    }
                }

                item { OutlinedTextField(revenueText, { revenueText = it }, label = { Text("Doanh số gross") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(netIncomeText, { netIncomeText = it }, label = { Text("Thực thu ròng (không gồm tips)") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(tipsText, { tipsText = it }, label = { Text("Tips") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(promotionText, { promotionText = it }, label = { Text("Khuyến mãi hãng trợ giá") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                if (serviceType == XanhTrip.SERVICE_FOOD) {
                    item { OutlinedTextField(foodCostText, { foodCostText = it }, label = { Text("Tiền ứng mua đồ ăn") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = XanhSoft)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Tự động tính", color = CryMuted, fontSize = 12.sp)
                            Text("Chiết khấu ${formatCurrency(discountPreview)} • ${String.format(Locale.US, "%.1f", discountPercent)}%", color = CryRed, fontWeight = FontWeight.Bold)
                            Text("Thực nhận ${formatCurrency(netPreview + tipsPreview)} • $pointsPreview điểm", color = XanhGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ChoiceButton("Tiền mặt", XanhTrip.PAYMENT_CASH, paymentType) { paymentType = it }
                        ChoiceButton("Ngân hàng", XanhTrip.PAYMENT_BANK, paymentType) { paymentType = it }
                        ChoiceButton("Thẻ ví", XanhTrip.PAYMENT_WALLET_CARD, paymentType) { paymentType = it }
                    }
                }

                item { WalletPicker("Ví tài xế", driverWallet, wallets, driverExpanded, { driverExpanded = it }) { driverWallet = it } }
                if (paymentType != XanhTrip.PAYMENT_WALLET_CARD) {
                    item {
                        WalletPicker(
                            if (paymentType == XanhTrip.PAYMENT_CASH) "Ví tiền mặt nhận tiền" else "Ví ngân hàng nhận tiền",
                            receiveWallet,
                            wallets,
                            receiveExpanded,
                            { receiveExpanded = it }
                        ) { receiveWallet = it }
                    }
                }

                item { OutlinedTextField(dateText, { dateText = it }, label = { Text("Ngày (dd/MM/yyyy)") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(note, { note = it }, label = { Text("Ghi chú chuyến xe") }, modifier = Modifier.fillMaxWidth()) }
                errorText?.let { item { Text(it, color = CryRed, fontSize = 12.sp) } }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val revenue = parseMoney(revenueText)
                    val net = parseMoney(netIncomeText)
                    val tips = parseMoney(tipsText) ?: 0.0
                    val promo = parseMoney(promotionText) ?: 0.0
                    val foodCost = if (serviceType == XanhTrip.SERVICE_FOOD) parseMoney(foodCostText) ?: 0.0 else 0.0
                    val date = parseDate(dateText)
                    when {
                        wallets.isEmpty() -> errorText = "Bạn cần tạo ví trước."
                        revenue == null || revenue <= 0 -> errorText = "Doanh số không hợp lệ."
                        net == null || net < 0 || net > revenue -> errorText = "Thực thu ròng không hợp lệ."
                        tips < 0 -> errorText = "Tips không hợp lệ."
                        promo < 0 || promo > revenue -> errorText = "Khuyến mãi không hợp lệ."
                        foodCost < 0 -> errorText = "Tiền ứng Food không hợp lệ."
                        driverWallet == null -> errorText = "Hãy chọn ví tài xế."
                        paymentType != XanhTrip.PAYMENT_WALLET_CARD && receiveWallet == null -> errorText = "Hãy chọn ví nhận tiền."
                        date == null -> errorText = "Ngày không hợp lệ."
                        else -> onConfirm(
                            serviceType,
                            revenue,
                            net,
                            tips,
                            promo,
                            foodCost,
                            paymentType,
                            timeSlot,
                            note,
                            driverWallet!!,
                            if (paymentType == XanhTrip.PAYMENT_WALLET_CARD) null else receiveWallet,
                            date
                        )
                    }
                }
            ) { Text("LƯU", color = XanhGreen, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("HỦY", color = CryMuted) } }
    )
}

@Composable
private fun ChoiceButton(label: String, value: String, selected: String, onSelect: (String) -> Unit) {
    Button(
        onClick = { onSelect(value) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected == value) XanhGreen else XanhSoft,
            contentColor = if (selected == value) Color.White else XanhGreen
        )
    ) { Text(label, fontSize = 11.sp) }
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
            modifier = Modifier.fillMaxWidth().clickable { onExpandedChange(true) },
            colors = CardDefaults.cardColors(containerColor = XanhSoft)
        ) {
            Text(selectedWallet?.name ?: "Chọn ví", modifier = Modifier.padding(12.dp), color = CryText)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            wallets.forEach { wallet ->
                DropdownMenuItem(
                    text = { Text("${wallet.name} • ${formatCurrency(wallet.balance)}") },
                    onClick = {
                        onSelect(wallet)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

private fun weekRange(offset: Int): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    calendar.add(Calendar.WEEK_OF_YEAR, offset)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val start = calendar.timeInMillis
    calendar.add(Calendar.DAY_OF_YEAR, 6)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return start to calendar.timeInMillis
}

private fun dayKey(timestamp: Long): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(timestamp))
private fun parseMoney(value: String): Double? = value.replace(".", "").replace(",", "").trim().toDoubleOrNull()
private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    formatter.maximumFractionDigits = 0
    return "${formatter.format(amount)} đ"
}
private fun formatDate(timestamp: Long): String = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).format(Date(timestamp))
private fun formatWeekday(timestamp: Long): String = SimpleDateFormat("EEEE", Locale("vi", "VN")).format(Date(timestamp)).replaceFirstChar { it.uppercase() }
private fun parseDate(value: String): Long? = try {
    SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).apply { isLenient = false }.parse(value)?.time
} catch (_: Exception) { null }

private fun formatPaymentType(type: String): String = when (type) {
    XanhTrip.PAYMENT_CASH -> "Tiền mặt"
    XanhTrip.PAYMENT_BANK -> "Chuyển khoản"
    XanhTrip.PAYMENT_WALLET_CARD -> "Thẻ ví"
    else -> type
}

private fun formatServiceType(type: String): String = when (type) {
    XanhTrip.SERVICE_BIKE -> "SM Bike"
    XanhTrip.SERVICE_EXPRESS_FAST -> "Express Siêu Tốc"
    XanhTrip.SERVICE_EXPRESS_2H -> "Express 2H"
    XanhTrip.SERVICE_FOOD -> "SM Food"
    else -> type
}

private fun formatTimeSlot(slot: String): String = when (slot) {
    XanhTrip.SLOT_MORNING -> "Sáng 06:00-09:00"
    XanhTrip.SLOT_NOON -> "Trưa 11:00-13:30"
    XanhTrip.SLOT_AFTERNOON -> "Chiều 16:30-19:30"
    XanhTrip.SLOT_OFFPEAK -> "Ngoài giờ"
    else -> slot
}
