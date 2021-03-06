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
import android.support.annotation.RestrictTo
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorCompat
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.util.AttributeSet
import android.view.View

/**
 * Bottom navigation view behaviour
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class BottomNavigationBehavior : CoordinatorLayout.Behavior<BottomNavigationView> {
    private var mPropertyAnimatorCompat: ViewPropertyAnimatorCompat? = null
    private var isShown = true

    var isEnabled = true

    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: BottomNavigationView, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: BottomNavigationView, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)

        if (!isEnabled) {
            return
        }

        if (dyConsumed > sMinScrollDownDistance) {
            toggleBottomNavigationBar(child, false)
        } else if (dyConsumed < sMinScrollUpDistance) {
            toggleBottomNavigationBar(child, true)
        }
    }

    private fun toggleBottomNavigationBar(bottomNavigationView: BottomNavigationView, show: Boolean) {
        if (isShown == show) {
            return
        }
        ensureOrCancelAnimator(bottomNavigationView)
        mPropertyAnimatorCompat!!.translationY((if (show) 0 else bottomNavigationView.height).toFloat()).start()
        isShown = show
    }

    private fun ensureOrCancelAnimator(child: BottomNavigationView) {
        if (mPropertyAnimatorCompat == null) {
            mPropertyAnimatorCompat = ViewCompat.animate(child)
            mPropertyAnimatorCompat!!.duration = animationDuration
            mPropertyAnimatorCompat!!.interpolator = FastOutLinearInInterpolator()
        } else {
            mPropertyAnimatorCompat!!.cancel()
        }
    }

    companion object {
        private const val sMinScrollDownDistance = 20
        private const val sMinScrollUpDistance = -20
        private const val animationDuration: Long = 200
    }
}