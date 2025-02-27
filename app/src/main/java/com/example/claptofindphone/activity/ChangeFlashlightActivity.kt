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
import com.example.claptofindphone.model.Flashlight
import com.example.claptofindphone.service.FlashlightController
import com.example.claptofindphone.utils.InstallData
import com.example.claptofindphone.utils.SharePreferenceUtils

class ChangeFlashlightActivity : BaseActivity() {
    private lateinit var changeFlashlightBinding: ActivityChangeFlashlightBinding
    private lateinit var changeFlashlightAdapter: FlashlightAdapter
    private lateinit var flashlightList: List<Flashlight>
    private lateinit var selectedFlashlightName: String
    private lateinit var  flashlightController:FlashlightController
    private var flashlightStatus: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeFlashlightBinding = ActivityChangeFlashlightBinding.inflate(layoutInflater)
        setContentView(changeFlashlightBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.change_flashlight_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // flashlight list
       flashlightList = InstallData.getFlashlightList()
        // flashlight share pres
        selectedFlashlightName =SharePreferenceUtils.getFlashName()
        flashlightStatus = SharePreferenceUtils.isOnFlash()
        // on off toggle
        updateOnOffToggle(flashlightStatus)
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            characteristics.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
         flashlightController = FlashlightController(cameraManager, cameraId)
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
            SharePreferenceUtils.setFlashName(selectedFlashlightName)
            SharePreferenceUtils.setOnFlash(flashlightStatus)
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



    private fun updateOnOffToggle(flashlightStatus:Boolean) {
        if (!flashlightStatus) {
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

    override fun onDestroy() {
        super.onDestroy()
        flashlightController.stopFlashing()
    }
}