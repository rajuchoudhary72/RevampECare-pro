package com.app.ecarepro.ui.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentFavouritesBinding
import com.app.ecarepro.favourite
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null

    private val binding get() = _binding!!

    private val mViewModel: FavouritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel.uiState.collect { uiState ->
                buildModels(uiState)
            }
        }
    }

    private fun buildModels(uiState: FavouritesUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message?:"")
        }
        if (uiState is FavouritesUiState.Success) {
            binding.recyclerView.withModels {
                uiState.favourites.forEach {
                    favourite {
                        id(it.menuID, it.chMenuID, it.sbChMenuID)
                        icon(it.icon)
                        title(it.title)
                        isChecked(it.isSelected)
                        clickListener { _ ->
                            mViewModel.onFavouriteClicked(it)
                        }
                    }
                }
            }
        }
    }

    private fun initViews() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.btnSave.setOnClickListener {
            (requireActivity() as MainActivity).showLoader(true)
            mViewModel.saveFavourites() { isSuccess, message ->
                (requireActivity() as MainActivity).showLoader(false)
                mainActivity().showMessage(message)
                if (isSuccess) {
                    setFragmentResult("favourites", bundleOf("isUpdate" to true))
                    findNavController().popBackStack()
                }
            }
        }

        binding.recyclerView.apply {
            addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin)
                )
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}