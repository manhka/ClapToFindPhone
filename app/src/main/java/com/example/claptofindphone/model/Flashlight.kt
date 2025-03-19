package com.example.claptofindphone.model

class Flashlight(
    val flashlightId:Int,
    val flashlightBg: Int,
    val flashlightName: String,
    var flashlightPremium: Int,
    val flashlightSelected: Int,
    val flashlightMode: List<Long>
)