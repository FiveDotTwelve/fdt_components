/*
 * Copyright (c) 2018. FiveDotTwelve.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fivedottwelve.components

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.RestrictTo
import android.support.transition.AutoTransition
import android.support.transition.TransitionManager
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

/**
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class BottomNavigationItemView : FrameLayout, View.OnClickListener {

    var isItemSelected = false
        set(newValue) {
            if (isItemSelected != newValue) {
                field = newValue
                updateItem()
            }
        }

    var clickListener: View.OnClickListener? = null
    var titleColor: ColorStateList? = null
        set(value) {
            field = value
            title.setTextColor(field)
        }
    var iconColor: ColorStateList? = null
        set(value) {
            field = value
            updateIconTintColor(context)
        }
    var itemBackground: Drawable? = null
        set(value) {
            if (value != null) {
                field = value
                container.background = field
            }
        }
    var hideTitleWhenNotSelected = false
        set(value) {
            field = value
            if (isItemSelected) {
                toggleTitle(true, false)
            } else {
                toggleTitle(!field, false)
            }
        }

    private lateinit var container: FrameLayout
    private lateinit var icon: ImageView
    private lateinit var title: TextView
    private lateinit var notification: TextView

    private val toggleTitleTransition: AutoTransition by lazy {
        val transition = AutoTransition()
        transition.duration = 150L
        transition.interpolator = AccelerateInterpolator()
        transition
    }
    private val hideNotificationInterpolator by lazy { AccelerateInterpolator() }
    private val showNotificationInterpolator by lazy { OvershootInterpolator() }
    private val notificationAnimationDuration = 150L
    private val notificationActiveMarginLeft by lazy { resources.getDimension(R.dimen.bottom_navigation_notification_margin_left_active) }
    private val notificationInactiveMarginLeft by lazy { resources.getDimension(R.dimen.bottom_navigation_notification_margin_left) }
    private val notificationActiveMarginTop by lazy { resources.getDimension(R.dimen.bottom_navigation_notification_margin_top_active) }
    private val notificationActiveMarginTopNoTitle by lazy { resources.getDimension(R.dimen.bottom_navigation_notification_margin_top_no_title) }
    private val notificationInactiveMarginTop by lazy { resources.getDimension(R.dimen.bottom_navigation_notification_margin_top) }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    fun setNotifications(count: Int, animate: Boolean = true) {
        val hide = count == 0
        val displayCount = when {
            count <= 0 -> ""
            count > 9 -> "9+"
            else -> count.toString()
        }

        notification.text = displayCount

        if (hide && notification.scaleX != 0f) {
            if (animate) {
                notification.animate()
                        .scaleX(0f)
                        .scaleY(0f)
                        .setInterpolator(hideNotificationInterpolator)
                        .setDuration(notificationAnimationDuration)
                        .start()
            } else {
                notification.scaleX = 0f
                notification.scaleY = 0f
            }
        } else if (!hide) {
            if (animate) {
                notification.scaleX = 0f
                notification.scaleY = 0f
                notification.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setInterpolator(showNotificationInterpolator)
                        .setDuration(notificationAnimationDuration)
                        .start()
            } else {
                notification.scaleX = 1f
                notification.scaleY = 1f
            }
        }
    }

    override fun onClick(v: View?) {
        clickListener?.onClick(v)
    }

    private fun updateItem() {
        val activeMarginTop = resources.getDimension(R.dimen.bottom_navigation_margin_top_active)
        val inactiveMarginTop = resources.getDimension(R.dimen.bottom_navigation_margin_top_inactive)
        val activeSize = resources.getDimension(R.dimen.bottom_navigation_text_size_active)
        val inactiveSize = resources.getDimension(R.dimen.bottom_navigation_text_size_inactive)

        icon.isSelected = isItemSelected
        title.isSelected = isItemSelected

        updateIconTintColor(context)

        when {
            isItemSelected -> {
                if (hideTitleWhenNotSelected) {
                    toggleTitle(true)
                } else {
                    ViewHelper.updateTopMargin(icon, inactiveMarginTop, activeMarginTop)
                    ViewHelper.updateLeftMargin(notification, notificationInactiveMarginLeft, notificationActiveMarginLeft)
                    ViewHelper.updateTopMargin(notification, notificationInactiveMarginTop, notificationActiveMarginTop)
                    ViewHelper.updateTextSize(title, inactiveSize, activeSize)
                }
            }
            hideTitleWhenNotSelected -> {
                toggleTitle(false)
            }
            else -> {
                ViewHelper.updateTopMargin(icon, activeMarginTop, inactiveMarginTop)
                ViewHelper.updateLeftMargin(notification, notificationActiveMarginLeft, notificationInactiveMarginLeft)
                ViewHelper.updateTopMargin(notification, notificationActiveMarginTop, notificationInactiveMarginTop)
                ViewHelper.updateTextSize(title, activeSize, inactiveSize)
            }
        }
    }

    private fun toggleTitle(show: Boolean, animate: Boolean = true) {
        if (animate) {
            TransitionManager.beginDelayedTransition(this, toggleTitleTransition)
        }


        val iconParams = icon.layoutParams as FrameLayout.LayoutParams
        iconParams.gravity = if (show) Gravity.CENTER_HORIZONTAL else Gravity.CENTER
        icon.layoutParams = iconParams

        title.visibility = if (show) View.VISIBLE else View.GONE

        val notificationParams = notification.layoutParams as FrameLayout.LayoutParams
        notificationParams.topMargin = if (show) notificationActiveMarginTop.toInt() else notificationActiveMarginTopNoTitle.toInt()
        notification.layoutParams = notificationParams
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.bottom_navigation_item_view, this, true)

        container = view.findViewById(R.id.bottom_navigation_container)
        icon = view.findViewById(R.id.bottom_navigation_item_icon)
        title = view.findViewById(R.id.bottom_navigation_item_title)
        notification = view.findViewById(R.id.bottom_navigation_notification)

        titleColor = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf()),
                intArrayOf(ContextCompat.getColor(context, R.color.colorBottomNavigationActiveColored), ContextCompat.getColor(context, R.color.colorBottomNavigationInactive))
        )

        iconColor = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf()),
                intArrayOf(ContextCompat.getColor(context, R.color.colorBottomNavigationActiveColored), ContextCompat.getColor(context, R.color.colorBottomNavigationInactive))
        )

        setOnClickListener(this)
        setNotifications(0, false)

        if (attrs == null) {
            return
        }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomNavigationItemView)

        try {
            setTitle(typedArray)
            setIcon(typedArray)
            setItemSelected(typedArray)
        } catch (exception: Exception) {
            //ignore
        } finally {
            typedArray.recycle()
        }

        post {
            updateIconTintColor(context)
            title.setTextColor(titleColor)
            updateItem()

            requestLayout()
        }
    }

    private fun updateIconTintColor(context: Context) {
        iconColor?.let {
            icon.setColorFilter(it.getColorForState(icon.drawableState, ContextCompat.getColor(context, R.color.colorBottomNavigationInactive)), PorterDuff.Mode.SRC_IN)
        }
    }

    private fun setTitle(typedArray: TypedArray) {
        if (typedArray.hasValue(R.styleable.BottomNavigationItemView_item_title)) {
            title.text = typedArray.getString(R.styleable.BottomNavigationItemView_item_title)
        } else {
            title.text = ""
        }
    }

    private fun setIcon(typedArray: TypedArray) {
        if (typedArray.hasValue(R.styleable.BottomNavigationItemView_item_icon)) {
            icon.visibility = View.VISIBLE
            icon.setImageDrawable(typedArray.getDrawable(R.styleable.BottomNavigationItemView_item_icon))
        } else {
            icon.visibility = View.GONE
            icon.setImageDrawable(null)
        }
    }

    private fun setItemSelected(typedArray: TypedArray) {
        val select = if (typedArray.hasValue(R.styleable.BottomNavigationItemView_item_selected)) {
            typedArray.getBoolean(R.styleable.BottomNavigationItemView_item_selected, false)
        } else {
            false
        }

        isItemSelected = select
    }
}