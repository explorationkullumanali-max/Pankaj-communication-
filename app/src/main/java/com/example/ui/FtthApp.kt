package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AcsSettingsDialog
import com.example.ui.screens.AddCustomerScreen
import com.example.ui.screens.CustomerDetailScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DetectingScreen
import com.example.ui.screens.OpticalCalculatorScreen
import com.example.ui.screens.RouterConfigScreen
import com.example.ui.screens.SearchCustomerScreen
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.RedPonProblem
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate900
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FtthApp(
    viewModel: FtthManagerViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val acsSettings by viewModel.acsSettings.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAcsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.notificationMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    if (showAcsDialog) {
        AcsSettingsDialog(
            viewModel = viewModel,
            onDismiss = { showAcsDialog = false }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Slate50,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900)
                    .statusBarsPadding()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentScreen != Screen.Dashboard) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier.testTag("top_bar_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    Text(
                        text = "Pankaj Communication Manager",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = if (currentScreen == Screen.Dashboard) TextAlign.Center else TextAlign.Start,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = if (currentScreen != Screen.Dashboard) 16.dp else 16.dp)
                    )
                }

                // Sub-header with Status Badges (as in original React app)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Local Mode Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFEAB308)) // Yellow-500
                            .clickable { showAcsDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("tag_local_mode")
                    ) {
                        Text(
                            text = if (acsSettings.isLocalMode) "LOCAL MODE" else "CLOUD ACS",
                            color = Slate900,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // GenieACS Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (acsSettings.isAcsOnline) EmeraldOnline else RedPonProblem)
                            .clickable { showAcsDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("tag_genieacs_status")
                    ) {
                        Text(
                            text = if (acsSettings.isAcsOnline) "GenieACS: Online" else "GenieACS: Offline",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Dashboard -> {
                    DashboardScreen(viewModel = viewModel)
                }
                is Screen.SearchCustomer -> {
                    SearchCustomerScreen(viewModel = viewModel)
                }
                is Screen.AddCustomer -> {
                    AddCustomerScreen(viewModel = viewModel)
                }
                is Screen.CustomerDetail -> {
                    CustomerDetailScreen(
                        customerId = screen.customerId,
                        viewModel = viewModel
                    )
                }
                is Screen.Detecting -> {
                    DetectingScreen(
                        customerId = screen.customerId,
                        viewModel = viewModel
                    )
                }
                is Screen.RouterConfig -> {
                    RouterConfigScreen(
                        customerId = screen.customerId,
                        viewModel = viewModel
                    )
                }
                is Screen.OpticalCalculator -> {
                    OpticalCalculatorScreen(viewModel = viewModel)
                }
            }
        }
    }
}
