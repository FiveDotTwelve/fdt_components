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
