package com.terminal.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.terminal.app.ui.components.CapsuleShape
import com.terminal.app.ui.components.LiquidGlassSurface
import com.terminal.app.ui.theme.AccentPrimary
import com.terminal.app.ui.theme.AccentSecondary
import com.terminal.app.ui.theme.Success
import com.terminal.app.ui.theme.TextMuted
import com.terminal.app.ui.theme.TextPrimary

/**
 * Home — welcome screen. Shows app identity and a short list of capabilities, all rendered
 * as liquid-glass cards with Capsule squircle corners.
 */
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        // App identity.
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CapsuleShape(24.dp, exponent = 4.0f))
                .background(
                    Brush.linearGradient(
                        listOf(AccentSecondary, AccentPrimary)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "TP",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                fontFamily = FontFamily.SansSerif
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "Terminal Tools",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "本地 Shell · 合法设备调试",
            color = TextMuted,
            fontSize = 13.sp
        )

        Spacer(Modifier.height(28.dp))

        // Feature cards.
        FeatureCard(
            icon = Icons.Filled.Code,
            title = "本地终端",
            subtitle = "交互式 /system/bin/sh · 无 root · 无注入"
        )
        Spacer(Modifier.height(12.dp))
        FeatureCard(
            icon = Icons.Filled.Shield,
            title = "安全声明",
            subtitle = "不含游戏外挂、驱动注入、反检测等任何作弊逻辑"
        )
        Spacer(Modifier.height(12.dp))
        FeatureCard(
            icon = Icons.Filled.Bolt,
            title = "液态玻璃 UI",
            subtitle = "LiquidGlass · Capsule · LiquidToggle"
        )

        Spacer(Modifier.height(20.dp))

        LiquidGlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = CapsuleShape(24.dp),
            cornerRadius = 24.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "运行环境",
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Success)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "app sandbox · 就绪",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    LiquidGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = CapsuleShape(20.dp, exponent = 4.0f),
        cornerRadius = 20.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AccentPrimary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = AccentPrimary)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    subtitle,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}
