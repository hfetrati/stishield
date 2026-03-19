package com.hemad.stishield.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hemad.stishield.R
import com.hemad.stishield.ui.theme.STIShieldTheme
import com.hemad.stishield.ui.utilities.CustomTextField
import com.hemad.stishield.ui.utilities.ErrorPopup
import com.hemad.stishield.ui.utilities.ProgressIndicator
import com.hemad.stishield.viewmodels.RegistrationScreenViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegistrationScreen(navController: NavHostController) {

    val context = LocalContext.current
    val viewModel: RegistrationScreenViewModel = viewModel{
        RegistrationScreenViewModel(context = context)
    }
    val emailFieldContent = viewModel.emailFieldState
    val nicknameFieldContent = viewModel.nicknameFieldState
    val processState = viewModel.processState
    val registrationSuccess = viewModel.registrationSuccessful
    val errorState = viewModel.errorState
    val isEmailValid = viewModel.isEmailValid
    val pageScrollState = viewModel.pageScrollState
    val keyboardVisibility = WindowInsets.isImeVisible
    val ON_SCREEN_INSTRUCTIONS = buildAnnotatedString {
        append("Please enter a nickname and a valid email address below to continue.\n\n")
        withStyle(style = SpanStyle(color = Color.Red)) {
            append("Note: Your nickname will be visible to others on the leaderboard.")
        }
    }

    LaunchedEffect(key1 = keyboardVisibility) {

        if (keyboardVisibility == true) {
            delay(200)
            viewModel.scrollPageToBottom()
        }
    }

    STIShieldTheme {

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding(),

            ) { innerPadding ->

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                if (processState.value) {
                    Dialog(onDismissRequest = {}) {
                        ProgressIndicator()
                    }
                }

                if (errorState.value != null) {
                    ErrorPopup(errorState.value!!, onDismiss = { viewModel.clearError() })
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(pageScrollState),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    Image(

                        painter = painterResource(id = R.drawable.logo_transparent_2),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 40.dp, bottom = 0.dp, start = 40.dp, end = 40.dp)
                            .size(160.dp)
                    )

                    Text(
                        modifier = Modifier.padding(20.dp),
                        text = "STIShield",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light
                    )

                    Text(
                        modifier = Modifier.padding(horizontal = 48.dp, vertical = 28.dp),
                        textAlign = TextAlign.Justify,
                        text = ON_SCREEN_INSTRUCTIONS
                    )

                    CustomTextField(
                        text = nicknameFieldContent.value,
                        placeholder = "Nickname",
                        onValueChangeFunc = { nicknameFieldContent.value = it }
                    )
                    CustomTextField(

                        text = emailFieldContent.value,
                        placeholder = "Email",
                        onValueChangeFunc = { emailFieldContent.value = it },
                    )

                    Button(
                        modifier = Modifier.padding(20.dp),
                        enabled = emailFieldContent.value.isNotEmpty() == true
                                && nicknameFieldContent.value.isNotEmpty() == true
                                && processState.value == false
                                && isEmailValid.value == true,
                        onClick = {
                            viewModel.registerNewUser(emailFieldContent.value, nicknameFieldContent.value)
                        }) {


                        Text(
                            modifier = Modifier.padding(8.dp),
                            fontSize = 16.sp,
                            text = "Register"
                        )
                    }

                    registrationSuccess.value?.let {
                        if (it) {
                            navController.navigate("main-screen"){
                                launchSingleTop = true
                            }
                        }
                    }


                }

            }

        }
    }
}
