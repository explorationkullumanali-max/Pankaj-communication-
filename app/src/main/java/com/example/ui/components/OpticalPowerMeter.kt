package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.RedPonProblem
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800

@Composable
fun OpticalPowerMeter(
    rxDbm: Double,
    txDbm: Double,
    modifier: Modifier = Modifier
) {
    val (statusLabel, statusColor, meterFraction) = when {
        rxDbm > -8.0 -> Triple("OVER-POWER", RedPonProblem, 1.0f)
        rxDbm >= -20.0 -> Triple("EXCELLENT", EmeraldOnline, 0.85f)
        rxDbm >= -24.0 -> Triple("GOOD", EmeraldOnline, 0.70f)
        rxDbm >= -27.0 -> Triple("WARNING", AmberWarning, 0.40f)
        else -> Triple("CRITICAL LOSS", RedPonProblem, 0.15f)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate100.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GPON OPTICAL POWER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = statusLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = statusColor
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Rx Optical Power",
                        fontSize = 11.sp,
                        color = Slate500
                    )
                    Text(
                        text = "${String.format("%.1f", rxDbm)} dBm",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = statusColor
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Tx Optical Power",
                        fontSize = 11.sp,
                        color = Slate500
                    )
                    Text(
                        text = "+${String.format("%.1f", txDbm)} dBm",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Slate800
                    )
                }
            }

            // Power meter bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFCBD5E1))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = meterFraction)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(statusColor)
                )
            }
        }
    }
}
