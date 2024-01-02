package com.app.ecarepro.ui.splash

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    val splashViewModel: SplashViewModel by viewModels()

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
        viewLifecycleOwner.lifecycleScope.launch {
            splashViewModel.getSliders()
            findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
        }
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