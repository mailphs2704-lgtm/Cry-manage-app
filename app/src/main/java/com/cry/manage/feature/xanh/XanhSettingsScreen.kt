package com.cry.manage.feature.xanh

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cry.manage.data.model.XanhSettingsVersion
import com.cry.manage.ui.components.ProjectBackground

private val SettingsCryRed = Color(0xFFC9233B)
private val SettingsCryText = Color(0xFF28242A)
private val SettingsCryMuted = Color(0xFF766C70)
private val SettingsXanh = Color(0xFF008C72)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XanhSettingsScreen(
    settings: XanhSettingsVersion,
    onBack: () -> Unit,
    onSave: (XanhSettingsVersion) -> Unit
) {
    var draft by remember(settings.id) { mutableStateOf(settings) }
    var message by remember { mutableStateOf<String?>(null) }

    ProjectBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = SettingsCryRed)
                        }
                    },
                    title = {
                        Column {
                            Text("Cài đặt Xanh SM", color = SettingsCryText, fontWeight = FontWeight.Bold, fontSize = 21.sp)
                            Text("Khung giờ • điểm số • mốc thưởng", color = SettingsCryMuted, fontSize = 12.sp)
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
                item {
                    InfoCard(
                        "Các thay đổi chỉ áp dụng cho chuyến được lưu sau thời điểm bạn bấm Lưu. " +
                            "Chuyến cũ giữ nguyên điểm đã ghi để báo cáo lịch sử không bị thay đổi."
                    )
                }

                item {
                    SectionCard("Khung giờ cao điểm") {
                        TimeRangeEditor(
                            title = "Buổi sáng",
                            start = draft.morningStart,
                            end = draft.morningEnd,
                            onStart = { draft = draft.copy(morningStart = it) },
                            onEnd = { draft = draft.copy(morningEnd = it) }
                        )
                        TimeRangeEditor(
                            title = "Buổi trưa",
                            start = draft.noonStart,
                            end = draft.noonEnd,
                            onStart = { draft = draft.copy(noonStart = it) },
                            onEnd = { draft = draft.copy(noonEnd = it) }
                        )
                        TimeRangeEditor(
                            title = "Buổi chiều",
                            start = draft.afternoonStart,
                            end = draft.afternoonEnd,
                            onStart = { draft = draft.copy(afternoonStart = it) },
                            onEnd = { draft = draft.copy(afternoonEnd = it) }
                        )
                        Text(
                            "Ngoài các khoảng trên được tính là Ngoài giờ.",
                            color = SettingsCryMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                item {
                    SectionCard("Điểm theo dịch vụ") {
                        PointsRow(
                            "Sáng",
                            draft.morningBike, draft.morningFast, draft.morning2h, draft.morningFood
                        ) { bike, fast, h2, food ->
                            draft = draft.copy(morningBike = bike, morningFast = fast, morning2h = h2, morningFood = food)
                        }
                        PointsRow(
                            "Trưa",
                            draft.noonBike, draft.noonFast, draft.noon2h, draft.noonFood
                        ) { bike, fast, h2, food ->
                            draft = draft.copy(noonBike = bike, noonFast = fast, noon2h = h2, noonFood = food)
                        }
                        PointsRow(
                            "Chiều",
                            draft.afternoonBike, draft.afternoonFast, draft.afternoon2h, draft.afternoonFood
                        ) { bike, fast, h2, food ->
                            draft = draft.copy(afternoonBike = bike, afternoonFast = fast, afternoon2h = h2, afternoonFood = food)
                        }
                        PointsRow(
                            "Ngoài giờ",
                            draft.offpeakBike, draft.offpeakFast, draft.offpeak2h, draft.offpeakFood
                        ) { bike, fast, h2, food ->
                            draft = draft.copy(offpeakBike = bike, offpeakFast = fast, offpeak2h = h2, offpeakFood = food)
                        }
                    }
                }

                item {
                    SectionCard("Mốc thưởng tuần") {
                        MilestoneEditor(
                            1,
                            draft.milestone1Points,
                            draft.milestone1Reward,
                            onPoints = { draft = draft.copy(milestone1Points = it) },
                            onReward = { draft = draft.copy(milestone1Reward = it) }
                        )
                        MilestoneEditor(
                            2,
                            draft.milestone2Points,
                            draft.milestone2Reward,
                            onPoints = { draft = draft.copy(milestone2Points = it) },
                            onReward = { draft = draft.copy(milestone2Reward = it) }
                        )
                        MilestoneEditor(
                            3,
                            draft.milestone3Points,
                            draft.milestone3Reward,
                            onPoints = { draft = draft.copy(milestone3Points = it) },
                            onReward = { draft = draft.copy(milestone3Reward = it) }
                        )
                    }
                }

                message?.let { item { Text(it, color = SettingsCryRed, fontSize = 12.sp) } }

                item {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SettingsXanh),
                        onClick = {
                            val error = validateDraft(draft)
                            if (error != null) {
                                message = error
                            } else {
                                onSave(draft)
                                message = "Đã lưu cấu hình mới."
                            }
                        }
                    ) {
                        Text("LƯU CẤU HÌNH", fontWeight = FontWeight.Bold)
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, color = SettingsCryText, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun InfoCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Text(text, modifier = Modifier.padding(14.dp), color = SettingsCryMuted, fontSize = 12.sp)
    }
}

@Composable
private fun TimeRangeEditor(
    title: String,
    start: String,
    end: String,
    onStart: (String) -> Unit,
    onEnd: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, color = SettingsCryText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = start,
                onValueChange = onStart,
                label = { Text("Từ") },
                placeholder = { Text("06:00") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = end,
                onValueChange = onEnd,
                label = { Text("Đến") },
                placeholder = { Text("09:00") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PointsRow(
    label: String,
    bike: Int,
    fast: Int,
    h2: Int,
    food: Int,
    onChange: (Int, Int, Int, Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = SettingsXanh, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            PointField("Bike", bike) { onChange(it, fast, h2, food) }
            PointField("Siêu tốc", fast) { onChange(bike, it, h2, food) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            PointField("Express 2H", h2) { onChange(bike, fast, it, food) }
            PointField("Food", food) { onChange(bike, fast, h2, it) }
        }
    }
}

@Composable
private fun RowScope.PointField(label: String, value: Int, onChange: (Int) -> Unit) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { text -> text.toIntOrNull()?.let(onChange) },
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.weight(1f)
    )
}

@Composable
private fun MilestoneEditor(
    index: Int,
    points: Int,
    reward: Double,
    onPoints: (Int) -> Unit,
    onReward: (Double) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Mốc $index", color = SettingsCryText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = points.toString(),
                onValueChange = { it.toIntOrNull()?.let(onPoints) },
                label = { Text("Điểm") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = reward.toLong().toString(),
                onValueChange = { it.replace(".", "").replace(",", "").toDoubleOrNull()?.let(onReward) },
                label = { Text("Tiền thưởng") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun validateDraft(settings: XanhSettingsVersion): String? {
    val timeRegex = Regex("^(?:[01]\\d|2[0-3]):[0-5]\\d$")
    val times = listOf(
        settings.morningStart, settings.morningEnd,
        settings.noonStart, settings.noonEnd,
        settings.afternoonStart, settings.afternoonEnd
    )
    if (times.any { !timeRegex.matches(it) }) return "Giờ phải đúng định dạng HH:mm, ví dụ 06:00."

    val points = listOf(
        settings.morningBike, settings.morningFast, settings.morning2h, settings.morningFood,
        settings.noonBike, settings.noonFast, settings.noon2h, settings.noonFood,
        settings.afternoonBike, settings.afternoonFast, settings.afternoon2h, settings.afternoonFood,
        settings.offpeakBike, settings.offpeakFast, settings.offpeak2h, settings.offpeakFood
    )
    if (points.any { it < 0 }) return "Điểm không được âm."

    val milestonePoints = listOf(settings.milestone1Points, settings.milestone2Points, settings.milestone3Points)
    if (milestonePoints.any { it <= 0 } || milestonePoints.distinct().size != 3) {
        return "Ba mốc điểm phải lớn hơn 0 và không được trùng nhau."
    }
    if (listOf(settings.milestone1Reward, settings.milestone2Reward, settings.milestone3Reward).any { it < 0 }) {
        return "Tiền thưởng không được âm."
    }
    return null
}
