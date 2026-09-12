package com.iicemeta.wheat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iicemeta.wheat.ui.components.AddRecordSheet
import com.iicemeta.wheat.ui.components.WheatBottomNavBar
import com.iicemeta.wheat.ui.components.WheatTopBar
import com.iicemeta.wheat.ui.navigation.Screen
import com.iicemeta.wheat.ui.navigation.WheatNavGraph
import com.iicemeta.wheat.ui.navigation.screenOf
import com.iicemeta.wheat.ui.theme.WheatTheme
import com.iicemeta.wheat.viewmodel.AddRecordViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WheatTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val current = screenOf(backStackEntry?.destination?.route)

                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
                var showAddSheet by remember { mutableStateOf(false) }
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val addRecordViewModel: AddRecordViewModel = viewModel()

                fun navigateTo(screen: Screen) {
                    if (current.route != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        WheatTopBar(
                            screen = current,
                            onSettingsClick = { navigateTo(Screen.Mine) }
                        )
                    },
                    bottomBar = {
                        WheatBottomNavBar(
                            current = current,
                            onTabClick = { navigateTo(it) },
                            onAddClick = { showAddSheet = true }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    WheatNavGraph(
                        navController = navController,
                        onAddClick = { showAddSheet = true },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                if (showAddSheet) {
                    AddRecordSheet(
                        viewModel = addRecordViewModel,
                        sheetState = sheetState,
                        onDismiss = {
                            showAddSheet = false
                            addRecordViewModel.reset()
                        },
                        onSaved = {
                            scope.launch {
                                showAddSheet = false
                                addRecordViewModel.reset()
                                // 回到首页看小麦成长，并提示习惯打卡成功
                                navigateTo(Screen.Home)
                                snackbarHostState.showSnackbar("记账成功！坚持打卡，小麦获得今天的阳光")
                            }
                        }
                    )
                }
            }
        }
    }
}
