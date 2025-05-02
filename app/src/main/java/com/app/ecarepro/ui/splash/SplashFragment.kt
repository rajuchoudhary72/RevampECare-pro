package com.app.ecarepro.ui.splash

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentSplashBinding
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.imageUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    val splashViewModel: SplashViewModel by viewModels()
    val systemViewModel: SystemViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimation()

        splashViewModel.school.observe(viewLifecycleOwner) { school ->
            school?.let {
                binding.apply {
                    logo.imageUrl(
                        school.logo,
                        requireContext().getDrawable(R.drawable.img_school_logo)
                    )
                    textInstituteName.text = school.schoolName
                    textInstituteAddress.text = school.city
                }
            }
        }

    }

    private suspend fun moveToHomeScreen() {
        systemViewModel.refreshAppLayout()
        delay(2000)
        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)

    }


    private fun startAnimation() {
        val anim: AnimationDrawable = binding.backgroundView.drawable as AnimationDrawable
        val run = Runnable { anim.start() }
        binding.backgroundView.post(run)

    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (splashViewModel.isUserAuthenticated()) {
                    moveToHomeScreen()
                } else {
                    splashViewModel.getSliders()
                    findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}