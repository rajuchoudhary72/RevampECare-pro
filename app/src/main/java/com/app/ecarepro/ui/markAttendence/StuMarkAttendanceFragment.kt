package com.app.ecarepro.ui.markAttendence

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStudentListToMarkAtt
import com.app.ecarepro.data.network.model.post_mark_attedance.StudentAtt
import com.app.ecarepro.databinding.FragmentStuMarkAttendenceBinding
import com.app.ecarepro.model.ClassesForClsTeach
import com.app.ecarepro.model.ClassesForSubTeach
import com.app.ecarepro.model.MySubject
import com.app.ecarepro.model.StudentListMarkAtt
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StuMarkAttendanceFragment : Fragment(),  MenuProvider, ItemListener<StudentAtt> {


    private lateinit var studentListWithData: NetworkStudentListToMarkAtt
    private var from: String=""
    private var isLateEnable: Boolean = false
    private lateinit var studentList: MutableList<StudentListMarkAtt>
    private   var uploadStudentList: ArrayList<StudentAtt> = ArrayList()
    private var subID: Int = 0
    private lateinit var mySubjectList: List<MySubject>
    private lateinit var classesForSubTeaches: List<ClassesForSubTeach>
    private lateinit var classesForClsTeaches: List<ClassesForClsTeach>
    private   var _binding: FragmentStuMarkAttendenceBinding? = null


    private val binding get() = _binding!!

    private val stuMarkAttendanceViewModel : StuMarkAttendanceViewModel by viewModels()
    private var classID= 0
    private var p  = 0
    private var a  = 0
    private var l  = 0
    private var lt  = 0
    private var na  = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding  = FragmentStuMarkAttendenceBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        from= requireArguments().getString(Constant.TO).toString()
        binding.toolbar.title=from
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        }
         val menuHost: MenuHost = requireActivity()
       menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
       // activity?.addMenuProvider(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         binding.autoInputSubInputLayout.isVisible=from==getString(R.string.subject_attendance)

        binding.autoCompleteClass.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, pos, id ->

                if (from==getString(R.string.subject_attendance)){
                    classID=classesForSubTeaches[pos].classID
                    getSubjectList(classID)
                }else{
                    subID=0
                    binding.autoCompleteSub.setText("Select Subject ",false)
                    classID=classesForClsTeaches[pos].classID
                    getStudentListToMarkAtt(classID,subID)
                }

            }

        binding.autoCompleteSub.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, pos, id ->
                subID=mySubjectList[pos].subID
                getStudentListToMarkAtt(classID,subID)
            }

        getClassList()





    }



    private fun getStudentListToMarkAtt(classID: Int, subID: Int) {

        lifecycleScope.launch {
            stuMarkAttendanceViewModel.stuListToMarkAttStateFlow.collectLatest {  when (it) {
                is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                } is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)
                    binding.recyclerNotice.isVisible = true

                    if (it.data != null) {

                        if (it.data.studentList != null) {

                            binding.recyclerNotice.isVisible = true
                            binding.tvNoData.isVisible = false

                            studentList= it.data.studentList.toMutableList()
                            isLateEnable=it.data.isLateEnable
                            studentListWithData=it.data


                            val studentListMarkAttAdapter = StudentListMarkAttAdapter(
                                it.data.studentList.toMutableList(),
                                it.data.isLateEnable,
                                it.data.hasMarked,
                                it.data.canEdit,
                                this@StuMarkAttendanceFragment
                            )

                            binding.recyclerNotice.apply {
                                setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(activity)
                                adapter = studentListMarkAttAdapter
                            }
                        } else {
                            binding.recyclerNotice.isVisible = false
                            binding.tvNoData.isVisible = true
                        }

                    }

                } }  } }
        stuMarkAttendanceViewModel.getStudentListToMarkAtt(classID,subID,Constant.currentDate())


    }

    private fun getSubjectList(classID: Int) {

        lifecycleScope.launch {
            stuMarkAttendanceViewModel.subjectsStateFlow.collectLatest {  when (it) {
                is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                } is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)
                    if (it.data != null) {
                        if (it.data.mySubjects != null) {
                            mySubjectList = it.data.mySubjects
                            val subjectDataString: ArrayList<String> = ArrayList()
                            subjectDataString.clear()
                            it.data.mySubjects.forEach { data ->
                                subjectDataString.add(data.subjectName )
                            }
                            val arrayAdapter = ArrayAdapter(
                                requireContext(),
                                R.layout.view_drop_down_menu,
                                subjectDataString
                            )
                            binding.autoCompleteSub.setAdapter(arrayAdapter)
                        } else {
                            binding.tvNoData.isVisible = true
                        } } } }  } }
        stuMarkAttendanceViewModel.mySubjects(classID)


    }

    private fun getClassList() {

        lifecycleScope.launch {
            stuMarkAttendanceViewModel.myClassStateFlow.collectLatest {  when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    } is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    } is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                         if (it.data != null) {
                            if (from==getString(R.string.subject_attendance)){
                                if (it.data.classesForSubTeach != null) {
                                    classesForSubTeaches = it.data.classesForSubTeach
                                    val classesDataString: ArrayList<String> = ArrayList()
                                    classesDataString.clear()
                                    it.data.classesForSubTeach.forEach { data ->
                                        classesDataString.add(data.className )
                                    }
                                    val arrayAdapter = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_list_item_1,
                                        classesDataString
                                    )
                                    binding.autoCompleteClass.setAdapter(arrayAdapter)
                                } else {
                                    binding.tvNoData.isVisible = true
                                }
                            }else{
                                if (it.data.classesForClsTeach != null) {
                                    classesForClsTeaches = it.data.classesForClsTeach
                                    val classesDataString: ArrayList<String> = ArrayList()
                                    classesDataString.clear()
                                    it.data.classesForClsTeach.forEach { data ->
                                        classesDataString.add(data.className )
                                    }
                                    val arrayAdapter = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_list_item_1,
                                        classesDataString
                                    )
                                    binding.autoCompleteClass.setAdapter(arrayAdapter)
                                } else {
                                    binding.tvNoData.isVisible = true
                                }
                            }
                         }

                    } }  } }
        stuMarkAttendanceViewModel.getMarkAttendance()

    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_save, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_save -> {
                 popUpDetailsMarkAttendance()
                true
            }
            else -> false
        }
    }

    private fun popUpDetailsMarkAttendance() {


        val tv_cancel: TextView
        val tv_ok: TextView
        val tv_present_count: TextView
        val tv_absent_count: TextView
        val tv_leave_count: TextView
        val tvLateCount: TextView
        val na_day: TextView
        val llLate: LinearLayout


        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.window!!.attributes.windowAnimations = R.style.Animations
        dialog.setContentView(R.layout.pop_up_mark_details_attendance)
        tv_cancel = dialog.findViewById(R.id.tv_cancel)
        tv_ok = dialog.findViewById(R.id.tv_ok)
        llLate = dialog.findViewById(R.id.llLate)
        tv_present_count = dialog.findViewById(R.id.tv_present_count)
        tv_absent_count = dialog.findViewById(R.id.tv_absent_count)
        tv_leave_count = dialog.findViewById(R.id.leave_day)
        na_day = dialog.findViewById(R.id.na_day)
        tvLateCount = dialog.findViewById(R.id.late_day)
        for (i in   studentList) {

            if (i.status == 1 && i.isLate == 0)
                p++
            else if (i.status == 2)
                a++
            else if (i.status == 3)
                l++
            else if (i.status == 1 && i.isLate == 1)
                lt++
            else if (i.status == 4)
                na++


        }
        tv_present_count.text = p.toString() + ""
        tv_absent_count.text = a.toString() + ""
        tv_leave_count.text = l.toString() + ""
        tvLateCount.text = lt.toString() + ""
        na_day.text = na.toString() + ""
        if (isLateEnable) llLate.visibility = View.VISIBLE else llLate.visibility =
            View.GONE
        dialog.show()
        tv_cancel.setOnClickListener { dialog.dismiss() }
        tv_ok.setOnClickListener {
            saveMarkAttendance()
            dialog.dismiss()
        }


    }

    private fun saveMarkAttendance(){
        var mode : Int = 1
        mode = if (from==getString(R.string.subject_attendance)){
            1
        }else{
            2
        }
        lifecycleScope.launch {
            stuMarkAttendanceViewModel.postMarkAttendanceStateFlow.collectLatest {  when (it) {
                is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                } is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)
                    if (it.data != null) {
                        mainActivity().showMessage("Successfully Uploaded!!!")
                          } } }  } }
        stuMarkAttendanceViewModel.postMarkAttendance(
            classID,
            subID,
            mode ,
            Constant.currentDate(),
            uploadStudentList
            )


    }

    override fun onItemClick(t: StudentAtt, pos: Int, boolean: Boolean) {
        uploadStudentList.add( t)
       val data= studentList[pos]
        data.status=t.status
        data.isLate=t.isLate
        studentList[pos] = data

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}