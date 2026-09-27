package com.terminal.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.terminal.app.ui.theme.AccentPrimary
import com.terminal.app.ui.theme.TextMuted
import com.terminal.app.ui.theme.TextPrimary

/**
 * Dock tab identifier.
 */
enum class DockTab(val label: String) {
    Home("首页"),
    Terminal("终端"),
    Settings("设置")
}

/**
 * GlassDock — iOS-27-style floating liquid-glass bottom navigation.
 *
 * Reference look (image 9): a frosted squircle bar floating above the home indicator,
 * with icon + Chinese-label tabs and a soft accent "pill" behind the active tab.
 *
 * The whole bar is a [LiquidGlassSurface] clipped with a [CapsuleShape] superellipse
 * (G2-ish continuous corners). The active pill is itself a smaller CapsuleShape.
 */
@Composable
fun GlassDock(
    activeTab: DockTab,
    onTabSelected: (DockTab) -> Unit,
    modifier: Modifier = Modifier
) {
    LiquidGlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .height(64.dp),
        shape = CapsuleShape(28.dp, exponent = 4.0f),
        cornerRadius = 28.dp,
        tint = Color.White.copy(alpha = 0.06f),
        blurRadius = 28.dp,
        specularAlpha = 0.5f,
        edgeAlpha = 0.5f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockTab.values().forEach { tab ->
                DockItem(
                    tab = tab,
                    isActive = tab == activeTab,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DockItem(
    tab: DockTab,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }

    val iconSize = if (isActive) 24.dp else 22.dp
    val icon: ImageVector = when (tab) {
        DockTab.Home -> Icons.Filled.Home
        DockTab.Terminal -> Icons.Filled.Code
        DockTab.Settings -> Icons.Filled.Settings
    }

    // Pill background cross-fades between inactive (transparent) and active (accent tint).
    val pillColor by animateColorAsState(
        targetValue = if (isActive) AccentPrimary.copy(alpha = 0.22f) else Color.Transparent,
        animationSpec = tween(220),
        label = "pillColor"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isActive) AccentPrimary else TextMuted,
        animationSpec = tween(220),
        label = "iconTint"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isActive) TextPrimary else TextMuted,
        animationSpec = tween(220),
        label = "labelColor"
    )

    Box(
        modifier = modifier
            .height(52.dp)
            .clickable(
                interactionSource = interaction,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Active pill behind the icon (always present, alpha-animated).
                Box(
                    modifier = Modifier
                        .width(46.dp)
                        .height(34.dp)
                        .clip(CapsuleShape(17.dp, exponent = 4.0f))
                        .background(pillColor)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = tab.label,
                    tint = iconTint,
                    modifier = Modifier
                        .size(iconSize)
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = tab.label,
                fontSize = 10.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = labelColor,
                maxLines = 1
            )
        }
    }
}
