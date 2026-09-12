package com.iicemeta.wheat.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iicemeta.wheat.ui.navigation.Screen
import com.iicemeta.wheat.ui.theme.AutumnMuted
import com.iicemeta.wheat.ui.theme.AutumnOrange

/**
 * 底部导航：4 个 Tab + 中央突出的记账 FAB（对应 html 原型的底部栏）。
 */
@Composable
fun WheatBottomNavBar(
    current: Screen,
    onTabClick: (Screen) -> Unit,
    onAddClick: () -> Unit
) {
    Surface(
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        color = Color.White,
        modifier = Modifier.border(
            width = 1.dp,
            color = Color(0xFFF1E3C5)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 10.dp)
                .windowInsetsPadding(WindowInsets.navigationBars),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTab(
                screen = Screen.Home,
                selected = current == Screen.Home,
                onClick = { onTabClick(Screen.Home) },
                modifier = Modifier.weight(1f)
            )
            BottomTab(
                screen = Screen.Granary,
                selected = current == Screen.Granary,
                onClick = { onTabClick(Screen.Granary) },
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // 占位块决定行高：FAB 以下沉方式叠在上面，不撑高导航栏，
                // 阴影始终落在白色容器内部，不会被底部安全区裁掉。
                Spacer(modifier = Modifier.size(48.dp))
                FloatingActionButton(
                    onClick = onAddClick,
                    shape = CircleShape,
                    containerColor = AutumnOrange,
                    contentColor = Color.White,
                    modifier = Modifier
                        .offset(y = (-18).dp)
                        .size(56.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "记一笔", modifier = Modifier.size(26.dp))
                }
            }
            BottomTab(
                screen = Screen.Stats,
                selected = current == Screen.Stats,
                onClick = { onTabClick(Screen.Stats) },
                modifier = Modifier.weight(1f)
            )
            BottomTab(
                screen = Screen.Mine,
                selected = current == Screen.Mine,
                onClick = { onTabClick(Screen.Mine) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BottomTab(
    screen: Screen,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = when (screen) {
        Screen.Home -> "麦田首页"
        Screen.Granary -> "秋收粮仓"
        Screen.Stats -> "收支统计"
        Screen.Mine -> "学生中心"
    }
    val color = if (selected) AutumnOrange else AutumnMuted
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(screen.icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
