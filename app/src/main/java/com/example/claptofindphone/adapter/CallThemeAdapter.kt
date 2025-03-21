package com.example.claptofindphone.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.claptofindphone.R
import com.example.claptofindphone.activity.EditThemeActivity
import com.example.claptofindphone.databinding.CallThemeItemBinding
import com.example.claptofindphone.databinding.DialogWatchAdBinding
import com.example.claptofindphone.model.CallTheme
import com.example.claptofindphone.service.PermissionController
import com.example.claptofindphone.utils.SharePreferenceUtils

class CallThemeAdapter(val context: Context,
    val callThemeList: List<CallTheme>): RecyclerView.Adapter<CallThemeAdapter.CallThemeViewHolder>() {
    class CallThemeViewHolder(callThemeItemBinding: CallThemeItemBinding) :
        RecyclerView.ViewHolder(callThemeItemBinding.root) {
        val callThemeItemBinding: CallThemeItemBinding = callThemeItemBinding
    }
    val callThemeName = SharePreferenceUtils.getThemeName()
    private var mLastClickTime:Long = 0
    val name=SharePreferenceUtils.getName()
    val phone=SharePreferenceUtils.getPhone()
    var selectedPosition = callThemeList.indexOfFirst { it.themeName == callThemeName }
   val permissionController = PermissionController()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallThemeViewHolder {
        val callThemeItemBinding= CallThemeItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CallThemeViewHolder(callThemeItemBinding)
    }
    override fun getItemCount(): Int {
      return callThemeList.size
    }
    override fun onBindViewHolder(holder: CallThemeViewHolder, position: Int) {
        val callThemeItem= callThemeList[position]
        val isPremiumVisible = SharePreferenceUtils.isCallThemePremiumVisible(position)
        holder.callThemeItemBinding.bgCallTheme.setBackgroundResource(callThemeItem.callThemeBg)
        holder.callThemeItemBinding.round2CallTheme.setImageResource(callThemeItem.callThemeRound2)
        holder.callThemeItemBinding.round1CallTheme.setImageResource(callThemeItem.callThemeRound1)
        holder.callThemeItemBinding.profileCallTheme.setImageResource(callThemeItem.callThemeProfile)
        holder.callThemeItemBinding.rejectCallThemeButton.setImageResource(callThemeItem.callThemeReject)
        holder.callThemeItemBinding.responseCallThemeButton.setImageResource(callThemeItem.callThemeResponse)
        holder.callThemeItemBinding.txtName.isSelected=true
        if (name==""){
            holder.callThemeItemBinding.txtName.text=context.getString(R.string.name)

        }else{
            holder.callThemeItemBinding.txtName.text=name
        }
        if (phone==""){
            holder.callThemeItemBinding.txtPhone.text=context.getString(R.string.phone)

        }else{
            holder.callThemeItemBinding.txtPhone.text=phone
        }



        holder.callThemeItemBinding.premiumButton.setImageResource(callThemeItem.callThemePremium)
        if (isPremiumVisible) {
            holder.callThemeItemBinding.premiumButton.setImageResource(callThemeItem.callThemePremium)
        } else {
            callThemeItem.callThemePremium=0
            holder.callThemeItemBinding.premiumButton.setImageResource(0)
        }

        if (position == selectedPosition) {
            holder.callThemeItemBinding.premiumButton.setImageResource(R.drawable.active_theme_ic)
            holder.callThemeItemBinding.premiumButton.visibility = View.VISIBLE
        }
        holder.itemView.setOnClickListener {
            if (SystemClock.elapsedRealtime()-mLastClickTime<1000){
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (callThemeItem.callThemePremium!=0){
                if (permissionController.isInternetAvailable(context)){
                    val dialogBinding = DialogWatchAdBinding.inflate(LayoutInflater.from(context))
                    // Create an AlertDialog with the inflated ViewBinding root
                    val customDialog = AlertDialog.Builder(context)
                        .setView(dialogBinding.root)
                        .setCancelable(false)
                        .create()
                    customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    // Show the dialog
                    customDialog.show()
                    dialogBinding.watchAdTitle.text=context.getString(R.string.dialog_calltheme_title)
                    dialogBinding.watchAdsContent.text=context.getString(R.string.dialog_calltheme_content)
                    dialogBinding.watchAdsButton.setOnClickListener {
                        SharePreferenceUtils.setIsCallThemePremiumVisible(position,false)
//                    holder.vibrateItemBinding.premiumButton.setImageResource(0)
                        selectedPosition = position
                        notifyDataSetChanged()
                        customDialog.dismiss()
                        val intent= Intent(context,EditThemeActivity::class.java)
                        intent.putExtra("call_theme",callThemeItem)
                        context.startActivity(intent)
                    }
                    dialogBinding.exitButton.setOnClickListener {
                        customDialog.dismiss()
                    }
                }else{
                  showCustomToast()
                }

            }else{
                val intent= Intent(context,EditThemeActivity::class.java)
                intent.putExtra("call_theme",callThemeItem)
                context.startActivity(intent)
            }
        }
    }
    private fun showCustomToast() {
        // Inflate layout custom_toast.xml
        val layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.custom_toast, null)
        // Tạo và hiển thị Toast
        val toast = Toast(context)
        toast.setGravity(Gravity.BOTTOM, 0, 100)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }

}