package com.example.data

object HardwareModels {
    val ONT_MODELS = listOf(
        "GX Earth 1000 E",
        "GX Earth 1000R",
        "Syrotech SY-GPON-1110-WDONT",
        "Syrotech SY-GPON-2010-WADONT",
        "Netlink HG323RW / V2801SG",
        "Optilink OP-XONT-7111",
        "ZTE F670L / F660",
        "Huawei HG8145V5 / EG8141A5",
        "Nokia G-2425G-A",
        "DBC EPON/GPON ONU",
        "Alphion AONT",
        "Fibersol ONU"
    )

    val WIFI_ROUTERS = listOf(
        "TP-Link XX530v AX3000 XPON",
        "TP-Link Archer C80",
        "TP-Link Archer C6 / AX10",
        "D-Link DIR-825 / DIR-819",
        "Tenda AC10 / AC5",
        "Mercusys AC12G"
    )

    val OTHER_MODELS = listOf(
        "Generic GPON/EPON",
        "Other / Unlisted"
    )

    val ALL_MODELS = ONT_MODELS + WIFI_ROUTERS + OTHER_MODELS
}
