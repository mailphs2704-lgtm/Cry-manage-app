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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.TextButton
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
    var milestones by remember(settings.id) { mutableStateOf(XanhSmRules.milestones(settings)) }
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
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    InfoCard(
                        "Thay đổi chỉ áp dụng cho chuyến được lưu sau thời điểm bấm Lưu. " +
                            "Chuyến cũ giữ nguyên điểm và dữ liệu lịch sử."
                    )
                }

                item {
                    SectionCard("Khung giờ cao điểm") {
                        TimeRangeEditor("Buổi sáng", draft.morningStart, draft.morningEnd,
                            { draft = draft.copy(morningStart = it) },
                            { draft = draft.copy(morningEnd = it) })
                        TimeRangeEditor("Buổi trưa", draft.noonStart, draft.noonEnd,
                            { draft = draft.copy(noonStart = it) },
                            { draft = draft.copy(noonEnd = it) })
                        TimeRangeEditor("Buổi chiều", draft.afternoonStart, draft.afternoonEnd,
                            { draft = draft.copy(afternoonStart = it) },
                            { draft = draft.copy(afternoonEnd = it) })
                        Text("Ngoài các khoảng trên được tính là Ngoài giờ.", color = SettingsCryMuted, fontSize = 12.sp)
                    }
                }

                item {
                    SectionCard("Điểm theo dịch vụ") {
                        PointsRow("Sáng", draft.morningBike, draft.morningFast, draft.morning2h, draft.morningFood) { a,b,c,d ->
                            draft = draft.copy(morningBike=a, morningFast=b, morning2h=c, morningFood=d)
                        }
                        PointsRow("Trưa", draft.noonBike, draft.noonFast, draft.noon2h, draft.noonFood) { a,b,c,d ->
                            draft = draft.copy(noonBike=a, noonFast=b, noon2h=c, noonFood=d)
                        }
                        PointsRow("Chiều", draft.afternoonBike, draft.afternoonFast, draft.afternoon2h, draft.afternoonFood) { a,b,c,d ->
                            draft = draft.copy(afternoonBike=a, afternoonFast=b, afternoon2h=c, afternoonFood=d)
                        }
                        PointsRow("Ngoài giờ", draft.offpeakBike, draft.offpeakFast, draft.offpeak2h, draft.offpeakFood) { a,b,c,d ->
                            draft = draft.copy(offpeakBike=a, offpeakFast=b, offpeak2h=c, offpeakFood=d)
                        }
                    }
                }

                item {
                    SectionCard("Mốc & tiền thưởng tuần") {
                        Text(
                            "Có thể thêm nhiều mốc. Màn hình chính sẽ tự hiển thị toàn bộ mốc theo thứ tự điểm.",
                            color = SettingsCryMuted,
                            fontSize = 12.sp
                        )

                        milestones.sortedBy { it.points }.forEachIndexed { index, milestone ->
                            DynamicMilestoneEditor(
                                index = index,
                                milestone = milestone,
                                canDelete = milestones.size > 1,
                                onChange = { changed ->
                                    val sorted = milestones.sortedBy { it.points }
                                    val old = sorted[index]
                                    milestones = milestones.map { if (it == old) changed else it }
                                },
                                onDelete = {
                                    val sorted = milestones.sortedBy { it.points }
                                    val old = sorted[index]
                                    milestones = milestones.filterNot { it == old }
                                }
                            )
                        }

                        TextButton(
                            onClick = {
                                val nextPoints = (milestones.maxOfOrNull { it.points } ?: 0) + 50
                                milestones = milestones + WeeklyMilestone(nextPoints, 0.0)
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text(" THÊM MỐC THƯỞNG")
                        }
                    }
                }

                message?.let { item { Text(it, color = SettingsCryRed, fontSize = 12.sp) } }

                item {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SettingsXanh),
                        onClick = {
                            val error = validateDraft(draft, milestones)
                            if (error != null) {
                                message = error
                            } else {
                                val sorted = milestones.sortedBy { it.points }
                                val saved = draft.copy(
                                    milestonesData = XanhSmRules.encodeMilestones(sorted),
                                    milestone1Points = sorted.getOrNull(0)?.points ?: draft.milestone1Points,
                                    milestone1Reward = sorted.getOrNull(0)?.reward ?: draft.milestone1Reward,
                                    milestone2Points = sorted.getOrNull(1)?.points ?: draft.milestone2Points,
                                    milestone2Reward = sorted.getOrNull(1)?.reward ?: draft.milestone2Reward,
                                    milestone3Points = sorted.getOrNull(2)?.points ?: draft.milestone3Points,
                                    milestone3Reward = sorted.getOrNull(2)?.reward ?: draft.milestone3Reward
                                )
                                onSave(saved)
                                message = "Đã lưu cấu hình mới."
                            }
                        }
                    ) { Text("LƯU CẤU HÌNH", fontWeight = FontWeight.Bold) }
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun DynamicMilestoneEditor(
    index: Int,
    milestone: WeeklyMilestone,
    canDelete: Boolean,
    onChange: (WeeklyMilestone) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.72f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Mốc ${index + 1}", modifier = Modifier.weight(1f), color = SettingsCryText, fontWeight = FontWeight.Bold)
                if (canDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa mốc", tint = SettingsCryRed)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = milestone.points.toString(),
                    onValueChange = { text -> text.toIntOrNull()?.let { onChange(milestone.copy(points = it)) } },
                    label = { Text("Điểm") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = milestone.reward.toLong().toString(),
                    onValueChange = { text ->
                        text.replace(".", "").replace(",", "").toDoubleOrNull()?.let {
                            onChange(milestone.copy(reward = it))
                        }
                    },
                    label = { Text("Tiền thưởng") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
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
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
private fun TimeRangeEditor(title: String, start: String, end: String, onStart: (String) -> Unit, onEnd: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, color = SettingsCryText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(start, onStart, label = { Text("Từ") }, placeholder = { Text("06:00") }, singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(end, onEnd, label = { Text("Đến") }, placeholder = { Text("09:00") }, singleLine = true, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PointsRow(label: String, bike: Int, fast: Int, h2: Int, food: Int, onChange: (Int, Int, Int, Int) -> Unit) {
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
    OutlinedTextField(value.toString(), { text -> text.toIntOrNull()?.let(onChange) }, label = { Text(label) }, singleLine = true, modifier = Modifier.weight(1f))
}

private fun validateDraft(settings: XanhSettingsVersion, milestones: List<WeeklyMilestone>): String? {
    val timeRegex = Regex("^(?:[01]\\d|2[0-3]):[0-5]\\d$")
    val times = listOf(settings.morningStart, settings.morningEnd, settings.noonStart, settings.noonEnd, settings.afternoonStart, settings.afternoonEnd)
    if (times.any { !timeRegex.matches(it) }) return "Giờ phải đúng định dạng HH:mm, ví dụ 06:00."

    fun minuteOfDay(value: String): Int {
        val p = value.split(':')
        return p[0].toInt() * 60 + p[1].toInt()
    }
    val ranges = listOf(
        minuteOfDay(settings.morningStart) to minuteOfDay(settings.morningEnd),
        minuteOfDay(settings.noonStart) to minuteOfDay(settings.noonEnd),
        minuteOfDay(settings.afternoonStart) to minuteOfDay(settings.afternoonEnd)
    )
    if (ranges.any { it.first >= it.second }) return "Giờ bắt đầu phải nhỏ hơn giờ kết thúc."
    val sortedRanges = ranges.sortedBy { it.first }
    if (!sortedRanges.zipWithNext().all { (a, b) -> a.second <= b.first }) return "Các khung giờ cao điểm không được chồng lấn."

    val points = listOf(
        settings.morningBike, settings.morningFast, settings.morning2h, settings.morningFood,
        settings.noonBike, settings.noonFast, settings.noon2h, settings.noonFood,
        settings.afternoonBike, settings.afternoonFast, settings.afternoon2h, settings.afternoonFood,
        settings.offpeakBike, settings.offpeakFast, settings.offpeak2h, settings.offpeakFood
    )
    if (points.any { it < 0 }) return "Điểm không được âm."
    if (milestones.isEmpty()) return "Cần ít nhất một mốc thưởng tuần."
    if (milestones.any { it.points <= 0 || it.reward < 0 }) return "Mốc điểm phải lớn hơn 0 và tiền thưởng không được âm."
    if (milestones.map { it.points }.distinct().size != milestones.size) return "Các mốc điểm không được trùng nhau."
    return null
}
