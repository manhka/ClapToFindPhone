package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.claptofindphone.R
import com.example.claptofindphone.adapter.VibrateAdapter
import com.example.claptofindphone.databinding.ActivityChangeVibrateBinding
import com.example.claptofindphone.model.Vibrate
import com.example.claptofindphone.service.VibrateController
import com.example.claptofindphone.utils.InstallData
import com.example.claptofindphone.utils.SharePreferenceUtils

class ChangeVibrateActivity : BaseActivity() {
    private lateinit var changeVibrateItemBinding: ActivityChangeVibrateBinding
    private lateinit var vibrateAdapter: VibrateAdapter
    private lateinit var vibrateList: List<Vibrate>
    private  var selectedVibrateId: Int?=null
    private var vibrateStatus: Boolean = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeVibrateItemBinding = ActivityChangeVibrateBinding.inflate(layoutInflater)
        setContentView(changeVibrateItemBinding.root)
       vibrateList= InstallData.getVibrateList(this)
        // vibrate share pres

        selectedVibrateId = SharePreferenceUtils.getVibrateId()

        vibrateStatus = SharePreferenceUtils.isOnVibrate()
        // on off toggle
        updateOnOffToggle(vibrateStatus)
        InstallData.getVibrateList(this)
        var selectedPosition = vibrateList.indexOfFirst { it.vibrateId == selectedVibrateId }
        val vibrate = vibrateList[selectedPosition]
        Handler().postDelayed({
            VibrateController.startPattern(vibrate.vibrateMode, 3000)

        },300L)
        changeVibrateItemBinding.rcvChangeVibrate.layoutManager = GridLayoutManager(this, 2)
        vibrateAdapter = VibrateAdapter(this, vibrateList) { vibrate ->
            selectedVibrateId = vibrate.vibrateId
            Handler().postDelayed({
                VibrateController.startPattern(vibrate.vibrateMode, 3000)

            },300L)
        }
        changeVibrateItemBinding.txtCustomVibrate.isSelected = true
        changeVibrateItemBinding.rcvChangeVibrate.adapter = vibrateAdapter
        changeVibrateItemBinding.saveButton.setOnClickListener {
            VibrateController.stopVibrating()
            SharePreferenceUtils.setVibrateId(selectedVibrateId!!)
            SharePreferenceUtils.setOnVibrate(vibrateStatus)
            finish()
        }
        changeVibrateItemBinding.onOffLayout.setOnClickListener {
            vibrateStatus = !vibrateStatus
            updateOnOffToggle(vibrateStatus)
        }
        changeVibrateItemBinding.backButton.setOnClickListener {
            VibrateController.stopVibrating()
            finish()
        }
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

}