package com.hemad.stishield.ui.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.hemad.stishield.R
import com.hemad.stishield.ui.theme.STIShieldTheme
import com.hemad.stishield.ui.utilities.TextAppBar
import com.hemad.stishield.ui.utilities.popBackStackOrIgnore

@Composable
fun ChooseConsultantScreen(navController: NavHostController,nextScreen:String) {

    val destinationScreen = nextScreen

    STIShieldTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                TextAppBar(
                    title = if (destinationScreen == "chat") "Safe Talk: Choose Your Consultant" else "Health Guard: Choose Your Consultant",
                    onBackClick = {navController.popBackStackOrIgnore()},
                    onActionClick = null
                )
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
            ) {

                Box(modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(
                        WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
                    )
                ) {

                    AsyncImage(
                        model = R.drawable.group,
                        contentDescription = null,
                        alignment = Alignment.TopCenter,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Column (modifier = Modifier
                        .fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.weight(2f))
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp)
                            .weight(1f),
                            horizontalArrangement = Arrangement.SpaceEvenly) {

                            ElevatedButton(
                                elevation = ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 12.dp,
                                focusedElevation = 8.dp,
                                hoveredElevation = 10.dp,
                            ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White,
                                ),
                                onClick = {navController.navigate(
                                route = if (destinationScreen == "chat") "chat-screen/obinna" else "risk-assessment-screen/obinna"
                            ){
                                    launchSingleTop = true
                                }}) {
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    fontSize = 16.sp,
                                    text = "Dr. Obinna"
                                )
                            }


                            Spacer(modifier = Modifier.weight(1f))


                            ElevatedButton(

                                elevation = ButtonDefaults.elevatedButtonElevation(
                                    defaultElevation = 12.dp,
                                    pressedElevation = 5.dp,
                                    focusedElevation = 8.dp,
                                    hoveredElevation = 8.dp,
                                ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White,
                                ),
                                onClick = {navController.navigate(
                                route = if (destinationScreen == "chat") "chat-screen/hana" else "risk-assessment-screen/hana"
                            ){
                                    launchSingleTop = true
                                }}) {
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    fontSize = 16.sp,
                                    text = "Dr. Hana"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}
