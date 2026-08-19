package com.cry.manage.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cry.manage.R
import androidx.compose.foundation.layout.offset

// ==================================================
// HOME SCREEN
// ==================================================

@Composable
fun HomeScreen(
    onStartClick: () -> Unit = {}
) {
    // ==================================================
    // KHUNG GỐC
    // ==================================================
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // ==================================================
        // BACKGROUND
        // ==================================================
        HomeBackground()


        // ==================================================
        // LOGO
        // ==================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            CryLogo(
                modifier = Modifier.size(600.dp)
            )
        }


        // ==================================================
        // BUTTON
        // ==================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(
                    start = 60.dp,
                    end = 60.dp,
                    bottom = 130.dp
                )
        ) {
            StartButton(
                onClick = onStartClick
            )
        }
    }
}


// ==================================================
// BACKGROUND
// ==================================================

@Composable
private fun HomeBackground() {

    Image(
        painter = painterResource(
            id = R.drawable.home_background
        ),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
}


// ==================================================
// LOGO
// ==================================================

@Composable
private fun CryLogo(
    modifier: Modifier = Modifier
) {

    Image(
        painter = painterResource(
            id = R.drawable.cry_manage_logo
        ),
        contentDescription = "Cry Manage",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}


// ==================================================
// NÚT BẮT ĐẦU
// ==================================================

@Composable
private fun StartButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .clip(
                RoundedCornerShape(40.dp)
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF1E2D),
                        Color(0xFFD90016)
                    )
                )
            )
            .border(
                width = 2.dp,
                color = Color.White,
                shape = RoundedCornerShape(40.dp)
            )
            .clickable {
                onClick()
            }
    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ==================================================
            // CHỮ "BẮT ĐẦU"
            // ==================================================
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Bắt đầu",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }


            // ==================================================
            // VẠCH NGĂN
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .background(
                        Color.White.copy(alpha = 0.55f)
                    )
            )

// MŨI TÊN
            Box(
                modifier = Modifier
                    .width(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "→",
                    fontSize = 42.sp,
                    color = Color.White,
                    modifier = Modifier.offset(y = (-10).dp)
                )
            }
        }
    }
}