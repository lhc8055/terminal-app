package com.terminal.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.terminal.app.ui.components.CapsuleShape
import com.terminal.app.ui.components.LiquidGlassSurface
import com.terminal.app.ui.components.LiquidToggle
import com.terminal.app.ui.theme.TextMuted
import com.terminal.app.ui.theme.TextPrimary

/**
 * Settings — basic UI configuration rendered as liquid-glass rows, each capped with a
 * LiquidToggle. Pure client-side toggles; no system / permission changes are made.
 */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    var haptics by remember { mutableStateOf(true) }
    var autoScroll by remember { mutableStateOf(true) }
    var keepAwake by remember { mutableStateOf(false) }
    var verbose by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            "设置",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 16.dp)
        )

        SettingsSectionLabel("终端")
        SettingsRow("触感反馈", "命令发送时震动反馈", haptics) { haptics = it }
        Spacer(Modifier.height(10.dp))
        SettingsRow("自动滚动", "新输出时滚到底部", autoScroll) { autoScroll = it }
        Spacer(Modifier.height(10.dp))
        SettingsRow("详细日志", "显示额外调试信息", verbose) { verbose = it }

        Spacer(Modifier.height(20.dp))
        SettingsSectionLabel("显示")

        SettingsRow("保持唤醒", "终端在前台时屏幕常亮", keepAwake) { keepAwake = it }

        Spacer(Modifier.height(20.dp))

        LiquidGlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = CapsuleShape(22.dp, exponent = 4.0f),
            cornerRadius = 22.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "版本",
                    color = TextMuted,
                    fontSize = 13.sp
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "1.0.0 · 合法调试版",
                    color = TextPrimary,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionLabel(text: String) {
    Text(
        text,
        color = TextMuted,
        fontSize = 12.sp,
        modifier = Modifier
            .padding(start = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    LiquidGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = CapsuleShape(20.dp, exponent = 4.0f),
        cornerRadius = 20.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, color = TextMuted, fontSize = 12.sp)
            }
            Spacer(Modifier.width(12.dp))
            LiquidToggle(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
