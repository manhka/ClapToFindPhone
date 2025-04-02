package com.example.claptofindphone.activity

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.example.claptofindphone.utils.SharePreferenceUtils
import java.util.Locale

open class BaseActivity : AppCompatActivity(){
    private var lastClickTime = 0L
    private val clickInterval = 300L
    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (overrideConfiguration != null) {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun attachBaseContext(newBase: Context) = super.attachBaseContext(
        newBase.createContext(
            Locale(SharePreferenceUtils.getLanguageCode())
        )
    )

    protected fun changeBackPressCallBack(action: () -> Unit) {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                action.invoke()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }
    private fun hideNavigationBar() {
        val decorView: View = window.decorView
        val uiOptions: Int =
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideNavigationBar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            enableEdgeToEdge()

        } else {
            // Android 9, 10 (API 28-29)

        }


    }


// prevent multiple touch
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastClickTime < clickInterval) {
                // Block multiple touch
                return true
            }
            lastClickTime = currentTime
        }
        return super.dispatchTouchEvent(ev)
    }

}