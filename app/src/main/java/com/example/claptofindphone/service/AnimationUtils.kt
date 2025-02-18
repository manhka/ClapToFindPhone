package com.example.claptofindphone.service

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.ImageView

class AnimationUtils {
    companion object {
        private var scaleAnimation: ScaleAnimation? = null
        private var alphaAnimation: AlphaAnimation? = null
        private var translateAnimation: TranslateAnimation? = null

        fun applyAnimations(view: ImageView) {
            scaleAnimation = ScaleAnimation(
                1f, 1.5f, 1f, 1.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 500
                repeatCount = Animation.INFINITE
                repeatMode = Animation.REVERSE
            }

            alphaAnimation = AlphaAnimation(0.5f, 1f).apply {
                duration = 500
                repeatCount = Animation.INFINITE
                repeatMode = Animation.REVERSE
            }

            translateAnimation = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -0.1f,
                Animation.RELATIVE_TO_SELF, 0.1f
            ).apply {
                duration = 500
                repeatCount = Animation.INFINITE
                repeatMode = Animation.REVERSE
            }

            view.startAnimation(scaleAnimation)
            view.startAnimation(alphaAnimation)
            view.startAnimation(translateAnimation)
        }

        fun applyWaveAnimation(view: ImageView) {
            val waveAnimation = ScaleAnimation(
                1f, 5f, 1f, 5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 1000
                repeatCount = Animation.INFINITE
                repeatMode = Animation.RESTART
            }

            val waveAlphaAnimation = AlphaAnimation(1f, 0f).apply {
                duration = 1000
                repeatCount = Animation.INFINITE
                repeatMode = Animation.RESTART
            }

            view.startAnimation(waveAnimation)
            view.startAnimation(waveAlphaAnimation)
        }
        fun applyShakeAnimation(view: ImageView) {
            val shake = TranslateAnimation(0f, 20f, 0f, 0f).apply {
                duration = 500
                repeatCount = Animation.INFINITE
                repeatMode = Animation.REVERSE // Đảo ngược
            }

            view.startAnimation(shake)
        }
        fun stopAnimations(view: ImageView) {
            view.clearAnimation()
            scaleAnimation = null
            alphaAnimation = null
            translateAnimation = null
        }
    }
}