package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OpticalLossCalculator
import com.example.ui.FtthManagerViewModel
import com.example.ui.Screen
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.RedPonProblem
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.TechBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpticalCalculatorScreen(
    viewModel: FtthManagerViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Dashboard)
    }

    var oltPowerText by remember { mutableStateOf("3.0") }
    var distanceText by remember { mutableStateOf("2.5") }
    var splitterRatio by remember { mutableStateOf("1:8") }
    var splicesText by remember { mutableStateOf("3") }
    var connectorsText by remember { mutableStateOf("4") }
    var splitterDropdownExpanded by remember { mutableStateOf(false) }

    val splitterOptions = listOf("1:2", "1:4", "1:8", "1:16", "1:32", "1:64")

    val oltPower = oltPowerText.toDoubleOrNull() ?: 3.0
    val distance = distanceText.toDoubleOrNull() ?: 0.0
    val splices = splicesText.toIntOrNull() ?: 0
    val connectors = connectorsText.toIntOrNull() ?: 0

    val result = OpticalLossCalculator.calculateRxPower(
        oltLaunchPowerDbm = oltPower,
        distanceKm = distance,
        splitterRatio = splitterRatio,
        numberOfSplices = splices,
        numberOfConnectors = connectors
    )

    val (badgeBg, badgeText) = when (result.signalStatus) {
        OpticalLossCalculator.OpticalStatus.EXCELLENT,
        OpticalLossCalculator.OpticalStatus.GOOD -> Pair(Color(0xFFD1FAE5), EmeraldOnline)
        OpticalLossCalculator.OpticalStatus.WARNING -> Pair(Color(0xFFFEF3C7), AmberWarning)
        else -> Pair(Color(0xFFFEE2E2), RedPonProblem)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("optical_calculator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "FTTH Optical Budget Calculator",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Slate800
        )

        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = badgeBg),
            border = BorderStroke(1.dp, badgeText.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "ESTIMATED ONT RX POWER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate600,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${result.estimatedRxPower} dBm",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = badgeText
                )
                Text(
                    text = "Total Link Attenuation: ${result.totalEstimatedLoss} dB",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate700,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = result.message,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Slate800,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        // Input Parameters Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Slate200)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "LINK PARAMETERS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 0.5.sp
                )

                // OLT Power
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("OLT SFP Power (dBm)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        OutlinedTextField(
                            value = oltPowerText,
                            onValueChange = { oltPowerText = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Fiber Distance (km)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        OutlinedTextField(
                            value = distanceText,
                            onValueChange = { distanceText = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Splitter Ratio
                Column {
                    Text("Optical Splitter Ratio", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                    ExposedDropdownMenuBox(
                        expanded = splitterDropdownExpanded,
                        onExpandedChange = { splitterDropdownExpanded = !splitterDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = splitterRatio,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = splitterDropdownExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(10.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = splitterDropdownExpanded,
                            onDismissRequest = { splitterDropdownExpanded = false }
                        ) {
                            splitterOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        splitterRatio = opt
                                        splitterDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Splices & Connectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Splice Count (0.1 dB)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        OutlinedTextField(
                            value = splicesText,
                            onValueChange = { splicesText = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Connectors (0.5 dB)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        OutlinedTextField(
                            value = connectorsText,
                            onValueChange = { connectorsText = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Return button
        Button(
            onClick = { viewModel.navigateTo(Screen.Dashboard) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Slate800,
                contentColor = Color.White
            )
        ) {
            Text("Back to Dashboard", fontWeight = FontWeight.Bold)
        }
    }
}
