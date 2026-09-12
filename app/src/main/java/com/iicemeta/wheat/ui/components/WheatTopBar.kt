package com.iicemeta.wheat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iicemeta.wheat.ui.navigation.Screen
import com.iicemeta.wheat.ui.theme.AutumnOrange
import com.iicemeta.wheat.ui.theme.Wheat100
import com.iicemeta.wheat.ui.theme.Wheat200
import com.iicemeta.wheat.ui.theme.Wheat900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WheatTopBar(
    screen: Screen,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AutumnOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Grass,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        screen.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Wheat900
                    )
                    Text(
                        screen.subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.8f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Wheat200),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                TextButton(onClick = onSettingsClick) {
                    Text("生活费设置", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Wheat900)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Wheat100)
    )
}
