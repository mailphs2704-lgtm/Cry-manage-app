package com.cry.manage.feature.home.project


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cry.manage.R
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.cry.manage.feature.wallet.WalletScreen
import androidx.compose.foundation.clickable

private val CryRed = Color(0xFFC9233B)

@Composable
fun ProjectMenuScreen() {

    var showWalletScreen by remember {
        mutableStateOf(false)
    }

    if (showWalletScreen) {
        WalletScreen(
            onBack = {
                showWalletScreen = false
            }
        )
        return
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // ====================================================
        // BACKGROUND
        // ====================================================

        Image(
            painter = painterResource(
                id = R.drawable.project_menu_background
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // ====================================================
        // TOP BAR
        // ====================================================

        ProjectTopBar()
        ProjectMenuSection(
            onCryManageClick = {
                showWalletScreen = true
            }
        )
    }
}


// ============================================================
// TOP BAR
// ============================================================

@Composable
private fun ProjectTopBar() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(
                start = 18.dp,
                end = 18.dp,
                top = 10.dp,
                bottom = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // =========================
        // NÚT SETTINGS
        // =========================
        Box(
            modifier = Modifier
                .size(55.dp)
                .clip(
                    RoundedCornerShape(18.dp)
                )
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Cài đặt",
                tint = CryRed,
                modifier = Modifier.size(35.dp)
            )
        }

        // Khoảng cách giữa nút và chữ
        Spacer(
            modifier = Modifier.width(28.dp)
        )

        // =========================
        // XIN CHÀO, CRY
        // =========================
        // =========================
// XIN CHÀO, CRY
// =========================
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(
                    id = R.drawable.xin_chao_only
                ),
                contentDescription = "Xin chào",
                modifier = Modifier
                    .width(115.dp)
            )
            Spacer(
                modifier = Modifier.width(18.dp)
            )
            Text(
                text = "Cry",
                color = CryRed,
                fontSize = 29.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-0.5).sp
            )
        }
    }
}
@Composable
private fun ProjectMenuSection(
    onCryManageClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 32.dp,
                end = 32.dp,
                top = 145.dp
            ),
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = "Danh sách",
            color = CryRed,
            fontSize = 24.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.3).sp
        )
        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(4.dp)
                    .clip(
                        RoundedCornerShape(4.dp)
                    )
                    .background(CryRed)
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(
                        RoundedCornerShape(50)
                    )
                    .background(CryRed)
            )
        }
        Spacer(
            modifier = Modifier.height(28.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(170.dp)
                    .clip(
                        RoundedCornerShape(28.dp)
                    )
                    .background(Color.White)
                    .clickable {
                        onCryManageClick()
                    },
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.cry_manage_logo
                    ),
                    contentDescription = "Cry Manage",
                    modifier = Modifier
                        .width(150.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(170.dp)
                    .clip(
                        RoundedCornerShape(28.dp)
                    )
                    .background(Color.White)
            )
        }
    }
}