package com.example.claptofindphone.activity

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

fun Context.createContext(newLocale: Locale): Context = if (buildMaxVersionN()) {
    createContextAndroidN(newLocale)
} else {

    createContextLegacy(newLocale)
}
private fun Context.createContextAndroidN(newLocale: Locale): Context {
    val resources: Resources = resources
    val configuration: Configuration = resources.configuration
    configuration.setLocale(newLocale)
    return createConfigurationContext(configuration)
}

private fun Context.createContextLegacy(newLocale: Locale): Context {
    val resources: Resources = resources
    val configuration: Configuration = resources.configuration
    configuration.locale = newLocale
    resources.updateConfiguration(configuration, resources.displayMetrics)
    return this
}
