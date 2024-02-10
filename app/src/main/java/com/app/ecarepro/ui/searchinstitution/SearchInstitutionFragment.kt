package com.app.ecarepro.ui.searchinstitution

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentSearchInstitutionBinding
import com.app.ecarepro.instituteView
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.addSystemWindowInsetToPadding
import com.rubensousa.decorator.LinearDividerDecoration
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchInstitutionFragment : Fragment() {

    private var _binding: FragmentSearchInstitutionBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: SearchInstitutionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchInstitutionBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            searchInstitutionViewModel = mViewModel
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.addSystemWindowInsetToPadding(
            topWindowInsetToPadding = true
        )
        binding.recyclerViewInstitute.addSystemWindowInsetToPadding(
            bottomWindowInsetToPadding = true
        )

        binding.btnClose.setOnClickListener { findNavController().popBackStack() }

        binding.recyclerViewInstitute.addItemDecoration(
            LinearMarginDecoration.create(
                margin = resources.getDimensionPixelSize(R.dimen.horizontal_margin),
            )
        )

        binding.recyclerViewInstitute.addItemDecoration(
            LinearDividerDecoration.create(
                size = resources.getDimensionPixelSize(R.dimen.divider_size),
                color = ContextCompat.getColor(
                    requireContext(),
                    R.color.md_theme_light_outlineVariant
                ),
                leftMargin = resources.getDimensionPixelSize(R.dimen.horizontal_margin),
                rightMargin = resources.getDimensionPixelSize(R.dimen.horizontal_margin),
            )
        )
        /*fetch all school list */
        fetchSchools()

        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel.schools.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.CREATED
            ).collectLatest { schools ->
                binding.recyclerViewInstitute.withModels {
                    schools.forEach { school ->
                        instituteView {
                            id(school.name)
                            school(school)
                            clickListener { _ ->
                                setFragmentResult(
                                    REQUEST_KEY_SCHOOL_CODE,
                                    bundleOf(
                                        PRAM_SCHOOL_CODE to school.schoolCode
                                    )
                                )
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun fetchSchools() {
        (requireActivity() as MainActivity).showLoader(true)
        mViewModel.getSchools {
            (requireActivity() as MainActivity).showLoader(false)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY_SCHOOL_CODE = "request_key_school_code"
        const val PRAM_SCHOOL_CODE = "school_code"
    }
}