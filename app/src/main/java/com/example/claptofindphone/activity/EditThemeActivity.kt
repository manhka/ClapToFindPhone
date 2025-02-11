package com.example.claptofindphone.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityEditThemeBinding
import com.example.claptofindphone.model.CallTheme
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.DefaultTheme

class EditThemeActivity : AppCompatActivity() {
    private lateinit var editThemeBinding: ActivityEditThemeBinding
    private lateinit var themeSharedPreferences: SharedPreferences
    private lateinit var selectedThemeName:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editThemeBinding = ActivityEditThemeBinding.inflate(layoutInflater)
        setContentView(editThemeBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_edit_theme)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        themeSharedPreferences= getSharedPreferences(Constant.SharePres.THEME_SHARE_PRES,
            MODE_PRIVATE)

// default theme
        val defaultTheme = intent.getSerializableExtra("default_theme") as? DefaultTheme
        if (defaultTheme != null) {
            editThemeBinding.callThemeLayout.visibility = View.GONE
            editThemeBinding.editButton.visibility = View.GONE
            editThemeBinding.defaultThemeLayout.visibility = View.VISIBLE
            editThemeBinding.backButton.setImageResource(R.drawable.back_ic)
            editThemeBinding.txtEditTheme.setTextColor(ContextCompat.getColor(this, R.color.black))
            editThemeBinding.activityEditTheme.setBackgroundResource(defaultTheme.defaultThemeBg)
            editThemeBinding.imgViewRound4.setImageResource(defaultTheme.defaultThemeRound4)
            editThemeBinding.imgViewRound3.setImageResource(defaultTheme.defaultThemeRound3)
            editThemeBinding.imgViewRound2.setImageResource(defaultTheme.defaultThemeRound2)
            editThemeBinding.imgViewRoundCenter.setImageResource(defaultTheme.defaultThemeRoundCenter)
            editThemeBinding.imgViewBell.setImageResource(defaultTheme.defaultThemeBell)
            editThemeBinding.imgViewSmallLeft.setImageResource(defaultTheme.defaultThemeSmallLeft)
            editThemeBinding.imgViewBigLeft.setImageResource(defaultTheme.defaultThemeBigLeft)
            editThemeBinding.imgViewSmallRight.setImageResource(defaultTheme.defaultThemeSmallRight)
            editThemeBinding.imgViewBigRight.setImageResource(defaultTheme.defaultThemeBigRight)
            selectedThemeName=defaultTheme.themeName
        }
        // call theme
        val callTheme= intent.getSerializableExtra("call_theme") as? CallTheme
        if(callTheme!=null){
            editThemeBinding.defaultThemeLayout.visibility = View.GONE
            editThemeBinding.callThemeLayout.visibility = View.VISIBLE
            editThemeBinding.editButton.visibility = View.VISIBLE
            editThemeBinding.backButton.setImageResource(R.drawable.back_ic2)
            editThemeBinding.txtEditTheme.setTextColor(ContextCompat.getColor(this, R.color.white))
            editThemeBinding.activityEditTheme.setBackgroundResource(callTheme.callThemeBg)
            editThemeBinding.imgViewCallThemeRound1.setImageResource(callTheme.callThemeRound1)
            editThemeBinding.imgViewCallThemeRound2.setImageResource(callTheme.callThemeRound2)
            editThemeBinding.imgViewCallThemeProfile.setImageResource(callTheme.callThemeProfile)
            editThemeBinding.txtName.text=callTheme.callThemeName
            editThemeBinding.txtPhone.text=callTheme.callThemePhone
            selectedThemeName=callTheme.themeName
        }
        editThemeBinding.applyButton.setOnClickListener {
            themeSharedPreferences.edit().putString(Constant.SharePres.ACTIVE_THEME_NAME,selectedThemeName).apply()
            val intent = Intent(this, ChangeThemeActivity::class.java)
            startActivity(intent)
            finish()
        }

        editThemeBinding.backButton.setOnClickListener {
            finish()
        }
    }
}