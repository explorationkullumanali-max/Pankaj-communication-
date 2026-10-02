package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.FtthManagerViewModel
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.TechBlue

@Composable
fun AcsSettingsDialog(
    viewModel: FtthManagerViewModel,
    onDismiss: () -> Unit
) {
    val acsSettings by viewModel.acsSettings.collectAsStateWithLifecycle()

    var serverUrl by remember { mutableStateOf(acsSettings.serverUrl) }
    var username by remember { mutableStateOf(acsSettings.authUsername) }
    var secret by remember { mutableStateOf(acsSettings.authSecret) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "TR-069 GenieACS Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Slate900
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Local Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (acsSettings.isLocalMode) "Local Gateway Mode" else "Remote Cloud ACS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Slate800
                        )
                        Text(
                            text = if (acsSettings.isLocalMode) "Direct ONT WebGUI probe" else "TR-069 NBI REST API",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                    Switch(
                        checked = acsSettings.isLocalMode,
                        onCheckedChange = { viewModel.toggleAcsMode() },
                        colors = SwitchDefaults.colors(checkedThumbColor = TechBlue)
                    )
                }

                // ACS Connectivity status toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GenieACS Link Status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Slate800
                        )
                        Text(
                            text = if (acsSettings.isAcsOnline) "Online & Connected" else "Offline / Standalone",
                            fontSize = 12.sp,
                            color = if (acsSettings.isAcsOnline) EmeraldOnline else Color(0xFFEF4444)
                        )
                    }
                    Switch(
                        checked = acsSettings.isAcsOnline,
                        onCheckedChange = { viewModel.toggleAcsConnection() }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Column {
                    Text("GenieACS URL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate700)
                    OutlinedTextField(
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Column {
                    Text("API Username", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate700)
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.saveAcsSettings(serverUrl, username, secret)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Slate900)
            ) {
                Text("Save Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
