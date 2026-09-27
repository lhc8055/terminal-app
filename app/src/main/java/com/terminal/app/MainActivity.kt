package com.terminal.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.terminal.app.ui.components.DockTab
import com.terminal.app.ui.components.GlassDock
import com.terminal.app.ui.home.HomeScreen
import com.terminal.app.ui.settings.SettingsScreen
import com.terminal.app.ui.terminal.TerminalScreen
import com.terminal.app.ui.theme.AccentSecondary
import com.terminal.app.ui.theme.BgPrimary
import com.terminal.app.ui.theme.BgSecondary
import com.terminal.app.ui.theme.TerminalAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge so the floating glass dock sits over the gesture area.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TerminalAppTheme {
                AppRoot()
            }
        }
    }
}

@Composable
private fun AppRoot() {
    var activeTab by remember { mutableStateOf(DockTab.Home) }

    // Vertical gradient backdrop — gives the glass something to refract / blur against.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(BgPrimary, BgSecondary, AccentSecondary.copy(alpha = 0.08f))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Content swaps on tab change with a soft fade.
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        fadeIn(tween(220)) togetherWith fadeOut(tween(220))
                    },
                    label = "tabContent"
                ) { tab ->
                    when (tab) {
                        DockTab.Home -> HomeScreen()
                        DockTab.Terminal -> TerminalScreen()
                        DockTab.Settings -> SettingsScreen()
                    }
                }
            }

            // Floating glass dock.
            GlassDock(
                activeTab = activeTab,
                onTabSelected = { activeTab = it },
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(top = 4.dp, bottom = 10.dp)
            )
        }
    }
}
