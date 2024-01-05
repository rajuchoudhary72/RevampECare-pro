package com.app.ecarepro.ui.notice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentNoticeListBinding
import com.app.ecarepro.epoxy_controler.NoticeEpoxyController
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.addSystemWindowInsetToPadding
import com.rubensousa.decorator.LinearDividerDecoration
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NoticeListFragment : Fragment() {

    private lateinit var binding: FragmentNoticeListBinding

    private val noticeViewModel : NoticeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

         binding= FragmentNoticeListBinding.inflate(inflater,container,false).apply {
             lifecycleOwner = viewLifecycleOwner
             mnoticeViewModel= noticeViewModel

         }

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

        binding.toggleButtonTypeNoti.addOnButtonCheckedListener {_,checkedId, isChecked ->
            when (binding.toggleButtonTypeNoti.checkedButtonId) {
                R.id.btn_noti -> {
                     binding.spinnerClass.visibility=View.INVISIBLE
                }
                else -> {
                    binding.spinnerClass.visibility=View.VISIBLE
                }
            }
        }

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

        val epoxyController = NoticeEpoxyController()
        binding.recyclerNotice.setController (epoxyController)


        noticeViewModel.getNotice(1,0) { Notices ->
            (requireActivity() as MainActivity).showLoader(false)
            epoxyController.setData(Notices)
        }

        noticeViewModel.getMyClass(1,0) { Myclasses ->
            (requireActivity() as MainActivity).showLoader(false)

            val spinnerAdapter  = CustomDropDownAdapter(requireContext(), Myclasses)
             binding.spinnerClass.adapter = spinnerAdapter
        }

        binding.spinnerClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


            }

        }


    }
}