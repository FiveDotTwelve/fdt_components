package com.fivedottwelve.components

import android.animation.ValueAnimator
import android.support.annotation.RestrictTo
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object ViewHelper {

    /**
     * Update top margin with animation
     */
    fun updateTopMargin(view: View, fromMargin: Float, toMargin: Float) {
        val animator = ValueAnimator.ofFloat(fromMargin, toMargin)
        animator.duration = 150
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            if (view.layoutParams is ViewGroup.MarginLayoutParams) {
                val p = view.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(p.leftMargin, animatedValue.toInt(), p.rightMargin, p.bottomMargin)
                view.requestLayout()
            }
        }
        animator.start()
    }

    /**
     * Update left margin with animation
     */
    fun updateLeftMargin(view: View, fromMargin: Float, toMargin: Float) {
        val animator = ValueAnimator.ofFloat(fromMargin, toMargin)
        animator.duration = 150
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            if (view.layoutParams is ViewGroup.MarginLayoutParams) {
                val p = view.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(animatedValue.toInt(), p.topMargin, p.rightMargin, p.bottomMargin)
                view.requestLayout()
            }
        }
        animator.start()
    }

    /**
     * Update text size with animation
     */
    fun updateTextSize(textView: TextView, fromSize: Float, toSize: Float) {
        val animator = ValueAnimator.ofFloat(fromSize, toSize)
        animator.duration = 150
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, animatedValue)
        }
        animator.start()
    }
}