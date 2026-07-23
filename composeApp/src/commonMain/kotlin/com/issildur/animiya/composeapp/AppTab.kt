package com.issildur.animiya.composeapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

/** Вкладки нижней навигации — по макету 01-home. */
enum class AppTab(val label: String, val icon: ImageVector) {
    Home("Главная", Icons.Outlined.Home),
    Catalog("Каталог", Icons.Outlined.GridView),
    My("Моё", Icons.Outlined.VideoLibrary),
    Profile("Профиль", Icons.Outlined.Person),
}
