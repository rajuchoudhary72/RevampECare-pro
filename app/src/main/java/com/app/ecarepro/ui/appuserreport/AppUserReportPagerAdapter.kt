package com.app.ecarepro.ui.appuserreport

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class AppUserReportPagerAdapter(
    private val fm: FragmentManager,
    private val tabHeaderArrayList: ArrayList<TabHeaderModel>,
    private val deviceUsersArrayList: ArrayList<DeviceUser>
) : FragmentPagerAdapter(
    fm
) {
    override fun getItem(i: Int): Fragment {
        val fragmentTransaction = fm.beginTransaction()
        val userReportFragment = UserRepostFragment()
        val appUserBundle = Bundle()
        when (tabHeaderArrayList[i].headerId) {
            0 -> appUserBundle.putInt("userType", 0)
            1 -> appUserBundle.putInt("userType", 2)
            2 -> appUserBundle.putInt("userType", 3)
            3 -> appUserBundle.putInt("userType", 1)
        }
        appUserBundle.putParcelableArrayList("DeviceInfo", deviceUsersArrayList);
        userReportFragment.arguments = appUserBundle
        fragmentTransaction.detach(userReportFragment)
        fragmentTransaction.attach(userReportFragment)
        fragmentTransaction.commitAllowingStateLoss()
        return userReportFragment
    }

    override fun getCount(): Int {
        return tabHeaderArrayList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabHeaderArrayList[position].tabName
    }
}
