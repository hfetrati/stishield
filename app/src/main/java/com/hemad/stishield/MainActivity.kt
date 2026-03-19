package com.hemad.stishield

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hemad.stishield.model.common.TimerService
import com.hemad.stishield.model.common.UserDataRepository
import com.hemad.stishield.model.common.scheduleDailyNotification
import com.hemad.stishield.ui.screens.ChatScreen
import com.hemad.stishield.ui.screens.ChooseConsultantScreen
import com.hemad.stishield.ui.screens.ChooseQuizScreen
import com.hemad.stishield.ui.screens.GameProgressScreen
import com.hemad.stishield.ui.screens.GameScreen
import com.hemad.stishield.ui.screens.LeaderBoardScreen
import com.hemad.stishield.ui.screens.MainScreen
import com.hemad.stishield.ui.screens.PrivacyPolicyScreen
import com.hemad.stishield.ui.screens.QuizScreen
import com.hemad.stishield.ui.screens.RegistrationScreen
import com.hemad.stishield.ui.screens.RiskAssessmentScreen
import com.hemad.stishield.ui.theme.STIShieldTheme
import com.mkrdeveloper.onboardingscreenjetpack.OnboardingScreen


class MainActivity : ComponentActivity() {


    override fun attachBaseContext(newBase: Context?) {

        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = 1.0f
        applyOverrideConfiguration(newOverride)

        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    val intent = Intent(this, TimerService::class.java)
                    startService(intent)
                }
                Lifecycle.Event.ON_STOP -> {
                    stopService(Intent(this, TimerService::class.java))
                }
                else -> {}
            }
        })
        enableEdgeToEdge()
        setContent {
            STIShieldTheme {
                MainApp()
            }
        }
        scheduleDailyNotification(this)

    }
}

@Composable
fun MainApp() {

    val context = LocalContext.current
    val userDataRepo = UserDataRepository(context)
    val isUserRegistered = userDataRepo.email != null
    val didFinishOnboarding = userDataRepo.didFinishOnboarding
    val startDestination = when {
        !isUserRegistered && !didFinishOnboarding -> "onboarding-screen"
        !isUserRegistered && didFinishOnboarding -> "registration-screen"
        isUserRegistered && didFinishOnboarding -> "main-screen"
        else -> "onboarding-screen"
    }


    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) }

        ) {
        composable("onboarding-screen") {
            OnboardingScreen(navController = navController)
        }

        composable("main-screen") {
            MainScreen(navController = navController)
        }
        composable(
            route = "chat-screen/{persona}",
            arguments = listOf(navArgument("persona"){type = NavType.StringType })

        ) {
            val persona = it.arguments?.getString("persona")
            ChatScreen(navController = navController,persona = persona!!)
        }
        composable(
            route = "risk-assessment-screen/{persona}",
            arguments = listOf(navArgument("persona"){type = NavType.StringType })
        ) {
            val persona = it.arguments?.getString("persona")
            RiskAssessmentScreen(navController = navController,persona = persona!!)
        }
        composable("game-screen") {
            GameScreen(navController = navController)
        }
        composable(
            route = "choose-consultant-screen/{nextScreen}",
            arguments = listOf(navArgument("nextScreen"){type = NavType.StringType })
        ) {
            val nextScreen = it.arguments?.getString("nextScreen")
            ChooseConsultantScreen(navController = navController,nextScreen = nextScreen!!)
        }
        composable("registration-screen") {
            RegistrationScreen(navController = navController)
        }

        composable("leaderboard-screen") {
            LeaderBoardScreen(navController = navController)
        }

        composable("choose-quiz-screen") {
            ChooseQuizScreen(navController = navController)
        }

        composable(
            route ="quiz-screen/{difficulty}",
            arguments = listOf(navArgument("difficulty"){type = NavType.StringType })

        ) {
            val difficulty = it.arguments?.getString("difficulty")
            QuizScreen(navController = navController,difficulty = difficulty!!)
        }

        composable("privacy-screen") {
            PrivacyPolicyScreen(navController = navController)
        }

        composable("game-progress-screen") {
            GameProgressScreen(navController = navController)
        }
    }

}





