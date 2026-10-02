package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mac: String,
    val name: String,
    val status: String = "Online", // "Online", "Internet Down", "PON Problem"
    val phone: String = "",
    val configType: String = "PPPoE", // "PPPoE", "Static IP"
    val pppoeUsername: String = "",
    val pppoePassword: String = "",
    val vlanId: String = "100",
    val ipAddress: String = "",
    val subnet: String = "255.255.255.0",
    val gateway: String = "",
    val routerModel: String = "GX Earth 1000 E",
    val detectionNotes: String = "",
    val rxPowerDbm: Double = -19.4,
    val txPowerDbm: Double = 2.3,
    val lastConfiguredAt: Long = System.currentTimeMillis()
)
