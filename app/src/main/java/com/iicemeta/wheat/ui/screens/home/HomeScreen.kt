package com.iicemeta.wheat.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iicemeta.wheat.data.model.Expense
import com.iicemeta.wheat.ui.components.WheatAnimation
import com.iicemeta.wheat.ui.components.categoryStyle
import com.iicemeta.wheat.ui.theme.AutumnDark
import com.iicemeta.wheat.ui.theme.AutumnGreenDark
import com.iicemeta.wheat.ui.theme.AutumnMuted
import com.iicemeta.wheat.ui.theme.AutumnOrange
import com.iicemeta.wheat.ui.theme.Wheat100
import com.iicemeta.wheat.ui.theme.Wheat200
import com.iicemeta.wheat.ui.theme.Wheat300
import com.iicemeta.wheat.ui.theme.Wheat600
import com.iicemeta.wheat.ui.theme.Wheat700
import com.iicemeta.wheat.ui.theme.Wheat800
import com.iicemeta.wheat.ui.theme.Wheat900
import com.iicemeta.wheat.util.stageTip
import com.iicemeta.wheat.util.stageTitle
import com.iicemeta.wheat.viewmodel.HomeViewModel
import com.iicemeta.wheat.viewmodel.WheatStage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HabitFieldCard(
            streakDays = uiState.streakDays,
            wheatExp = uiState.wheatExp,
            daysToNext = uiState.daysToNext,
            todayCheckedIn = uiState.todayCheckedIn,
            stage = uiState.wheatStage
        )

        QuickRecordCard(onClick = onAddClick)

        TodayRecordsSection(records = uiState.todayRecords)
    }
}

/** 习惯麦田卡：连击徽章 + 打卡状态 + 成长值 + 小麦动效 + 贴士。 */
@Composable
private fun HabitFieldCard(
    streakDays: Int,
    wheatExp: Int,
    daysToNext: Int,
    todayCheckedIn: Boolean,
    stage: WheatStage
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Wheat100),
        border = BorderStroke(1.dp, Wheat300.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFFBF0), Wheat100, Wheat200.copy(alpha = 0.7f))
                    )
                )
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFF3D6),
                        border = BorderStroke(1.dp, Color(0xFFF0D9A8))
                    ) {
                        Text(
                            text = "连续记账 $streakDays 天",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Wheat800,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("今日记账状态：", fontSize = 12.sp, color = Wheat700)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (todayCheckedIn) Color(0xFFD9F2E4) else Color(0xFFFFE9D6)
                    ) {
                        Text(
                            text = if (todayCheckedIn) "✓ 今日已打卡" else "○ 今日待打卡",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (todayCheckedIn) AutumnGreenDark else AutumnOrange,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "麦穗成长值 ",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Wheat900
                    )
                    Text(
                        text = "$wheatExp",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AutumnOrange
                    )
                    Text(
                        text = " / 100 · ${stageTitle(stage)}",
                        fontSize = 13.sp,
                        color = Wheat700,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }

                LinearProgressIndicator(
                    progress = { wheatExp / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .height(8.dp)
                        .clip(CircleShape),
                    color = AutumnOrange,
                    trackColor = Color.White.copy(alpha = 0.8f)
                )

                WheatAnimation(
                    stage = stage,
                    modifier = Modifier
                        .height(190.dp)
                        .padding(top = 4.dp)
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.75f),
                    border = BorderStroke(1.dp, Wheat200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val tip = remember(streakDays, stage) {
                            stageTip(
                                com.iicemeta.wheat.util.HabitInfo(
                                    streakDays = streakDays,
                                    stage = stage,
                                    expPercent = wheatExp / 100f,
                                    todayCheckedIn = todayCheckedIn,
                                    daysToNext = daysToNext
                                )
                            )
                        }
                        Text(
                            text = tip,
                            fontSize = 12.sp,
                            color = Wheat800,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (stage == WheatStage.HARVEST) "已经丰收" else "距升级还差${daysToNext}天",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AutumnOrange
                        )
                    }
                }
            }
        }
    }
}

/** 快捷记账入口：文案只谈习惯，不谈金额刺激。 */
@Composable
private fun QuickRecordCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .background(Brush.horizontalGradient(listOf(AutumnOrange, Wheat600)))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.25f)) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "记一笔",
                    tint = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("记一笔账 · 今日打卡", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    "无论金额大小，坚持记录就是给麦田阳光和水分",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun TodayRecordsSection(records: List<Expense>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("今日财务记录", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Wheat900)
            Text("共 ${records.size} 笔", fontSize = 12.sp, color = Wheat700)
        }

        if (records.isEmpty()) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Wheat200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "今天还没有记账，快记下第一笔，给小麦浇水吧",
                    fontSize = 13.sp,
                    color = AutumnMuted,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            records.forEach { tx ->
                TodayRecordRow(tx = tx)
            }
        }
    }
}

@Composable
private fun TodayRecordRow(tx: Expense) {
    val style = categoryStyle(tx.category)
    val time = remember(tx.date) {
        SimpleDateFormat("HH:mm", Locale.CHINA).format(Date(tx.date))
    }
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Wheat200.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(12.dp), color = style.fg.copy(alpha = 0.12f)) {
                Icon(
                    imageVector = style.icon,
                    contentDescription = style.label,
                    tint = style.fg,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(10.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.note.ifEmpty { style.label },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AutumnDark
                )
                Text("${style.label} · $time", fontSize = 12.sp, color = AutumnMuted)
            }
            Text(
                text = if (tx.isIncome) "+¥%.2f".format(tx.amount) else "-¥%.2f".format(tx.amount),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (tx.isIncome) AutumnGreenDark else AutumnOrange
            )
        }
    }
}
