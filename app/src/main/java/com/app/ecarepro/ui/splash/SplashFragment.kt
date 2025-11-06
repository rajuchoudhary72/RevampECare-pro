package com.app.ecarepro.ui.splash

import android.content.Intent
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
import v2.MainActivity


@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    val splashViewModel: SplashViewModel by viewModels()
    val systemViewModel: SystemViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().finish()
        startActivity(Intent(requireContext(), MainActivity::class.java))
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // startAnimation()

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
        /*we comment this code due to we recent  stop user session  */
        /* viewLifecycleOwner.lifecycleScope.launch {
             try {
                 if (splashViewModel.isUserAuthenticated()) {
                     if (splashViewModel.isUserSessionAvailable()) {
                         moveToHomeScreen()
                     } else {
                         mainActivity().showLoader(true)
                         *//*if  existing  user logged  and  first time run App after implementation  of user session then
                        need to pass session ID in header  so  call create session api  *//*
                        systemViewModel.createUserSession { success, message ->
                            viewLifecycleOwner.lifecycleScope.launch {
                                mainActivity().showLoader(false)
                                if (success) {
                                    moveToHomeScreen()
                                } else {
                                    mainActivity().showMessage(message)
                                    mainActivity().logout(true)
                                }
                            }
                        }
                    }
                }
                else {
                    splashViewModel.getSliders()
                    findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/


        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (splashViewModel.isUserAuthenticated()) {
                    moveToHomeScreen()
                } else {
                   /* splashViewModel.getSliders()
                    findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)*/

                    requireActivity().finish()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }
            } catch (e: Exception) {
                e.printStackTrace()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}