package com.example.claptofindphone.utils

import android.content.Context
import com.example.claptofindphone.R
import com.example.claptofindphone.model.CallTheme
import com.example.claptofindphone.model.Constant
import com.example.claptofindphone.model.DefaultTheme
import com.example.claptofindphone.model.Flashlight
import com.example.claptofindphone.model.Language
import com.example.claptofindphone.model.Sound
import com.example.claptofindphone.model.Vibrate


class InstallData {
    companion object {

        fun getFlashlightList(context: Context): List<Flashlight> {
            return listOf(
                Flashlight(
                    1,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Flashlight.default),
                    0,
                    R.drawable.active_theme_ic,
                    listOf(200L, 200L)
                ),
                Flashlight(
                    2,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Flashlight.flashlight1),
                    R.drawable.ic_premium,
                    R.drawable.tick_ic,
                    listOf(300L, 300L)
                ),
                Flashlight(
                    3,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Flashlight.flashlight2),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(500L, 500L)
                ),
                Flashlight(
                    4,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Flashlight.flashlight3),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(200L, 100L, 200L)
                ),
                Flashlight(
                    5,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Flashlight.flashlight4),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(1000L, 500L)
                ),
                Flashlight(
                    6,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Flashlight.flashlight5),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(300L, 300L, 600L, 300L)
                ),
                Flashlight(
                    7,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Flashlight.flashlight6),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(150L, 150L)
                ),
                Flashlight(
                    8,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Flashlight.flashlight7),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(700L, 300L, 100L)
                ),
                Flashlight(
                    9,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Flashlight.flashlight8),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(500L, 100L, 200L, 300L)
                ),
                Flashlight(
                    10,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Flashlight.flashlight9),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(1000L, 1000L)
                ),
                Flashlight(
                    11,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Flashlight.flashlight10),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(100L, 100L, 100L)
                ),
                Flashlight(
                    12,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Flashlight.flashlight11),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(400L, 200L, 600L)
                )
            )
        }

        fun getListSound(context: Context): List<Sound> {
            return listOf(
                Sound(
                    1,
                    context.getString(Constant.Sound.CAT),
                    R.drawable.cat_meowing_ic,
                    R.drawable.bg_sound_passive,
                    R.raw.cat_meowing
                ),
                Sound(
                    2,
                    context.getString(Constant.Sound.DOG),
                    R.drawable.dog_barking_ic,
                    R.drawable.bg_sound_passive,
                    R.raw.dog_barking
                ),
                Sound(
                    3,
                    context.getString(Constant.Sound.HEY_STAY_HERE),
                    R.drawable.hey_stay_here_ic,
                    R.drawable.bg_sound_passive,
                    R.raw.stay_here
                ),
                Sound(
                    4,
                    context.getString(Constant.Sound.WHISTLE),
                    R.drawable.whistle_ic,
                    R.drawable.bg_sound_passive,
                    R.raw.whistle
                ),
                Sound(
                    5,
                    context.getString(Constant.Sound.HELLO),
                    R.drawable.hello_ic,
                    R.drawable.bg_sound_passive,
                    R.raw.hello
                ),
                Sound(
                    6,
                    context.getString(Constant.Sound.CAR_HONK),
                    R.drawable.car_horn_ic,
                    R.drawable.bg_sound_passive,
                    R.raw.car_honk
                ),
                Sound(
                    7,
                    context.getString(Constant.Sound.DOOR_BELL),
                    R.drawable.door_bell_ic,
                    R.drawable.bg_sound_passive,
                    R.raw.door_bell
                ),
                Sound(
                    8,
                    context.getString(Constant.Sound.PARTY_HORN),
                    R.drawable.party_horn_ic,
                    R.drawable.bg_sound_passive,
                    R.raw.party_horn
                ),
                Sound(
                    9,
                    context.getString(Constant.Sound.POLICE_WHISTLE),
                    R.drawable.police_whistle_ic,
                    R.drawable.bg_sound_passive,
                    R.raw.police_whistle
                ),
                Sound(
                    10,
                    context.getString(Constant.Sound.CAVALRY),
                    R.drawable.cavalry_ic,
                    R.drawable.bg_sound_passive,
                    R.raw.cavalry
                ),
                Sound(
                    11,
                    context.getString(Constant.Sound.ARMY_TRUMPET),
                    R.drawable.army_trumpet_ic,
                    R.drawable.bg_sound_passive,
                    R.raw.army_trumpet
                ),
                Sound(
                    12,
                    context.getString(Constant.Sound.RIFLE),
                    R.drawable.rifle_ic,
                    R.drawable.bg_sound_passive,
                    R.raw.rifle
                )
            )
        }

        fun getVibrateList(context: Context): List<Vibrate> {
            return listOf(
                Vibrate(
                    1,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Vibrate.default),
                    0,
                    R.drawable.active_theme_ic,
                    listOf(0L, 100L)
                ),
                Vibrate(
                    2,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Vibrate.vibrate1),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(0L, 200L, 100L, 300L)
                ),
                Vibrate(
                    3,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Vibrate.vibrate2),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(0L, 300L, 100L, 500L)
                ),
                Vibrate(
                    4,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Vibrate.vibrate3),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(0L, 400L, 200L, 400L, 200L, 600L)
                ),
                Vibrate(
                    5,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Vibrate.vibrate4),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(0L, 500L, 100L, 300L, 200L, 500L)
                ),
                Vibrate(
                    6,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Vibrate.vibrate5),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(0L, 600L, 200L, 400L)
                ),
                Vibrate(
                    7,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Vibrate.vibrate6),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(0L, 700L, 100L, 100L, 100L, 700L)
                ),
                Vibrate(
                    8,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Vibrate.vibrate7),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(0L, 200L, 300L, 200L, 300L, 400L)
                ),
                Vibrate(
                    9,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Vibrate.vibrate8),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(0L, 300L, 100L, 200L, 100L, 300L, 200L, 500L)
                ),
                Vibrate(
                    10,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Vibrate.vibrate9),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(0L, 400L, 100L, 300L, 100L, 400L)
                ),
                Vibrate(
                    11,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Vibrate.vibrate10),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(0L, 500L, 100L, 200L, 300L, 600L)
                ),
                Vibrate(
                    12,
                    R.drawable.bg_passive_item,
                    context.getString(Constant.Vibrate.vibrate11),
                    R.drawable.ic_premium,
                    R.drawable.active_theme_ic,
                    listOf(0L, 600L, 200L, 400L, 200L, 300L)
                )
            )
        }

        fun getCallThemeList(): List<CallTheme> {
            return listOf(
                CallTheme(
                    Constant.CallTheme.CallTheme1,
                    R.drawable.bg_call_theme_1,
                    R.raw.anim_avatar,
                    R.drawable.call_theme_round2,
                    R.drawable.call_theme_round1,
                    R.drawable.profile_circle,
                    R.raw.call,
                    R.raw.call_end,
                    R.drawable.call_ic,
                    R.drawable.call_end_ic,
                    R.string.name.toString(),
                    R.string.phone.toString(),
                    0
                ),
                CallTheme(
                    Constant.CallTheme.CallTheme2,
                    R.drawable.bg_call_theme_2,
                    R.raw.anim_avatar,
                    R.drawable.call_theme_round2,
                    R.drawable.call_theme_round1,
                    R.drawable.profile_circle,
                    R.raw.call_2,
                    R.raw.call_end2,
                    R.drawable.call_ic,
                    R.drawable.call_end_ic,
                    R.string.name.toString(),
                    R.string.phone.toString(),
                    R.drawable.ic_premium
                ),
                CallTheme(
                    Constant.CallTheme.CallTheme3,
                    R.drawable.bg_call_theme_3,
                    R.raw.anim_avatar,
                    R.drawable.call_theme_round2,
                    R.drawable.call_theme_round1,
                    R.drawable.profile_circle,
                    R.raw.call_like,
                    R.raw.call_end_dislike,
                    R.drawable.like_ic,
                    R.drawable.dislike_ic,

                    R.string.name.toString(),
                    R.string.phone.toString(),
                    R.drawable.ic_premium
                ),
                CallTheme(
                    Constant.CallTheme.CallTheme4,
                    R.drawable.bg_call_theme_4,
                    R.raw.anim_avatar,
                    R.drawable.call_theme_round2,
                    R.drawable.call_theme_round1,
                    R.drawable.profile_circle,
                    R.raw.call_4,
                    R.raw.call_end_4,
                    R.drawable.call2,
                    R.drawable.call_end2,
                    R.string.name.toString(),
                    R.string.phone.toString(),
                    R.drawable.ic_premium
                ),
                CallTheme(
                    Constant.CallTheme.CallTheme5,
                    R.drawable.bg_call_theme_5,
                    R.raw.anim_avatar,
                    R.drawable.call_theme_round2,
                    R.drawable.call_theme_round1,
                    R.drawable.profile_circle,
                    R.raw.call,
                    R.raw.call_end,
                    R.drawable.call_ic,
                    R.drawable.call_end_ic,
                    R.string.name.toString(),
                    R.string.phone.toString(),
                    R.drawable.ic_premium
                ),
                CallTheme(
                    Constant.CallTheme.CallTheme6,
                    R.drawable.bg_call_theme_6,
                    R.raw.anim_avatar,
                    R.drawable.call_theme_round2,
                    R.drawable.call_theme_round1,
                    R.drawable.profile_circle,
                    R.raw.call_2,
                    R.raw.call_end2,
                    R.drawable.call_ic,
                    R.drawable.call_end_ic,
                    R.string.name.toString(),
                    R.string.phone.toString(),
                    R.drawable.ic_premium
                ),
                CallTheme(
                    Constant.CallTheme.CallTheme7,
                    R.drawable.bg_call_theme_7,
                    R.raw.anim_avatar,
                    R.drawable.call_theme_round2,
                    R.drawable.call_theme_round1,
                    R.drawable.profile_circle,
                    R.raw.call_4,
                    R.raw.call_end_4,
                    R.drawable.call2,
                    R.drawable.call_end2,
                    R.string.name.toString(),
                    R.string.phone.toString(),
                    R.drawable.ic_premium
                ),
                CallTheme(
                    Constant.CallTheme.CallTheme8,
                    R.drawable.bg_call_theme_8,
                    R.raw.anim_avatar,
                    R.drawable.call_theme_round2,
                    R.drawable.call_theme_round1,
                    R.drawable.profile_circle,
                    R.raw.call_like,
                    R.raw.call_end_dislike,
                    R.drawable.like_ic,
                    R.drawable.dislike_ic,
                    R.string.name.toString(),
                    R.string.phone.toString(),
                    R.drawable.ic_premium
                ),
                CallTheme(
                    Constant.CallTheme.CallTheme9,
                    R.drawable.bg_call_theme_9,
                    R.raw.anim_avatar,
                    R.drawable.call_theme_round2,
                    R.drawable.call_theme_round1,
                    R.drawable.profile_circle,
                    R.raw.call,
                    R.raw.call_end,
                    R.drawable.call_ic,
                    R.drawable.call_end_ic,
                    R.string.name.toString(),
                    R.string.phone.toString(),
                    R.drawable.ic_premium
                ),
                CallTheme(
                    Constant.CallTheme.CallTheme10,
                    R.drawable.bg_call_theme_10,
                    R.raw.anim_avatar,
                    R.drawable.call_theme_round2,
                    R.drawable.call_theme_round1,
                    R.drawable.profile_circle,
                    R.raw.call_4,
                    R.raw.call_end_4,
                    R.drawable.call2,
                    R.drawable.call_end2,
                    R.string.name.toString(),
                    R.string.phone.toString(),
                    R.drawable.ic_premium
                )
            )
        }

        fun getDefaultThemeList(): List<DefaultTheme> {
            return listOf(
                DefaultTheme(
                    Constant.DefaultTheme.DefaultTheme1,
                    R.color.bg_df_theme1,
                    R.drawable.round_circle_df_theme1,
                    R.raw.anim_df_lottie1,
                    R.drawable.bell_default_theme1,
                    R.drawable.active_theme_ic
                ), DefaultTheme(
                    Constant.DefaultTheme.DefaultTheme2,
                    R.color.bg_df_theme2,
                    R.drawable.round_circle_df_theme2,
                    R.raw.anim_df_lottie2,
                    R.drawable.bell_default_theme2,
                    0
                ),

                DefaultTheme(
                    Constant.DefaultTheme.DefaultTheme3,
                    R.color.bg_df_theme3,

                    R.drawable.round_circle_df_theme3,
                    R.raw.anim_df_lottie3,
                    R.drawable.bell_default_theme3,
                    0
                ),

                DefaultTheme(
                    Constant.DefaultTheme.DefaultTheme4,
                    R.color.bg_df_theme4,
                    R.drawable.round_circle_df_theme4,
                    R.raw.anim_df_lottie4, R.drawable.bell_default_theme4, 0
                )
            )

        }

        fun getLanguageList(): List<Language> {
            return mutableListOf(
                Language(
                    Constant.Country.ENGLISH,
                    "en",
                    R.drawable.bg_btn_grey,
                    R.drawable.english,
                    R.drawable.passive_radio
                ),
                Language(
                    Constant.Country.VIETNAM,
                    "vi",
                    R.drawable.bg_btn_grey,
                    R.drawable.vietnam,
                    R.drawable.passive_radio
                ),
                Language(
                    Constant.Country.FRENCH,
                    "fr",
                    R.drawable.bg_btn_grey,
                    R.drawable.france,
                    R.drawable.passive_radio
                ),
                Language(
                    Constant.Country.INDIA,
                    "hi",
                    R.drawable.bg_btn_grey,
                    R.drawable.india,
                    R.drawable.passive_radio
                ),
                Language(
                    Constant.Country.INDONESIA,
                    "su",
                    R.drawable.bg_btn_grey,
                    R.drawable.indonesia,
                    R.drawable.passive_radio
                ),
                Language(
                    Constant.Country.JAPAN,
                    "ja",
                    R.drawable.bg_btn_grey,
                    R.drawable.japan,
                    R.drawable.passive_radio
                ),
                Language(
                    Constant.Country.BRAZILIAN,
                    "pt",
                    R.drawable.bg_btn_grey,
                    R.drawable.brazil,
                    R.drawable.passive_radio
                ),
                Language(
                    Constant.Country.KOREAN,
                    "ko",
                    R.drawable.bg_btn_grey,
                    R.drawable.south_korea,
                    R.drawable.passive_radio
                ),
                Language(
                    Constant.Country.TURKEY,
                    "tr",
                    R.drawable.bg_btn_grey,
                    R.drawable.turkey,
                    R.drawable.passive_radio
                )
            )
        }
    }
}