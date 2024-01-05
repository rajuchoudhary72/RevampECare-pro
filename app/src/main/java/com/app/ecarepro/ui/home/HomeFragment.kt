package com.app.ecarepro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.Carousel
import com.app.ecarepro.R
import com.app.ecarepro.cardOption
import com.app.ecarepro.dashboardCard
import com.app.ecarepro.databinding.FragmentHomeBinding
import com.app.ecarepro.labelCenter
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.views.carouselNoSnapBuilder
import com.app.ecarepro.viewAllWidget
import com.rubensousa.decorator.ColumnProvider
import com.rubensousa.decorator.DecorationLookup
import com.rubensousa.decorator.GridMarginDecoration
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

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

        binding.btnMenu.setOnClickListener { systemViewModel.openDrawer(true) }

        mViewModel.insertUser()

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
                clickListener{_ ->
                    findNavController().navigate(R.id.noticeListFragment)
                }
            }

            labelCenter {
                id("fav")
                spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
            }

            (12..22).forEach {
                cardOption {
                    id(it)
                    clickListener{_ ->
                        findNavController().navigate(R.id.favouritesFragment)
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}