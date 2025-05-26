package com.app.ecarepro.ui.syllabus.teacher

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentTeacherSyallabusSubBinding
import com.app.ecarepro.model.Syllabuse
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class TeacherSyllabusSubFragment : Fragment(), ItemListener<Syllabuse> {

    private lateinit var binding: FragmentTeacherSyallabusSubBinding
    private val syllabusShareViewModel : SyllabusShareViewModel by activityViewModels()
    private val teacherSyllabusViewModel: TeacherSyllabusViewModel by activityViewModels()
    private var syllabustList: List<Syllabuse>? = null
    private lateinit var syllabusListFilter: List<Syllabuse>


    var className: String = "All"
    var filterType : Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            className = it.getString(ARG_ITEM_CLASS_NAME, "")
            filterType = it.getInt(FILTER_TYPE, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentTeacherSyallabusSubBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            syllabusShareViewModel.getSyllabusMutableLiveData()
                .observe(viewLifecycleOwner) { syllabus ->
                    syllabus.let { syllabusData ->
                        if (syllabusData != null) {
                            if (syllabusData.isNotEmpty()) {

                                var filterList = syllabusData

                                if (className != "All") {
                                    filterList = syllabusData.filter { it.classSTD == className }
                                }

                                syllabustList=filterList
                                setUpSearch()

                            }
                            else {
                                binding.recyclerSyllabus.isVisible = false
                                binding.tvNoData.isVisible = true

                            }

                        }else {
                            binding.recyclerSyllabus.isVisible = false
                            binding.tvNoData.isVisible = true

                        }
                    }
                }




        }


    }


    fun setUpSearch(){
        lifecycleScope.launch {
            teacherSyllabusViewModel.searchQuery.collectLatest {
                if (it.isNotEmpty() && syllabustList != null) {
                    when (filterType) {
                        0 -> {
                            syllabusListFilter = syllabustList!!.filter { s ->
                                s.subject.lowercase().contains(it.lowercase())
                            }
                            setUpTabLayout(syllabusListFilter)

                        }

                        1 -> {
                            syllabusListFilter = syllabustList!!.filter { s ->
                                s.title.lowercase().contains(it.lowercase())
                            }
                            setUpTabLayout(syllabusListFilter)

                        }
                    }
                } else {
                    syllabustList?.let { it1 -> setUpTabLayout(it1) }
                }


            }
        }
    }

    private fun setUpTabLayout(filterList: List<Syllabuse>) {
        if (!filterList.isNullOrEmpty()) {
        val syllabusesListAdapter =
            TeacherSyllabusListAdapter(filterList, this@TeacherSyllabusSubFragment)


        binding.recyclerSyllabus.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = syllabusesListAdapter
        }
        binding.recyclerSyllabus.isVisible = true
        binding.tvNoData.isVisible = false


    } else {
        binding.recyclerSyllabus.isVisible = false
        binding.tvNoData.isVisible = true

    }
    }



    override fun onItemClick(t: Syllabuse, pos: Int, boolean: Boolean) {
        when (pos) {
            1 -> {
                openFile(t.filePath)
            }

            2 -> {
                try {
                    downloadFile(t.filePath)
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }

            }

            3 -> {
                findNavController().navigate(R.id.addSyllabusFragment, Bundle().apply {
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
                    teacherSyllabusViewModel.isDataLoaded=false
                }
            }
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
                    androidDownloader.downloadFile(fileSource, getString(R.string.assessment))
                }

                2 -> {
                    val androidDownloader = AndroidDownloader(requireContext())
                    androidDownloader.downloadFile(fileSource, "Photo", "image/jpeg")
                }

                3 -> {
                    val androidDownloader = AndroidDownloader(requireContext())
                    androidDownloader.downloadFile(fileSource, getString(R.string.assessment),"application/vnd.openxmlformats-officedocument.wordprocessingml.document")
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


    companion object {
        private const val ARG_ITEM_CLASS_NAME = "item_class_name"
        private const val FILTER_TYPE = "filterType"

        fun newInstance( className: String,filter: Int)= TeacherSyllabusSubFragment().apply {
            arguments= Bundle().apply {
                putString(ARG_ITEM_CLASS_NAME,className)
                putInt(FILTER_TYPE,filter)

            }
        }

    }



}