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
package com.vitorpamplona.amethyst.ui.screen.loggedIn.chats.privateDM.send

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitorpamplona.amethyst.R
import com.vitorpamplona.amethyst.model.LocalCache
import com.vitorpamplona.amethyst.model.User
import com.vitorpamplona.amethyst.ui.actions.MentionPreservingInputTransformation
import com.vitorpamplona.amethyst.ui.actions.StrippingFailureDialog
import com.vitorpamplona.amethyst.ui.actions.UrlUserTagOutputTransformation
import com.vitorpamplona.amethyst.ui.actions.uploads.SelectFromGallery
import com.vitorpamplona.amethyst.ui.actions.uploads.SelectedMedia
import com.vitorpamplona.amethyst.ui.components.ThinPaddingTextField
import com.vitorpamplona.amethyst.ui.navigation.bottombars.KeyboardAwareBackHandler
import com.vitorpamplona.amethyst.ui.navigation.navs.EmptyNav
import com.vitorpamplona.amethyst.ui.navigation.navs.INav
import com.vitorpamplona.amethyst.ui.navigation.routes.routeFor
import com.vitorpamplona.amethyst.ui.note.ClickableUserPicture
import com.vitorpamplona.amethyst.ui.note.creators.emojiSuggestions.ShowEmojiSuggestionList
import com.vitorpamplona.amethyst.ui.note.creators.userSuggestions.ShowUserSuggestionList
import com.vitorpamplona.amethyst.ui.note.showCount
import com.vitorpamplona.amethyst.ui.note.timeAheadNoDot
import com.vitorpamplona.amethyst.ui.screen.loggedIn.AccountViewModel
import com.vitorpamplona.amethyst.ui.screen.loggedIn.chats.feed.LocalMessengerDmStyle
import com.vitorpamplona.amethyst.ui.screen.loggedIn.chats.feed.LocalMessengerLikeReaction
import com.vitorpamplona.amethyst.ui.screen.loggedIn.chats.privateDM.send.upload.RoomChatFileUploadDialog
import com.vitorpamplona.amethyst.ui.screen.loggedIn.chats.privateDM.theme.LocalMessengerChatPalette
import com.vitorpamplona.amethyst.ui.screen.loggedIn.chats.utils.DisplayReplyingToNote
import com.vitorpamplona.amethyst.ui.screen.loggedIn.chats.utils.ThinSendButton
import com.vitorpamplona.amethyst.ui.screen.loggedIn.mockAccountViewModel
import com.vitorpamplona.amethyst.ui.stringRes
import com.vitorpamplona.amethyst.ui.theme.EditFieldBorder
import com.vitorpamplona.amethyst.ui.theme.EditFieldModifier
import com.vitorpamplona.amethyst.ui.theme.EditFieldTrailingIconModifier
import com.vitorpamplona.amethyst.ui.theme.Font12SP
import com.vitorpamplona.amethyst.ui.theme.PostKeyboard
import com.vitorpamplona.amethyst.ui.theme.Size25dp
import com.vitorpamplona.amethyst.ui.theme.SpacedBy10dp
import com.vitorpamplona.amethyst.ui.theme.SuggestionListDefaultHeightChat
import com.vitorpamplona.amethyst.ui.theme.ThemeComparisonColumn
import com.vitorpamplona.amethyst.ui.theme.isLight
import com.vitorpamplona.amethyst.ui.theme.placeholderText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Preview
@Composable
fun PrivateMessageEditFieldRowPreview() {
    val channelScreenModel: ChatNewMessageViewModel = viewModel()
    val accountViewModel = mockAccountViewModel()
    channelScreenModel.init(accountViewModel)

    ThemeComparisonColumn {
        PrivateMessageEditFieldRow(
            channelScreenModel = channelScreenModel,
            accountViewModel = accountViewModel,
            onSendNewMessage = {},
            nav = EmptyNav(),
        )
    }
}

@Composable
fun PrivateMessageEditFieldRow(
    channelScreenModel: ChatNewMessageViewModel,
    accountViewModel: AccountViewModel,
    onSendNewMessage: () -> Unit,
    nav: INav,
) {
    KeyboardAwareBackHandler {
        if (channelScreenModel.message.text.isNotBlank()) {
            accountViewModel.launchSigner {
                channelScreenModel.sendDraftSync()
                // Rotate the draft tag only AFTER the async save completes. Doing it
                // synchronously here (before launchSigner runs) would make sendDraftSync
                // persist under a freshly-rotated tag, duplicating the draft. See the
                // matching order in NewGroupDMScreen.
                channelScreenModel.cancel()
            }
        } else {
            channelScreenModel.cancel()
        }
        nav.popBack()
    }

    channelScreenModel.replyTo.value?.let {
        DisplayReplyingToNote(it, accountViewModel, nav) {
            channelScreenModel.clearReply()
        }
    }

    channelScreenModel.uploadState?.let { uploading ->
        uploading.multiOrchestrator?.let { _ ->
            RoomChatFileUploadDialog(
                channelScreenModel = channelScreenModel,
                state = uploading,
                onUpload = onSendNewMessage,
                onCancel = uploading::reset,
                accountViewModel = accountViewModel,
                nav = nav,
            )
        }
    }

    StrippingFailureDialog(channelScreenModel.strippingFailureConfirmation)

    channelScreenModel.encryptedUploadErrorTitle?.let { title ->
        EncryptedUploadErrorDialog(
            title = title,
            message = channelScreenModel.encryptedUploadErrorMessage ?: "",
            onDismiss = channelScreenModel::dismissEncryptedUploadError,
            onRetryWithoutEncryption = channelScreenModel::retryWithoutEncryption,
        )
    }

    val messengerStyle = LocalMessengerDmStyle.current
    val composerModifier =
        if (messengerStyle) {
            Modifier
                .fillMaxWidth()
                .background(
                    Color.Black.copy(
                        alpha = if (MaterialTheme.colorScheme.isLight) 0.08f else 0.22f,
                    ),
                ).padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 8.dp)
        } else {
            EditFieldModifier
        }

    Column(modifier = composerModifier) {
        channelScreenModel.userSuggestions?.let {
            ShowUserSuggestionList(
                it,
                channelScreenModel::autocompleteWithUser,
                accountViewModel,
                SuggestionListDefaultHeightChat,
            )
        }

        channelScreenModel.emojiSuggestions?.let {
            ShowEmojiSuggestionList(
                it,
                channelScreenModel::autocompleteWithEmoji,
                channelScreenModel::autocompleteWithEmojiUrl,
                SuggestionListDefaultHeightChat,
            )
        }

        if (channelScreenModel.wantsExpirationDate) {
            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.Center) {
                val context = LocalContext.current
                Text(
                    stringRes(
                        R.string.this_message_will_disappear_in,
                        timeAheadNoDot(channelScreenModel.expirationDate, context),
                    ),
                    fontSize = Font12SP,
                    color = MaterialTheme.colorScheme.placeholderText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        val missingRelays by channelScreenModel.recipientsMissingDmRelays.collectAsStateWithLifecycle()
        if (missingRelays.isNotEmpty()) {
            RecipientMissingRelaysWarning(missingRelays, accountViewModel, nav)
        } else {
            EditField(channelScreenModel, onSendNewMessage, accountViewModel)
        }
    }
}

@Composable
fun EditField(
    channelScreenModel: ChatNewMessageViewModel,
    onSendNewMessage: () -> Unit,
    accountViewModel: AccountViewModel,
) {
    val messengerStyle = LocalMessengerDmStyle.current
    val messengerPalette = LocalMessengerChatPalette.current
    val messengerFieldColor =
        if (MaterialTheme.colorScheme.isLight) {
            Color.White.copy(alpha = 0.94f)
        } else {
            Color(0xFF102A24).copy(alpha = 0.94f)
        }

    ThinPaddingTextField(
        state = channelScreenModel.message,
        onTextChanged = { channelScreenModel.onMessageChanged() },
        onContentReceived = { uri, mimeType ->
            channelScreenModel.pickedMedia(persistentListOf(SelectedMedia(uri, mimeType)))
        },
        inputTransformation = MentionPreservingInputTransformation,
        keyboardOptions = PostKeyboard,
        shape = if (messengerStyle) RoundedCornerShape(28.dp) else EditFieldBorder,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = stringRes(R.string.reply_here),
                color = MaterialTheme.colorScheme.placeholderText,
            )
        },
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ThinSendButton(
                    isActive = channelScreenModel.canPost(),
                    modifier = EditFieldTrailingIconModifier,
                ) {
                    accountViewModel.launchSigner {
                        channelScreenModel.sendPostSync()
                        onSendNewMessage()
                    }
                }
                val likeEmoji = LocalMessengerLikeReaction.current
                if (messengerStyle) {
                    IconButton(
                        enabled = accountViewModel.isWriteable(),
                        modifier =
                            Modifier
                                .padding(end = 2.dp)
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    (messengerPalette?.accent ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.12f),
                                ),
                        onClick = {
                            accountViewModel.launchSigner {
                                channelScreenModel.sendQuickEmoji(likeEmoji)
                                onSendNewMessage()
                            }
                        },
                    ) {
                        Text(text = likeEmoji, fontSize = 18.sp)
                    }
                }
            }
        },
        leadingIcon = {
            KeyboardLeadingIcon(channelScreenModel, accountViewModel)
        },
        colors =
            if (messengerStyle) {
                TextFieldDefaults.colors(
                    focusedContainerColor = messengerFieldColor,
                    unfocusedContainerColor = messengerFieldColor,
                    disabledContainerColor = messengerFieldColor.copy(alpha = 0.6f),
                    cursorColor = messengerPalette?.accent ?: MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                )
            } else {
                TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            },
        contentPadding =
            if (messengerStyle) {
                PaddingValues(start = 6.dp, top = 8.dp, end = 4.dp, bottom = 8.dp)
            } else {
                PaddingValues(start = 10.dp, top = 12.dp, end = 10.dp, bottom = 12.dp)
            },
        outputTransformation = UrlUserTagOutputTransformation(MaterialTheme.colorScheme.primary),
    )
}

@Preview
@Composable
fun RecipientMissingRelaysWarningPreview() {
    val user1 = LocalCache.getOrCreateUser("460c25e682fda7832b52d1f22d3d22b3176d972f60dcdc3212ed8c92ef85065c")
    val user2 = LocalCache.getOrCreateUser("ca89cb11f1c75d5b6622268ff43d2288ea8b2cb5b9aa996ff9ff704fc904b78b")

    ThemeComparisonColumn {
        RecipientMissingRelaysWarning(
            persistentListOf(user1, user2),
            mockAccountViewModel(),
            EmptyNav(),
        )
    }
}

@Composable
fun RecipientMissingRelaysWarning(
    users: ImmutableList<User>,
    accountViewModel: AccountViewModel,
    nav: INav,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = SpacedBy10dp,
    ) {
        UserGallery(users) { user ->
            ClickableUserPicture(
                user,
                Size25dp,
                accountViewModel,
                onClick = {
                    nav.nav { routeFor(user) }
                },
            )
        }

        Text(
            text = stringRes(R.string.recipient_missing_dm_relays),
            color = MaterialTheme.colorScheme.error,
            fontSize = Font12SP,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun UserGallery(
    users: ImmutableList<User>,
    galleryUser: @Composable RowScope.(user: User) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((-10).dp),
    ) {
        users.take(6).forEach {
            key(it.pubkeyHex) {
                galleryUser(it)
            }
        }

        if (users.size > 6) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier
                        .size(Size25dp)
                        .clip(shape = CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Text(
                    text = "+" + showCount(users.size - 6),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
fun KeyboardLeadingIcon(
    channelScreenModel: ChatNewMessageViewModel,
    accountViewModel: AccountViewModel,
) {
    val messengerPalette = LocalMessengerChatPalette.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 4.dp, end = 4.dp),
    ) {
        SelectFromGallery(
            isUploading = channelScreenModel.isUploadingImage,
            tint = messengerPalette?.accent ?: MaterialTheme.colorScheme.placeholderText,
            modifier = Modifier,
            onImageChosen = channelScreenModel::pickedMedia,
        )
    }
}

@Composable
fun EncryptedUploadErrorDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onRetryWithoutEncryption: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(message)
                Text(
                    stringRes(R.string.upload_without_encryption_warning),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onRetryWithoutEncryption) {
                Text(stringRes(R.string.retry_without_encryption))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringRes(R.string.cancel))
            }
        },
    )
}
