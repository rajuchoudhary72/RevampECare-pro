package com.app.ecarepro.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentHelpBinding
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: HelpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHelpBinding.inflate(inflater, container, false).apply {
            viewModel = mViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.btnFaq.setOnClickListener {
            val url = "https://www.franciscansolutions.com/Default.aspx#faqBox"
            val bundle = Bundle()
            bundle.putString("title", "F&Q")
            bundle.putString("url", url)
            findNavController().navigate(R.id.webViewFragment, bundle)
            mViewModel.sentAnalyticEvent(
                event = AnalyticsConstants.Events.FAQ_CLICK,
                attributes = mapOf(
                    AnalyticsConstants.Attributes.URL to url
                )
            )
        }

    }


    override fun onResume() {
        super.onResume()
        mViewModel.sendScreenEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}