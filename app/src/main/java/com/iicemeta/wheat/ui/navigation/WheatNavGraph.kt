package com.iicemeta.wheat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iicemeta.wheat.ui.screens.granary.GranaryScreen
import com.iicemeta.wheat.ui.screens.home.HomeScreen
import com.iicemeta.wheat.ui.screens.mine.MineScreen
import com.iicemeta.wheat.ui.screens.stats.StatsScreen
import com.iicemeta.wheat.viewmodel.GranaryViewModel
import com.iicemeta.wheat.viewmodel.HomeViewModel
import com.iicemeta.wheat.viewmodel.MineViewModel
import com.iicemeta.wheat.viewmodel.StatsViewModel

@Composable
fun WheatNavGraph(
    navController: NavHostController,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel()
            HomeScreen(viewModel = homeViewModel, onAddClick = onAddClick)
        }

        composable(Screen.Granary.route) {
            val granaryViewModel: GranaryViewModel = viewModel()
            GranaryScreen(viewModel = granaryViewModel)
        }

        composable(Screen.Stats.route) {
            val statsViewModel: StatsViewModel = viewModel()
            StatsScreen(viewModel = statsViewModel)
        }

        composable(Screen.Mine.route) {
            val mineViewModel: MineViewModel = viewModel()
            MineScreen(viewModel = mineViewModel)
        }
    }
}
