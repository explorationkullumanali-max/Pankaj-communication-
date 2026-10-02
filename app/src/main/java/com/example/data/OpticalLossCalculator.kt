package com.example.data

object OpticalLossCalculator {
    data class LossResult(
        val totalEstimatedLoss: Double,
        val estimatedRxPower: Double,
        val signalStatus: OpticalStatus,
        val message: String
    )

    enum class OpticalStatus {
        EXCELLENT,
        GOOD,
        WARNING,
        CRITICAL_LOW,
        TOO_STRONG
    }

    fun calculateRxPower(
        oltLaunchPowerDbm: Double = 3.0,
        distanceKm: Double = 2.0,
        splitterRatio: String = "1:8",
        numberOfSplices: Int = 3,
        numberOfConnectors: Int = 4
    ): LossResult {
        val fiberLoss = distanceKm * 0.25 // 0.25 dB per km at 1490nm
        val splitterLoss = when (splitterRatio) {
            "1:2" -> 3.5
            "1:4" -> 7.2
            "1:8" -> 10.5
            "1:16" -> 13.8
            "1:32" -> 17.2
            "1:64" -> 20.5
            else -> 10.5
        }
        val spliceLoss = numberOfSplices * 0.1
        val connectorLoss = numberOfConnectors * 0.5
        val totalLoss = fiberLoss + splitterLoss + spliceLoss + connectorLoss
        val estimatedRx = oltLaunchPowerDbm - totalLoss

        val (status, msg) = evaluateRxPower(estimatedRx)

        return LossResult(
            totalEstimatedLoss = (Math.round(totalLoss * 10.0) / 10.0),
            estimatedRxPower = (Math.round(estimatedRx * 10.0) / 10.0),
            signalStatus = status,
            message = msg
        )
    }

    fun evaluateRxPower(rxDbm: Double): Pair<OpticalStatus, String> {
        return when {
            rxDbm > -8.0 -> Pair(OpticalStatus.TOO_STRONG, "Signal too high (> -8 dBm). May saturate optical receiver.")
            rxDbm >= -18.0 -> Pair(OpticalStatus.EXCELLENT, "Excellent GPON Optical Level (-8 to -18 dBm).")
            rxDbm >= -24.0 -> Pair(OpticalStatus.GOOD, "Good Optical Level (-18 to -24 dBm). Normal operation.")
            rxDbm >= -27.0 -> Pair(OpticalStatus.WARNING, "Marginal signal (-24 to -27 dBm). High risk of packet loss.")
            else -> Pair(OpticalStatus.CRITICAL_LOW, "PON Problem! Rx < -27 dBm. Fiber break or excessive attenuation.")
        }
    }
}
