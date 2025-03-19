package com.example.claptofindphone.model
import com.example.claptofindphone.R

class Constant {
    object Permission {
        // permission
        const val RECORDING_PERMISSION = "recording_permission"
        const val OVERLAY_PERMISSION = "overlay_permission"
        const val  BOTH_PERMISSION="both_permission"
       const val AUDIO_PERMISSION_REQUEST_CODE = 1001
       const val OVERLAY_PERMISSION_REQUEST_CODE = 1002
    }

    object Service {
        const val CLAP_TO_FIND_PHONE = "clap_to_find_phone"
        const val VOICE_PASSCODE = "voice_passcode"
        const val DONT_TOUCH_MY_PHONE = "dont_touch_my_phone"
        const val CHARGER_PHONE = "charger_phone"
        const val POCKET_MODE = "pocket_mode"
        const val RUNNING_SERVICE = "running_service"
        const val CLAP_AND_WHISTLE_RUNNING = "clap_and_whistle_running"
        const val TURN_OFF_SOUND = "TURN_OFF_SOUND"
        const val VOICE_PASSCODE_RUNNING = "voice_passcode_running"
        const val POCKET_MODE_RUNNING = "pocket_mode_running"
        const val TOUCH_PHONE_RUNNING = "touch_phone_running"
        const val CHARGER_ALARM_RUNNING = "charger_alarm_running"
    }

    object Country {
        const val ENGLISH = "English"
        const val VIETNAM = "Vietnam"
        const val FRENCH = "French"
        const val INDIA = "India"
        const val INDONESIA = "Indonesia"
        const val JAPAN = "Japan"
        const val BRAZILIAN = "Brazilian"
        const val KOREAN = "Korean"
        const val TURKEY = "Turkey"
    }

    object Sound {
        // Make sure context is passed in or accessed
        val CAT = R.string.cat_meowing
        val DOG = R.string.dog_barking
        val HEY_STAY_HERE = R.string.hey_stay_here
        val WHISTLE = R.string.whistle
        val HELLO = R.string.hello
        val CAR_HONK = R.string.car_horn
        val DOOR_BELL = R.string.door_bell
        val PARTY_HORN = R.string.party_horn
        val POLICE_WHISTLE = R.string.police_whistle
        val CAVALRY = R.string.cavarly
        val ARMY_TRUMPET = R.string.army_trumpet
        val RIFLE = R.string.rifle
    }


    object Flashlight {
         val default = R.string.default_flash
         val flashlight1 = R.string.flashlight1
         val flashlight2 = R.string.flashlight2
         val flashlight3 = R.string.flashlight3
         val flashlight4 = R.string.flashlight4
         val flashlight5 = R.string.flashlight5
         val flashlight6 = R.string.flashlight6
         val flashlight7 = R.string.flashlight7
         val flashlight8 = R.string.flashlight8
         val flashlight9 = R.string.flashlight9
         val flashlight10 = R.string.flashlight10
         val flashlight11 = R.string.flashlight11

    }

    object Vibrate {
         val default = R.string.vibrate_default
         val vibrate1 = R.string.vibrate1
         val vibrate2 = R.string.vibrate2
         val vibrate3 = R.string.vibrate3
         val vibrate4 =R.string.vibrate4
         val vibrate5 = R.string.vibrate5
         val vibrate6 = R.string.vibrate6
         val vibrate7 = R.string.vibrate7
         val vibrate8 = R.string.vibrate8
         val vibrate9 = R.string.vibrate9
         val vibrate10 = R.string.vibrate10
         val vibrate11 = R.string.vibrate11

    }

    object CallTheme {
        const val CallTheme1 = "call theme 1"
        const val CallTheme2 = "call theme 2"
        const val CallTheme3 = "call theme 3"
        const val CallTheme4 = "call theme 4"
        const val CallTheme5 = "call theme 5"
        const val CallTheme6 = "call theme 6"
        const val CallTheme7 = "call theme 7"
        const val CallTheme8 = "call theme 8"
        const val CallTheme9 = "call theme 9"
        const val CallTheme10 = "call theme 10"
    }

    object DefaultTheme {
        const val DefaultTheme1 = "default theme 1"
        const val DefaultTheme2 = "default theme 2"
        const val DefaultTheme3 = "default theme 3"
        const val DefaultTheme4 = "default theme 4"
    }

object Notification{
   val CHANNEL_ID = "clap_service_channel"
    val CHANNEL_NAME = "Clap Detection Service"
}

    companion object {
        val DEFAULT_PASSCODE="12ASF3456SFSA789MNASHDNSJSDF"
        val CHANNEL_NAME: CharSequence="ClapFindPhone_Notification"
        val CHANNEL_ID: String= "ClapFindPhone_Notification_ID"
        val URL_PRIVACY = "https://sites.google.com/view/nksoftpolicy/home"
        val URL_APP =
            "https://play.google.com/store/apps/details?id=1"
    }

}