package com.app.ecarepro.ui.students_list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStudentListBinding
import com.app.ecarepro.model.Student
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class  StudentListFragment : Fragment(), ItemListener<Student> {

    private var filterPos: Int = 0
    private var studentList= mutableListOf<Student>()
    private lateinit var studentListFilter: List<Student>
    private var toFragment: String = ""
    private lateinit var binding: FragmentStudentListBinding
    private val studentListViewModel: StudentListViewModel by viewModels()
    private var schoolType = 2
    private var sortType = 0
    private var rollNoFilterAsc=true
    private var admissionFilterAsc=true
    private var nameFilterAsc=true



    private val filterList =
        listOf<String>("Name", "Admission Number", "Class", "Father Name", "Contact Number")

    @Inject
    lateinit var userDataStore: UserDataStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStudentListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = studentListViewModel
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            toFragment = requireArguments().getString(Constant.TO).toString()
        } catch (_: Exception) {
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rbGroupSchoolType.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rb_all -> {
                        schoolType = 2
                        studentListViewModel.getStudentList(schoolType,toFragment)

                    }

                    R.id.rb_boarding -> {
                        schoolType = 1
                        studentListViewModel.getStudentList(schoolType,toFragment)

                    }

                    R.id.rb_day_scolar -> {
                        schoolType = 0
                        studentListViewModel.getStudentList(schoolType,toFragment)

                    }

                }
            })

        lifecycleScope.launch {
            studentListViewModel.searchQuery.collectLatest {

                if (it.isNotEmpty() && studentList != null) {
                    when (filterPos) {
                        0 -> {
                            studentListFilter = studentList.filter { s ->
                                s.name!!.lowercase().contains(it.lowercase())
                            }

                        }

                        1 -> {
                            studentListFilter = studentList.filter { s ->
                                s.admissionNumber!!.lowercase().contains(it.lowercase())
                            }

                        }

                        2 -> {
                            studentListFilter = studentList.filter { s ->
                                s.`class`!!.lowercase().contains(it.lowercase())
                            }

                        }

                        3 -> {
                            studentListFilter = studentList.filter { s ->
                                s.fatherName!!.lowercase().contains(it.lowercase())
                            }

                        }

                        4 -> {
                            studentListFilter = studentList.filter { s ->
                                s.contactMob!!.lowercase().contains(it.lowercase())
                            }

                        }
                    }
                    setupRecycleViewStudentList(studentListFilter)
                } else {
                    studentList.let { it1 -> setupRecycleViewStudentList(it1) }
                }


            }
        }

        lifecycleScope.launch {
            studentListViewModel.studentListStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.rvStudentList.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvStudentList.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvStudentList.isVisible = true

                        if (it.data != null) {

                            studentList = it.data.students.toMutableList()
                            setupRecycleViewStudentList(it.data.students)


                        }

                    }


                    else -> {}
                }


            }

        }

        /* now we  pass  this  boolean  from setting */

        studentListViewModel.getStudentList(schoolType,toFragment)
        checkIsBoarding()


//        binding.imgFilter.setOnClickListener {
//            popupFilter()
//        }


        binding.tvSortByRollNo.setOnClickListener {
            rollNoFilterAsc=!rollNoFilterAsc
            if (studentList!=null){
                studentList = if (rollNoFilterAsc) studentList.sortedBy  { it.rollNumber }.toMutableList()
                else  studentList.sortedByDescending { it.rollNumber }.toMutableList()
                setupRecycleViewStudentList(studentList)
            }

        }
        binding.tvSortByAdmission.setOnClickListener {
            admissionFilterAsc=!admissionFilterAsc
            if (studentList!=null){
                studentList = if (admissionFilterAsc) studentList.sortedBy  { it.admissionNumber }.toMutableList()
                else  studentList.sortedByDescending { it.admissionNumber }.toMutableList()
                setupRecycleViewStudentList(studentList)
            }
            }

        binding.tvSortByName.setOnClickListener {
            nameFilterAsc=!nameFilterAsc
            if (studentList!=null){
                studentList = if (nameFilterAsc) studentList.sortedBy  { it.name!!.trim().lowercase() }.toMutableList()
                else  studentList.sortedByDescending { it.name!!.trim().lowercase()  }.toMutableList()
                setupRecycleViewStudentList(studentList)
            }
        }


    }


    private fun  popupFilter() {
        val menuItemView = requireView().findViewById<View>(R.id.menu_filter)
        val popupMenu = PopupMenu(requireContext(), menuItemView)
        popupMenu.menuInflater.inflate(R.menu.filter_menu_student_list, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            setFilterAction(item.itemId)
            true
        }
        popupMenu.show()
    }

    private fun setFilterAction(type: Int) {
        when (type) {
            R.id.menu_by_roll_no -> {
                  }

            R.id.menu_by_admission_no -> {

               }  }
    }

    private fun setupRecycleViewStudentList(students: List<Student>) {
        if (students.isNotEmpty()) {
            binding.rvStudentList.isVisible = true
            binding.tvNoData.isVisible = false



            val circularAdapter = StudentListAdapter(
                students,
                this@StudentListFragment
            )
            binding.rvStudentList.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(activity, 2)
                adapter = circularAdapter
            }
            val adapterFatherDesignation =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                    filterList)
            binding.taskList.setAdapter(adapterFatherDesignation)
            binding.taskList.setOnItemClickListener { a, e, position, c ->
                filterPos = position
            }
        } else {
            binding.rvStudentList.isVisible = false
            binding.tvNoData.isVisible = true
        }
    }

    override fun onItemClick(t: Student, pos: Int, boolean: Boolean) {


//        when (toFragment) {
//            Constant.FRA_ADD_APPRE -> {
//                findNavController().navigate(
//                    R.id.action_studentListFragment2_to_addAppreciationFragment,
//                    Bundle().apply {
//                        putInt(Constant.STUDENT_ID_ARGUMENT, t.stID!!)
//                    })
//            }
//
//            Constant.FRA_VIEW_APPRE -> {
//                findNavController().navigate(
//                    R.id.action_studentListFragment2_to_appreciationListFragment,
//                    Bundle().apply {
//                        putInt(Constant.STUDENT_ID_ARGUMENT, t.stID!!)
//                    })
//            }
//
//            Constant.FRA_ADD_INFE -> {
//                findNavController().navigate(
//                    R.id.action_studentListFragment2_to_addInfractionFragment,
//                    Bundle().apply {
//                        putInt(Constant.STUDENT_ID_ARGUMENT, t.stID!!)
//                    })
//            }
//
//            Constant.FRA_VIEW_INFE -> {
//                findNavController().navigate(
//                    R.id.action_studentListFragment2_to_infractionListFragment,
//                    Bundle().apply {
//                        putInt(Constant.STUDENT_ID_ARGUMENT, t.stID!!)
//                    })
//            }
//
//            Constant.PROFILE_FRA_STU -> {
//                findNavController().navigate(
//                    R.id.action_studentListFragment2_to_studentProfileNavHostFragment,
//                    Bundle().apply {
//                        putInt(Constant.STUDENT_ID_ARGUMENT, t.stID!!)
//                    })
//            }
//        }

    }
    private fun checkIsBoarding() {
        lifecycleScope.launch {
            userDataStore.getSchoolData()?.let {
                binding.rbGroupSchoolType.isVisible = it.isBoardingSchool ?: false
            }
        }
    }






}