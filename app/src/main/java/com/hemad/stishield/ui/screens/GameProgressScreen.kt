package com.hemad.stishield.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hemad.stishield.R
import com.hemad.stishield.model.game.GameRepository
import com.hemad.stishield.ui.theme.STIShieldTheme
import com.hemad.stishield.ui.theme.lora
import com.hemad.stishield.ui.utilities.ScrollTextPopup
import com.hemad.stishield.ui.utilities.TextAppBar
import com.hemad.stishield.ui.utilities.formatTextWithHtmlTags
import com.hemad.stishield.ui.utilities.popBackStackOrIgnore

@Composable
fun GameProgressScreen(navController: NavHostController){

    val context = LocalContext.current
    val repository = GameRepository(context)
    val factList = repository.facts
    val allScrollsNum = factList.size
    val foundScrollsNum = repository.getCurrentFactIndex()
    val popUpText = remember { mutableStateOf(AnnotatedString("")) }
    val showFactPopUp = remember { mutableStateOf(false) }
    val PAGE_TITLE = "Your Progress"
    var topText = AnnotatedString("")

    STIShieldTheme {

        if (showFactPopUp.value) {
            ScrollTextPopup(
                text = popUpText.value ,
                onDismiss = { showFactPopUp.value = false }
            )
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            topBar = {
                TextAppBar(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    fontFamily = lora,
                    title = PAGE_TITLE,
                    backgroundColor = Color(0xFFFDEACA),
                    onBackClick = { navController.popBackStackOrIgnore() },

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
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.compass_1),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .size(50.dp)
                    )



                    if (foundScrollsNum == 0){
                        topText = AnnotatedString("You haven't found any of the scrolls yet.")
                    } else if (foundScrollsNum == allScrollsNum) {
                        topText = AnnotatedString("Well done warrior! You have found all the scrolls.")
                    } else {
                        val scrollsRemaining = allScrollsNum - foundScrollsNum
                        topText =  formatTextWithHtmlTags("You have found <b>$foundScrollsNum</b> of the scrolls. <b>$scrollsRemaining</b> more to go.")
                    }

                    Text(
                        modifier = Modifier.padding(25.dp),
                        fontFamily = lora,
                        text = topText,
                        textAlign = TextAlign.Justify
                    )

                    for ((index,fact) in factList.withIndex()){
                        val enabled = if (index < foundScrollsNum) true else false
                        TextButton(
                            modifier = Modifier.padding(horizontal = 40.dp, vertical = 16.dp),
                            enabled = enabled,
                            onClick = {
                                popUpText.value = formatTextWithHtmlTags(fact.description)
                                showFactPopUp.value = true
                            }
                        ) {
                            Text(
                                textAlign = TextAlign.Center,
                                fontFamily = lora,
                                text = "📜 "+fact.title,

                            )
                        }
                    }


                }
            }

        }
    }


}