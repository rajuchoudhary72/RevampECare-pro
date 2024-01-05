package com.app.ecarepro.ui.notice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentNoticeDetailsBinding


class NoticeDetailsFragment : Fragment() {

    private lateinit var noticeDetailsBinding: FragmentNoticeDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        noticeDetailsBinding = FragmentNoticeDetailsBinding.inflate(inflater, container, false)
        return noticeDetailsBinding.root

    }




}