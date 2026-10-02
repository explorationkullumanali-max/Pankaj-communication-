package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.HardwareModels
import com.example.ui.FtthManagerViewModel
import com.example.ui.Screen
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.TechBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouterConfigScreen(
    customerId: Long,
    viewModel: FtthManagerViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.CustomerDetail(customerId))
    }

    val activeCustomer by viewModel.activeCustomer.collectAsStateWithLifecycle()
    val allCustomers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val customer = activeCustomer ?: allCustomers.find { it.id == customerId }

    val configState by viewModel.configState.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }
    var hardwareDropdownExpanded by remember { mutableStateOf(false) }

    if (customer == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Customer not found", color = Slate500)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("router_config_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Device Configuration",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Slate800
        )

        // Auto-Detect Result Alert
        if (configState.detectionNotes.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldLight),
                border = BorderStroke(1.dp, EmeraldOnline.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = EmeraldOnline,
                        modifier = Modifier
                            .size(22.dp)
                            .padding(top = 1.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Auto-Detect Successful",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF065F46)
                        )
                        Text(
                            text = configState.detectionNotes,
                            fontSize = 13.sp,
                            color = Color(0xFF047857),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Mode Segmented Pill Buttons (PPPoE vs Static IP)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFE2E8F0))
                .padding(4.dp)
        ) {
            val isPppoe = configState.configType == "PPPoE"

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isPppoe) Color.White else Color.Transparent)
                    .clickable { viewModel.updateConfigState { it.copy(configType = "PPPoE") } }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PPPoE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isPppoe) TechBlue else Slate600
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (!isPppoe) Color.White else Color.Transparent)
                    .clickable { viewModel.updateConfigState { it.copy(configType = "Static IP") } }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Static IP",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (!isPppoe) TechBlue else Slate600
                )
            }
        }

        // Hardware Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Slate200)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "HARDWARE SELECTED",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate700,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = hardwareDropdownExpanded,
                    onExpandedChange = { hardwareDropdownExpanded = !hardwareDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = configState.routerModel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = hardwareDropdownExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("select_hardware_model"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechBlue,
                            unfocusedBorderColor = Slate200
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = hardwareDropdownExpanded,
                        onDismissRequest = { hardwareDropdownExpanded = false }
                    ) {
                        Text(
                            text = "Popular ONTs (BSNL & LCO)",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = Slate500,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                        HardwareModels.ONT_MODELS.forEach { model ->
                            DropdownMenuItem(
                                text = { Text(model, fontSize = 13.sp) },
                                onClick = {
                                    viewModel.updateConfigState { it.copy(routerModel = model) }
                                    hardwareDropdownExpanded = false
                                }
                            )
                        }

                        Text(
                            text = "Wi-Fi Routers",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = Slate500,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                        HardwareModels.WIFI_ROUTERS.forEach { model ->
                            DropdownMenuItem(
                                text = { Text(model, fontSize = 13.sp) },
                                onClick = {
                                    viewModel.updateConfigState { it.copy(routerModel = model) }
                                    hardwareDropdownExpanded = false
                                }
                            )
                        }

                        Text(
                            text = "Other",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = Slate500,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                        HardwareModels.OTHER_MODELS.forEach { model ->
                            DropdownMenuItem(
                                text = { Text(model, fontSize = 13.sp) },
                                onClick = {
                                    viewModel.updateConfigState { it.copy(routerModel = model) }
                                    hardwareDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // PPPoE Form Fields
        if (configState.configType == "PPPoE") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)), // light blue
                border = BorderStroke(1.dp, Color(0xFFDBEAFE))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column {
                        Text(
                            text = "USERNAME",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = configState.pppoeUsername,
                            onValueChange = { un -> viewModel.updateConfigState { it.copy(pppoeUsername = un) } },
                            placeholder = { Text("e.g., 94xxxxxxx@bsnl.in") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_pppoe_username"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = TechBlue,
                                unfocusedBorderColor = Color(0xFFBFDBFE)
                            ),
                            singleLine = true
                        )
                    }

                    Column {
                        Text(
                            text = "PASSWORD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = configState.pppoePassword,
                            onValueChange = { pw -> viewModel.updateConfigState { it.copy(pppoePassword = pw) } },
                            placeholder = { Text("Password") },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password",
                                        tint = Slate500
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_pppoe_password"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = TechBlue,
                                unfocusedBorderColor = Color(0xFFBFDBFE)
                            ),
                            singleLine = true
                        )
                    }

                    Column {
                        Text(
                            text = "VLAN ID (INTERNET)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = configState.vlanId,
                            onValueChange = { vlan -> viewModel.updateConfigState { it.copy(vlanId = vlan) } },
                            placeholder = { Text("Default: 100") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_pppoe_vlan"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = TechBlue,
                                unfocusedBorderColor = Color(0xFFBFDBFE)
                            ),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Static IP Form Fields
        if (configState.configType == "Static IP") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)), // light purple
                border = BorderStroke(1.dp, Color(0xFFF3E8FF))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column {
                        Text(
                            text = "IP ADDRESS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B21A8),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = configState.ipAddress,
                            onValueChange = { ip -> viewModel.updateConfigState { it.copy(ipAddress = ip) } },
                            placeholder = { Text("192.168.1.100") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_static_ip"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = PurpleAccent,
                                unfocusedBorderColor = Color(0xFFE9D5FF)
                            ),
                            singleLine = true
                        )
                    }

                    Column {
                        Text(
                            text = "SUBNET MASK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B21A8),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = configState.subnet,
                            onValueChange = { sub -> viewModel.updateConfigState { it.copy(subnet = sub) } },
                            placeholder = { Text("255.255.255.0") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_static_subnet"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = PurpleAccent,
                                unfocusedBorderColor = Color(0xFFE9D5FF)
                            ),
                            singleLine = true
                        )
                    }

                    Column {
                        Text(
                            text = "GATEWAY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B21A8),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = configState.gateway,
                            onValueChange = { gw -> viewModel.updateConfigState { it.copy(gateway = gw) } },
                            placeholder = { Text("192.168.1.1") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_static_gateway"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = PurpleAccent,
                                unfocusedBorderColor = Color(0xFFE9D5FF)
                            ),
                            singleLine = true
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.navigateTo(Screen.CustomerDetail(customerId)) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("btn_config_cancel"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE2E8F0),
                    contentColor = Slate800
                )
            ) {
                Text(text = "Cancel", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.pushConfigToAcs(customer) },
                enabled = !configState.isPushing,
                modifier = Modifier
                    .weight(2f)
                    .height(52.dp)
                    .testTag("btn_push_config_acs"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Slate900,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
            ) {
                if (configState.isPushing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Pushing...", fontWeight = FontWeight.Bold)
                } else {
                    Text(text = "Push to ACS Device", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
