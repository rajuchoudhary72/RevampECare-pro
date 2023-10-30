package com.app.ecarepro.ui.splash

import android.animation.ArgbEvaluator
import android.animation.TimeAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null


    private val binding get() = _binding!!

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
            delay(2000)
            findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
        }
    }

    private fun startAnimation() {
        val start = Color.parseColor("#63BF53")
        val mid = Color.parseColor("#63BF53")
        val end = Color.parseColor("#0F763B")
        val evaluator = ArgbEvaluator()
        val gradient = binding.backgroundView.background as GradientDrawable
        val animator = TimeAnimator.ofFloat(0.0f, 1.0f)
        animator.setDuration(500)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener { valueAnimator ->
            val fraction = valueAnimator.animatedFraction
            val newStrat = evaluator.evaluate(fraction, start, end) as Int
            val newMid = evaluator.evaluate(fraction, mid, start) as Int
            val newEnd = evaluator.evaluate(fraction, end, mid) as Int
            val newArray = intArrayOf(newStrat, newMid, newEnd)
            gradient.colors = newArray
        }

        animator.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}