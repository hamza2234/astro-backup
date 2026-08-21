/*
 * Copyright (c) 2025 Vitor Pamplona
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.vitorpamplona.amethyst.ui.screen.loggedIn.chats.privateDM.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.vitorpamplona.amethyst.R
import com.vitorpamplona.amethyst.commons.model.privateChats.Chatroom

@Immutable
data class MessengerChatPalette(
    val id: String,
    val nameRes: Int,
    val background: Color,
    val me: Color,
    val them: Color,
    val accent: Color,
    @DrawableRes val wallpaper: Int,
)

private data class MessengerThemeSpec(
    val id: String,
    val nameRes: Int,
    val bgLight: Color,
    val bgDark: Color,
    val meLight: Color,
    val meDark: Color,
    val themLight: Color,
    val themDark: Color,
    val accent: Color,
    @DrawableRes val wallpaper: Int,
) {
    fun resolve(isLight: Boolean) =
        MessengerChatPalette(
            id = id,
            nameRes = nameRes,
            background = if (isLight) bgLight else bgDark,
            me = if (isLight) meLight else meDark,
            them = if (isLight) themLight else themDark,
            accent = accent,
            wallpaper = wallpaper,
        )
}

private val Catalog =
    listOf(
        MessengerThemeSpec("classic", R.string.chat_theme_classic, Color(0xFFECE5DD), Color(0xFF0B141A), Color(0xFFD9FDD3), Color(0xFF005C4B), Color(0xFFFFFFFF), Color(0xFF1F2C34), Color(0xFF00A884), R.drawable.chat_bg_classic),
        MessengerThemeSpec("midnight", R.string.chat_theme_midnight, Color(0xFFE8EAF0), Color(0xFF0B1020), Color(0xFF5B6CFF).copy(alpha = 0.28f), Color(0xFF5B6CFF).copy(alpha = 0.42f), Color(0xFFD5DAE8), Color(0xFF1C2438), Color(0xFF7C8CFF), R.drawable.chat_bg_midnight),
        MessengerThemeSpec("ocean", R.string.chat_theme_ocean, Color(0xFFE4F4F8), Color(0xFF062028), Color(0xFF00A8C6).copy(alpha = 0.28f), Color(0xFF00A8C6).copy(alpha = 0.42f), Color(0xFFD2E8EE), Color(0xFF14343C), Color(0xFF00B4D8), R.drawable.chat_bg_ocean),
        MessengerThemeSpec("sunset", R.string.chat_theme_sunset, Color(0xFFFFF1E6), Color(0xFF24140C), Color(0xFFFF7A45).copy(alpha = 0.28f), Color(0xFFFF7A45).copy(alpha = 0.40f), Color(0xFFFFE0CC), Color(0xFF3A2418), Color(0xFFFF7A45), R.drawable.chat_bg_sunset),
        MessengerThemeSpec("forest", R.string.chat_theme_forest, Color(0xFFEAF3E8), Color(0xFF101810), Color(0xFF2E8B57).copy(alpha = 0.28f), Color(0xFF3CB371).copy(alpha = 0.40f), Color(0xFFD5E6D2), Color(0xFF1C2C1C), Color(0xFF2E8B57), R.drawable.chat_bg_forest),
        MessengerThemeSpec("rose", R.string.chat_theme_rose, Color(0xFFFFEEF2), Color(0xFF241018), Color(0xFFE85D8C).copy(alpha = 0.26f), Color(0xFFE85D8C).copy(alpha = 0.40f), Color(0xFFF8D6E0), Color(0xFF3A1C28), Color(0xFFE85D8C), R.drawable.chat_bg_rose),
        MessengerThemeSpec("sand", R.string.chat_theme_sand, Color(0xFFF7F0E4), Color(0xFF1C1810), Color(0xFFC4A574).copy(alpha = 0.40f), Color(0xFFC4A574).copy(alpha = 0.35f), Color(0xFFE8DCC8), Color(0xFF2C281C), Color(0xFFC4A574), R.drawable.chat_bg_sand),
        MessengerThemeSpec("lavender", R.string.chat_theme_lavender, Color(0xFFF3EEF8), Color(0xFF181020), Color(0xFF8B6CC4).copy(alpha = 0.26f), Color(0xFF8B6CC4).copy(alpha = 0.42f), Color(0xFFE4D8F0), Color(0xFF2C2038), Color(0xFF8B6CC4), R.drawable.chat_bg_lavender),
        MessengerThemeSpec("ember", R.string.chat_theme_ember, Color(0xFFFFF0EA), Color(0xFF200C0C), Color(0xFFE53935).copy(alpha = 0.22f), Color(0xFFE53935).copy(alpha = 0.38f), Color(0xFFF5D8D4), Color(0xFF381818), Color(0xFFE53935), R.drawable.chat_bg_ember),
        MessengerThemeSpec("ice", R.string.chat_theme_ice, Color(0xFFEEF6FA), Color(0xFF0C1820), Color(0xFF4FC3F7).copy(alpha = 0.30f), Color(0xFF4FC3F7).copy(alpha = 0.38f), Color(0xFFD8E8F0), Color(0xFF1C2C38), Color(0xFF4FC3F7), R.drawable.chat_bg_ice),
        MessengerThemeSpec("graphite", R.string.chat_theme_graphite, Color(0xFFEEEEEE), Color(0xFF121212), Color(0xFF607D8B).copy(alpha = 0.28f), Color(0xFF90A4AE).copy(alpha = 0.32f), Color(0xFFE0E0E0), Color(0xFF2A2A2A), Color(0xFF90A4AE), R.drawable.chat_bg_graphite),
        MessengerThemeSpec("matcha", R.string.chat_theme_matcha, Color(0xFFF2F6E8), Color(0xFF14180C), Color(0xFF7CB342).copy(alpha = 0.28f), Color(0xFF9CCC65).copy(alpha = 0.35f), Color(0xFFE4ECC8), Color(0xFF242C14), Color(0xFF7CB342), R.drawable.chat_bg_matcha),
        MessengerThemeSpec("coral", R.string.chat_theme_coral, Color(0xFFFFF3EE), Color(0xFF241410), Color(0xFFFF7043).copy(alpha = 0.26f), Color(0xFFFF7043).copy(alpha = 0.40f), Color(0xFFFFE0D4), Color(0xFF3A241C), Color(0xFFFF7043), R.drawable.chat_bg_coral),
        MessengerThemeSpec("grape", R.string.chat_theme_grape, Color(0xFFF6EEF8), Color(0xFF180C20), Color(0xFFAB47BC).copy(alpha = 0.24f), Color(0xFFAB47BC).copy(alpha = 0.40f), Color(0xFFE8D4EE), Color(0xFF301828), Color(0xFFAB47BC), R.drawable.chat_bg_grape),
        MessengerThemeSpec("amber", R.string.chat_theme_amber, Color(0xFFFFF8E8), Color(0xFF20180C), Color(0xFFFFB300).copy(alpha = 0.32f), Color(0xFFFFB300).copy(alpha = 0.32f), Color(0xFFFFEFC0), Color(0xFF382C14), Color(0xFFFFB300), R.drawable.chat_bg_amber),
    )

fun messengerThemeIds(): List<String> = Catalog.map { it.id }

fun resolveMessengerChatPalette(
    themeId: String,
    isLight: Boolean,
): MessengerChatPalette {
    val spec = Catalog.firstOrNull { it.id == themeId } ?: Catalog.first()
    return spec.resolve(isLight)
}

val LocalMessengerChatPalette = compositionLocalOf<MessengerChatPalette?> { null }

fun defaultMessengerThemeId(): String = Chatroom.DEFAULT_CHAT_THEME_ID
