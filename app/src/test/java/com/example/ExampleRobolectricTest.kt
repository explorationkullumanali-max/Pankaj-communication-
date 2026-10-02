package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.OpticalLossCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Pankaj Comm", appName)
  }

  @Test
  fun `verify optical loss calculation`() {
    val result = OpticalLossCalculator.calculateRxPower(
      oltLaunchPowerDbm = 3.0,
      distanceKm = 2.0,
      splitterRatio = "1:8",
      numberOfSplices = 3,
      numberOfConnectors = 4
    )
    assertTrue(result.totalEstimatedLoss > 0.0)
    assertTrue(result.estimatedRxPower < 3.0)
  }
}
