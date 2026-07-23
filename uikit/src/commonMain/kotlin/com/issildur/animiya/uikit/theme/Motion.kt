package com.issildur.animiya.uikit.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

/**
 * Длительности и кривые анимаций (токены → motion).
 * Числа M3; берём как собственные токены, не завязываясь на experimental Expressive.
 */
object AnimiyaMotion {
    // Длительности, мс
    const val ShortMs = 150
    const val MediumMs = 300
    const val LongMs = 500

    val Standard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val EmphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
}
