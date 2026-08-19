package com.cry.manage.feature.wallet

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.cry.manage.data.model.Wallet
import java.text.NumberFormat
import java.util.Locale

private val CryRed = Color(0xFFC9233B)
private val CryRedDark = Color(0xFF9E1830)
private val CrySoft = Color(0xFFFFEEF1)
private val CryText = Color(0xFF28242A)
private val CryMuted = Color(0xFF766C70)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    onBack: () -> Unit,
    viewModel: WalletViewModel = viewModel()
) {
    val wallets by viewModel.wallets.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingWallet by remember { mutableStateOf<Wallet?>(null) }
    var balanceEditingWallet by remember { mutableStateOf<Wallet?>(null) }
    var deletingWallet by remember { mutableStateOf<Wallet?>(null) }

    val totalBalance = wallets.sumOf { it.balance }
    val availableBalance = wallets.filter { it.isAvailable }.sumOf { it.balance }

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
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = CryRed
                            )
                        }
                    },
                    title = {
                        Column {
                            Text(
                                text = "Quản lý ví",
                                color = CryText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp
                            )
                            Text(
                                text = "Số dư và trạng thái ví",
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
                    Icon(Icons.Default.Add, contentDescription = "Thêm ví")
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
                    WalletBalanceHero(
                        totalBalance = totalBalance,
                        availableBalance = availableBalance,
                        walletCount = wallets.size
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Danh sách ví",
                            modifier = Modifier.weight(1f),
                            color = CryText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${wallets.count { it.isAvailable }}/${wallets.size} khả dụng",
                            color = CryMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                if (wallets.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Chưa có ví nào",
                                    color = CryText,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(5.dp))
                                Text(
                                    text = "Nhấn nút + để tạo ví đầu tiên.",
                                    color = CryMuted,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                } else {
                    items(wallets, key = { it.id }) { wallet ->
                        WalletItem(
                            wallet = wallet,
                            onAvailabilityChanged = { available ->
                                viewModel.updateAvailability(wallet.id, available)
                            },
                            onEdit = { editingWallet = wallet },
                            onDelete = { deletingWallet = wallet },
                            onUpdateBalance = { balanceEditingWallet = wallet }
                        )
                    }
                }

                item { Spacer(Modifier.height(88.dp)) }
            }
        }
    }

    if (showAddDialog) {
        WalletDialog(
            title = "Thêm ví",
            initialWallet = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, description, balance, available ->
                viewModel.addWallet(
                    name = name,
                    description = description,
                    balance = balance,
                    isAvailable = available
                )
                showAddDialog = false
            }
        )
    }

    editingWallet?.let { wallet ->
        WalletDialog(
            title = "Sửa ví",
            initialWallet = wallet,
            onDismiss = { editingWallet = null },
            onConfirm = { name, description, balance, available ->
                viewModel.updateWallet(
                    wallet.copy(
                        name = name,
                        description = description,
                        balance = balance,
                        isAvailable = available
                    )
                )
                editingWallet = null
            }
        )
    }

    balanceEditingWallet?.let { wallet ->
        UpdateBalanceDialog(
            wallet = wallet,
            onDismiss = { balanceEditingWallet = null },
            onConfirm = { newBalance ->
                viewModel.updateBalance(wallet.id, newBalance)
                balanceEditingWallet = null
            }
        )
    }

    deletingWallet?.let { wallet ->
        AlertDialog(
            onDismissRequest = { deletingWallet = null },
            title = {
                Text(
                    text = "Xóa ví?",
                    color = CryText,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Bạn có chắc muốn xóa ví \"${wallet.name}\" không?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteWallet(wallet)
                        deletingWallet = null
                    }
                ) {
                    Text("XÓA", color = CryRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingWallet = null }) {
                    Text("HỦY", color = CryMuted)
                }
            }
        )
    }
}

@Composable
private fun WalletBalanceHero(
    totalBalance: Double,
    availableBalance: Double,
    walletCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(CryRed, CryRedDark)
                    )
                )
                .padding(22.dp)
        ) {
            Text(
                text = "Tổng số dư",
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = formatCurrency(totalBalance),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 29.sp
            )
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.14f))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Khả dụng",
                        color = Color.White.copy(alpha = 0.74f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = formatCurrency(availableBalance),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "$walletCount ví",
                    color = Color.White.copy(alpha = 0.82f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun WalletItem(
    wallet: Wallet,
    onAvailabilityChanged: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onUpdateBalance: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(CrySoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = CryRed
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = wallet.name,
                        color = CryText,
                        fontWeight = FontWeight.Bold
                    )
                    if (wallet.description.isNotBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = wallet.description,
                            color = CryMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Switch(
                    checked = wallet.isAvailable,
                    onCheckedChange = onAvailabilityChanged,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = CryRed
                    )
                )
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Số dư",
                        color = CryMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = formatCurrency(wallet.balance),
                        color = CryText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                IconButton(onClick = onUpdateBalance) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = "Cập nhật số dư",
                        tint = CryRed
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Sửa",
                        tint = CryMuted
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = CryRed
                    )
                }
            }
        }
    }
}

@Composable
private fun UpdateBalanceDialog(
    wallet: Wallet,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var balanceText by remember(wallet) {
        mutableStateOf(wallet.balance.toString())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Cập nhật số dư",
                color = CryText,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = wallet.name,
                    color = CryRed,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Số dư mới") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newBalance = balanceText
                        .replace(",", "")
                        .replace(".", "")
                        .toDoubleOrNull()
                    if (newBalance != null) onConfirm(newBalance)
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

@Composable
private fun WalletDialog(
    title: String,
    initialWallet: Wallet?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, Boolean) -> Unit
) {
    var name by remember(initialWallet) {
        mutableStateOf(initialWallet?.name ?: "")
    }
    var description by remember(initialWallet) {
        mutableStateOf(initialWallet?.description ?: "")
    }
    var balanceText by remember(initialWallet) {
        mutableStateOf(initialWallet?.balance?.toString() ?: "0")
    }
    var isAvailable by remember(initialWallet) {
        mutableStateOf(initialWallet?.isAvailable ?: true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = CryText,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên ví") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Số dư") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ví khả dụng",
                            color = CryText,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Được dùng cho các chức năng tài chính",
                            color = CryMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = { isAvailable = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CryRed
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val balance = balanceText
                        .replace(",", "")
                        .replace(".", "")
                        .toDoubleOrNull()
                        ?: 0.0
                    if (name.isNotBlank()) {
                        onConfirm(
                            name.trim(),
                            description.trim(),
                            balance,
                            isAvailable
                        )
                    }
                }
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = CryMuted)
            }
        }
    )
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    formatter.maximumFractionDigits = 0
    return "${formatter.format(amount)} đ"
}
