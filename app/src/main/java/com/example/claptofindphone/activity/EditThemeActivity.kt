package com.example.claptofindphone.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityEditThemeBinding
import com.example.claptofindphone.databinding.DialogEditThemeBinding
import com.example.claptofindphone.model.CallTheme
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.DefaultTheme
import com.example.claptofindphone.service.AnimationUtils

class EditThemeActivity : AppCompatActivity() {
    private lateinit var editThemeBinding: ActivityEditThemeBinding
    private lateinit var themeSharedPreferences: SharedPreferences
    private lateinit var selectedThemeName:String
    private lateinit var name:String
    private lateinit var phone:String
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
        name=themeSharedPreferences.getString(Constant.SharePres.NAME,getString(R.string.name)).toString()
        phone=themeSharedPreferences.getString(Constant.SharePres.PHONE,getString(R.string.phone)).toString()
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

            AnimationUtils.applyWaveAnimation(editThemeBinding.imgViewRound3)
            editThemeBinding.imgViewRound3.postDelayed({
                AnimationUtils.applyWaveAnimation(editThemeBinding.imgViewRound4)
            }, 500)

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
            editThemeBinding.txtName.text=name
            editThemeBinding.txtPhone.text=phone
            selectedThemeName=callTheme.themeName

            AnimationUtils.applyWaveAnimation(editThemeBinding.imgViewCallThemeRound1)
            editThemeBinding.imgViewCallThemeRound1.postDelayed({
                AnimationUtils.applyWaveAnimation(editThemeBinding.imgViewCallThemeRound2)
            }, 500)
        }
        editThemeBinding.applyButton.setOnClickListener {
            AnimationUtils.stopAnimations(editThemeBinding.imgViewCallThemeRound1)
            AnimationUtils.stopAnimations(editThemeBinding.imgViewCallThemeRound2)
            AnimationUtils.stopAnimations(editThemeBinding.imgViewRound3)
            AnimationUtils.stopAnimations(editThemeBinding.imgViewRound4)
            themeSharedPreferences.edit().putString(Constant.SharePres.ACTIVE_THEME_NAME,selectedThemeName).apply()
            val intent = Intent(this, ChangeThemeActivity::class.java)
            startActivity(intent)
            finish()
        }

        editThemeBinding.backButton.setOnClickListener {
            AnimationUtils.stopAnimations(editThemeBinding.imgViewCallThemeRound1)
            AnimationUtils.stopAnimations(editThemeBinding.imgViewCallThemeRound2)
            AnimationUtils.stopAnimations(editThemeBinding.imgViewRound3)
            AnimationUtils.stopAnimations(editThemeBinding.imgViewRound4)
            val intent=Intent(this,ChangeThemeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
        editThemeBinding.editButton.setOnClickListener {
            showEditDialog()
        }

    }

    private fun showEditDialog() {

        // Inflate the custom layout for the dialog using DialogCustomBinding
        val dialogBinding = DialogEditThemeBinding.inflate(layoutInflater)
        // Create an AlertDialog with the inflated ViewBinding root
        val customDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        // Show the dialog
        customDialog.show()
        dialogBinding.deleteNameButton.setOnClickListener {
            dialogBinding.etxName.setText("")
        }
        name=themeSharedPreferences.getString(Constant.SharePres.NAME,getString(R.string.name)).toString()
        phone=themeSharedPreferences.getString(Constant.SharePres.PHONE,getString(R.string.phone)).toString()
        dialogBinding.etxPhone.setText(phone)
        dialogBinding.etxName.setText(name)
        val nameFilter = InputFilter { source, start, end, dest, dstart, dend ->
            // Kiểm tra nếu ký tự nhập vào không phải là chữ cái hoặc chữ số
            if (source.matches("[^a-zA-Z0-9 ]".toRegex())) {
                return@InputFilter ""
            }

            // Kiểm tra nếu tổng số ký tự trong EditText vượt quá 30
            if ((dest.length + source.length) > 30) {
                return@InputFilter ""
            }

            // Nếu không vi phạm điều kiện nào, trả về null (cho phép nhập)
            return@InputFilter null
        }


        val phoneFilter = InputFilter { source, start, end, dest, dstart, dend ->
            // Kiểm tra nếu ký tự nhập vào không phải là chữ số
            if (source.matches("[^0-9]".toRegex())) {
                return@InputFilter ""
            }

            // Kiểm tra nếu tổng số ký tự trong EditText vượt quá 12
            if ((dest.length + source.length) > 12) {
                return@InputFilter ""
            }
            // Nếu không vi phạm điều kiện nào, trả về null (cho phép nhập)
            null
        }

        dialogBinding.etxPhone.filters= arrayOf(phoneFilter)
        dialogBinding.etxName.filters= arrayOf(nameFilter)

        dialogBinding.deletePhoneButton.setOnClickListener {
            dialogBinding.etxPhone.setText("")
        }
        dialogBinding.cancelButton.setOnClickListener {
            customDialog.dismiss()
        }
        dialogBinding.saveButton.setOnClickListener {
            if (dialogBinding.etxName.text.toString().isEmpty() || dialogBinding.etxPhone.text.toString().isEmpty()){
                Toast.makeText(this,getString(R.string.empty),Toast.LENGTH_SHORT).show()
            }else{
                themeSharedPreferences.edit().putString(Constant.SharePres.NAME,dialogBinding.etxName.text.trim().toString()).apply()
                themeSharedPreferences.edit().putString(Constant.SharePres.PHONE,dialogBinding.etxPhone.text.trim().toString()).apply()
                customDialog.dismiss()
                editThemeBinding.txtName.text=dialogBinding.etxName.text.trim().toString()
                editThemeBinding.txtPhone.text=dialogBinding.etxPhone.text.trim().toString()
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }
}