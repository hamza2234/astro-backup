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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitorpamplona.amethyst.R
import com.vitorpamplona.amethyst.commons.icons.symbols.Icon
import com.vitorpamplona.amethyst.commons.icons.symbols.MaterialSymbol
import com.vitorpamplona.amethyst.commons.icons.symbols.MaterialSymbols
import com.vitorpamplona.amethyst.model.LocalCache
import com.vitorpamplona.amethyst.model.User
import com.vitorpamplona.amethyst.ui.actions.MentionPreservingInputTransformation
import com.vitorpamplona.amethyst.ui.actions.StrippingFailureDialog
import com.vitorpamplona.amethyst.ui.actions.UrlUserTagOutputTransformation
import com.vitorpamplona.amethyst.ui.actions.uploads.FileSelect
import com.vitorpamplona.amethyst.ui.actions.uploads.GallerySelect
import com.vitorpamplona.amethyst.ui.actions.uploads.SelectFromGallery
import com.vitorpamplona.amethyst.ui.actions.uploads.SelectedMedia
import com.vitorpamplona.amethyst.ui.actions.uploads.TakePicture
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
    val messengerPalette = LocalMessengerChatPalette.current
    val composerModifier =
        if (messengerStyle) {
            val composerColor =
                (messengerPalette?.accent ?: Color(0xFF55F5C6))
                    .copy(alpha = 0.20f)
                    .compositeOver(Color(0xFF062720))
            Modifier
                .fillMaxWidth()
                .background(composerColor)
                .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 8.dp)
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
    if (messengerStyle) {
        MessengerEditField(
            channelScreenModel = channelScreenModel,
            onSendNewMessage = onSendNewMessage,
            accountViewModel = accountViewModel,
            accent = messengerPalette?.accent ?: Color(0xFF55F5C6),
        )
        return
    }

    ThinPaddingTextField(
        state = channelScreenModel.message,
        onTextChanged = { channelScreenModel.onMessageChanged() },
        onContentReceived = { uri, mimeType ->
            channelScreenModel.pickedMedia(persistentListOf(SelectedMedia(uri, mimeType)))
        },
        inputTransformation = MentionPreservingInputTransformation,
        keyboardOptions = PostKeyboard,
        shape = EditFieldBorder,
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
            }
        },
        leadingIcon = {
            KeyboardLeadingIcon(channelScreenModel, accountViewModel)
        },
        colors =
            TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
        contentPadding =
            PaddingValues(start = 10.dp, top = 12.dp, end = 10.dp, bottom = 12.dp),
        outputTransformation = UrlUserTagOutputTransformation(MaterialTheme.colorScheme.primary),
    )
}

@Composable
private fun MessengerEditField(
    channelScreenModel: ChatNewMessageViewModel,
    onSendNewMessage: () -> Unit,
    accountViewModel: AccountViewModel,
    accent: Color,
) {
    val fieldColor = accent.copy(alpha = 0.16f).compositeOver(Color(0xFF09241F))
    val isTyping = channelScreenModel.message.text.isNotBlank()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            MessengerFileButton(
                enabled = !channelScreenModel.isUploadingFile && !channelScreenModel.isUploadingImage,
                tint = accent,
                onFilesChosen = channelScreenModel::pickedMedia,
            )

            MessengerCameraButton(
                enabled = !channelScreenModel.isUploadingFile && !channelScreenModel.isUploadingImage,
                tint = accent,
                onPictureTaken = channelScreenModel::pickedMedia,
            )

            MessengerGalleryButton(
                enabled = !channelScreenModel.isUploadingFile && !channelScreenModel.isUploadingImage,
                tint = accent,
                onImageChosen = channelScreenModel::pickedMedia,
            )

            MessengerStaticIcon(
                symbol = MaterialSymbols.Mic,
                tint = accent,
            )

            ThinPaddingTextField(
                state = channelScreenModel.message,
                onTextChanged = { channelScreenModel.onMessageChanged() },
                onContentReceived = { uri, mimeType ->
                    channelScreenModel.pickedMedia(persistentListOf(SelectedMedia(uri, mimeType)))
                },
                inputTransformation = MentionPreservingInputTransformation,
                keyboardOptions = PostKeyboard,
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = stringRes(R.string.reply_here),
                        color = Color.White.copy(alpha = 0.54f),
                    )
                },
                leadingIcon = {
                    Icon(
                        symbol = MaterialSymbols.EmojiEmotions,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(22.dp),
                    )
                },
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = fieldColor,
                        unfocusedContainerColor = fieldColor,
                        disabledContainerColor = fieldColor.copy(alpha = 0.6f),
                        cursorColor = accent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                contentPadding = PaddingValues(start = 4.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
                outputTransformation = UrlUserTagOutputTransformation(accent),
            )

            if (isTyping) {
                MessengerSendButton(
                    accent = accent,
                    enabled = channelScreenModel.canPost(),
                ) {
                    accountViewModel.launchSigner {
                        channelScreenModel.sendPostSync()
                        onSendNewMessage()
                    }
                }
            } else {
                val likeEmoji = LocalMessengerLikeReaction.current
                IconButton(
                    enabled = accountViewModel.isWriteable(),
                    modifier =
                        Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF123F3B)),
                    onClick = {
                        accountViewModel.launchSigner {
                            channelScreenModel.sendQuickEmoji(likeEmoji)
                            onSendNewMessage()
                        }
                    },
                ) {
                    Text(text = likeEmoji, fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
private fun MessengerFileButton(
    enabled: Boolean,
    tint: Color,
    onFilesChosen: (ImmutableList<SelectedMedia>) -> Unit,
) {
    var showFileSelect by remember { mutableStateOf(false) }
    if (showFileSelect) {
        FileSelect { files ->
            showFileSelect = false
            if (files.isNotEmpty()) {
                onFilesChosen(files)
            }
        }
    }

    MessengerToolbarIcon(
        symbol = MaterialSymbols.Add,
        contentDescription = stringRes(id = R.string.upload_file),
        enabled = enabled,
        tint = tint,
        onClick = { showFileSelect = true },
    )
}

@Composable
private fun MessengerCameraButton(
    enabled: Boolean,
    tint: Color,
    onPictureTaken: (ImmutableList<SelectedMedia>) -> Unit,
) {
    var showCamera by remember { mutableStateOf(false) }
    if (showCamera) {
        TakePicture { media ->
            showCamera = false
            if (media.isNotEmpty()) {
                onPictureTaken(media)
            }
        }
    }

    MessengerToolbarIcon(
        symbol = MaterialSymbols.CameraAlt,
        contentDescription = stringRes(id = R.string.take_a_picture),
        enabled = enabled,
        tint = tint,
        onClick = { showCamera = true },
    )
}

@Composable
private fun MessengerGalleryButton(
    enabled: Boolean,
    tint: Color,
    onImageChosen: (ImmutableList<SelectedMedia>) -> Unit,
) {
    var showGallerySelect by remember { mutableStateOf(false) }
    if (showGallerySelect) {
        GallerySelect { media ->
            showGallerySelect = false
            if (media.isNotEmpty()) {
                onImageChosen(media)
            }
        }
    }

    MessengerToolbarIcon(
        symbol = MaterialSymbols.Image,
        contentDescription = stringRes(id = R.string.upload_image),
        enabled = enabled,
        tint = tint,
        onClick = { showGallerySelect = true },
    )
}

@Composable
private fun MessengerToolbarIcon(
    symbol: MaterialSymbol,
    contentDescription: String,
    enabled: Boolean,
    tint: Color,
    onClick: () -> Unit,
) {
    IconButton(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier.size(38.dp),
    ) {
        Icon(
            symbol = symbol,
            contentDescription = contentDescription,
            modifier = Modifier.size(23.dp),
            tint = if (enabled) tint else tint.copy(alpha = 0.38f),
        )
    }
}

@Composable
private fun MessengerStaticIcon(
    symbol: MaterialSymbol,
    tint: Color,
) {
    IconButton(
        enabled = false,
        onClick = {},
        modifier = Modifier.size(38.dp),
    ) {
        Icon(
            symbol = symbol,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = tint.copy(alpha = 0.62f),
        )
    }
}

@Composable
private fun MessengerSendButton(
    accent: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        enabled = enabled,
        modifier =
            Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (enabled) accent else Color(0xFF123F3B)),
        onClick = onClick,
    ) {
        Icon(
            symbol = MaterialSymbols.AutoMirrored.Send,
            contentDescription = stringRes(id = R.string.accessibility_send),
            modifier = Modifier.size(22.dp),
            tint = if (enabled) Color(0xFF062720) else accent.copy(alpha = 0.48f),
        )
    }
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
