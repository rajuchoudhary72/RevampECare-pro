package com.app.ecarepro.ui.students_list.students_new_list

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentStudentListSubBinding
import com.app.ecarepro.model.Student
import com.app.ecarepro.ui.students_list.StudentListViewModel
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentListSubFragment() : Fragment(),
    ItemListener<Student> {

    var className: String = ""
    var toFragment: String= ""

    private val studentsListShareViewModel : StudentsListShareViewModel by activityViewModels()


    private lateinit var binding: FragmentStudentListSubBinding
    private val studentListViewModel: StudentListViewModel by viewModels()
    private lateinit var studentListFilter: List<Student>
    private var rollNoFilterAsc=true
    private var admissionFilterAsc=true
    private var nameFilterAsc=true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding = FragmentStudentListSubBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = studentListViewModel
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            className = it.getString(ARG_ITEM_CLASS_NAME, "")
            toFragment = it.getString(TO_FRAGMENT, "")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            studentsListShareViewModel.getStudentMutableLiveData().observe(viewLifecycleOwner){ studentList ->
                studentList.let { students->

                   if (students!=null) {
                       if (students.isNotEmpty()) {

                           var studentList: List<Student> = students.filter { it.`class` == className }
                           binding.tvTotalCount.text = studentList.size.toString()
                           binding.tvBoysCount.text = studentList.filter { it.gender == "Male" }.size.toString()
                           binding.tvGirlsCount.text = studentList.filter { it.gender == "Female" }.size.toString()

                           lifecycleScope.launch {
                               studentListViewModel.searchQuery.collectLatest {

                                   if (it.isNotEmpty() && studentList != null) {
                                       studentListFilter = studentList .filter { s ->
                                           s.name!!.lowercase().contains(it.lowercase())
                                                   || s.name.lowercase().contains(it.lowercase())
                                                   || s.admissionNumber!!.lowercase().contains(it.lowercase())
                                                   || s.`class`!!.lowercase().contains(it.lowercase())
                                                   || s.fatherName!!.lowercase().contains(it.lowercase())
                                                   || s.contactMob!!.lowercase().contains(it.lowercase())


                                       }
                                       setupRecycleViewStudentList(studentListFilter)
                                   } else {
                                       setupRecycleViewStudentList(studentList)
                                   }


                               }
                           }


//                           binding.tvSortByRollNo.setOnClickListener {
//                               rollNoFilterAsc = !rollNoFilterAsc
//                               studentList =
//                                   if (rollNoFilterAsc) studentList.sortedBy { it.rollNumber }.toMutableList()
//                                   else studentList.sortedByDescending { it.rollNumber }.toMutableList()
//                               setupRecycleViewStudentList(studentList)
//                           }

                           binding.llSortByRollNo.background.setTint(resources.getColor(R.color.app_color))
                           binding.llSortByAdmission.background=resources.getDrawable(R.drawable.bg_rounded_corner_green,null)
                           binding.llSortByName.background=resources.getDrawable(R.drawable.bg_rounded_corner_green,null)

                           binding.tvSortByRollNo.setTextColor(resources.getColorStateList(R.color.white,null))
                           binding.tvSortByAdmission.setTextColor(resources.getColorStateList(R.color.black,null))
                           binding.tvSortByName.setTextColor(resources.getColorStateList(R.color.black,null))

                           binding.ivSortByName.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), PorterDuff.Mode.SRC_IN)
                           binding.ivSortByRollNo.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN)
                           binding.ivSortByAdmission.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), PorterDuff.Mode.SRC_IN)


                           binding.tvSortByRollNo.setOnClickListener {
                               try {
                                   rollNoFilterAsc = !rollNoFilterAsc
                                   studentList = studentList
                                       .sortedWith(compareBy { it.rollNumber?.toIntOrNull() ?: Int.MAX_VALUE })
                                       .let { if (rollNoFilterAsc) it else it.asReversed() }

                                       .toMutableList()
                                   setupRecycleViewStudentList(studentList)

                               } catch (e: Exception) {
                                   e.printStackTrace()
                               }
                               sortButtonUi(1)
                           }

                           binding.tvSortByAdmission.setOnClickListener {
                               try {
                                   admissionFilterAsc = !admissionFilterAsc
                                   studentList = studentList
                                       .sortedWith(compareBy { it.admissionNumber?.toIntOrNull() ?: Int.MAX_VALUE })
                                       .let { if (admissionFilterAsc) it else it.asReversed() }
                                       .toMutableList()
                                   setupRecycleViewStudentList(studentList)
                               } catch (e: Exception) {
                                   e.printStackTrace()
                               }
                               sortButtonUi(2)

                           }

//                           binding.tvSortByAdmission.setOnClickListener {
//                               admissionFilterAsc = !admissionFilterAsc
//                               studentList = if (admissionFilterAsc) studentList.sortedBy { it.admissionNumber }
//                                   .toMutableList()
//                               else studentList.sortedByDescending { it.admissionNumber }.toMutableList()
//                               setupRecycleViewStudentList(studentList)
//                           }


                           binding.tvSortByName.setOnClickListener {
                               nameFilterAsc = !nameFilterAsc
                               studentList = if (nameFilterAsc) studentList.sortedBy { it.name!!.trim().lowercase() }
                                   .toMutableList()
                               else studentList.sortedByDescending { it.name!!.trim().lowercase() }.toMutableList()
                               setupRecycleViewStudentList(studentList)
                               sortButtonUi(3)

                           }


                       } else {
                           binding.rvStudentList.isVisible = false
                           binding.tvNoData.isVisible = true
                       }
                   } else {
                       binding.rvStudentList.isVisible = false
                       binding.tvNoData.isVisible = true
                   }
               }

            }
        }


    }


    fun sortButtonUi(pos:Int){
        when(pos){
            1 ->{
                binding.llSortByRollNo.background.setTint(resources.getColor(R.color.app_color))
                binding.llSortByAdmission.background=resources.getDrawable(R.drawable.bg_rounded_corner_green,null)
                binding.llSortByName.background=resources.getDrawable(R.drawable.bg_rounded_corner_green,null)

                binding.tvSortByRollNo.setTextColor(resources.getColorStateList(R.color.white,null))
                binding.tvSortByAdmission.setTextColor(resources.getColorStateList(R.color.black,null))
                binding.tvSortByName.setTextColor(resources.getColorStateList(R.color.black,null))

                binding.ivSortByName.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), PorterDuff.Mode.SRC_IN)
                binding.ivSortByRollNo.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN)
                binding.ivSortByAdmission.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), PorterDuff.Mode.SRC_IN)

            }
            2 ->{
                binding.llSortByRollNo.background=resources.getDrawable(R.drawable.bg_rounded_corner_green,null)
                binding.llSortByAdmission.background.setTint(resources.getColor(R.color.app_color))
                binding.llSortByName.background=resources.getDrawable(R.drawable.bg_rounded_corner_green,null)

                binding.tvSortByRollNo.setTextColor(resources.getColorStateList(R.color.black,null))
                binding.tvSortByAdmission.setTextColor(resources.getColorStateList(R.color.white,null))
                binding.tvSortByName.setTextColor(resources.getColorStateList(R.color.black,null))

                binding.ivSortByName.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), PorterDuff.Mode.SRC_IN)
                binding.ivSortByRollNo.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), PorterDuff.Mode.SRC_IN)
                binding.ivSortByAdmission.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN)

            }
            3 ->{
                binding.llSortByRollNo.background=resources.getDrawable(R.drawable.bg_rounded_corner_green,null)
                binding.llSortByAdmission.background=resources.getDrawable(R.drawable.bg_rounded_corner_green,null)
                binding.llSortByName.background.setTint(resources.getColor(R.color.app_color))

                binding.tvSortByRollNo.setTextColor(resources.getColorStateList(R.color.black,null))
                binding.tvSortByAdmission.setTextColor(resources.getColorStateList(R.color.black,null))
                binding.tvSortByName.setTextColor(resources.getColorStateList(R.color.white,null))

                binding.ivSortByName.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN)
                binding.ivSortByRollNo.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), PorterDuff.Mode.SRC_IN)
                binding.ivSortByAdmission.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), PorterDuff.Mode.SRC_IN)

            }
        }
    }

    private fun setupRecycleViewStudentList(students: List<Student>) {
        if (students.isNotEmpty()) {
            binding.rvStudentList.isVisible = true
            binding.tvNoData.isVisible = false


            val circularAdapter = StudentListNewAdapter(
                students,
                this@StudentListSubFragment,
                toFragment
            )
            binding.rvStudentList.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(activity, 2)
                adapter = circularAdapter
            }

        } else {
            binding.rvStudentList.isVisible = false
            binding.tvNoData.isVisible = true
        }
    }

    override fun onItemClick(t: Student, pos: Int, boolean: Boolean) {


        when (toFragment) {
            Constant.APPRECIATION_FRAG -> {
                when(pos){
                    0->{
                        findNavController().navigate(
                            R.id.action_studentListFragment2_to_appreciationListFragment,
                            Bundle().apply {
                                putInt(Constant.STUDENT_ID_ARGUMENT, t.stID!!)
                            })
                    }
                    1->{
                        findNavController().navigate(
                            R.id.action_studentListFragment2_to_addAppreciationFragment,
                            Bundle().apply {
                                putInt(Constant.STUDENT_ID_ARGUMENT, t.stID!!)
                            })
                    }
                }

            }
            Constant.INFRECTION_FRAG -> {
                when(pos){
                    0->{
                        findNavController().navigate(
                            R.id.action_studentListFragment2_to_infractionListFragment,
                            Bundle().apply {
                                putInt(Constant.USER_ID, t.stID!!)
                                putInt(Constant.USER_TYPE, Constant.STUDENT_TYPE)
                            })
                    }
                    1->{
                        findNavController().navigate(
                            R.id.action_studentListFragment2_to_addInfractionFragment,
                            Bundle().apply {
                                putInt(Constant.USER_ID, t.stID!!)
                                putInt(Constant.USER_TYPE, Constant.STUDENT_TYPE)
                            })
                    }
                }

            }
            Constant.PROFILE_FRA_STU -> {
                findNavController().navigate(
                    R.id.action_studentListFragment2_to_studentProfileNavHostFragment,
                    Bundle().apply {
                        putInt(Constant.STUDENT_ID_ARGUMENT, t.stID!!)
                    })
            }
        }

    }

    companion object {
        private const val ARG_ITEM_CLASS_NAME = "item_class_name"
        private const val TO_FRAGMENT = "item_session"

        fun newInstance( className: String, toFragment: String)= StudentListSubFragment().apply {
            arguments= Bundle().apply {
                putString(ARG_ITEM_CLASS_NAME,className)
                putString(TO_FRAGMENT,toFragment)

            }
        }

    }



}