package com.roland.android.calculator.util

import android.content.Context
import android.os.*
import android.view.MotionEvent
import android.view.View

@Suppress("DEPRECATION")
object Haptic {
    internal class ClickFeedback(private val context: Context) : View.OnTouchListener {
        private var viewStillTouched = false
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    haptic(context, 50)
                    view.isPressed = true
                    viewStillTouched = true
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    view.isPressed = view.isFocused
                    viewStillTouched = view.isFocused
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (viewStillTouched) { view.performClick() }
                    haptic(context, 30)
                    view.isPressed = false
                    return true
                }
            }
            return true
        }
    }

    private fun haptic(context: Context, milliSec: Long) {
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