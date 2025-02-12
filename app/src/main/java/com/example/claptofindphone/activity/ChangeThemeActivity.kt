package com.example.claptofindphone.activity

import android.content.SharedPreferences
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
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.DefaultTheme

class ChangeThemeActivity : AppCompatActivity() {
    private lateinit var changeThemeBinding: ActivityChangeThemeBinding
    private lateinit var defaultThemeAdapter: DefaultThemeAdapter
    private lateinit var callThemeAdapter: CallThemeAdapter
    private lateinit var defaultThemeList: List<DefaultTheme>
    private lateinit var callThemeList: List<CallTheme>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeThemeBinding = ActivityChangeThemeBinding.inflate(layoutInflater)
        setContentView(changeThemeBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_change_theme)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getDefaultThemeList()

        //default theme
        defaultThemeAdapter = DefaultThemeAdapter(this, defaultThemeList)
        changeThemeBinding.rcvDefaultTheme.adapter = defaultThemeAdapter
        changeThemeBinding.rcvDefaultTheme.layoutManager = GridLayoutManager(this, 2)
        // call theme
        getCallThemeList()
        callThemeAdapter = CallThemeAdapter(this, callThemeList)
        changeThemeBinding.rcvCallTheme.adapter = callThemeAdapter
        changeThemeBinding.rcvCallTheme.layoutManager = GridLayoutManager(this, 2)
        changeThemeBinding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun getDefaultThemeList() {
        defaultThemeList = listOf(
            DefaultTheme(
                Constant.DefaultTheme.DefaultTheme1,
                R.color.bg_df_theme1,
                R.drawable.round4_df_theme1,
                R.drawable.round3_theme_df1,
                R.drawable.round2_theme_df1,
                R.drawable.round_center_theme_df1,
                R.drawable.small_left_theme_df1,
                R.drawable.big_left_theme_df1,
                R.drawable.small_right_theme_df1,
                R.drawable.big_right_theme_df1,
                R.drawable.bell_theme_df1,
                R.drawable.active_theme_ic
            ), DefaultTheme(
                Constant.DefaultTheme.DefaultTheme2,
                R.color.bg_df_theme2, R.drawable.round4_df_theme2, R.drawable.round3_theme_df2,
                R.drawable.round2_theme_df2, R.drawable.round_center_theme_df2,
                R.drawable.small_left_theme_df2, R.drawable.big_left_theme_df2,
                R.drawable.small_right_theme_df2, R.drawable.big_right_theme_df2,
                R.drawable.bell_theme_df2, 0
            ),

            DefaultTheme(
                Constant.DefaultTheme.DefaultTheme3,
                R.color.bg_df_theme3, R.drawable.round3_theme_df3, R.drawable.round3_theme_df3,
                R.drawable.round2_theme_df3, R.drawable.round_center_theme_df3,
                R.drawable.small_left_theme_df3, R.drawable.big_left_theme_df3,
                R.drawable.small_right_theme_df3, R.drawable.big_right_theme_df3,
                R.drawable.bell_theme_df3, 0
            ),

            DefaultTheme(
                Constant.DefaultTheme.DefaultTheme4,
                R.color.bg_df_theme4, R.drawable.round4_df_theme4, R.drawable.round3_theme_df4,
                R.drawable.round2_theme_df4, R.drawable.round_center_theme_df4,
                R.drawable.small_left_theme_df4, R.drawable.big_left_theme_df4,
                R.drawable.small_right_theme_df4, R.drawable.big_right_theme_df4,
                R.drawable.bell_theme_df4, 0
            )
        )

    }

    private fun getCallThemeList() {
        callThemeList = listOf(
            CallTheme(
                Constant.CallTheme.CallTheme1,
                R.drawable.bg_call_theme1,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme2,
                R.drawable.bg_call_theme2,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme3,
                R.drawable.bg_call_theme3,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme4,
                R.drawable.bg_call_theme4,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme5,
                R.drawable.bg_call_theme5,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme6,
                R.drawable.bg_call_theme6,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme7,
                R.drawable.bg_call_theme7,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme8,
                R.drawable.bg_call_theme8,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme9,
                R.drawable.bg_call_theme9,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            ),
            CallTheme(
                Constant.CallTheme.CallTheme10,
                R.drawable.bg_call_theme10,
                R.drawable.call_theme_round2,
                R.drawable.call_theme_round1,
                R.drawable.profile_circle,
                R.drawable.call_ic,
                R.drawable.call_end_ic,
                getString(R.string.name),
                getString(R.string.phone),
                0
            )
        )
    }
}