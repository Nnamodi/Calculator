package com.roland.android.calculator.util

import android.content.Context
import android.os.*
import android.view.MotionEvent
import android.view.View

object Haptic {
    internal class ClickFeedback(private val context: Context) : View.OnTouchListener {
        private var halfWidth = 0f
        private var negHalfWidth = 0f
        private var halfHeight = 0f
        private var negHalfHeight = 0f

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    haptic(context, 50)
                    view.isPressed = true
                    halfWidth = event.x + (view.width / 2)
                    negHalfWidth = event.x - (view.width / 2)
                    halfHeight = event.y + (view.height / 2)
                    negHalfHeight = event.y - (view.height / 2)
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    if ((event.x > halfWidth || event.x < negHalfWidth) ||
                        (event.y > halfHeight || event.y < negHalfHeight)
                    ) { view.isPressed = false }
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    view.apply {
                        if (isPressed) { performClick(); isPressed = false }
                    }
                    haptic(context, 30)
                    return true
                }
            }
            return true
        }
    }

    @Suppress("DEPRECATION")
    fun haptic(context: Context, milliSec: Long) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
                    .vibrate(
                        CombinedVibration.createParallel(
                            VibrationEffect.createOneShot(
                                milliSec, VibrationEffect.EFFECT_CLICK
                            )
                        )
                    )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                    .vibrate(
                        VibrationEffect.createOneShot(
                            milliSec, VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
            }
            else -> {
                (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                    .vibrate(milliSec)
            }
        }
    }
}