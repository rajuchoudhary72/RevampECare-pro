package com.app.ecarepro.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.Carousel
import com.app.ecarepro.R
import com.app.ecarepro.addMoreFavourites
import com.app.ecarepro.cardOption
import com.app.ecarepro.dashboardCard
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.Slider
import com.app.ecarepro.databinding.FragmentHomeBinding
import com.app.ecarepro.labelCenter
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.views.carouselNoSnapBuilder
import com.app.ecarepro.utils.imageUrl
import com.app.ecarepro.viewAllWidget
import com.rubensousa.decorator.ColumnProvider
import com.rubensousa.decorator.DecorationLookup
import com.rubensousa.decorator.GridMarginDecoration
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private  var schoolData: NetworkSchool?=null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: HomeViewModel by viewModels()

    private val systemViewModel: SystemViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObservers()
    }

    private fun setUpViews() {
        binding.btnMenu.setOnClickListener { systemViewModel.openDrawer(true) }
        binding.imgUserAvatar.setOnClickListener { findNavController().navigate(R.id.profileFragment) }
        binding.recyclerView.addItemDecoration(
            LinearMarginDecoration.create(
                margin = resources.getDimensionPixelOffset(
                    R.dimen.horizontal_margin
                ),
                decorationLookup = object : DecorationLookup {
                    override fun shouldApplyDecoration(position: Int, itemCount: Int): Boolean {
                        return binding.recyclerView.adapter?.getItemViewType(position) == R.layout.item_view_all_widget
                    }
                }
            )
        )
        binding.recyclerView.addItemDecoration(
            GridMarginDecoration.create(
                margin = resources.getDimensionPixelOffset(
                    R.dimen.horizontal_margin
                ),
                columnProvider = object : ColumnProvider {
                    override fun getNumberOfColumns(): Int {
                        return 3
                    }

                },
                decorationLookup = object : DecorationLookup {
                    override fun shouldApplyDecoration(position: Int, itemCount: Int): Boolean {
                        return binding.recyclerView.adapter?.getItemViewType(position) == R.layout.item_card_option
                    }
                }
            )
        )
    }

    private fun setUpObservers() {
        lifecycleScope.launch {
            mViewModel
                .uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
                .collectLatest { uiState ->
                    handleUiState(uiState)
                }
        }
        mViewModel.schoolData.observe(viewLifecycleOwner){
            schoolData=it
        }
    }

    private fun handleUiState(uiState: HomeUiState) {
        (requireActivity() as MainActivity).showLoader(uiState is HomeUiState.Loading)
        if (uiState is HomeUiState.Success) {
            buildUiModels(uiState)

        }
    }

    private fun buildUiModels(uiState: HomeUiState.Success) {
        uiState.user.let { user ->

            binding.apply {
                imgUserAvatar.imageUrl(user.photo)
                txtUserName.text = user.name
            }
        }

        binding.recyclerView.withModels {
            carouselNoSnapBuilder {
                id("carousel")
                numViewsToShowOnScreen(1.2f)
                spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                padding(
                    Carousel.Padding(
                        150,
                        resources.getDimensionPixelOffset(
                            R.dimen.horizontal_margin
                        ), 150,
                        resources.getDimensionPixelOffset(
                            R.dimen.horizontal_margin
                        ),
                        resources.getDimensionPixelOffset(
                            R.dimen.horizontal_margin
                        )
                    )
                )
                (0..7).forEach {
                    dashboardCard {
                        id(it)
                    }
                }
            }

            viewAllWidget {
                id("view_all_widget")
                spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                clickListener { _ ->
                    findNavController().navigate(R.id.widgetsFragment)
                }
            }

            labelCenter {
                id("fav")
                spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
            }

            uiState.favourites.forEach { favouriteSlider ->
                cardOption {
                    id(favouriteSlider.module)
                    data(favouriteSlider)
                    clickListener { _ -> navigateToFavourites(favouriteSlider) }
                }
            }

            addMoreFavourites {
                id("add more")
                clickListener { _ ->
                    findNavController().navigate(R.id.favouritesFragment)
                }
            }
        }
    }

    private fun navigateToFavourites(favouriteSlider: Slider) {
        if (favouriteSlider.module.contains("notice", true)) {
            findNavController().navigate(R.id.noticeListFragment)
        } else if (favouriteSlider.module.contains("thought", true)) {
            findNavController().navigate(R.id.thoughtsListFragment)
        } else if (favouriteSlider.module.contains("circular", true)) {
            findNavController().navigate(R.id.circularFragment)
        } else if (favouriteSlider.module.contains("library", true)) {
            findNavController().navigate(R.id.bookLibraryFragment)
        } else if (favouriteSlider.module.contains("syllabus", true)) {
            findNavController().navigate(R.id.classSyllabus)
        } else if (favouriteSlider.module.contains("activity", true)) {
            findNavController().navigate(R.id.calenderActivityNavHost)
        } else if (favouriteSlider.module.contains("pay slip", true)) {
            findNavController().navigate(R.id.paySlipFragment)
        } else if (favouriteSlider.module.contains("Questionnaire", true)) {
            findNavController().navigate(R.id.questionnaireListFragment)
        } else if (favouriteSlider.module.contains("Class Promotion", true)) {
            findNavController().navigate(R.id.classPromotionFragment)
        }else if (favouriteSlider.module.contains("Website", true)) {
            schoolData?.let {
                it.webSite?.let { url ->
                    val bundle = Bundle()
                    bundle.putString("title", "Website")
                    bundle.putString("url", "$url")
                    findNavController().navigate(R.id.webViewFragment, bundle)
                }
            }

        }else if (favouriteSlider.module.contains("Marks Entry", true)) {
            schoolData?.let {
                it.marksEntryURL?.let { url ->

                    val bundle = Bundle()
                    bundle.putString("title", "Marks Entry")
                    bundle.putString("url", url)
                    //findNavController().navigate(R.id.webViewFragment, bundle)
                    findNavController().navigate(R.id.excellenceAwardFragment, bundle)

                }
            }

        }else if (favouriteSlider.module.contains("Assessment", true)) {
            schoolData?.let {
                it.assessmentMarksURL?.let {url->
                    val bundle = Bundle()
                    bundle.putString("title", "Assessment")
                    bundle.putString("url", url)
                    findNavController().navigate(R.id.webViewFragment,bundle)
                }

            }

        } else {
            Log.e("Home", favouriteSlider.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}