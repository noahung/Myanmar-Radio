package com.noahaung.myanmarradio

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StationListFragment.newInstance(false) // All Stations
            1 -> StationListFragment.newInstance(true)  // Favorites
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}