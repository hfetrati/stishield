package com.hemad.stishield.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hemad.stishield.ui.theme.STIShieldTheme
import com.hemad.stishield.ui.utilities.CenterImageAppBar
import com.hemad.stishield.ui.utilities.CustomCard
import com.hemad.stishield.ui.utilities.RequestNotificationPermission
import com.hemad.stishield.ui.utilities.sendEmail
import com.hemad.stishield.viewmodels.MainScreenViewModel
import kotlinx.coroutines.launch
import com.hemad.stishield.R


@Composable
fun MainScreen (navController: NavHostController) {

    val context = LocalContext.current
    val viewModel: MainScreenViewModel = viewModel{
        MainScreenViewModel(context = context)
    }
    val userNickname = viewModel.userNickname
    val dropdownExpanded = viewModel.dropdownExpanded
    val scrollState = rememberScrollState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                coroutineScope.launch {
                    scrollState.scrollTo(0)
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    RequestNotificationPermission()

    STIShieldTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                CenterImageAppBar(
                    imageResId = R.drawable.logo_transparent_2,
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
                                text = { Text("Data and Privacy") },
                                onClick = {
                                    dropdownExpanded.value = false
                                    navController.navigate("privacy-screen"){
                                        launchSingleTop = true
                                    }
                            })
                            DropdownMenuItem(
                                text = { Text("Report a Problem") },
                                onClick = {
                                    dropdownExpanded.value = false
                                    sendEmail(context)

                                })
                        }
                    },
                    actionIconVector = Icons.Outlined.MoreVert
                )
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 50.dp),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            text = "Hello $userNickname! \uD83D\uDC4B\uD83C\uDFFE"
                        )

                        CustomCard(
                            title = "Knowledge Quest",
                            imageResId = R.drawable.sword,
                            onClick = {navController.navigate("game-screen"){
                                launchSingleTop = true
                            } }
                        )

                        CustomCard(
                            title = "STI Smart",
                            imageResId = R.drawable.quiz_2,
                            onClick = {navController.navigate("choose-quiz-screen"){
                                launchSingleTop = true
                            } }
                        )

                        CustomCard(
                            title = "Health Guard",
                            imageResId = R.drawable.shield_1,
                            onClick = {navController.navigate("choose-consultant-screen/risk"){
                                launchSingleTop = true
                            } }
                        )
                        CustomCard(
                            title = "Safe Talk",
                            imageResId = R.drawable.chat_2,
                            onClick = {navController.navigate("choose-consultant-screen/chat"){
                                launchSingleTop = true
                            } }
                        )
                        CustomCard(
                            title = "Your Stats",
                            imageResId = R.drawable.leaderboard,
                            onClick = {navController.navigate("leaderboard-screen"){
                                launchSingleTop = true
                            } }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(120.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .size(36.dp),
                            painter = painterResource(id = R.drawable.lab_logo),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.LightGray)

                        )
                    }
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 12.dp),
                        textAlign = TextAlign.Center,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        text = "Developed at Persuasive Computing Lab"
                    )
                }

            }
        }}

}

