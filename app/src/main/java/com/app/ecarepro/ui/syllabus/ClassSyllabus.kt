package com.app.ecarepro.ui.syllabus

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentClassSyllabusBinding
import com.app.ecarepro.model.SyllabusLST
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ClassSyllabus : Fragment(), ItemListener<SyllabusLST> {

    private lateinit var binding: FragmentClassSyllabusBinding
    private val classSyllabusViewModel: ClassSyllabusViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentClassSyllabusBinding.inflate(inflater, container, false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.syllabus)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            classSyllabusViewModel.classSyllabusStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.recyclerSyllabus.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerSyllabus.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerSyllabus.isVisible = true
                        try {
                            if (it.data != null) {
                                if (it.data.syllabusLST.size > 0) {
                                    if (it.data.syllabusLST.isNotEmpty()) {

                                        binding.recyclerSyllabus.isVisible = true
                                        binding.tvNoData.isVisible = false

                                        val noticeAdapter =
                                            SyllabusListAdapter(
                                                it.data.syllabusLST,
                                                this@ClassSyllabus
                                            )

                                        binding.recyclerSyllabus.apply {
                                            setHasFixedSize(true)
                                            layoutManager = LinearLayoutManager(activity)
                                            adapter = noticeAdapter
                                        }
                                    } else {
                                        binding.recyclerSyllabus.isVisible = false
                                        binding.tvNoData.isVisible = true
                                    }
                                }

                            }
                        } catch (e: NullPointerException) {

                        }


                    }


                }
            }
        }

        classSyllabusViewModel.getClassSyllabus()

    }

    override fun onItemClick(t: SyllabusLST, pos: Int, boolean: Boolean) {
        try {
            try {
                if (pos == 1) {
                    openFile(t.filePath)
                } else if (pos == 2) {
                    downloadFile(t.filePath)
                }
            } catch (e: SecurityException) {

            }
        }catch (e:NullPointerException){
            e.printStackTrace()
        }


    }


    private fun openFile(fileSource: String) {
        when (Constant.isPdfUrl(fileSource)){
            1 -> {
                findNavController().navigate(R.id.openPdfFragment, Bundle().apply {
                    putString(Constant.URL_ARGUMENT, fileSource)
                })
            }
            2 -> {
                findNavController().navigate(
                    R.id.photoViewFragmentFragment,
                    bundleOf(PhotoViewFragmentFragment.PHOTO to fileSource)
                )
            }
            3 -> {
                findNavController().navigate(R.id.openPdfFragment, Bundle().apply {
                    putString(Constant.URL_ARGUMENT, fileSource)
                })
            }
            5 -> {
                findNavController().navigate(R.id.openPdfFragment, Bundle().apply {
                    putString(Constant.URL_ARGUMENT, fileSource)
                })
            }else -> {
            findNavController().navigate(
                R.id.photoViewFragmentFragment,
                bundleOf(PhotoViewFragmentFragment.PHOTO to fileSource)
            )
        }
        }



    }


    private fun downloadFile(fileSource: String) {
        try {
            when (Constant.isPdfUrl(fileSource)) {
                1 -> {
                    val androidDownloader = AndroidDownloader(requireContext())
                    androidDownloader.downloadFile(fileSource, getString(R.string.syllabus
                    ))
                }

                2 -> {
                    val androidDownloader = AndroidDownloader(requireContext())
                    androidDownloader.downloadFile(fileSource, "Photo", "image/jpeg")
                }

                3 -> {
                    val androidDownloader = AndroidDownloader(requireContext())
                    androidDownloader.downloadFile(fileSource, getString(R.string.syllabus),"application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                }
                5 -> {
                    val androidDownloader = AndroidDownloader(requireContext())
                    androidDownloader.downloadFile(fileSource, getString(R.string.syllabus),"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                }

                else -> {
                    val androidDownloader = AndroidDownloader(requireContext())
                    androidDownloader.downloadFile(fileSource, "Photo", "image/jpeg")
                }
            }
        }catch (e:SecurityException){
            e.printStackTrace()
        }
    }
}