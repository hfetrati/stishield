package com.hemad.stishield.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.hemad.stishield.R
import com.hemad.stishield.ui.theme.STIShieldTheme
import com.hemad.stishield.ui.theme.lora
import com.hemad.stishield.ui.utilities.ErrorPopup
import com.hemad.stishield.ui.utilities.ProgressIndicator
import com.hemad.stishield.ui.utilities.ScrollTextPopup
import com.hemad.stishield.ui.utilities.TextAppBar
import com.hemad.stishield.ui.utilities.WoodenButton
import com.hemad.stishield.ui.utilities.popBackStackOrIgnore
import com.hemad.stishield.viewmodels.GameScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen (navController: NavHostController) {

    val context = LocalContext.current
    val viewModel: GameScreenViewModel = viewModel{
        GameScreenViewModel(context = context)
    }
    val popUpText = viewModel.popUpText
    val showFactPopUp = viewModel.showSTIFactPopUpState
    val showRestartPopUP = viewModel.showRestartGameConfirmationPopUpState
    val mainText = viewModel.mainTextState
    val buttonTitles = viewModel.buttonTitles
    val correctChoiceIndex = viewModel.correctChoiceIndex
    val pageTitle = viewModel.pageTitle
    val imageID = viewModel.imageIDState
    val screenScrollState = viewModel.screenScrollState
    val textScrollState = viewModel.textScrollState
    val dropdownExpanded = viewModel.dropdownExpanded
    val onButtonPressedFunc:(String) -> Unit = {
            buttonTitle ->
        viewModel.onButtonPressed(buttonTitle)
    }
    val showTheEndImage = viewModel.showTheEndImage
    val processState = viewModel.processState
    val errorState = viewModel.errorState
    val scope = rememberCoroutineScope()


    STIShieldTheme {

        if (showRestartPopUP.value) {
            AlertDialog(
                onDismissRequest = { showRestartPopUP.value = false },
                title = {
                    Text(text = "Confirm Restart")
                },
                text = {
                    Text("Are you sure you want to restart the game? All your progress will be lost.")
                },
                confirmButton = {
                    Button(onClick = {
                        showRestartPopUP.value = false
                        viewModel.restartGame()
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showRestartPopUP.value = false
                    }) {
                        Text("No")
                    }
                }
            )
        }

        if (showFactPopUp.value) {
            ScrollTextPopup(
                text = popUpText.value ,
                onDismiss = { viewModel.dismissPopUp() }
            )
        }

        if (processState.value) {
            Dialog(onDismissRequest = {}) {
                ProgressIndicator()
            }
        }

        if (errorState.value != null) {
            ErrorPopup(errorState.value!!, onDismiss = { viewModel.clearError() })
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),

            topBar = {
                TextAppBar(
                    title = pageTitle.value,
                    fontFamily = lora,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    backgroundColor = Color(0xFFFDEACA),
                    onBackClick = {navController.popBackStackOrIgnore()},
                    onActionClick = {
                        dropdownExpanded.value = !dropdownExpanded.value
                    },
                    dropdownMenu =

                    {
                        DropdownMenu(
                            modifier = Modifier.background(Color.White),
                            expanded = dropdownExpanded.value,
                            onDismissRequest = { dropdownExpanded.value = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("View Your Progress") },
                                onClick = {
                                    dropdownExpanded.value = false
                                    navController.navigate("game-progress-screen"){
                                        launchSingleTop = true
                                    }
                                })
                            DropdownMenuItem(
                                text = { Text("Restart Game") },
                                onClick = {
                                    dropdownExpanded.value = false
                                    showRestartPopUP.value = true

                                })
                        }
                    },
                    actionIconVector = Icons.Outlined.MoreVert
                )
            }

        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = Color(0xFFFFEECA)
            ) {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(screenScrollState)
                ) {
                    imageID.value?.let { imageRes ->

                        AsyncImage(
                            model = imageRes,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    if (showTheEndImage.value){
                        Text(
                            modifier = Modifier
                                .padding(20.dp),
                            fontFamily = lora,
                            textAlign = TextAlign.Justify,
                            text = mainText.value
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .padding(20.dp)
                                .height(300.dp)
                                .verticalScroll(textScrollState),
                            fontFamily = lora,
                            textAlign = TextAlign.Justify,
                            text = mainText.value
                        )
                    }

                    if (!buttonTitles.isEmpty()){

                        for ((index,title) in buttonTitles.withIndex()) {

                            key(title) {

                                val isClickedState = remember { mutableStateOf(false) }
                                val normalColor = Color.Transparent
                                val correctColor by animateColorAsState(
                                    targetValue = if (isClickedState.value) Color(0xFF32CD32) else normalColor,
                                    animationSpec = if (isClickedState.value) snap() else tween(durationMillis = 1000)
                                )
                                val wrongColor by animateColorAsState(
                                    targetValue = if (isClickedState.value) Color.Red else normalColor,
                                    animationSpec = if (isClickedState.value) snap() else tween(durationMillis = 1000)
                                )
                                val buttonColor = correctChoiceIndex.value?.let {
                                    if (index == it) correctColor else wrongColor
                                } ?: normalColor

                                WoodenButton(text = title,
                                    colors = ButtonDefaults.buttonColors(buttonColor),
                                    onClick = {
                                        isClickedState.value = true
                                        scope.launch {
                                            delay(150)
                                            isClickedState.value = false
                                        }
                                        onButtonPressedFunc(title)
                                    })

                            }
                        }
                    }

                    if(showTheEndImage.value) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            horizontalArrangement = Arrangement.Center
                        ){
                            Image(
                                painter = painterResource(id = R.drawable.pyramid_1),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                            )
                        }

                    }

                }
            }
        }
    }
}