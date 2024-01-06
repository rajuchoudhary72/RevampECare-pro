package com.app.ecarepro.ui.notice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentNoticeDetailsBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NoticeDetailsFragment : Fragment() {

    private lateinit var noticeDetailsBinding: FragmentNoticeDetailsBinding

    private val _noticeDetailsViewModel : NoticeDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        noticeDetailsBinding = FragmentNoticeDetailsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner= viewLifecycleOwner
            noticeDetailsViewModel=_noticeDetailsViewModel
        }
        return noticeDetailsBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _noticeDetailsViewModel.getNoticeDTL(0,1){
            noticeDetailsBinding.noticeDetailData=it
        }

    }
}