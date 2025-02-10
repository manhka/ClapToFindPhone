package com.example.claptofindphone.activity

import android.content.Context
import android.content.SharedPreferences
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.claptofindphone.R
import com.example.claptofindphone.adapter.FlashlightAdapter
import com.example.claptofindphone.databinding.ActivityChangeFlashlightBinding
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.Flashlight
import com.example.claptofindphone.service.FlashlightController

class ChangeFlashlightActivity : AppCompatActivity() {
    private lateinit var changeFlashlightBinding: ActivityChangeFlashlightBinding
    private lateinit var changeFlashlightAdapter: FlashlightAdapter
    private lateinit var flashlightList: List<Flashlight>
    private lateinit var selectedFlashlightName: String
    private lateinit var flashlightSharedPreferences: SharedPreferences
    private var flashlightStatus: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeFlashlightBinding = ActivityChangeFlashlightBinding.inflate(layoutInflater)
        setContentView(changeFlashlightBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // flashlight list
        getFlashlightList()

        // flashlight share pres
        flashlightSharedPreferences = getSharedPreferences(
            Constant.SharePres.FLASHLIGHT_SHARE_PRES,
            MODE_PRIVATE
        )
        selectedFlashlightName =
            flashlightSharedPreferences.getString(
                Constant.SharePres.ACTIVE_FLASHLIGHT_NAME,
                Constant.Flashlight.default
            ).toString()
        flashlightStatus =
            flashlightSharedPreferences.getBoolean(Constant.SharePres.FLASHLIGHT_STATUS, true)
        // on off toggle
        updateOnOffToggle(flashlightStatus)
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            characteristics.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
        val flashlightController = FlashlightController(cameraManager, cameraId)
        var selectedPosition =
            flashlightList.indexOfFirst { it.flashlightName == selectedFlashlightName }
        val flashlight = flashlightList[selectedPosition]
        flashlightController.startPattern(flashlight.flashlightMode, 3000)

        changeFlashlightAdapter = FlashlightAdapter(this, flashlightList) { flashlight ->
            selectedFlashlightName = flashlight.flashlightName
            flashlightController.startPattern(flashlight.flashlightMode, 3000)
        }

        changeFlashlightBinding.rcvChangeFlashlight.layoutManager = GridLayoutManager(this, 2)
        changeFlashlightBinding.rcvChangeFlashlight.adapter = changeFlashlightAdapter
        changeFlashlightBinding.saveButton.setOnClickListener {
            flashlightSharedPreferences.edit()
                .putString(Constant.SharePres.ACTIVE_FLASHLIGHT_NAME, selectedFlashlightName)
                .apply()
            flashlightSharedPreferences.edit()
                .putBoolean(Constant.SharePres.FLASHLIGHT_STATUS, flashlightStatus)
                .apply()
            finish()

        }
        changeFlashlightBinding.onOffLayout.setOnClickListener {
            flashlightStatus = !flashlightStatus
            updateOnOffToggle(flashlightStatus)
        }
        changeFlashlightBinding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun getFlashlightList() {
        flashlightList = listOf(
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.default,
                0,
                R.drawable.active_theme_ic,
                listOf(0L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight1,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(300L, 300L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight2,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(500L, 500L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight3,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(200L, 100L, 200L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight4,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(1000L, 500L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight5,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(300L, 300L, 600L, 300L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight6,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(150L, 150L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight7,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(700L, 300L, 100L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight8,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(500L, 100L, 200L, 300L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight9,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(1000L, 1000L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight10,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(100L, 100L, 100L)
            ),
            Flashlight(
                R.drawable.bg_passive_item,
                Constant.Flashlight.flashlight11,
                R.drawable.ic_premium,
                R.drawable.active_theme_ic,
                listOf(400L, 200L, 600L)
            ),
        )
    }

    private fun updateOnOffToggle(flashlightStatus:Boolean) {

        if (flashlightStatus) {
            changeFlashlightBinding.offButton.visibility = View.VISIBLE
            changeFlashlightBinding.onButton.visibility = View.GONE
            changeFlashlightBinding.txtOn.visibility = View.GONE
            changeFlashlightBinding.txtOff.visibility = View.VISIBLE
            changeFlashlightBinding.onOffLayout.setBackgroundResource(R.drawable.bg_off_btn)
        } else {
            changeFlashlightBinding.offButton.visibility = View.GONE
            changeFlashlightBinding.onButton.visibility = View.VISIBLE
            changeFlashlightBinding.txtOn.visibility = View.VISIBLE
            changeFlashlightBinding.txtOff.visibility = View.GONE
            changeFlashlightBinding.onOffLayout.setBackgroundResource(R.drawable.bg_on_btn)
        }
    }
}