package com.iicemeta.wheat.ui.screens.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iicemeta.wheat.ui.theme.AutumnMuted
import com.iicemeta.wheat.ui.theme.AutumnOrange
import com.iicemeta.wheat.ui.theme.AutumnYellow
import com.iicemeta.wheat.ui.theme.Wheat200
import com.iicemeta.wheat.ui.theme.Wheat700
import com.iicemeta.wheat.ui.theme.Wheat800
import com.iicemeta.wheat.ui.theme.Wheat900
import com.iicemeta.wheat.viewmodel.DayExpense
import com.iicemeta.wheat.viewmodel.StatsViewModel

@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 月度总览
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Brush.horizontalGradient(listOf(Wheat800, Wheat700)))
                    .padding(20.dp)
            ) {
                Text(
                    if (uiState.monthLabel.isEmpty()) "本月度总览" else "${uiState.monthLabel} · 月度总览",
                    fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text("本月累计支出", fontSize = 12.sp, color = Wheat200)
                        Text(
                            "¥%.2f".format(uiState.monthSpent),
                            fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("节余比例", fontSize = 12.sp, color = Wheat200)
                        Text(
                            "%.0f%%".format(uiState.savedPercent * 100),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AutumnYellow
                        )
                    }
                }
            }
        }

        // 近 7 天收支曲线
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Wheat200),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("近7天支出曲线", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Wheat900)
                Spacer(Modifier.height(8.dp))
                ExpenseCurve(
                    days = uiState.last7Days,
                    maxAmount = uiState.maxDayAmount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
        }

        // 习惯贴士：强调坚持而非金额
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFFFF6E3),
            border = BorderStroke(1.dp, Color(0xFFF0D9A8)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(
                    Icons.Default.Lightbulb, contentDescription = null, tint = AutumnOrange,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("麦穗习惯贴士", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Wheat800)
                    Spacer(Modifier.height(4.dp))
                    val tip = remember(uiState.streakDays) {
                        if (uiState.streakDays > 0) {
                            "你已连续 ${uiState.streakDays} 天坚持记账打卡！记录是对财务最好的掌控，继续保持好习惯哦！"
                        } else {
                            "今天还没有打卡。记下任意一笔，小麦就能获得今天的阳光，开始你的连续记录吧！"
                        }
                    }
                    Text(tip, fontSize = 12.sp, color = Wheat800)
                }
            }
        }
    }
}

/** 手绘折线 + 面积填充 + 圆点，替代 Chart.js。 */
@Composable
private fun ExpenseCurve(days: List<DayExpense>, maxAmount: Double, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            Canvas(modifier = Modifier.matchParentSize()) {
                if (days.isEmpty()) return@Canvas
                val w = size.width
                val h = size.height
                val padL = 8f
                val padR = 8f
                val padT = 16f
                val padB = 8f
                val stepX = if (days.size > 1) (w - padL - padR) / (days.size - 1) else 0f
                val points = days.mapIndexed { i, d ->
                    val x = padL + i * stepX
                    val ratio = (d.amount / maxAmount).toFloat().coerceIn(0f, 1f)
                    val y = padT + (h - padT - padB) * (1f - ratio)
                    Offset(x, y)
                }
                val line = Path().apply {
                    points.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
                }
                val area = Path().apply {
                    addPath(line)
                    lineTo(points.last().x, h - padB)
                    lineTo(points.first().x, h - padB)
                    close()
                }
                drawPath(area, Brush.verticalGradient(listOf(AutumnOrange.copy(alpha = 0.25f), Color.Transparent)))
                drawPath(line, AutumnOrange, style = Stroke(width = 7f))
                points.forEach { p ->
                    drawCircle(Color.White, 11f, p)
                    drawCircle(AutumnOrange, 8f, p)
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { d ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(d.label, fontSize = 10.sp, color = AutumnMuted)
                    if (d.amount > 0) {
                        Text("%.0f".format(d.amount), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Wheat700)
                    }
                }
            }
        }
    }
}
