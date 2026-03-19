package com.hemad.stishield.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.hemad.stishield.R
import com.hemad.stishield.ui.theme.STIShieldTheme
import com.hemad.stishield.ui.utilities.ErrorPopup
import com.hemad.stishield.ui.utilities.ProgressIndicator
import com.hemad.stishield.ui.utilities.TextAppBar
import com.hemad.stishield.ui.utilities.popBackStackOrIgnore
import com.hemad.stishield.viewmodels.QuizScreenViewModel

@Composable
fun QuizScreen(navController: NavHostController,difficulty:String) {

    val context = LocalContext.current
    val viewModel: QuizScreenViewModel = viewModel{
        QuizScreenViewModel(context = context,difficulty)
    }
    val mainText = viewModel.mainTextState
    val questionNumberText = viewModel.questionNumberTextState
    val buttonTitles = viewModel.buttonTitles
    val correctChoiceIndex = viewModel.correctChoiceIndex
    val processState = viewModel.processState
    val errorState = viewModel.errorState
    val isAnswerSelected = viewModel.isAnswerSelected
    val showTryAgainButton = viewModel.showTryAgainButton


    val scope = rememberCoroutineScope()


    STIShieldTheme {

        if (errorState.value != null) {
            ErrorPopup(errorState.value!!, onDismiss = { viewModel.clearError() })
        }

        if (processState.value) {
            Dialog(onDismissRequest = {}) {
                ProgressIndicator()
            }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                TextAppBar(
                    title = if(questionNumberText.value.isNotEmpty()) questionNumberText.value else "STI Smart",
                    onBackClick = {navController.popBackStackOrIgnore()},
                    onActionClick = { viewModel.resetQuiz() },
                    actionIconVector = Icons.Filled.Refresh
                )
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AsyncImage(
                        model = R.drawable.volunteers_2, // Provide the drawable resource
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop, // Maintain the same content scaling
                        alignment = Alignment.TopCenter, // Align the image as desired
                        alpha = 0.85f // Apply transparency
                    )
                    Column(modifier = Modifier
                        .fillMaxSize(),
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(20.dp)
                                .height(130.dp)
                                .verticalScroll(rememberScrollState()),
                            textAlign = TextAlign.Justify,
                            fontSize = 20.sp,
                            text = mainText.value
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            if (!buttonTitles.isEmpty()){

                                for ((index,title) in buttonTitles.withIndex()) {

                                    key(title) {

                                        val normalColor = Color.White
                                        val correctColor by animateColorAsState(
                                            targetValue = if (isAnswerSelected.value) Color(0xFF32CD32) else normalColor,
                                            animationSpec = if (isAnswerSelected.value) snap() else tween(durationMillis = 1)
                                        )
                                        val wrongColor by animateColorAsState(
                                            targetValue = if (isAnswerSelected.value) Color.Red else normalColor,
                                            animationSpec = if (isAnswerSelected.value) snap() else tween(durationMillis = 1)
                                        )

                                        val buttonColor = if (correctChoiceIndex.value != null) {
                                            if (index == correctChoiceIndex.value) correctColor else wrongColor
                                        } else {
                                            normalColor
                                        }

                                        ElevatedButton(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(4.dp)
                                                .shadow(12.dp, shape = RoundedCornerShape(8.dp), ambientColor = Color.White, spotColor = Color.White)
                                            ,
                                            enabled = !isAnswerSelected.value,
                                            elevation = ButtonDefaults.elevatedButtonElevation(
                                                defaultElevation = 16.dp,
                                                pressedElevation = 8.dp,
                                                hoveredElevation = 8.dp,
                                                focusedElevation = 8.dp,
                                                disabledElevation = 0.dp
                                            ),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = buttonColor,
                                                disabledContainerColor = buttonColor),
                                            shape = RoundedCornerShape(8.dp),
                                            onClick = {
                                                isAnswerSelected.value = true
                                                viewModel.onAnswerButtonPressed(title)
                                            }
                                        ) {
                                            Text(
                                                color = Color.Black,
                                                fontSize = 18.sp,
                                                text = title
                                            )
                                        }

                                    }
                                }


                            }

                            if (showTryAgainButton.value == true){
                                ElevatedButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp)
                                        .shadow(12.dp, shape = RoundedCornerShape(8.dp), ambientColor = Color.White, spotColor = Color.White)
                                    ,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color.Black,
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    onClick = {
                                        viewModel.onTryAgainButtonPressed()
                                    }
                                ) {
                                    Text(
                                        fontSize = 18.sp,
                                        text = "Try Again"
                                    )
                                }
                            } else {
                                ElevatedButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp)
                                        .shadow(12.dp, shape = RoundedCornerShape(8.dp), ambientColor = Color.White, spotColor = Color.White)
                                    ,
                                    enabled = isAnswerSelected.value,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        disabledContainerColor = Color.LightGray.copy(alpha = 0.8f),
                                        contentColor = Color.Black,
                                        disabledContentColor = Color.White.copy(alpha = 0.7f)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    onClick = {
                                        isAnswerSelected.value = true
                                        viewModel.onNextButtonPressed()
                                    }
                                ) {
                                    Text(
                                        fontSize = 18.sp,
                                        text = "Next"
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
