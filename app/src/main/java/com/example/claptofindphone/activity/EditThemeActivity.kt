package com.example.claptofindphone.activity


import android.app.AlertDialog
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.claptofindphone.R
import com.example.claptofindphone.databinding.ActivityEditThemeBinding
import com.example.claptofindphone.databinding.DialogEditThemeBinding
import com.example.claptofindphone.model.CallTheme
import com.example.claptofindphone.model.DefaultTheme
import com.example.claptofindphone.utils.SharePreferenceUtils


class EditThemeActivity : BaseActivity() {
    private lateinit var editThemeBinding: ActivityEditThemeBinding
    private lateinit var selectedThemeName:String
    private lateinit var name:String
    private lateinit var phone:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editThemeBinding = ActivityEditThemeBinding.inflate(layoutInflater)
        setContentView(editThemeBinding.root)
        name=SharePreferenceUtils.getName()
        phone=SharePreferenceUtils.getPhone()
        val defaultTheme = intent.getSerializableExtra("default_theme") as? DefaultTheme
        if (defaultTheme != null) {
            editThemeBinding.callThemeLayout.visibility = View.GONE
            editThemeBinding.editButton.visibility = View.GONE
            editThemeBinding.defaultThemeLayout.visibility = View.VISIBLE
            editThemeBinding.rejectButton.visibility=View.GONE
            editThemeBinding.responseButton.visibility=View.GONE
            editThemeBinding.backButton.setImageResource(R.drawable.back_ic)
            editThemeBinding.txtEditTheme.setTextColor(ContextCompat.getColor(this, R.color.black))
            editThemeBinding.activityEditTheme.setBackgroundResource(defaultTheme.defaultThemeBg)
            editThemeBinding.dfThemeLottie.setAnimation(defaultTheme.defaultThemeLottie)
            editThemeBinding.bellDefaultTheme.setImageResource(defaultTheme.defaultTheme)
            selectedThemeName=defaultTheme.themeName

        }
        // call theme
        val callTheme= intent.getSerializableExtra("call_theme") as? CallTheme
        if(callTheme!=null){
            editThemeBinding.txtName.isSelected=true
            editThemeBinding.defaultThemeLayout.visibility = View.GONE
            editThemeBinding.callThemeLayout.visibility = View.VISIBLE
            editThemeBinding.rejectButton.visibility=View.VISIBLE
            editThemeBinding.responseButton.visibility=View.VISIBLE
            editThemeBinding.editButton.visibility = View.VISIBLE
            editThemeBinding.backButton.setImageResource(R.drawable.back_ic2)
            editThemeBinding.txtEditTheme.setTextColor(ContextCompat.getColor(this, R.color.white))
            editThemeBinding.activityEditTheme.setBackgroundResource(callTheme.callThemeBg)
            editThemeBinding.callThemeLottie.setAnimation(callTheme.callThemeLottie)
            editThemeBinding.rejectButton.setAnimation(callTheme.callThemeRejectLottie)
            editThemeBinding.responseButton.setAnimation(callTheme.callThemeResponseLottie)
            if (name!=""){
                editThemeBinding.txtName.text=name
            }
            if (phone!=""){
                editThemeBinding.txtPhone.text=phone
            }
            selectedThemeName=callTheme.themeName
        }
        editThemeBinding.applyButton.setOnClickListener {
            SharePreferenceUtils.setThemeName(selectedThemeName)
            finish()

        }

        editThemeBinding.backButton.setOnClickListener {
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
        name= SharePreferenceUtils.getName()
        phone=SharePreferenceUtils.getPhone()
        dialogBinding.etxPhone.setText(phone)
        dialogBinding.etxName.setText(name)


        val alphanumericVietnameseFilter = object : InputFilter {
            override fun filter(
                source: CharSequence?, start: Int, end: Int,
                dest: Spanned?, dstart: Int, dend: Int
            ): CharSequence? {
                val regex = Regex("[^\\p{L}\\p{N} ]") // Chặn tất cả ký tự không phải chữ cái, số hoặc dấu cách
                return if (source != null && regex.containsMatchIn(source)) {
                    "" // Chặn nhập ký tự không hợp lệ
                } else {
                    null // Cho phép nhập
                }
            }
        }

// Áp dụng bộ lọc vào EditText
        dialogBinding.etxName.filters = arrayOf(
            InputFilter.LengthFilter(30), // Giới hạn tối đa 30 ký tự
            alphanumericVietnameseFilter // Chặn ký tự đặc biệt, cho phép tiếng Việt
        )


        val phoneFilter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isDigit(source[i])) { // Kiểm tra từng ký tự có phải số hay không
                    return@InputFilter ""
                }
            }

            // Kiểm tra nếu tổng số ký tự trong EditText vượt quá 12
            if ((dest.length + (end - start)) > 12) {
                return@InputFilter ""
            }

            // Nếu không vi phạm điều kiện nào, cho phép nhập
            null
        }

        dialogBinding.etxPhone.filters= arrayOf(phoneFilter)
//        dialogBinding.etxName.filters= arrayOf(nameFilter)

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
                SharePreferenceUtils.setName(dialogBinding.etxName.text.trim().toString())
                SharePreferenceUtils.setPhone(dialogBinding.etxPhone.text.trim().toString())
                customDialog.dismiss()
                editThemeBinding.txtName.text=dialogBinding.etxName.text.trim().toString()
                editThemeBinding.txtPhone.text=dialogBinding.etxPhone.text.trim().toString()
            }
        }
    }


}