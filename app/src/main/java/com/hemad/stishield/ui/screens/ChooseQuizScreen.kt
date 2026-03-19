package com.hemad.stishield.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.hemad.stishield.R
import com.hemad.stishield.model.common.UserDataRepository
import com.hemad.stishield.ui.theme.STIShieldTheme
import com.hemad.stishield.ui.utilities.CustomCard2
import com.hemad.stishield.ui.utilities.TextAppBar
import com.hemad.stishield.ui.utilities.formatTextWithHtmlTags
import com.hemad.stishield.ui.utilities.popBackStackOrIgnore

@Composable
fun ChooseQuizScreen(navController: NavHostController) {

    val mainText = formatTextWithHtmlTags("Hey! We’re Kwame and Amina, and we’re volunteering with the STI Education Outreach Program. We’ve prepared some quizes for you, so you can evaluate your knowledge and learn important stuff about sexual health. Choose a question pack below to start. By answering all questions in each pack correctly you earn <b>100 ⭐</b>.")
    val context = LocalContext.current
    val userRepo = UserDataRepository(context)

    STIShieldTheme {

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                TextAppBar(
                    title = "STI Smart",
                    onBackClick = {navController.popBackStackOrIgnore()}
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
                        model = R.drawable.volunteers_2,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter,
                        alpha = 0.85f
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
                            fontSize = 16.sp,
                            text = mainText
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            CustomCard2(
                                title = "Easy",
                                imageResId = R.drawable.complete_badge,
                                scoreString = userRepo.highestQuizScore_easy.toString()+"%",
                                showIcon = if (userRepo.highestQuizScore_easy == 100) true else false,
                                onClick = {navController.navigate("quiz-screen/easy"){
                                    launchSingleTop = true
                                } }
                            )

                            CustomCard2(
                                title = "Medium",
                                imageResId = R.drawable.complete_badge,
                                scoreString = userRepo.highestQuizScore_medium.toString()+"%",
                                showIcon = if (userRepo.highestQuizScore_medium == 100) true else false,
                                onClick = {navController.navigate("quiz-screen/medium"){
                                    launchSingleTop = true
                                } }
                            )

                            CustomCard2(
                                title = "Hard",
                                imageResId = R.drawable.complete_badge,
                                scoreString = userRepo.highestQuizScore_hard.toString()+"%",
                                showIcon = if (userRepo.highestQuizScore_hard == 100) true else false,
                                onClick = {navController.navigate("quiz-screen/hard"){
                                    launchSingleTop = true
                                } }
                            )
                        }
                    }
                }

            }
        }
    }

}