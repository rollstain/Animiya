package com.issildur.animiya.uikit.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Отступы по 4dp-гриду (токены → spacing).
 * Значения — константы, поэтому обычный object, без CompositionLocal.
 */
object AnimiyaSpacing {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 40.dp
    val xxxl = 48.dp

    /** Боковое поле экрана. */
    val screenPadding = 16.dp

    /** Промежуток в сетке постеров. */
    val gridGutter = 12.dp
}

/**
 * Радиусы (токены → radius).
 * [Shapes] отдаётся в MaterialTheme; [sheetTop] и [pill] — вне стандартной шкалы M3.
 */
object AnimiyaRadii {
    val none = 0.dp
    val xs = 4.dp
    val sm = 8.dp   // чип
    val md = 12.dp  // постер, карточка
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 28.dp
    val xxxl = 32.dp
    val sheetTop = 24.dp
}

internal val AnimiyaShapes = Shapes(
    extraSmall = RoundedCornerShape(AnimiyaRadii.xs),
    small = RoundedCornerShape(AnimiyaRadii.sm),
    medium = RoundedCornerShape(AnimiyaRadii.md),
    large = RoundedCornerShape(AnimiyaRadii.lg),
    extraLarge = RoundedCornerShape(AnimiyaRadii.xxl),
)

/** Верхнее скругление bottom sheet (только верхние углы). */
val AnimiyaSheetShape = RoundedCornerShape(
    topStart = AnimiyaRadii.sheetTop,
    topEnd = AnimiyaRadii.sheetTop,
)

/**
 * Пропорция постера 2:3 (crop). Единого 2:3 у источников нет, crop — нейтральный дефолт.
 */
const val PosterAspectRatio: Float = 2f / 3f
