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

package com.fivedottwelve.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fivedottwelve.components.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.BottomNavigationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            bottom_nav_bar.selectItem(savedInstanceState.getInt(BOTTOM_NAV_BAR_POSITION_KEY))
        }

        bottom_nav_bar.setNotificationAt(99, 1)
        bottom_nav_bar.listener = this
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(BOTTOM_NAV_BAR_POSITION_KEY, bottom_nav_bar.getSelectedItemIndex())
        super.onSaveInstanceState(outState)
    }

    override fun onItemSelected(id: Int) {

    }


    companion object {
        private const val BOTTOM_NAV_BAR_POSITION_KEY = "bottomNavBarPositionKey"
    }
}
