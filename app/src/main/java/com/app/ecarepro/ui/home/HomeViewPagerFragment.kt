package com.app.ecarepro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.app.ecarepro.databinding.FragmentHomeViewPagerBinding
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.attendance.AttendanceFragment
import com.app.ecarepro.ui.dashbord.DashboardFragment
import com.app.ecarepro.ui.feed.FeedsFragment
import com.app.ecarepro.utils.FadeOutTransformation
import com.app.ecarepro.utils.SwipeControlTouchListener
import com.app.ecarepro.utils.SwipeDirection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeViewPagerFragment : Fragment() {

    private var _binding: FragmentHomeViewPagerBinding? = null
    private val binding get() = _binding!!

    private val systemViewModel: SystemViewModel by activityViewModels()

    private val fragments: List<Fragment> by lazy {
        mutableListOf(
            HomeFragment(),
            DashboardFragment(),
            AttendanceFragment(),
            FeedsFragment()
        )
    }

    private val swipeControlTouchListener by lazy {
        SwipeControlTouchListener().apply {
            setSwipeDirection(SwipeDirection.LEFT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            systemViewModel.navigateBack.collectLatest {
                if (binding.viewPager.currentItem != 0) {
                    binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() - 1, false);
                } else {
                    findNavController().popBackStack()
                }
            }
        }

        binding.viewPager.apply {
            //isUserInputEnabled = false
            // setOnTouchListener(swipeControlTouchListener)
            adapter = HomeViewPagerAdapter(this@HomeViewPagerFragment, fragments)
            setPageTransformer(FadeOutTransformation())
        }

        // onInfinitePageChangeCallback(fragments.size + 2)

    }

    private fun onInfinitePageChangeCallback(listSize: Int) {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    when (binding.viewPager.currentItem) {
                        listSize - 1 -> binding.viewPager.setCurrentItem(1, false)
                        0 -> binding.viewPager.setCurrentItem(listSize - 2, false)
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position != 0 && position != listSize - 1) {
                    // pageIndicatorView.setSelected(position-1)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}