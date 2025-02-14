package com.example.claptofindphone.model

class Constant {
    object SharePres {
        const val FIRST_TIME_JOIN_SHARE_PRES = "first_time_join"
        const val FIRST_TIME_GET_IN_APP = "first_time_get_in_app"
        const val FIRST_TIME_GET_IN_CLAP_AND_WHISTLE = "first_time_get_in_clap_and_whistle"
        const val FIRST_TIME_GET_IN_VOICE_PASSCODE = "first_time_get_in_voice_passcode"
        const val FIRST_TIME_GET_IN_POCKET_MODE = "first_time_get_in_pocket_mode"
        const val FIRST_TIME_GET_IN_CHARGER_ALARM = "first_time_get_in_charger_alarm"
        const val FIRST_TIME_GET_IN_TOUCH_PHONE = "first_time_get_in_touch_phone"
        const val LANGUAGE_SHARE_PRES = "language"
        const val CURRENT_LANGUAGE = "current_language"
        const val SERVICE_SHARE_PRES = "service_share_pres"

        // flashlight
        const val FLASHLIGHT_SHARE_PRES = "flashlight_share_pres"
        const val FLASHLIGHT_STATUS = "flashlight_status"
        const val ACTIVE_FLASHLIGHT_NAME = "active_flashlight_name"

        // vibrate
        const val VIBRATE_SHARE_PRES = "vibrate_share_pres"
        const val VIBRATE_STATUS = "vibrate_status"
        const val ACTIVE_VIBRATE_NAME = "active_vibrate_name"

        // sound
        const val SOUND_SHARE_PRES = "sound_share_pres"
        const val SOUND_STATUS = "sound_status"
        const val ACTIVE_SOUND_NAME = "active_sound_name"
        const val TIME_SOUND_PLAY = "time_sound_play"
        const val SOUND_VOLUME = "sound_volume"

        // theme
        const val THEME_SHARE_PRES = "theme_share_pres"
        const val ACTIVE_THEME_NAME = "active_theme_name"
        const val NAME = "name"
        const val PHONE = "phone"

        // voice passcode
        const val VOICE_PASSCODE_SHARE_PRES = "voice_passcode_share_pres"
        const val PASSCODE = "passcode"
        // notification
        const val NOTIFICATION_SHARE_PRES ="notification_share_pres"
        const val DENY_COUNT="deny_count"
    }

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
        const val CAT = "Cat meowing"
        const val DOG = "Dog barking"
        const val HEY_STAY_HERE = "Hey stay here"
        const val WHISTLE = "Whistle"
        const val HELLO = "Hello"
        const val CAR_HONK = "Car horn"
        const val DOOR_BELL = "Door bell"
        const val PARTY_HORN = "Party horn"
        const val POLICE_WHISTLE = "Police whistle"
        const val CAVARLY = "Cavarly"
        const val ARMY_TRUMPET = "Army trumpet"
        const val RIFLE = "Rifle"
    }

    object Flashlight {
        const val default = "Default"
        const val flashlight1 = "Flashlight 1"
        const val flashlight2 = "Flashlight 2"
        const val flashlight3 = "Flashlight 3"
        const val flashlight4 = "Flashlight 4"
        const val flashlight5 = "Flashlight 5"
        const val flashlight6 = "Flashlight 6"
        const val flashlight7 = "Flashlight 7"
        const val flashlight8 = "Flashlight 8"
        const val flashlight9 = "Flashlight 9"
        const val flashlight10 = "Flashlight 10"
        const val flashlight11 = "Flashlight 11"

    }

    object Vibrate {
        const val default = "Default"
        const val vibrate1 = "Vibrate 1"
        const val vibrate2 = "Vibrate 2"
        const val vibrate3 = "Vibrate 3"
        const val vibrate4 = "Vibrate 4"
        const val vibrate5 = "Vibrate 5"
        const val vibrate6 = "Vibrate 6"
        const val vibrate7 = "Vibrate 7"
        const val vibrate8 = "Vibrate 8"
        const val vibrate9 = "Vibrate 9"
        const val vibrate10 = "Vibrate 10"
        const val vibrate11 = "Vibrate 11"

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


    companion object {
        val URL_PRIVACY = "https://sites.google.com/view/nksoftpolicy/home"
        val URL_APP =
            "https://play.google.com/store/apps/details?id=1"
    }

}