package com.cry.manage.feature.wallet

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cry.manage.data.model.Wallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    onBack: () -> Unit,
    viewModel: WalletViewModel = viewModel()
) {
    val wallets by viewModel.wallets.collectAsState()

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var editingWallet by remember {
        mutableStateOf<Wallet?>(null)
    }
    var balanceEditingWallet by remember {
        mutableStateOf<Wallet?>(null)
    }
    var deletingWallet by remember {
        mutableStateOf<Wallet?>(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                title = {
                    Text("Quản lý ví")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddDialog = true
                }
            )
            {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm ví"
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            val totalBalance = wallets.sumOf {
                it.balance
            }

            Text(
                text = "Tổng số dư",
                fontWeight = FontWeight.Medium
            )

            Text(
                text = String.format(
                    "%,.0f đ",
                    totalBalance
                ),
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            if (wallets.isEmpty()) {

                Text(
                    text = "Chưa có ví nào."
                )

            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = wallets,
                        key = { it.id }
                    ) { wallet ->

                        WalletItem(
                            wallet = wallet,
                            onAvailabilityChanged = { available ->
                                viewModel.updateAvailability(
                                    wallet.id,
                                    available
                                )
                            },
                            onEdit = {
                                editingWallet = wallet
                            },
                            onDelete = {
                                deletingWallet = wallet
                            },
                            onUpdateBalance = {
                                balanceEditingWallet = wallet

                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {

        WalletDialog(
            title = "Thêm ví",
            initialWallet = null,
            onDismiss = {
                showAddDialog = false
            },
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
            onDismiss = {
                editingWallet = null
            },
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
            onDismiss = {
                balanceEditingWallet = null
            },
            onConfirm = { newBalance ->

                viewModel.updateBalance(
                    id = wallet.id,
                    balance = newBalance
                )

                balanceEditingWallet = null
            }
        )
    }
    deletingWallet?.let { wallet ->

        AlertDialog(
            onDismissRequest = {
                deletingWallet = null
            },

            title = {
                Text("Xóa ví?")
            },

            text = {
                Text(
                    "Bạn có chắc muốn xóa ví \"${wallet.name}\" không?"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteWallet(wallet)
                        deletingWallet = null
                    }
                ) {
                    Text("XÓA")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        deletingWallet = null
                    }
                ) {
                    Text("HỦY")
                }
            }
        )
    }
}

@Composable
private fun UpdateBalanceDialog(
    wallet: Wallet,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var balanceText by remember(wallet) {
        mutableStateOf(
            wallet.balance.toString()
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Cập nhật số dư")
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = wallet.name,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = balanceText,
                    onValueChange = {
                        balanceText = it
                    },
                    label = {
                        Text("Số dư mới")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = {

                    val newBalance =
                        balanceText
                            .replace(",", "")
                            .toDoubleOrNull()

                    if (newBalance != null) {
                        onConfirm(newBalance)
                    }
                }
            ) {
                Text("LƯU")
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("HỦY")
            }
        }
    )
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
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = wallet.name,
                        fontWeight = FontWeight.Bold
                    )

                    if (wallet.description.isNotBlank()) {

                        Text(
                            text = wallet.description
                        )
                    }
                }

                IconButton(
                    onClick = onEdit
                ) {

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Sửa"
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = String.format(
                    "%,.0f đ",
                    wallet.balance
                ),
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (wallet.isAvailable) {
                        "Khả dụng"
                    } else {
                        "Đang tắt"
                    },
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = wallet.isAvailable,
                    onCheckedChange = onAvailabilityChanged
                )
            }
        }
    }
}


@Composable
private fun WalletDialog(
    title: String,
    initialWallet: Wallet?,
    onDismiss: () -> Unit,
    onConfirm: (
        String,
        String,
        Double,
        Boolean
    ) -> Unit
) {

    var name by remember(
        initialWallet
    ) {
        mutableStateOf(
            initialWallet?.name ?: ""
        )
    }

    var description by remember(
        initialWallet
    ) {
        mutableStateOf(
            initialWallet?.description ?: ""
        )
    }

    var balanceText by remember(
        initialWallet
    ) {
        mutableStateOf(
            initialWallet?.balance?.toString() ?: "0"
        )
    }

    var isAvailable by remember(
        initialWallet
    ) {
        mutableStateOf(
            initialWallet?.isAvailable ?: true
        )
    }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text(title)
        },

        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text("Tên ví")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("Mô tả")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = balanceText,
                    onValueChange = {
                        balanceText = it
                    },
                    label = {
                        Text("Số dư")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Khả dụng",
                        modifier = Modifier.weight(1f)
                    )

                    Switch(
                        checked = isAvailable,
                        onCheckedChange = {
                            isAvailable = it
                        }
                    )
                }
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    val balance =
                        balanceText
                            .replace(",", "")
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

            Button(
                onClick = onDismiss
            ) {
                Text("Hủy")
            }
        }
    )
}