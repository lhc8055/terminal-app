package com.terminal.app.ui.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.terminal.app.ui.components.CapsuleShape
import com.terminal.app.ui.theme.AccentPrimary
import com.terminal.app.ui.theme.TermBg
import com.terminal.app.ui.theme.TermFg
import com.terminal.app.ui.theme.TermPrompt
import com.terminal.app.ui.theme.TextMuted

/**
 * Terminal screen — scrollable shell buffer on top, command input bar at the bottom.
 *
 * The whole surface sits inside a [CapsuleShape] squircle so it visually matches the rest
 * of the glass UI even though it isn't translucent (we need a high-contrast terminal bg for
 * legibility).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    modifier: Modifier = Modifier,
    vm: TerminalViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        vm.start()
    }

    val buffer by vm.buffer.collectAsState()
    var input by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    // Auto-scroll to the latest output whenever the buffer changes (instant — terminal feel).
    LaunchedEffect(buffer) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Toolbar: clear + Ctrl+C.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "local /system/bin/sh",
                color = TextMuted,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    text = "清屏",
                    color = TextMuted,
                    onClick = { vm.clear() }
                )
                Spacer(Modifier.width(8.dp))
                TextButton(
                    text = "Ctrl+C",
                    color = AccentPrimary,
                    onClick = { vm.sendCtrlC() }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Terminal surface.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(CapsuleShape(20.dp, exponent = 4.0f))
                .background(TermBg)
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = buffer.ifEmpty { "(等待输出…)" },
                    color = TermFg,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Input bar.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$",
                color = TermPrompt,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("输入命令", color = TextMuted, fontSize = 13.sp) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = TermFg
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (input.isNotBlank()) {
                            vm.execute(input.trim())
                            input = ""
                        }
                    }
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AccentPrimary,
                    unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
                    cursorColor = AccentPrimary,
                    textColor = TermFg
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (input.isNotBlank()) {
                                vm.execute(input.trim())
                                input = ""
                            }
                        }
                    ) {
                        Icon(
                            Icons.Filled.Send,
                            contentDescription = "发送",
                            tint = AccentPrimary
                        )
                    }
                }
            )
        }
    }
}

/**
 * Minimal monospace text button — avoids pulling in Material3's experimental TextButton.
 */
@Composable
private fun TextButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

