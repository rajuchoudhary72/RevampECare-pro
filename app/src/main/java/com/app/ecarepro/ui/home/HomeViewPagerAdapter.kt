package com.app.ecarepro.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class HomeViewPagerAdapter(fragment: Fragment, val originalList: List<Fragment>) :
    FragmentStateAdapter(fragment) {

    private val newList: List<Fragment> =
        listOf(originalList.last()) + originalList + listOf(originalList.first())

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun createFragment(position: Int): Fragment {
        return originalList[position]
    }
}