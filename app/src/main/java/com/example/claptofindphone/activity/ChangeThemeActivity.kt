package com.example.claptofindphone.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.claptofindphone.R
import com.example.claptofindphone.adapter.CallThemeAdapter
import com.example.claptofindphone.adapter.DefaultThemeAdapter
import com.example.claptofindphone.databinding.ActivityChangeThemeBinding
import com.example.claptofindphone.model.CallTheme
import com.example.claptofindphone.model.DefaultTheme
import com.example.claptofindphone.utils.InstallData

class ChangeThemeActivity : BaseActivity() {
    private lateinit var changeThemeBinding: ActivityChangeThemeBinding
    private lateinit var defaultThemeAdapter: DefaultThemeAdapter
    private lateinit var callThemeAdapter: CallThemeAdapter
    private lateinit var defaultThemeList: List<DefaultTheme>
    private lateinit var callThemeList: List<CallTheme>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeThemeBinding = ActivityChangeThemeBinding.inflate(layoutInflater)
        setContentView(changeThemeBinding.root)

    }

    override fun onResume() {
        super.onResume()
        defaultThemeList=InstallData.getDefaultThemeList()
        //default theme
        defaultThemeAdapter = DefaultThemeAdapter(this, defaultThemeList)
        changeThemeBinding.rcvDefaultTheme.adapter = defaultThemeAdapter
        changeThemeBinding.rcvDefaultTheme.layoutManager = GridLayoutManager(this, 2)
        // call theme
        callThemeList=InstallData.getCallThemeList()
        callThemeAdapter = CallThemeAdapter(this, callThemeList)
        changeThemeBinding.rcvCallTheme.adapter = callThemeAdapter
        changeThemeBinding.rcvCallTheme.layoutManager = GridLayoutManager(this, 2)
        changeThemeBinding.backButton.setOnClickListener {
            finish()
        }
    }
}