package com.terminal.app.ui.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.geometry.Rect

/**
 * Capsule — Superellipse rounded-corner curve.
 *
 * Inspired by the algorithm used in https://github.com/Kyant0/Capsule.
 *
 * The classic superellipse:  |x / a|^n + |y / b|^n = 1
 *
 *   - n < 2  : astroid-ish (concave corners, not used here)
 *   - n = 2  : perfect ellipse / circle
 *   - n > 2  : "squircle" - corners smoothly blend into straight edges (G1 continuous)
 *   - n -> ∞ : rectangle
 *
 * For UI rounded rectangles, n in [3, 6] gives the iconic iOS / macOS continuous-corner feel.
 *
 * This implementation draws a per-corner quarter superellipse and connects them with
 * straight edge segments. Because the superellipse's tangent at each corner endpoint is
 * exactly aligned with the adjacent edge direction, the result has no visible kink at the
 * corner-to-edge junctions (G1 continuity), unlike a vanilla [RoundedCornerShape].
 */
class CapsuleShape(
    private val cornerRadiusDp: Dp,
    private val exponent: Float = DEFAULT_EXPONENT
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) {
            return Outline.Rectangle(Rect(0f, 0f, w, h))
        }

        // Convert Dp -> px so the corner scales with screen density.
        val cornerRadius = with(density) { cornerRadiusDp.toPx() }
        val r = cornerRadius.coerceAtMost(minOf(w, h) / 2f)
        val path = Path()

        // Top edge (left -> right).
        path.moveTo(r, 0f)
        path.lineTo(w - r, 0f)

        // Top-right corner: (w-r, 0) -> (w, r). Center (w-r, r). Sweep t: -π/2 -> 0.
        appendQuarter(path, cx = w - r, cy = r, r = r, startT = -PI / 2f, endT = 0f, n = exponent)

        // Right edge (top -> bottom).
        path.lineTo(w, h - r)

        // Bottom-right corner: (w, h-r) -> (w-r, h). Center (w-r, h-r). Sweep t: 0 -> π/2.
        appendQuarter(path, cx = w - r, cy = h - r, r = r, startT = 0f, endT = PI / 2f, n = exponent)

        // Bottom edge (right -> left).
        path.lineTo(r, h)

        // Bottom-left corner: (r, h) -> (0, h-r). Center (r, h-r). Sweep t: π/2 -> π.
        appendQuarter(path, cx = r, cy = h - r, r = r, startT = PI / 2f, endT = PI, n = exponent)

        // Left edge (bottom -> top).
        path.lineTo(0f, r)

        // Top-left corner: (0, r) -> (r, 0). Center (r, r). Sweep t: π -> 3π/2.
        appendQuarter(path, cx = r, cy = r, r = r, startT = PI, endT = 3f * PI / 2f, n = exponent)

        path.close()
        return Outline.Generic(path)
    }

    /**
     * Append a sampled quarter superellipse. The first sample point is the path's current
     * end (already provided by the caller), so we begin at i=1 and end inclusively at i=steps,
     * which lands exactly on (endT).
     */
    private fun appendQuarter(
        path: Path,
        cx: Float,
        cy: Float,
        r: Float,
        startT: Float,
        endT: Float,
        n: Float
    ) {
        val steps = 16
        val inv = 2f / n
        for (i in 1..steps) {
            val t = startT + (endT - startT) * (i.toFloat() / steps)
            val ct = cos(t)
            val st = sin(t)
            val ox = (if (ct >= 0f) 1f else -1f) * abs(ct).pow(inv) * r
            val oy = (if (st >= 0f) 1f else -1f) * abs(st).pow(inv) * r
            path.lineTo(cx + ox, cy + oy)
        }
    }

    private fun Float.pow(e: Float): Float = Math.pow(this.toDouble(), e.toDouble()).toFloat()

    companion object {
        private const val PI = Math.PI.toFloat()

        /** 4.5 ≈ the Apple "continuous corner" feel. */
        const val DEFAULT_EXPONENT = 4.5f

        /** Convenience factory mirroring [androidx.compose.foundation.shape.RoundedCornerShape]. */
        fun fromDp(cornerRadiusDp: Dp, exponent: Float = DEFAULT_EXPONENT): CapsuleShape =
            CapsuleShape(cornerRadiusDp, exponent)
    }
}
