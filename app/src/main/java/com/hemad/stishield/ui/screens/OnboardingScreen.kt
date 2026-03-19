package com.mkrdeveloper.onboardingscreenjetpack

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hemad.stishield.R
import com.hemad.stishield.model.common.UserDataRepository
import com.hemad.stishield.ui.utilities.PageIndicator


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavHostController) {

    val context = LocalContext.current
    val repository = UserDataRepository(context)
    val pageCount = 5

    val images = listOf(
        R.drawable.sword,
        R.drawable.quiz_2,
        R.drawable.chat_2,
        R.drawable.shield_1,
        R.drawable.leaderboard
    )

    val titles = listOf(
        "Knowledge Quest",
        "STI Smart",
        "Safe Talk",
        "Health Guard",
        "Leaderboard"
    )

    val descriptions = listOf(
        "Embark on a thrilling text-based adventure while uncovering STI wisdom hidden in ancient quests!",
        "Test your knowledge and learn what you don't know along the way!",
        "Get answers to all your STI questions from our friendly virtual doctors in full confidentiality.",
        "Share your story with our virtual doctors and receive personalized recommendations to stay protected.",
        "Earn ⭐ by completing exciting tasks in the app! Keep active and engaged to rack up extra ⭐ the longer you stay. Climb the leaderboard, and show off your dedication and achievement!"

    )
    val pagerState = rememberPagerState(
        pageCount = { pageCount }
    )

    Column(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            contentPadding = PaddingValues(20.dp),
            state = pagerState
        ) { currentPage ->
            Column(
                Modifier
                    .padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(180.dp)
                ) {
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.elevatedCardElevation(8.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = images[currentPage]),
                            contentDescription = titles[currentPage],
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(36.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Text(
                    text = titles[currentPage],
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Text(
                    text = descriptions[currentPage],
                    Modifier
                        .padding(top = 40.dp)
                        .height(200.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
        }
        if (pagerState.currentPage != pageCount-1){
            PageIndicator(
                pageCount = pageCount,
                currentPage = pagerState.currentPage,
                modifier = Modifier.padding(60.dp)
            )
        }

        if (pagerState.currentPage == pageCount-1){
            Button(
                modifier = Modifier.padding(24.dp),
                onClick = {
                    repository.didFinishOnboarding = true
                    navController.navigate("registration-screen"){
                        launchSingleTop = true
                    }
                }
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Get Started",
                    fontSize = 20.sp,
                )
            }
        }

    }
}



