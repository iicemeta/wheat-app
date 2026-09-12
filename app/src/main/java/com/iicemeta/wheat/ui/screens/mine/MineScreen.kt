package com.iicemeta.wheat.ui.screens.mine

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iicemeta.wheat.ui.theme.AutumnGreenDark
import com.iicemeta.wheat.ui.theme.AutumnMuted
import com.iicemeta.wheat.ui.theme.AutumnOrange
import com.iicemeta.wheat.ui.theme.Wheat100
import com.iicemeta.wheat.ui.theme.Wheat200
import com.iicemeta.wheat.ui.theme.Wheat300
import com.iicemeta.wheat.ui.theme.Wheat500
import com.iicemeta.wheat.ui.theme.Wheat700
import com.iicemeta.wheat.ui.theme.Wheat900
import com.iicemeta.wheat.viewmodel.MineViewModel

@Composable
fun MineScreen(viewModel: MineViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 农场主卡
        Card(shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Row(
                modifier = Modifier
                    .background(Brush.horizontalGradient(listOf(AutumnOrange, Wheat500)))
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.25f)) {
                    Icon(
                        Icons.Default.Spa, contentDescription = null, tint = Color.White,
                        modifier = Modifier.size(56.dp).padding(14.dp)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("默默耕耘的农场主", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        "坚持本身就是收获 · 麦穗习惯版 v1.0",
                        color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp
                    )
                }
            }
        }

        // 习惯数据
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HabitStatCell("连续打卡", "${uiState.streakDays} 天", Modifier.weight(1f))
            HabitStatCell("累计打卡", "${uiState.totalCheckInDays} 天", Modifier.weight(1f))
            HabitStatCell("本月记录", "${uiState.monthRecordCount} 笔", Modifier.weight(1f))
        }

        // 耕耘信条
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Wheat200),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(12.dp), color = Wheat100) {
                    Icon(
                        Icons.Default.Grass, contentDescription = null, tint = AutumnGreenDark,
                        modifier = Modifier.size(40.dp).padding(10.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("耕耘信条", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Wheat900)
                    Text(
                        "小麦不问花了多少，只问今天是否坚持。战胜遗忘，就是掌控财务的第一步。",
                        fontSize = 12.sp, color = AutumnMuted
                    )
                }
            }
        }

        // 生活费设置入口（与粮仓页同一数据源）
        BudgetSettingCard(budget = uiState.monthlyBudget, onSave = { viewModel.saveBudget(it) })
    }
}

@Composable
private fun HabitStatCell(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Wheat200),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = AutumnOrange)
            Spacer(Modifier.height(4.dp))
            Text(label, fontSize = 12.sp, color = AutumnMuted)
        }
    }
}

@Composable
private fun BudgetSettingCard(budget: Double, onSave: (Double) -> Unit) {
    var editing by remember { mutableStateOf(false) }
    var input by remember(budget) { mutableStateOf(budget.toInt().toString()) }
    var error by remember { mutableStateOf<String?>(null) }
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Wheat300.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("每月生活费粮仓", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Wheat900)
                    Text("¥%.0f/月".format(budget), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AutumnOrange)
                }
                Button(
                    onClick = { editing = !editing },
                    colors = ButtonDefaults.buttonColors(containerColor = Wheat700),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (editing) "取消" else "生活费设置")
                }
            }
            if (editing) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "粮仓容量只影响剩余水位展示，不影响小麦生长。小麦只认坚持。",
                    fontSize = 12.sp, color = AutumnMuted
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it; error = null },
                    label = { Text("每月生活费总额（元）") },
                    singleLine = true,
                    isError = error != null,
                    supportingText = error?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val value = input.toDoubleOrNull()
                        if (value == null || value <= 0) {
                            error = "请输入有效的预算金额"
                        } else {
                            onSave(value)
                            editing = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AutumnOrange),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("保存粮仓设置") }
            }
        }
    }
}
