package com.fivedottwelve.components

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.RestrictTo
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object ViewHelper {

    /**
     * Return a tint drawable
     *
     * @param drawable
     * @param color
     * @param forceTint
     * @return
     */
    fun getTintDrawable(drawable: Drawable, @ColorInt color: Int, forceTint: Boolean): Drawable {
        if (forceTint) {
            drawable.clearColorFilter()
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            drawable.invalidateSelf()
            return drawable
        }
        val wrapDrawable = DrawableCompat.wrap(drawable).mutate()
        DrawableCompat.setTint(wrapDrawable, color)
        return wrapDrawable
    }

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
     * Update bottom margin with animation
     */
    fun updateBottomMargin(view: View, fromMargin: Float, toMargin: Float, duration: Int) {
        val animator = ValueAnimator.ofFloat(fromMargin, toMargin)
        animator.duration = duration.toLong()
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            if (view.layoutParams is ViewGroup.MarginLayoutParams) {
                val p = view.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, animatedValue.toInt())
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

    /**
     * Update alpha
     */
    fun updateAlpha(view: View, fromValue: Float, toValue: Float) {
        val animator = ValueAnimator.ofFloat(fromValue, toValue)
        animator.duration = 150
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            view.alpha = animatedValue
        }
        animator.start()
    }

    /**
     * Update text color with animation
     */
    fun updateTextColor(textView: TextView, @ColorInt fromColor: Int,
                        @ColorInt toColor: Int) {
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        colorAnimation.duration = 150
        colorAnimation.addUpdateListener { animator -> textView.setTextColor(animator.animatedValue as Int) }
        colorAnimation.start()
    }

    /**
     * Update text color with animation
     */
    fun updateViewBackgroundColor(view: View, @ColorInt fromColor: Int,
                                  @ColorInt toColor: Int) {
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        colorAnimation.duration = 150
        colorAnimation.addUpdateListener { animator -> view.setBackgroundColor(animator.animatedValue as Int) }
        colorAnimation.start()
    }

    /**
     * Update image view color with animation
     */
    fun updateDrawableColor(context: Context, drawable: Drawable,
                            imageView: ImageView, @ColorInt fromColor: Int,
                            @ColorInt toColor: Int, forceTint: Boolean) {
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        colorAnimation.duration = 150
        colorAnimation.addUpdateListener { animator ->
            imageView.setImageDrawable(ViewHelper.getTintDrawable(drawable,
                    animator.animatedValue as Int, forceTint))
            imageView.requestLayout()
        }
        colorAnimation.start()
    }

    /**
     * Update width
     */
    fun updateWidth(view: View, fromWidth: Float, toWidth: Float) {
        val animator = ValueAnimator.ofFloat(fromWidth, toWidth)
        animator.duration = 150
        animator.addUpdateListener { anim ->
            val params = view.layoutParams
            params.width = Math.round(anim.animatedValue as Float)
            view.layoutParams = params
        }
        animator.start()
    }
}