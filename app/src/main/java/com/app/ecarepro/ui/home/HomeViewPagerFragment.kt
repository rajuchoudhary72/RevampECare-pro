package com.app.ecarepro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.ecarepro.databinding.FragmentHomeViewPagerBinding
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.attendance.AttendanceFragment
import com.app.ecarepro.ui.dashbord.DashboardFragment
import com.app.ecarepro.ui.feed.FeedsFragment
import com.app.ecarepro.ui.message.inbox.InboxMessageFragment
import com.app.ecarepro.ui.message.sent.SentMessageFragment
import com.app.ecarepro.utils.FadeOutTransformation
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeViewPagerFragment : Fragment() {

    private var _binding: FragmentHomeViewPagerBinding? = null
    private val binding get() = _binding!!

    private val systemViewModel: SystemViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()

        viewLifecycleOwner.lifecycleScope.launch {
            systemViewModel.navigateBack.collectLatest {
                if (binding.viewPager.currentItem != 0) {
                    binding.viewPager.setCurrentItem(binding.viewPager.currentItem - 1, false);
                } else {
                    findNavController().popBackStack()
                }
            }
        }

    }

    private fun setupViewPager() {
        val tabItem = mutableListOf("Home", "Dashboard", "Attendance", "Feeds")
        binding.viewPager.apply {
            isUserInputEnabled = false
            setPageTransformer(FadeOutTransformation())
        }
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return tabItem.size
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> HomeFragment()
                    1 -> DashboardFragment()
                    2 -> AttendanceFragment()
                    3 -> FeedsFragment()
                    else -> HomeFragment()
                }
            }

        }

        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab, position ->
            tab.text = tabItem[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}