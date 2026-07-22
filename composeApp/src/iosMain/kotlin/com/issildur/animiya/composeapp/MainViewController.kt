package com.issildur.animiya.composeapp

import androidx.compose.ui.window.ComposeUIViewController
import com.issildur.animiya.composeapp.di.initKoin
import platform.UIKit.UIViewController

/**
 * Точка входа для Swift.
 *
 * Из Swift вызывается как `ComposeAppKt.mainViewController()` и оборачивается
 * в `UIViewControllerRepresentable`.
 */
fun mainViewController(): UIViewController = ComposeUIViewController { App() }

/** Инициализация DI со стороны Swift — до первого показа UI. */
fun initKoinIos(isDebug: Boolean) {
    initKoin(isDebug = isDebug)
}
