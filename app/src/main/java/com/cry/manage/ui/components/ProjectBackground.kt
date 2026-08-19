package com.cry.manage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val BackgroundTop = Color(0xFFFFFAFB)
private val BackgroundMiddle = Color(0xFFFFF4F6)
private val BackgroundBottom = Color(0xFFFFECEF)

/**
 * Lightweight background used by scroll-heavy feature screens.
 *
 * The original Project Menu keeps its artwork image. Feature screens use the
 * same white/red visual identity with a GPU-cheap gradient so LazyColumn
 * scrolling does not have to composite a large bitmap plus a translucent
 * full-screen overlay on every frame.
 */
@Composable
fun ProjectBackground(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundTop,
                        BackgroundMiddle,
                        BackgroundBottom
                    )
                )
            )
    ) {
        content()
    }
}
