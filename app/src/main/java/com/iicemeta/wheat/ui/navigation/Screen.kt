package com.iicemeta.wheat.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
) {
    data object Home : Screen(
        route = "home",
        title = "麦穗 Wheat",
        subtitle = "坚持记账，静待麦田成熟",
        icon = Icons.Default.Grass
    )

    data object Granary : Screen(
        route = "granary",
        title = "秋收粮仓",
        subtitle = "科学储粮，理性消费",
        icon = Icons.Default.Warehouse
    )

    data object Stats : Screen(
        route = "stats",
        title = "秋收账本",
        subtitle = "复盘习惯，一目了然",
        icon = Icons.Default.PieChart
    )

    data object Mine : Screen(
        route = "mine",
        title = "学生中心",
        subtitle = "默默耕耘，自有回响",
        icon = Icons.Default.Person
    )
}

/** 底部导航栏的 Tab（中间的记账 FAB 不占路由，由 MainActivity 的 BottomSheet 承载）。 */
val bottomBarTabs = listOf(
    Screen.Home,
    Screen.Granary,
    Screen.Stats,
    Screen.Mine
)

fun screenOf(route: String?): Screen = when (route) {
    Screen.Granary.route -> Screen.Granary
    Screen.Stats.route -> Screen.Stats
    Screen.Mine.route -> Screen.Mine
    else -> Screen.Home
}
