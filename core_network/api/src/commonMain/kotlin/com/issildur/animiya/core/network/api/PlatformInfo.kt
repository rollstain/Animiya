package com.issildur.animiya.core.network.api

/**
 * Сведения о платформе для формирования User-Agent.
 *
 * Вынесено в интерфейс не ради абстракции как таковой: следующим источником
 * будет Shikimori, где осмысленный User-Agent обязателен под угрозой бана по IP,
 * а маскировка под браузер запрещена явно. Единая точка формирования UA
 * гарантирует, что это требование нельзя случайно нарушить.
 */
interface PlatformInfo {
    val appName: String
    val appVersion: String
    val platformName: String
    val osVersion: String
    val deviceModel: String

    val userAgent: String
        get() = "$appName/$appVersion ($platformName $osVersion; $deviceModel)"
}
