package com.iicemeta.wheat.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iicemeta.wheat.ui.theme.AutumnMuted
import com.iicemeta.wheat.ui.theme.AutumnOrange
import com.iicemeta.wheat.ui.theme.Wheat100
import com.iicemeta.wheat.ui.theme.Wheat200
import com.iicemeta.wheat.ui.theme.Wheat300
import com.iicemeta.wheat.ui.theme.Wheat50
import com.iicemeta.wheat.ui.theme.Wheat600
import com.iicemeta.wheat.ui.theme.Wheat800
import com.iicemeta.wheat.ui.theme.Wheat900
import com.iicemeta.wheat.viewmodel.AddRecordViewModel

/**
 * 记账弹窗（对应 html 原型的 #add-modal）：金额 + 分类九宫格 + 备注。
 * 文案只谈习惯打卡，不谈金额奖励。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordSheet(
    viewModel: AddRecordViewModel,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Wheat50
    ) {
        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 32.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Spa, contentDescription = null, tint = AutumnOrange)
                    Spacer(Modifier.size(8.dp))
                    Text("坚持记账 · 积累麦穗养分", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Wheat900)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "关闭", tint = AutumnMuted)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("金额（元）", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Wheat800)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = { viewModel.onAmountChanged(it) },
                placeholder = { Text("0.00") },
                prefix = { Text("¥", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AutumnOrange) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = uiState.error != null,
                supportingText = uiState.error?.let { { Text(it) } },
                textStyle = TextStyle(
                    fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Wheat900
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text("消费类型", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Wheat800)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recordCategories.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { category ->
                            val style = categoryStyle(category)
                            val selected = uiState.category == category
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (selected) Wheat100 else Color.White,
                                border = BorderStroke(
                                    if (selected) 2.dp else 1.dp,
                                    if (selected) Wheat300 else Wheat200
                                ),
                                onClick = { viewModel.onCategoryChanged(category) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(style.icon, contentDescription = null, tint = style.fg)
                                    Text(
                                        style.label, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                        color = if (selected) Wheat900 else AutumnMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("备注说明", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Wheat800)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.note,
                onValueChange = { viewModel.onNoteChanged(it) },
                placeholder = { Text("例如：食堂三楼黄焖鸡米饭") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(AutumnOrange, Wheat600).map {
                                if (uiState.isSaving) it.copy(alpha = 0.5f) else it
                            }
                        )
                    )
                    .clickable(enabled = !uiState.isSaving) { viewModel.save(onSaved) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.size(8.dp))
                    Text(
                        if (uiState.isSaving) "保存中..." else "确认记账并完成今日打卡",
                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp
                    )
                }
            }
        }
    }
}
