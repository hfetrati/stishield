package com.hemad.stishield.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hemad.stishield.ui.utilities.TextAppBar
import com.hemad.stishield.ui.utilities.formatTextWithHtmlTags
import com.hemad.stishield.ui.utilities.popBackStackOrIgnore

@Composable
fun PrivacyPolicyScreen(navController: NavHostController) {
    val privacyPolicyText = """
        <b>1. Data Source</b>
        
        Our app provides sexual health advice and information based on reliable and authoritative sources, including the Centers for Disease Control and Prevention (CDC), the World Health Organization (WHO) and Public Health Agency of Canada. We ensure that all the data and guidance offered are up-to-date and derived from these reputable organizations.
        
        <b>2. Data Collection</b>
        
        We collect and store your email address and nickname for account and leaderboard functionality. Registration timestamp and total app usage time is also recorded. Your data is stored securely using DynamoDB, a cloud database service provided by Amazon. The app may store limited non-sensitive data locally on your device (such as app progress and preferences) to support functionality. Chat interactions are not persistently stored by the app and are cleared after each session. This application was developed as part of an academic research project.
    """.trimIndent()

    Scaffold(
        topBar = {
            TextAppBar(
                title = "Data and Privacy",
                onBackClick = {navController.popBackStackOrIgnore()}
            )
        },

    ) { innerPaddings ->
        Surface(
            modifier = Modifier
                .padding(innerPaddings)
                .fillMaxSize()
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = formatTextWithHtmlTags(privacyPolicyText),
                    textAlign = TextAlign.Justify
                )
            }
        }

    }

}
