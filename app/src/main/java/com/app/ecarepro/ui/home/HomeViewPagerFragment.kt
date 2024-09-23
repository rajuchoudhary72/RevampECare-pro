package com.app.ecarepro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentHomeViewPagerBinding
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.attendance.AttendanceFragment
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.ui.dashbord.DashboardFragment
import com.app.ecarepro.ui.feed.FeedsFragment
import com.app.ecarepro.ui.studentProfile.StudentProfileDetailsFragment
import com.app.ecarepro.ui.timeTable.TimeTableDayWiseNavHostFragment
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.FadeOutTransformation
import com.app.ecarepro.utils.SwipeControlTouchListener
import com.app.ecarepro.utils.SwipeDirection
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeViewPagerFragment : Fragment() {

    private var _binding: FragmentHomeViewPagerBinding? = null
    private val binding get() = _binding!!
    private var showDashboard=false
    private var showAttendance=false
    private var showFeeds=false


    private val systemViewModel: SystemViewModel by activityViewModels()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        _binding = FragmentHomeViewPagerBinding.inflate(inflater, container, false)

        try {
            showDashboard =  requireArguments().getBoolean("Dashboard")
            showAttendance =  requireArguments().getBoolean("Attendance")
            showFeeds =  requireArguments().getBoolean("Feed")
        }catch (e:Exception){}

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val fragmentList: ArrayList<Fragment> = ArrayList()
        val fragmentName= mutableListOf<String>()

        if (showDashboard){
            fragmentList.add(DashboardFragment() )
            fragmentName.add(" Dashboard")
        }
        if (showAttendance){
            fragmentList.add(AttendanceFragment() )
            fragmentName.add("Attendance")
        }
        if (showFeeds){
            fragmentList.add(FeedsFragment() )
            fragmentName.add("Feed")
        }


        val viewPagerAdapter = ViewPagerAdapter(
            fragmentList,
            activity?.supportFragmentManager!!,
            lifecycle
        )
        binding.viewPager.adapter = viewPagerAdapter


        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->
            tab.text = fragmentName[position]
        }.attach()


        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu -> {
                    systemViewModel.bottomNavPositionSet(0)
                    systemViewModel.openDrawer(true)
                    true
                }
                R.id.profile -> {
                    systemViewModel.bottomNavPositionSet(1)
                    binding.rlBottomNavigation.isVisible=false
                    findNavController().navigate(R.id.action_homeViewPagerFragment_to_profileFragment)

                    true
                }
                R.id.home -> {
                    systemViewModel.bottomNavPositionSet(2)
                    binding.rlBottomNavigation.isVisible=false
                    findNavController().navigate(R.id.action_homeViewPagerFragment_to_homeFragment)
                    true
                }
                R.id.notification -> {
                    systemViewModel.bottomNavPositionSet(3)
                    binding.rlBottomNavigation.isVisible=false
                    findNavController().navigate(R.id.action_homeViewPagerFragment_to_notificationFragment)
                    true
                }
                R.id.message -> {
                    systemViewModel.bottomNavPositionSet(4)
                    binding.rlBottomNavigation.isVisible=false
                    findNavController().navigate(R.id.action_homeViewPagerFragment_to_messageFragment)
                    true
                }

                else -> {false}
            }
        }

        binding.bottomNavigationView.menu.forEach { it.isChecked = false }
        binding.bottomNavigationView.menu.findItem(R.id.home).setChecked(true)
        binding.bottomNavigationView.menu.findItem(R.id.home).setChecked(false)

    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}