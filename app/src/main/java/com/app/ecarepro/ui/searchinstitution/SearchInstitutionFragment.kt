package com.app.ecarepro.ui.searchinstitution

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentSearchInstitutionBinding
import com.app.ecarepro.instituteView
import com.app.ecarepro.utils.addSystemWindowInsetToPadding
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.rubensousa.decorator.LinearDividerDecoration
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

@AndroidEntryPoint
class SearchInstitutionFragment : Fragment() {

    private var _binding: FragmentSearchInstitutionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mViewModel: SearchInstitutionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchInstitutionBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.addSystemWindowInsetToPadding(
            topWindowInsetToPadding = true,
            bottomWindowInsetToPadding = true
        )

        binding.btnClose.setOnClickListener { findNavController().popBackStack() }

        binding.recyclerViewInstitute.addItemDecoration(
            LinearMarginDecoration(
                topMargin = resources.getDimensionPixelSize(R.dimen.vertical_margin),
                bottomMargin = resources.getDimensionPixelSize(R.dimen.vertical_margin),
                leftMargin = resources.getDimensionPixelSize(R.dimen.horizontal_margin),
                rightMargin = resources.getDimensionPixelSize(R.dimen.horizontal_margin)
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

        binding.recyclerViewInstitute.withModels {
            (1..100).forEach {
                instituteView {
                    id(it)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}