package com.app.ecarepro.ui.notice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentNoticeListBinding
import com.app.ecarepro.databinding.NoticeListItemBinding
import com.app.ecarepro.instituteView
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.addSystemWindowInsetToPadding
import com.rubensousa.decorator.LinearDividerDecoration
import com.rubensousa.decorator.LinearMarginDecoration


class NoticeListFragment : Fragment() {

    private lateinit var binding: FragmentNoticeListBinding

    private val noticeViewModel : NoticeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

         binding= FragmentNoticeListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerNotice.addSystemWindowInsetToPadding(
            bottomWindowInsetToPadding = true
        )


        binding.recyclerNotice.addItemDecoration(
            LinearMarginDecoration.create(
                margin = resources.getDimensionPixelSize(R.dimen.horizontal_margin),
            )
        )

        binding.recyclerNotice.addItemDecoration(
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

        (requireActivity() as MainActivity).showLoader(true)

        noticeViewModel.getNotice(1,0) { Notices ->

            (requireActivity() as MainActivity).showLoader(false)
            binding.recyclerNotice.withModels {
                Notices.forEach {
                    instituteView {

                    }
                }
            }


        }


    }
}