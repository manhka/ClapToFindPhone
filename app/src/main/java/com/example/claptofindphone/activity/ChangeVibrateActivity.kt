package com.example.claptofindphone.activity

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.claptofindphone.R
import com.example.claptofindphone.adapter.VibrateAdapter
import com.example.claptofindphone.databinding.ActivityChangeVibrateBinding
import com.example.claptofindphone.databinding.VibrateItemBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.Vibrate
import com.example.claptofindphone.service.VibrateController
import com.example.claptofindphone.utils.SharePreferenceUtils

class ChangeVibrateActivity : BaseActivity() {
    private lateinit var changeVibrateItemBinding: ActivityChangeVibrateBinding
    private lateinit var vibrateAdapter: VibrateAdapter
    private lateinit var vibrateList: List<Vibrate>
    private lateinit var selectedVibrateName: String
    private lateinit var vibrateController: VibrateController
    private var vibrateStatus: Boolean = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeVibrateItemBinding = ActivityChangeVibrateBinding.inflate(layoutInflater)
        setContentView(changeVibrateItemBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.change_vibrate_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getVibrateList()
        vibrateController = VibrateController(this)

        // vibrate share pres

        selectedVibrateName = SharePreferenceUtils.getVibrateName()

        vibrateStatus = SharePreferenceUtils.isOnVibrate()
        // on off toggle
        updateOnOffToggle(vibrateStatus)
        var selectedPosition = vibrateList.indexOfFirst { it.vibrateName == selectedVibrateName }
        val vibrate = vibrateList[selectedPosition]
        vibrateController.startPattern(vibrate.vibrateMode, 3000)
        changeVibrateItemBinding.rcvChangeVibrate.layoutManager = GridLayoutManager(this, 2)
        vibrateAdapter = VibrateAdapter(this, vibrateList) { vibrate ->
            selectedVibrateName = vibrate.vibrateName
            vibrateController.startPattern(vibrate.vibrateMode, 3000)
        }
        changeVibrateItemBinding.rcvChangeVibrate.adapter = vibrateAdapter
        changeVibrateItemBinding.saveButton.setOnClickListener {

            SharePreferenceUtils.setVibrateName(selectedVibrateName)
            SharePreferenceUtils.setOnVibrate(vibrateStatus)

            finish()
        }
        changeVibrateItemBinding.onOffLayout.setOnClickListener {
            vibrateStatus = !vibrateStatus
            updateOnOffToggle(vibrateStatus)
        }
        changeVibrateItemBinding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun getVibrateList() {
        vibrateList = listOf(
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.default,
                0,
                R.drawable.active_theme_ic,
                listOf(0L, 100L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate1,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 200L, 100L, 300L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate2,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 300L, 100L, 500L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate3,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 400L, 200L, 400L, 200L, 600L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate4,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 500L, 100L, 300L, 200L, 500L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate5,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 600L, 200L, 400L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate6,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 700L, 100L, 100L, 100L, 700L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate7,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 200L, 300L, 200L, 300L, 400L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate8,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 300L, 100L, 200L, 100L, 300L, 200L, 500L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate9,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 400L, 100L, 300L, 100L, 400L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate10,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 500L, 100L, 200L, 300L, 600L)
            ),
            Vibrate(
                R.drawable.bg_passive_item,
                Constant.Vibrate.vibrate11,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(0L, 600L, 200L, 400L, 200L, 300L)
            ),
        )
    }

    private fun updateOnOffToggle(vibrateStatus: Boolean) {

        if (!vibrateStatus) {
            changeVibrateItemBinding.offButton.visibility = View.VISIBLE
            changeVibrateItemBinding.onButton.visibility = View.GONE
            changeVibrateItemBinding.txtOn.visibility = View.GONE
            changeVibrateItemBinding.txtOff.visibility = View.VISIBLE
            changeVibrateItemBinding.onOffLayout.setBackgroundResource(R.drawable.bg_off_btn)
        } else {
            changeVibrateItemBinding.offButton.visibility = View.GONE
            changeVibrateItemBinding.onButton.visibility = View.VISIBLE
            changeVibrateItemBinding.txtOn.visibility = View.VISIBLE
            changeVibrateItemBinding.txtOff.visibility = View.GONE
            changeVibrateItemBinding.onOffLayout.setBackgroundResource(R.drawable.bg_on_btn)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vibrateController.stopVibrating()
    }
}