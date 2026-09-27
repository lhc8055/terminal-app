package com.terminal.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.terminal.app.ui.theme.GlassEdge
import com.terminal.app.ui.theme.GlassHighlight
import com.terminal.app.ui.theme.GlassTint

/**
 * LiquidGlass — frosted, refractive glass surface inspired by
 * https://github.com/Kyant0/AndroidLiquidGlass.
 *
 * Visual recipe (in z-order, bottom -> top):
 *   1. Translucent tinted base      — cool blue tint, low alpha
 *   2. (API 31+) blurred scrim      — frosts whatever is drawn beneath
 *   3. Specular highlight           — soft top-down gradient, simulates light from above
 *   4. Refraction edge              — 1dp bright rim, brighter on top edge
 *   5. Inner soft shadow at bottom  — gives the glass "thickness"
 *
 * The clip [shape] should normally be a [CapsuleShape] so the whole surface inherits the
 * superellipse continuous-corner silhouette.
 *
 * On API < 31 the blur is a no-op (Modifier.blur falls back gracefully), so the surface
 * still reads as glass thanks to the tint + specular + edge layering — just without
 * true frosted-blur of the content behind it.
 */
@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = CapsuleShape(28.dp),
    cornerRadius: Dp = 28.dp,
    tint: Color = GlassTint.copy(alpha = 0.10f),
    blurRadius: Dp = 24.dp,
    specularAlpha: Float = 0.55f,
    edgeAlpha: Float = 0.55f,
    content: @Composable () -> Unit
) {
    val capsule = if (shape is CapsuleShape) shape else CapsuleShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(capsule)
            // 1. Tinted base.
            .background(tint)
            // 2. Frosted scrim (no-op on API < 31).
            .blur(blurRadius)
            // 3 + 4 + 5. Specular, edge rim light, inner thickness shadow.
            .drawGlassSpecular(capsule, specularAlpha, edgeAlpha)
            // Edge stroke overlay for crisp definition.
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = GlassEdge.copy(alpha = 0.35f)
                ),
                shape = capsule
            )
    ) {
        content()
    }
}

/**
 * Draw the liquid-glass optical layers: a soft specular gradient from the top, a bright
 * thin rim along the very top edge, and a faint inner shadow along the bottom.
 */
private fun Modifier.drawGlassSpecular(
    shape: Shape,
    specularAlpha: Float,
    edgeAlpha: Float
): Modifier = this.drawWithContent {
    drawContent()

    val w = size.width
    val h = size.height

    // 3. Specular highlight: brightest at the top, fading down.
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                GlassHighlight.copy(alpha = specularAlpha),
                GlassHighlight.copy(alpha = specularAlpha * 0.35f),
                Color.Transparent
            ),
            startY = 0f,
            endY = h * 0.6f
        ),
        size = size
    )

    // 4. Top rim light: a thin bright band right under the top edge.
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                GlassHighlight.copy(alpha = edgeAlpha),
                Color.Transparent
            ),
            startY = 0f,
            endY = h * 0.18f
        ),
        size = size
    )

    // 5. Bottom inner shadow: simulates the glass "thickness" refracting at the lower lip.
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.10f)
            ),
            startY = h * 0.65f,
            endY = h
        ),
        size = size
    )

    // 6. Diagonal sheen: subtle bright streak from top-left, gives the "wet" liquid feel.
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                GlassHighlight.copy(alpha = 0.10f),
                Color.Transparent
            ),
            start = Offset(0f, 0f),
            end = Offset(w * 0.7f, h * 0.7f)
        ),
        size = size
    )
}
