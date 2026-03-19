package com.hemad.stishield.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.breens.beetablescompose.BeeTablesCompose
import com.hemad.stishield.R
import com.hemad.stishield.ui.theme.STIShieldTheme
import com.hemad.stishield.ui.utilities.ErrorPopup
import com.hemad.stishield.ui.utilities.ProgressIndicator
import com.hemad.stishield.ui.utilities.TextAppBar
import com.hemad.stishield.ui.utilities.formatTextWithHtmlTags
import com.hemad.stishield.ui.utilities.popBackStackOrIgnore
import com.hemad.stishield.viewmodels.LeaderBoardScreenViewModel


@Composable
fun LeaderBoardScreen(navController: NavHostController) {

    val context = LocalContext.current
    val viewModel: LeaderBoardScreenViewModel = viewModel() {
        LeaderBoardScreenViewModel(context = context)
    }
    val dataTable = viewModel.dataTable
    val tableTitles = viewModel.tableTitles
    val processState = viewModel.processState
    val showInfoPopup = viewModel.showInfoPopup
    val errorState = viewModel.errorState
    val userPoints = viewModel.userPoints
    val userRank = viewModel.userRank
    val PAGE_TITLE = "Your Stats"

    LaunchedEffect(Unit) {
        viewModel.syncUserPointsAndLoadLeaderboard()
    }

    STIShieldTheme {

        if (processState.value) {
            Dialog(onDismissRequest = {}) {
                ProgressIndicator()
            }
        }

        if (errorState.value != null) {
            ErrorPopup(errorState.value!!, onDismiss = { viewModel.clearError() })
        }

        if (showInfoPopup.value){
            AlertDialog(
                icon = {
                    Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                },
                onDismissRequest = { showInfoPopup.value = false },
                title = {
                    Text(
                        textAlign = TextAlign.Center,
                        text = viewModel.infoTitle
                    )
                },
                text = {
                    Text(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        text = formatTextWithHtmlTags(viewModel.infoText)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showInfoPopup.value = false }
                    ) {
                        Text("OK")
                    }
                }

            )
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            topBar = {
                TextAppBar(
                    title = PAGE_TITLE,
                    onBackClick = { navController.popBackStackOrIgnore() },
                    onActionClick = {
                        showInfoPopup.value = true
                    },
                    actionIconVector = Icons.Outlined.Info

                )
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                if (dataTable.value.isNullOrEmpty()){
                    Box(modifier = Modifier
                        .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "No data to show.",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.trophy),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Your ⭐",
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = userPoints.toString())
                        }
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Your Rank",
                                fontWeight = FontWeight.Bold

                            )
                            Text(text = userRank.value)
                        }
                    }

                    if (dataTable.value != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally)
                        {
                            BeeTablesCompose(
                                data = dataTable.value!!,
                                headerTableTitles = tableTitles,
                                shape = RoundedCornerShape(8.dp),
                                headerTitlesTextStyle = TextStyle(fontWeight = FontWeight.Bold),
                                headerTitlesBackGroundColor = Color(0xFFCFE5FF)
                            )
                        }

                    }
                }
            }
        }
    }
}
