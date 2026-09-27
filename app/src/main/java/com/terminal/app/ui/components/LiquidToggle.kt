package com.terminal.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.terminal.app.ui.theme.AccentPrimary
import com.terminal.app.ui.theme.GlassEdge
import com.terminal.app.ui.theme.GlassHighlight

/**
 * LiquidToggle — liquid-glass switch.
 *
 * Inspired by the LiquidToggle in https://github.com/Kyant0/AndroidLiquidGlass.
 *
 * Visual behavior:
 *   - The track is a small squircle (CapsuleShape) made of liquid glass.
 *   - The knob is a frosted circle that "pours" across the track with a spring.
 *   - When ON, an accent tint blooms behind the knob; when OFF, the track stays neutral glass.
 *   - A faint specular highlight runs along the top of both track and knob.
 */
@Composable
fun LiquidToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interaction = remember { MutableInteractionSource() }

    // Track is 52 x 30 dp.
    val trackWidth = 52.dp
    val trackHeight = 30.dp
    val knobSize = 24.dp
    val padding = 3.dp

    val targetOffset by animateDpAsState(
        targetValue = if (checked) trackWidth - knobSize - padding else padding,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "knobOffset"
    )

    // Bloom alpha for the ON-state accent glow.
    val bloomAlpha by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bloom"
    )

    val trackShape = CapsuleShape(trackHeight / 2)

    Box(
        modifier = modifier
            .size(width = trackWidth, height = trackHeight)
            .clip(trackShape)
            .background(
                if (checked) AccentPrimary.copy(alpha = 0.55f)
                else Color.White.copy(alpha = 0.08f)
            )
            .blur(6.dp)
            // Accent bloom on the ON side.
            .drawWithContent {
                drawContent()
                if (bloomAlpha > 0f) {
                    val bx = if (checked) size.width - size.height else 0f
                    drawCircle(
                        color = AccentPrimary.copy(alpha = 0.45f * bloomAlpha),
                        radius = size.height * 0.55f,
                        center = androidx.compose.ui.geometry.Offset(
                            bx + size.height / 2f,
                            size.height / 2f
                        )
                    )
                }
            }
            // Specular highlight on top of the track.
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GlassHighlight.copy(alpha = 0.45f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.5f
                    )
                )
            }
            // Glass edge.
            .drawWithContent {
                drawContent()
                drawRect(
                    color = GlassEdge.copy(alpha = 0.35f),
                    size = size
                )
            }
            .clickable(
                interactionSource = interaction,
                indication = null
            ) {
                if (enabled) onCheckedChange(!checked)
            }
    ) {
        // The knob.
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = targetOffset)
                .size(knobSize)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.85f))
                .blur(2.dp)
                .drawWithContent {
                    drawContent()
                    // Knob specular top-light.
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                GlassHighlight.copy(alpha = 0.85f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height * 0.6f
                        )
                    )
                    // Subtle rim.
                    drawRect(
                        color = GlassEdge.copy(alpha = 0.6f),
                        size = size
                    )
                }
        )
    }
}
