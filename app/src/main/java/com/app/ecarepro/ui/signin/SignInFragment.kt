package com.app.ecarepro.ui.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.SignInBannerViewBindingModel_
import com.app.ecarepro.databinding.FragmentSignInBinding
import com.app.ecarepro.ui.views.banner.slider
import com.app.ecarepro.utils.addSystemWindowInsetToPadding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.banner.addSystemWindowInsetToPadding(topWindowInsetToPadding = true)
        binding.banner.withModels {
            slider {
                id("carousel")
                indicatorVisible(true)
                indicatorDotColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.md_theme_light_outlineVariant
                    )
                )
                indicatorSelectedDotColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.md_theme_light_primary
                    )
                )
                infinite(true)
                models(
                    (0..10).map {
                        SignInBannerViewBindingModel_()
                            .id(it)
                    }
                )
                copier { oldModel ->
                    SignInBannerViewBindingModel_()
                        .id(oldModel.id())
                }
            }
        }

        binding.btnForgetPassword.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)
        }

        binding.btnHelp.setOnClickListener {
            findNavController().navigate(R.id.helpFragment)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}