package com.fivedottwelve.components

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout


/**
 * Custom implementation of Bottom Navigation View base on Material design guideline @see https://material.io/guidelines/components/bottom-navigation.html
 *
 * Declare [BottomNavigationView] inside your xml file and add [BottomNavigationItemView] inside with proper configuration.
 *
 * <pre>
 *     layout resource file:
 *     &lt;
 *         <com.fivedottwelve.components.BottomNavigationView
 *              android:id="@+id/bottom_nav_bar"
 *              android:layout_width="0dp"
 *              android:layout_height="wrap_content"
 *              android:orientation="horizontal"
 *              app:item_background="@drawable/white_ripple"
 *              app:layout_constraintBottom_toBottomOf="parent"
 *              app:layout_constraintEnd_toEndOf="parent"
 *              app:layout_constraintStart_toStartOf="parent">
 *
 *              <com.fivedottwelve.components.BottomNavigationItemView
 *                  android:layout_width="wrap_content"
 *                  android:layout_height="wrap_content"
 *                  app:item_icon="@drawable/ic_android_white_24dp"
 *                  app:item_selected="true"
 *                  app:item_title="@string/bottom_nav_one" />
 *
 *              <com.fivedottwelve.components.BottomNavigationItemView
 *                  android:layout_width="wrap_content"
 *                  android:layout_height="wrap_content"
 *                  app:item_icon="@drawable/ic_audiotrack_white_24dp"
 *                  app:item_title="@string/bottom_nav_two" />
 *
 *              <com.fivedottwelve.components.BottomNavigationItemView
 *                  android:layout_width="wrap_content"
 *                  android:layout_height="wrap_content"
 *                  app:item_icon="@drawable/ic_beenhere_white_24dp"
 *                  app:item_title="@string/bottom_nav_three" />
 *
 *              <com.fivedottwelve.components.BottomNavigationItemView
 *                  android:layout_width="wrap_content"
 *                  android:layout_height="wrap_content"
 *                  app:item_icon="@drawable/ic_beenhere_white_24dp"
 *                  app:item_title="@string/bottom_nav_three" />
 *
 *          </com.fivedottwelve.components.BottomNavigationView>
 *     /&gt;
 */
class BottomNavigationView : LinearLayout, View.OnClickListener, CoordinatorLayout.AttachedBehavior {

    /**
     * Interface used as callback for [BottomNavigationView] item click events
     */
    interface BottomNavigationListener {
        /**
         * Called when menu item is clicked
         *
         * @param id represents item index in the navigation view
         */
        fun onItemSelected(id: Int)
    }


    /**
     * Set this value in order to receive click events
     */
    var listener: BottomNavigationListener? = null

    private val views = mutableListOf<BottomNavigationItemView>()

    private var selectedItemIndex = NO_POSITION
    private var titleColor: ColorStateList? = null
    private var iconColor: ColorStateList? = null
    private var itemBackground: Drawable? = null
    @ColorInt
    private var navBackgroundColor: Int = 0
    private var hideTitleWhenNotSelected = false
    private var hideOnScroll = false

    private val notificationQue: SparseArray<Int> = SparseArray()

    private val layoutBehavior: BottomNavigationBehavior by lazy { BottomNavigationBehavior() }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    /**
     * If item with at given index is available selecting it,
     * if not does nothing
     *
     * @param index of item to select. if less then 0 or more then items count does skipping
     */
    fun selectItem(index: Int) {
        if (index < 0 || index > views.size) {
            return
        }

        selectedItemIndex = index
        selectCurrentItem()
    }

    /**
     * Returns currently selected item index
     */
    fun getSelectedItemIndex(): Int {
        selectedItemIndex = getSelectedItem()
        return selectedItemIndex
    }

    /**
     * Adds un read notification badge to the item at [index]
     * with [notificationCount] displayed.
     *
     * If [notificationCount] is greater than 9 then displays "9+"
     */
    fun setNotificationAt(notificationCount: Int, index: Int) {

        notificationQue.put(index, notificationCount)

        if (views.isEmpty()) {
            return
        }

        consumeNotifications()
    }

    override fun onClick(v: View?) {
        if (v !is BottomNavigationItemView) {
            return
        }

        val clickedItemIndex = views.indexOf(v)

        if (clickedItemIndex != NO_POSITION) {
            selectedItemIndex = clickedItemIndex

            if (!v.isItemSelected) {
                listener?.onItemSelected(v.id)
            }

            selectCurrentItem()
        }
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> = layoutBehavior

    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomNavigationView)

        try {
            setTitleColor(typedArray)
            setIconColor(typedArray)
            setItemBackground(typedArray)
            setNavBackgroundColor(typedArray)
            setHideTitleWhenNotSelected(typedArray)
            setHideOnScroll(typedArray)
        } catch (exception: Exception) {
            //ignore
        } finally {
            typedArray.recycle()
        }

        ViewCompat.setElevation(this, resources.getDimension(R.dimen.bottom_navigation_elevation))

        setBackgroundColor(navBackgroundColor)
        (behavior as BottomNavigationBehavior).isEnabled = hideOnScroll

        post {
            val height = resources.getDimension(R.dimen.bottom_navigation_height).toInt()
            val params = layoutParams
            params.height = height
            layoutParams = params

            cacheMenuItems()
            updateItemsConfig()

            requestLayout()

            consumeNotifications()

            if (selectedItemIndex == NO_POSITION) {
                selectedItemIndex = getSelectedItem()
            } else {
                selectCurrentItem()
            }
        }
    }

    private fun selectCurrentItem() {
        views.forEachIndexed { index, view -> view.isItemSelected = index == selectedItemIndex }
    }

    private fun setTitleColor(typedArray: TypedArray) {
        titleColor = if (typedArray.hasValue(R.styleable.BottomNavigationView_item_title_color)) {
            typedArray.getColorStateList(R.styleable.BottomNavigationView_item_title_color)
        } else {
            null
        }
    }

    private fun setIconColor(typedArray: TypedArray) {
        iconColor = if (typedArray.hasValue(R.styleable.BottomNavigationView_item_icon_tint)) {
            typedArray.getColorStateList(R.styleable.BottomNavigationView_item_icon_tint)
        } else {
            null
        }
    }

    private fun setItemBackground(typedArray: TypedArray) {
        itemBackground = if (typedArray.hasValue(R.styleable.BottomNavigationView_item_background)) {
            typedArray.getDrawable(R.styleable.BottomNavigationView_item_background)
        } else {
            null
        }
    }

    private fun setNavBackgroundColor(typedArray: TypedArray) {
        navBackgroundColor = if (typedArray.hasValue(R.styleable.BottomNavigationView_nav_background)) {
            typedArray.getColor(R.styleable.BottomNavigationView_nav_background, fetchPrimaryColor())
        } else {
            fetchPrimaryColor()
        }
    }

    private fun setHideTitleWhenNotSelected(typedArray: TypedArray) {
        hideTitleWhenNotSelected = typedArray.getBoolean(R.styleable.BottomNavigationView_hide_title_when_not_selected, false)
    }

    private fun setHideOnScroll(typedArray: TypedArray) {
        hideOnScroll = typedArray.getBoolean(R.styleable.BottomNavigationView_hide_on_scroll, false)
    }

    private fun consumeNotifications() {
        for (i in 0 until notificationQue.size()) {
            val index = notificationQue.keyAt(i)
            val notificationCount = notificationQue[index]


            if (index < 0 || index > views.size) {
                return
            }

            val itemView = views[index]
            itemView.setNotifications(notificationCount)
        }

        notificationQue.clear()
    }

    private fun cacheMenuItems() {
        views.clear()

        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view is BottomNavigationItemView) {
                val params = view.layoutParams as LinearLayout.LayoutParams
                params.width = 0
                params.weight = 1f
                view.layoutParams = params

                views.add(view)
            }
        }
    }

    private fun updateItemsConfig() {
        views.forEach {
            val view = it
            view.clickListener = this
            view.hideTitleWhenNotSelected = hideTitleWhenNotSelected

            titleColor?.let { view.titleColor = it }
            iconColor?.let { view.iconColor = it }
            itemBackground?.let { view.itemBackground = it.constantState.newDrawable().mutate() }

        }
    }

    private fun getSelectedItem(): Int {
        views.forEachIndexed { index, view -> if (view.isItemSelected) return index }

        return NO_POSITION
    }

    @ColorInt
    private fun fetchPrimaryColor(): Int {
        val typedValue = TypedValue()

        val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorPrimary))
        val color = a.getColor(0, 0)

        a.recycle()

        return color
    }

    companion object {

        private const val NO_POSITION = -1

    }
}