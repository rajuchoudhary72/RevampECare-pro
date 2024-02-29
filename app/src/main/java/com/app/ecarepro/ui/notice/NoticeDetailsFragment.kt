package com.app.ecarepro.ui.notice

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentNoticeDetailsBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NoticeDetailsFragment : Fragment() {

    private var manager: DownloadManager? = null
    private lateinit var fileSource: String
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

        val noticeID=  requireArguments().getInt(Constant.NOTICE_ID_ARGUMENT)
        _noticeDetailsViewModel.getNoticeDTL(noticeID,1)


        return noticeDetailsBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noticeDetailsBinding.relView.setOnClickListener {
            findNavController().navigate(R.id.action_noticeDetailsFragment_to_openPdfFragment,Bundle( ).apply {
                putString(Constant.URL_ARGUMENT, fileSource)
            })
        }

        noticeDetailsBinding.relDownload.setOnClickListener {
           val androidDownloader = AndroidDownloader(requireContext())
            androidDownloader.downloadFile(fileSource, getString(R.string.notice) )
        }

        lifecycleScope.launch {
            _noticeDetailsViewModel._noticeStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }
                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data!=null){
                            noticeDetailsBinding.noticeDetailData=it.data.notice
                            fileSource=it.data.notice.filePath

                          }
                        }


                }
            }
        }


    }
}