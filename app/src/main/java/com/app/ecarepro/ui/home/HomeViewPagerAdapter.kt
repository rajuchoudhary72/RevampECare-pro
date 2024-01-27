package com.app.ecarepro.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.ecarepro.ui.dashbord.DashboardFragment
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class HomeViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> DashboardFragment()
            else -> HomeFragment()
        }
    }
}