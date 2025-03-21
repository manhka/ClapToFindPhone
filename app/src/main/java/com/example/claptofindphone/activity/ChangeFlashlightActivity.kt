package com.example.claptofindphone.activity


import android.os.Bundle
import android.view.View
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
    private  var selectedFlashlightId: Int?=null
    private var flashlightStatus: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeFlashlightBinding = ActivityChangeFlashlightBinding.inflate(layoutInflater)
        setContentView(changeFlashlightBinding.root)
        // flashlight list
        flashlightList = InstallData.getFlashlightList(this)
        // flashlight share pres
        selectedFlashlightId = SharePreferenceUtils.getFlashlightId()
        flashlightStatus = SharePreferenceUtils.isOnFlash()
        // on off toggle
        updateOnOffToggle(flashlightStatus)
        var selectedPosition =
            flashlightList.indexOfFirst { it.flashlightId == selectedFlashlightId }
        val flashlight = flashlightList[selectedPosition]
        FlashlightController.startPattern(flashlight.flashlightMode, 3000)

        changeFlashlightAdapter = FlashlightAdapter(this, flashlightList) { flashlight ->

            selectedFlashlightId = flashlight.flashlightId
            FlashlightController.startPattern(flashlight.flashlightMode, 3000)
        }
        changeFlashlightBinding.txtCustomFlashlight.isSelected = true
        changeFlashlightBinding.rcvChangeFlashlight.layoutManager = GridLayoutManager(this, 2)
        changeFlashlightBinding.rcvChangeFlashlight.adapter = changeFlashlightAdapter
        changeFlashlightBinding.saveButton.setOnClickListener {
            FlashlightController.stopFlashing()
            SharePreferenceUtils.setFlashlightId(selectedFlashlightId!!)
            SharePreferenceUtils.setOnFlash(flashlightStatus)
            finish()

        }
        changeFlashlightBinding.onOffLayout.setOnClickListener {
            flashlightStatus = !flashlightStatus
            updateOnOffToggle(flashlightStatus)
        }
        changeFlashlightBinding.backButton.setOnClickListener {
            FlashlightController.stopFlashing()
            finish()
        }
    }


    private fun updateOnOffToggle(flashlightStatus: Boolean) {
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
        FlashlightController.stopFlashing()
    }
}