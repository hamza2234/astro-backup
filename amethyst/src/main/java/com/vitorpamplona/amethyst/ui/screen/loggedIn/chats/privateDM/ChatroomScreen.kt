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
package com.vitorpamplona.amethyst.ui.screen.loggedIn.chats.privateDM

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.vitorpamplona.amethyst.ui.call.CallActivity
import com.vitorpamplona.amethyst.ui.call.rememberCallWithPermission
import com.vitorpamplona.amethyst.ui.layouts.DisappearingScaffold
import com.vitorpamplona.amethyst.ui.navigation.navs.INav
import com.vitorpamplona.amethyst.ui.screen.loggedIn.AccountViewModel
import com.vitorpamplona.amethyst.ui.screen.loggedIn.chats.privateDM.header.RenderRoomTopBar
import com.vitorpamplona.amethyst.ui.screen.loggedIn.chats.privateDM.theme.resolveMessengerChatPalette
import com.vitorpamplona.amethyst.ui.theme.isLight
import com.vitorpamplona.quartz.nip01Core.core.HexKey
import com.vitorpamplona.quartz.nip17Dm.base.ChatroomKey
import com.vitorpamplona.quartz.nipACWebRtcCalls.tags.CallType

@Composable
fun ChatroomScreen(
    roomId: ChatroomKey,
    draftMessage: String? = null,
    attachmentUri: String? = null,
    replyToNote: HexKey? = null,
    editFromDraft: HexKey? = null,
    expiresDays: Int? = null,
    accountViewModel: AccountViewModel,
    nav: INav,
) {
    val context = LocalContext.current
    val view = LocalView.current
    val callsEnabled by accountViewModel.account.settings.callsEnabled
        .collectAsState()
    val isCallSupported = roomId.users.size <= 5 && callsEnabled
    val startVoiceCall =
        rememberCallWithPermission(context) {
            CallActivity.launchForOutgoingCall(
                context,
                roomId.users.toSet(),
                CallType.VOICE,
            )
        }
    val startVideoCall =
        rememberCallWithPermission(context, isVideo = true) {
            CallActivity.launchForOutgoingCall(
                context,
                roomId.users.toSet(),
                CallType.VIDEO,
            )
        }

    if (roomId.users.size == 1) {
        val chatroom =
            remember(roomId, accountViewModel) {
                accountViewModel.account.chatroomList.getOrCreatePrivateChatroom(roomId)
            }
        val themeId by chatroom.sharedThemeId.collectAsState()
        val palette = resolveMessengerChatPalette(themeId, MaterialTheme.colorScheme.isLight)
        val systemBarColor = palette.accent.copy(alpha = 0.20f).compositeOver(Color(0xFF062720))
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window
            val insets = WindowCompat.getInsetsController(window, view)
            insets.isAppearanceLightNavigationBars = false
            insets.isAppearanceLightStatusBars = false
            @Suppress("DEPRECATION")
            window.statusBarColor = systemBarColor.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = systemBarColor.toArgb()
        }
    }

    DisappearingScaffold(
        isInvertedLayout = true,
        topBar = {
            RenderRoomTopBar(
                room = roomId,
                accountViewModel = accountViewModel,
                nav = nav,
                onCallClick = if (isCallSupported) ({ _ -> startVoiceCall() }) else null,
                onVideoCallClick = if (isCallSupported) ({ _ -> startVideoCall() }) else null,
            )
        },
        accountViewModel = accountViewModel,
        allowBarHide = false,
    ) {
        Column(Modifier.padding(it)) {
            ChatroomView(
                room = roomId,
                draftMessage = draftMessage,
                attachmentUri = attachmentUri,
                replyToNote = replyToNote,
                editFromDraft = editFromDraft,
                expiresDays = expiresDays,
                accountViewModel = accountViewModel,
                nav = nav,
            )
        }
    }
}
