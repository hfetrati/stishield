package com.hemad.stishield.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hemad.stishield.R
import com.hemad.stishield.model.common.ChatItem
import com.hemad.stishield.model.common.Message
import com.hemad.stishield.ui.theme.STIShieldTheme
import com.hemad.stishield.ui.utilities.ErrorPopup
import com.hemad.stishield.ui.utilities.ImageAppBar
import com.hemad.stishield.ui.utilities.MessageBubbleWithButtons
import com.hemad.stishield.ui.utilities.TypingAnimation
import com.hemad.stishield.ui.utilities.formatTextWithHtmlTags
import com.hemad.stishield.ui.utilities.popBackStackOrIgnore
import com.hemad.stishield.viewmodels.RiskAssessmentScreenViewModel

@Composable
fun RiskAssessmentScreen (navController: NavHostController, persona:String) {

    val context = LocalContext.current
    val viewModel: RiskAssessmentScreenViewModel = viewModel{
        RiskAssessmentScreenViewModel(context = context)
    }
    val messages =  viewModel.messageListState
    val listState = viewModel.messageListScrollState
    val buttonsEnabledStateList = viewModel.buttonsEnabledStateList
    var consultantName:String = if (persona == "obinna") "Dr. Obinna" else "Dr. Hana"
    var consultantImageID:Int = if (persona == "obinna") R.drawable.obinna else  R.drawable.hana
    val errorState = viewModel.errorState
    val onButtonPressedFunc:(Int, String) -> Unit = {
        questionID,buttonTitle ->
        viewModel.onChoiceButtonPressed(questionID,buttonTitle)

    }

    STIShieldTheme {

        if (errorState.value != null) {
            ErrorPopup(errorState.value!!, onDismiss = { viewModel.clearError() })
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
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
                        state = listState
                    ) {
                        items(messages) { item ->
                            when (item) {
                                is ChatItem.MessageItem -> MessageBubbleWithButtons(
                                    text = formatTextWithHtmlTags(item.message.body),
                                    role = item.message.role,
                                    imageID = if (item.message.role == Message.SenderRole.BOT) consultantImageID else R.drawable.default_user,
                                    buttonTitles = item.message.buttonTitles,
                                    buttonsEnabledState = item.message.id?.let { buttonsEnabledStateList[it] },
                                    id = item.message.id,
                                    onButtonPressed = onButtonPressedFunc
                                )

                                is ChatItem.LoadingIndicator -> {
                                    TypingAnimation()
                                }

                                is ChatItem.MessageItemWithServerResult -> {

                                    val message = item.message
                                    MessageBubbleWithButtons(
                                        text = formatTextWithHtmlTags(message.body),
                                        role = message.role,
                                        imageID = if (message.role == Message.SenderRole.BOT) consultantImageID else R.drawable.default_user,
                                        buttonTitles = message.buttonTitles,
                                        buttonsEnabledState = message.id?.let { buttonsEnabledStateList[it] },
                                        id = message.id,
                                        onButtonPressed = onButtonPressedFunc
                                    )

                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

