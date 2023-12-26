package com.app.ecarepro.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentOnboardingBinding
import com.app.ecarepro.model.OnboardingItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var adapter: OnBoardingViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = adapter
        binding.dotsIndicator.attachTo(binding.viewPager)

        binding.btn.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingFragment_to_homeFragment)
        }

        adapter.submitList(OnboardingItem.DUMMY_ITEM)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}