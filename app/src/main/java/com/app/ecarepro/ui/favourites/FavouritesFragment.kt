package com.app.ecarepro.ui.favourites

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.animation.ValueAnimator.ofObject
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyTouchHelper
import com.airbnb.epoxy.EpoxyTouchHelper.DragCallbacks
import com.app.ecarepro.FavouriteBindingModel_
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.Favourites
import com.app.ecarepro.databinding.FragmentFavouritesBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody


@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null

    private val binding get() = _binding!!

    private val mViewModel: FavouritesViewModel by viewModels()

    private var controller: EpoxyController? = null

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

    private fun setupDragging(carousels: MutableList<Favourites>) {
        try {
            if (controller == null) return
            EpoxyTouchHelper.initDragging(controller) // an EpoxyController must be used
                .withRecyclerView(binding.recyclerView) // The recyclerview the controller is used with
                .forVerticalList() // Specify the directions that you want to drag in
                .withTarget(FavouriteBindingModel_::class.java) // Specify the type of model or models that should be draggable
                .andCallbacks(object : DragCallbacks<FavouriteBindingModel_>() {

                    @ColorInt
                    val selectedBackgroundColor: Int = Color.argb(200, 200, 200, 200)
                    var backgroundAnimator: ValueAnimator? = null

                    override fun onModelMoved(
                        fromPosition: Int, toPosition: Int,
                        modelBeingMoved: FavouriteBindingModel_, itemView: View
                    )
                    {
                        Log.e("Hari", "onModelMoved: ${modelBeingMoved.title()} : $fromPosition -> $toPosition" )
                        val carouselIndex: Int = carousels.indexOfFirst { modelBeingMoved.title() == it.title }
                        carousels.add(carouselIndex + (toPosition - fromPosition), carousels.removeAt(carouselIndex)
                        )
                    }


                    override fun onDragStarted(
                        model: FavouriteBindingModel_,
                        itemView: View,
                        adapterPosition: Int
                    ) {
                        backgroundAnimator = ValueAnimator
                            .ofObject(ArgbEvaluator(), Color.WHITE, selectedBackgroundColor)
                        backgroundAnimator?.addUpdateListener(
                            AnimatorUpdateListener { animator: ValueAnimator ->
                                itemView.setBackgroundColor(
                                    animator.animatedValue as Int
                                )
                            }
                        )

                        backgroundAnimator?.start()

                        itemView
                            .animate()
                            .scaleX(1.05f)
                            .scaleY(1.05f)
                    }

                    override fun onDragReleased(model: FavouriteBindingModel_, itemView: View) {
                        if (backgroundAnimator != null) {
                            backgroundAnimator!!.cancel()
                        }

                        backgroundAnimator =
                            ofObject(
                                ArgbEvaluator(), (itemView.background as ColorDrawable).color,
                                Color.WHITE
                            )
                        backgroundAnimator!!.addUpdateListener { animator: ValueAnimator ->
                            itemView.setBackgroundColor(
                                animator.animatedValue as Int
                            )
                        }

                        backgroundAnimator!!.start()

                        itemView
                            .animate()
                            .scaleX(1f)
                            .scaleY(1f)
                    }

                    override fun clearView(model: FavouriteBindingModel_, itemView: View) {
                        onDragReleased(model, itemView);
                    }

                    override fun isDragEnabledForModel(model: FavouriteBindingModel_): Boolean {
                        // Override this to toggle disabling dragging for a model
                        return model.isChecked
                    }
                })
        }catch (e:IndexOutOfBoundsException){

        }

    }

    private fun buildModels(uiState: FavouritesUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message ?: "")
        }
        if (uiState is FavouritesUiState.Success) {
            setupDragging(uiState.favourites.toMutableList())
            binding.recyclerView.withModels {
                controller = this
                uiState.favourites.forEach {
                    FavouriteBindingModel_()
                        .id(it.menuID, it.chMenuID, it.sbChMenuID)
                        .icon(it.icon)
                        .title(it.title)
                        .isChecked(it.isSelected)
                        .clickListener { _ ->
                            mViewModel.onFavouriteClicked(it)
                        }
                        .addTo(this)

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