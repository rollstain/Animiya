package com.issildur.animiya.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.issildur.animiya.core.utils.AppError
import com.issildur.animiya.uikit.component.ErrorState

/**
 * Готовое состояние ошибки по [AppError]: маппит в текст и рисует generic
 * [ErrorState] из design-системы. Убирает дублирование error-экрана по фичам.
 */
@Composable
fun AppErrorContent(
    error: AppError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ErrorState(
        message = error.toReadableMessage(),
        onRetry = onRetry,
        modifier = modifier,
    )
}
