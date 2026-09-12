package com.iicemeta.wheat.ui.screens.granary

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iicemeta.wheat.ui.components.categoryStyle
import com.iicemeta.wheat.ui.theme.AutumnGreenDark
import com.iicemeta.wheat.ui.theme.AutumnMuted
import com.iicemeta.wheat.ui.theme.AutumnOrange
import com.iicemeta.wheat.ui.theme.GranaryEmpty
import com.iicemeta.wheat.ui.theme.GranaryFull
import com.iicemeta.wheat.ui.theme.GranaryLow
import com.iicemeta.wheat.ui.theme.GranaryMedium
import com.iicemeta.wheat.ui.theme.Wheat100
import com.iicemeta.wheat.ui.theme.Wheat200
import com.iicemeta.wheat.ui.theme.Wheat300
import com.iicemeta.wheat.ui.theme.Wheat700
import com.iicemeta.wheat.ui.theme.Wheat800
import com.iicemeta.wheat.ui.theme.Wheat900
import com.iicemeta.wheat.viewmodel.GranaryViewModel

@Composable
fun GranaryScreen(viewModel: GranaryViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Wheat200.copy(alpha = 0.55f)),
            border = BorderStroke(1.dp, Wheat300.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Surface(shape = CircleShape, color = Color(0xFFFFE3B3)) {
                            Text(
                                "秋收粮仓 · 本月",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Wheat800,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("生活费粮仓仓储", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Wheat900)
                    }
                    Surface(shape = RoundedCornerShape(14.dp), color = AutumnOrange.copy(alpha = 0.15f)) {
                        Icon(
                            Icons.Default.Warehouse, contentDescription = null, tint = AutumnOrange,
                            modifier = Modifier.size(48.dp).padding(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // 粮仓水位：垂直倒灌
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, Wheat300.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("粮仓剩余储量", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Wheat800)
                            Text(
                                "%.0f%%".format(uiState.remainingPercent * 100),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = AutumnOrange
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        GranarySilo(
                            percent = uiState.remainingPercent,
                            status = viewModel.granaryStatus(uiState.remainingPercent)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // 预算概览网格
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BudgetCell(
                        label = "每月总生活费",
                        value = "¥%.2f".format(uiState.monthlyBudget),
                        valueColor = Wheat900,
                        modifier = Modifier.weight(1f)
                    )
                    BudgetCell(
                        label = "本月已消耗",
                        value = "¥%.2f".format(uiState.totalSpent),
                        valueColor = AutumnOrange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 日均消费参考
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Wheat200),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = RoundedCornerShape(12.dp), color = Wheat100) {
                    Icon(
                        Icons.Default.Timeline, contentDescription = null, tint = Wheat700,
                        modifier = Modifier.size(40.dp).padding(10.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("日均消费参考", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Wheat900)
                    Text(
                        "本月第 ${uiState.dayOfMonth} 天 · 约合每日 ¥%.2f".format(uiState.dailyAverage),
                        fontSize = 12.sp, color = AutumnMuted
                    )
                }
                val healthy = uiState.remainingPercent > 0.3f
                Surface(
                    shape = CircleShape,
                    color = if (healthy) Color(0xFFD9F2E4) else Color(0xFFFFE4D6)
                ) {
                    Text(
                        if (healthy) "状态健康" else "需要节约",
                        fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        color = if (healthy) AutumnGreenDark else AutumnOrange,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // 消耗去向统计
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Wheat200),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("粮仓消耗去向统计", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Wheat900)
                if (uiState.shares.isEmpty()) {
                    Text("本月还没有支出记录，粮仓满满。", fontSize = 13.sp, color = AutumnMuted)
                } else {
                    uiState.shares.forEach { share ->
                        val style = categoryStyle(share.category)
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(style.label, fontSize = 12.sp, color = Wheat800)
                                Text(
                                    "¥%.2f (%.0f%%)".format(share.amount, share.percent * 100),
                                    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Wheat800
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { share.percent },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                color = style.fg,
                                trackColor = Wheat100
                            )
                        }
                    }
                }
            }
        }

        // 预算设置
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Wheat200),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("月度预算", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Wheat900)
                    IconButton(onClick = { viewModel.toggleEditBudget() }) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑预算", tint = Wheat700)
                    }
                }
                if (uiState.isEditingBudget) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = uiState.budgetInput,
                            onValueChange = { viewModel.onBudgetInputChanged(it) },
                            label = { Text("预算金额") },
                            prefix = { Text("¥") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.saveBudget() },
                            colors = ButtonDefaults.buttonColors(containerColor = Wheat700)
                        ) { Text("保存") }
                    }
                } else {
                    Text(
                        "¥%.0f/月".format(uiState.monthlyBudget),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AutumnOrange
                    )
                }
            }
        }
    }
}

/** 粮仓筒：水位随剩余比例升降，文案随水位变化。 */
@Composable
private fun GranarySilo(percent: Float, status: String) {
    val animated by animateFloatAsState(
        targetValue = percent.coerceIn(0f, 1f),
        animationSpec = tween(800),
        label = "granary_fill"
    )
    val fillColor = when {
        percent > 0.6f -> GranaryFull
        percent > 0.3f -> GranaryMedium
        percent > 0.1f -> GranaryLow
        else -> GranaryEmpty
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Wheat100)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .fillMaxHeight(fraction = animated.coerceAtLeast(0.02f))
                .background(Brush.verticalGradient(listOf(fillColor, fillColor.copy(alpha = 0.75f))))
        )
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.92f),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                status,
                fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Wheat900,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun BudgetCell(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, Wheat200),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, fontSize = 12.sp, color = Wheat700)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}
