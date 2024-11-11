package com.app.ecarepro.ui.syllabus.teacher

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
import com.app.ecarepro.databinding.FragmentTeacherSyllabusBinding
import com.app.ecarepro.model.SyllabusLST
import com.app.ecarepro.model.Syllabuse
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.syllabus.SyllabusListAdapter
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TeacherSyllabusFragment : Fragment(), ItemListener<Syllabuse> {


    private lateinit var binding : FragmentTeacherSyllabusBinding
    private  val teacherSyllabusViewModel: TeacherSyllabusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentTeacherSyllabusBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fbAdd.setOnClickListener {
            findNavController().navigate(R.id.addSyllabusFragment )
        }


        lifecycleScope.launch {
            teacherSyllabusViewModel.teacherSyllabusStateFlow.collectLatest {
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

                            if (it.data.syllabuses.isNotEmpty()) {

                                binding.recyclerSyllabus.isVisible = true
                                binding.tvNoData.isVisible = false

                                val noticeAdapter =
                                    TeacherSyllabusListAdapter(it.data.syllabuses, this@TeacherSyllabusFragment)

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

        teacherSyllabusViewModel.getTeacherSyllabuses()


    }

    override fun onItemClick(t: Syllabuse, pos: Int, boolean: Boolean) {
        when (pos) {
            1 -> {
                findNavController().navigate(R.id.openPdfFragment,
                    Bundle().apply {
                        putString(Constant.URL_ARGUMENT, t.filePath)
                    })
            }
            2 -> {
                val androidDownloader = AndroidDownloader(requireContext())
                androidDownloader.downloadFile(t.filePath, getString(R.string.syallabus) )
            }
            3 -> {
                findNavController().navigate(R.id.addSyllabusFragment,Bundle( ).apply {
                    putBoolean("edit", true)
                    putString(Constant.ID, t.id)
                    putInt("classID", t.classID)
                    putString("classSTD", t.classSTD)
                    putString("sections", t.sections)
                    putInt("subID", t.subID)
                    putString("subject", t.subject)
                    putString("title", t.title)
                    putString("fileName", t.fileName)
                })
            }
            4 -> {
                teacherSyllabusViewModel.deleteSyllabus(t.id).invokeOnCompletion {
                    teacherSyllabusViewModel.getTeacherSyllabuses()
                }
            }
        }

    }
}