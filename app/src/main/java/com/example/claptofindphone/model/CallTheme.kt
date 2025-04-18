package com.example.claptofindphone.model

import java.io.Serializable

class CallTheme(
    val themeName:String,
    val callThemeBg:Int,
    val callThemeLottie:Int,
    val callThemeRound2:Int,
    val callThemeRound1:Int,
    val callThemeProfile:Int,
    val callThemeResponseLottie:Int,
    val callThemeRejectLottie:Int,
    val callThemeResponse:Int,
    val callThemeReject:Int,

    val callThemeName:String,
    val callThemePhone:String,
    var callThemePremium:Int
): Serializable