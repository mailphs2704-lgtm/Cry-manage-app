package com.cry.manage.feature.finance

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.cry.manage.feature.wallet.WalletViewModel
import java.text.NumberFormat
import java.util.Locale

private val CryRed = Color(0xFFC9233B)
private val CryRedDark = Color(0xFF9E1830)
private val CrySoft = Color(0xFFFFEEF1)
private val CryText = Color(0xFF28242A)
private val CryMuted = Color(0xFF766C70)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceHomeScreen(
    onBack: () -> Unit,
    onManageWallets: () -> Unit,
    onManageTransactions: () -> Unit,
    viewModel: WalletViewModel = viewModel()
) {
    val wallets by viewModel.wallets.collectAsState()
    val totalBalance = wallets.sumOf { it.balance }
    val availableWallets = wallets.filter { it.isAvailable }
    val availableBalance = availableWallets.sumOf { it.balance }

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
                                text = "Tài chính của bạn",
                                color = CryText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp
                            )
                            Text(
                                text = "Tổng quan hôm nay",
                                color = CryMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { Spacer(Modifier.height(2.dp)) }

                item {
                    BalanceHeroCard(
                        totalBalance = totalBalance,
                        availableBalance = availableBalance
                    )
                }

                item {
                    Text(
                        text = "Chức năng",
                        color = CryText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "Thu chi",
                            subtitle = "Ghi giao dịch",
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = CryRed,
                                    modifier = Modifier.size(28.dp)
                                )
                            },
                            onClick = onManageTransactions
                        )

                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "Ví tiền",
                            subtitle = "Quản lý số dư",
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = CryRed,
                                    modifier = Modifier.size(28.dp)
                                )
                            },
                            onClick = onManageWallets
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ví khả dụng",
                            modifier = Modifier.weight(1f),
                            color = CryText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${availableWallets.size} ví",
                            color = CryMuted,
                            fontSize = 13.sp
                        )
                    }
                }

                if (availableWallets.isEmpty()) {
                    item {
                        EmptyWalletCard(onClick = onManageWallets)
                    }
                } else {
                    items(availableWallets, key = { it.id }) { wallet ->
                        AvailableWalletCard(wallet, onManageWallets)
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun BalanceHeroCard(
    totalBalance: Double,
    availableBalance: Double
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
            Spacer(Modifier.height(6.dp))
            Text(
                text = formatCurrency(totalBalance),
                color = Color.White,
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.14f))
                    .padding(horizontal = 16.dp, vertical = 13.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Khả dụng",
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatCurrency(availableBalance),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CrySoft),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                color = CryText,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = CryMuted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun EmptyWalletCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Chưa có ví khả dụng",
                color = CryText,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Nhấn để mở Quản lý ví",
                color = CryMuted,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun AvailableWalletCard(wallet: Wallet, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
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
            Text(
                text = formatCurrency(wallet.balance),
                color = CryRed,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    formatter.maximumFractionDigits = 0
    return "${formatter.format(amount)} đ"
}
