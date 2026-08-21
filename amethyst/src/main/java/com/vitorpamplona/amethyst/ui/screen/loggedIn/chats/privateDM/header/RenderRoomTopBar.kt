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
package com.vitorpamplona.amethyst.ui.screen.loggedIn.chats.privateDM.header

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitorpamplona.amethyst.R
import com.vitorpamplona.amethyst.commons.icons.symbols.Icon
import com.vitorpamplona.amethyst.commons.icons.symbols.MaterialSymbols
import com.vitorpamplona.amethyst.commons.model.privateChats.ChatControlMessage
import com.vitorpamplona.amethyst.ui.navigation.navs.INav
import com.vitorpamplona.amethyst.ui.navigation.topbars.ShorterTopAppBar
import com.vitorpamplona.amethyst.ui.navigation.topbars.TopBarExtensibleWithBackButton
import com.vitorpamplona.amethyst.ui.note.ArrowBackIcon
import com.vitorpamplona.amethyst.ui.note.ClickableUserPicture
import com.vitorpamplona.amethyst.ui.note.NonClickableUserPictures
import com.vitorpamplona.amethyst.ui.note.ShowFollowingOrUnfollowingButton
import com.vitorpamplona.amethyst.ui.note.UserCompose
import com.vitorpamplona.amethyst.ui.note.UsernameDisplay
import com.vitorpamplona.amethyst.ui.screen.loggedIn.AccountViewModel
import com.vitorpamplona.amethyst.ui.screen.loggedIn.chats.rooms.LoadUser
import com.vitorpamplona.amethyst.ui.stringRes
import com.vitorpamplona.amethyst.ui.theme.DividerThickness
import com.vitorpamplona.amethyst.ui.theme.DoubleHorzSpacer
import com.vitorpamplona.amethyst.ui.theme.Size34dp
import com.vitorpamplona.amethyst.ui.theme.Size40dp
import com.vitorpamplona.amethyst.ui.theme.StdPadding
import com.vitorpamplona.amethyst.ui.theme.ZeroPadding
import com.vitorpamplona.amethyst.ui.theme.isLight
import com.vitorpamplona.quartz.nip17Dm.base.ChatroomKey
import com.vitorpamplona.quartz.utils.TimeUtils
import kotlinx.collections.immutable.toPersistentList

@Composable
fun RenderRoomTopBar(
    room: ChatroomKey,
    accountViewModel: AccountViewModel,
    nav: INav,
    onCallClick: ((String) -> Unit)? = null,
    onVideoCallClick: ((String) -> Unit)? = null,
) {
    if (room.users.size == 1) {
        MessengerPeerTopBar(room, accountViewModel, nav, onCallClick, onVideoCallClick)
    } else {
        TopBarExtensibleWithBackButton(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NonClickableUserPictures(
                        room = room,
                        accountViewModel = accountViewModel,
                        size = Size34dp,
                    )

                    RoomNameOnlyDisplay(room, Modifier.padding(start = 10.dp).weight(1f), FontWeight.Normal, accountViewModel)

                    if (onVideoCallClick != null) {
                        IconButton(
                            onClick = { onVideoCallClick(room.users.joinToString(",")) },
                            modifier = Modifier.size(40.dp),
                        ) {
                            Icon(
                                symbol = MaterialSymbols.Videocam,
                                contentDescription = stringRes(R.string.call_video),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }

                    if (onCallClick != null) {
                        IconButton(
                            onClick = { onCallClick(room.users.joinToString(",")) },
                            modifier = Modifier.size(40.dp),
                        ) {
                            Icon(
                                symbol = MaterialSymbols.Call,
                                contentDescription = stringRes(R.string.call_voice),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            },
            extendableRow = {
                GroupMembersHeader(room = room, accountViewModel = accountViewModel, nav = nav)
            },
            popBack = nav::popBack,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessengerPeerTopBar(
    room: ChatroomKey,
    accountViewModel: AccountViewModel,
    nav: INav,
    onCallClick: ((String) -> Unit)?,
    onVideoCallClick: ((String) -> Unit)?,
) {
    var showStrip by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }
    var showLikePicker by remember { mutableStateOf(false) }
    val chatroom =
        remember(room, accountViewModel) {
            accountViewModel.account.chatroomList.getOrCreatePrivateChatroom(room)
        }
    val themeId by chatroom.sharedThemeId.collectAsStateWithLifecycle()
    val reactions = accountViewModel.reactionChoices()
    val topBarColor = if (MaterialTheme.colorScheme.isLight) Color(0xFF075E54) else Color(0xFF062720)
    val topBarContentColor = Color(0xFFE9FFF7)
    val topBarActionColor = Color(0xFF55F5C6)

    Column {
        ShorterTopAppBar(
            navigationIcon = {
                IconButton(onClick = nav::popBack) { ArrowBackIcon(topBarContentColor) }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarColor,
                    titleContentColor = topBarContentColor,
                    navigationIconContentColor = topBarContentColor,
                    actionIconContentColor = topBarActionColor,
                ),
            title = {
                LoadUser(baseUserHex = room.users.first(), accountViewModel) { baseUser ->
                    if (baseUser != null) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            ClickableUserPicture(
                                baseUser = baseUser,
                                accountViewModel = accountViewModel,
                                size = Size40dp,
                            )
                            Spacer(modifier = DoubleHorzSpacer)
                            UsernameDisplay(
                                baseUser,
                                Modifier
                                    .weight(1f)
                                    .clickable { showStrip = !showStrip },
                                fontWeight = FontWeight.SemiBold,
                                textColor = topBarContentColor,
                                accountViewModel = accountViewModel,
                            )
                            if (onVideoCallClick != null) {
                                IconButton(
                                    onClick = { onVideoCallClick(baseUser.pubkeyHex) },
                                    modifier = Modifier.size(40.dp),
                                ) {
                                    Icon(
                                        symbol = MaterialSymbols.Videocam,
                                        contentDescription = stringRes(R.string.call_video),
                                        tint = topBarActionColor,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }
                            if (onCallClick != null) {
                                IconButton(
                                    onClick = { onCallClick(baseUser.pubkeyHex) },
                                    modifier = Modifier.size(40.dp),
                                ) {
                                    Icon(
                                        symbol = MaterialSymbols.Call,
                                        contentDescription = stringRes(R.string.call_voice),
                                        tint = topBarActionColor,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            },
        )
        AnimatedVisibility(showStrip) {
            val elevation = if (MaterialTheme.colorScheme.isLight) 1.dp else 10.dp
            Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = elevation) {
                LoadUser(baseUserHex = room.users.first(), accountViewModel) { peer ->
                    if (peer != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ShowFollowingOrUnfollowingButton(peer, accountViewModel)
                            Spacer(Modifier.weight(1f))
                            Box {
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(
                                        symbol = MaterialSymbols.MoreVert,
                                        contentDescription = stringRes(R.string.chat_peer_options),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                    DropdownMenuItem(
                                        text = { Text(stringRes(R.string.chat_theme_title)) },
                                        onClick = {
                                            showMenu = false
                                            showThemePicker = true
                                        },
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringRes(R.string.chat_like_emoji)) },
                                        onClick = {
                                            showMenu = false
                                            showLikePicker = true
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showLikePicker) {
            AlertDialog(
                onDismissRequest = { showLikePicker = false },
                title = { Text(stringRes(R.string.chat_like_emoji)) },
                text = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        reactions.forEach { emoji ->
                            Text(
                                text = emoji,
                                modifier =
                                    Modifier
                                        .clickable {
                                            chatroom.composerLike.value = emoji
                                            showLikePicker = false
                                        }.padding(8.dp),
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLikePicker = false }) {
                        Text(stringRes(R.string.close))
                    }
                },
            )
        }
    }

    if (showThemePicker) {
        ChatThemePickerSheet(
            selectedId = themeId,
            onSelect = { id ->
                chatroom.applySharedTheme(id, TimeUtils.now())
                accountViewModel.sendEncryptedChatControl(room.users, ChatControlMessage.encodeTheme(id))
                showThemePicker = false
            },
            onDismiss = { showThemePicker = false },
        )
    }
}

@Composable
fun GroupMembersHeader(
    room: ChatroomKey,
    lineModifier: Modifier = StdPadding,
    accountViewModel: AccountViewModel,
    nav: INav,
) {
    val list = remember(room) { room.users.toPersistentList() }

    Row(
        modifier = Modifier.fillMaxWidth().padding(5.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringRes(id = R.string.messages_group_descriptor),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )

        EditRoomSubjectButton(room, accountViewModel)
    }

    LazyColumn(
        modifier = Modifier,
        state = rememberLazyListState(),
    ) {
        itemsIndexed(list, key = { _, item -> item }) { _, item ->
            LoadUser(baseUserHex = item, accountViewModel) {
                if (it != null) {
                    UserCompose(
                        baseUser = it,
                        modifier = lineModifier,
                        accountViewModel = accountViewModel,
                        nav = nav,
                    )
                    HorizontalDivider(
                        thickness = DividerThickness,
                    )
                }
            }
        }
    }
}

@Composable
private fun EditRoomSubjectButton(
    room: ChatroomKey,
    accountViewModel: AccountViewModel,
) {
    var wantsToPost by remember { mutableStateOf(false) }

    if (wantsToPost) {
        NewChatroomSubjectDialog({ wantsToPost = false }, accountViewModel, room)
    }

    FilledTonalButton(
        modifier =
            Modifier
                .padding(horizontal = 3.dp)
                .width(50.dp),
        onClick = { wantsToPost = true },
        contentPadding = ZeroPadding,
    ) {
        Icon(
            symbol = MaterialSymbols.EditNote,
            contentDescription = stringRes(R.string.edits_the_channel_metadata),
        )
    }
}
