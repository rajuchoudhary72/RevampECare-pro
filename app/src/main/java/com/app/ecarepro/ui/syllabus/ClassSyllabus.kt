package com.app.ecarepro.ui.syllabus

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
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

                        if (it.data != null) {

                            if (it.data.syllabusLST.isNotEmpty()) {

                                binding.recyclerSyllabus.isVisible = true
                                binding.tvNoData.isVisible = false

                                val noticeAdapter =
                                    SyllabusListAdapter(it.data.syllabusLST, this@ClassSyllabus)

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


                }
            }
        }

        classSyllabusViewModel.getClassSyllabus()

    }

    override fun onItemClick(t: SyllabusLST, pos: Int, boolean: Boolean) {
        if (pos == 1) {
            findNavController().navigate(R.id.action_classSyllabus_to_openPdfFragment,
                Bundle().apply {
                    putString(Constant.URL_ARGUMENT, t.filePath)
                })
        } else if (pos == 2) {
            val androidDownloader = AndroidDownloader(requireContext())
            androidDownloader.downloadFile(t.filePath, getString(R.string.syallabus) )

        }
    }
}