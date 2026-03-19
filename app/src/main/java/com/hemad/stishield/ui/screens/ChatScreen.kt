package com.hemad.stishield.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hemad.stishield.R
import com.hemad.stishield.model.common.ChatItem
import com.hemad.stishield.model.common.Message
import com.hemad.stishield.ui.theme.STIShieldTheme
import com.hemad.stishield.ui.utilities.ImageAppBar
import com.hemad.stishield.ui.utilities.CustomButton
import com.hemad.stishield.ui.utilities.CustomTextField
import com.hemad.stishield.ui.utilities.ErrorPopup
import com.hemad.stishield.ui.utilities.MessageBubble
import com.hemad.stishield.ui.utilities.TypingAnimation
import com.hemad.stishield.ui.utilities.beautifyMarkdownString
import com.hemad.stishield.ui.utilities.popBackStackOrIgnore
import com.hemad.stishield.viewmodels.ChatScreenViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(navController: NavHostController,persona:String) {

    val viewModel: ChatScreenViewModel = viewModel{
        ChatScreenViewModel(persona)
    }
    val textFieldContent = viewModel.textFieldState
    val messages = viewModel.messageListState
    val listState = viewModel.messageListScrollState
    val errorState = viewModel.errorState
    val processState = viewModel.processState
    val keyboardVisibility = WindowInsets.isImeVisible
    val scope = rememberCoroutineScope()
    var consultantName:String = if (persona == "obinna") "Dr. Obinna" else "Dr. Hana"
    var consultantImageID:Int = if (persona == "obinna") R.drawable.obinna else R.drawable.hana
    
    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        viewModel.loadFirstMessage()
    }

    DisposableEffect(Unit) {
        onDispose {
            scope.launch {
                viewModel.clearHistory()
            }
        }
    }

    LaunchedEffect(key1 = keyboardVisibility) {

        if (keyboardVisibility == true) {
            viewModel.scrollToLastItem()
        }
    }

    STIShieldTheme {

        if (errorState.value != null) {
            ErrorPopup(errorState.value!!, onDismiss = { viewModel.clearError() })
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding(),
            topBar = {
                ImageAppBar(
                    name = consultantName,
                    imageID = consultantImageID,
                    onBackClick = {navController.popBackStackOrIgnore()},
                    onRefreshClick = {viewModel.refresh()}
                )
            }
        ) { innerPadding ->

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()

                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f),
                        state = listState

                    ) {
                        items(messages) {item ->
                            when (item) {
                                is ChatItem.MessageItem -> MessageBubble(
                                    text = beautifyMarkdownString(item.message.body),
                                    role = item.message.role,
                                    imageID = if (item.message.role == Message.SenderRole.BOT) consultantImageID else R.drawable.default_user
                                )
                                is ChatItem.LoadingIndicator -> {
                                    TypingAnimation()
                                }

                                is ChatItem.MessageItemWithServerResult -> null
                            }
                        }

                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shadowElevation = 100.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            CustomTextField(
                                modifier = Modifier.weight(1f),
                                placeholder = "Enter your message",
                                text = textFieldContent.value,
                                onValueChangeFunc = { viewModel.onTextFieldContentChanged(it) },
                                trailingIconFunc = { viewModel.clearTextField() },
                            )

                            CustomButton(enabled = textFieldContent.value.isNotEmpty() == true && processState.value == false) {
                                viewModel.onSendButtonPressed()
                            }

                        }
                    }
                }
            }

        }
    }
}
