package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityWaitBinding
import com.example.claptofindphone.model.Constant

class WaitActivity : BaseActivity() {
    private lateinit var waitBinding: ActivityWaitBinding
    private var runningService: String? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        waitBinding = ActivityWaitBinding.inflate(layoutInflater)
        setContentView(waitBinding.root)
        changeBackPressCallBack {  }
        runningService = intent?.getStringExtra(Constant.Service.RUNNING_SERVICE)
        if (runningService == Constant.Service.TOUCH_PHONE_RUNNING) {
            waitBinding.txtTitleWait.text = getString(R.string.dont_touch_my_phone)
            waitBinding.content.text=getString(R.string.wait_touch_phone_content)
        } else if (runningService == Constant.Service.CHARGER_ALARM_RUNNING) {
            waitBinding.txtTitleWait.text = getString(R.string.wait_charger_alarm_title)
            waitBinding.content.text=getString(R.string.wait_charger_alarm_content)
        } else if (runningService == Constant.Service.POCKET_MODE_RUNNING) {
            waitBinding.txtTitleWait.text = getString(R.string.wait_pocket_mode_title)
            waitBinding.content.text=getString(R.string.wait_pocket_mode_content)
        }
        val timeCountDown: TextView = waitBinding.txtWaitTime
        var time = 6;
        object : CountDownTimer(5000, 1000) {
            override fun onTick(p0: Long) {
                time--
                timeCountDown.text = time.toString();
            }
            override fun onFinish() {
                val intent = Intent(this@WaitActivity,HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }



}