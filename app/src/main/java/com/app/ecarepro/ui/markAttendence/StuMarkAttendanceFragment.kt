package com.app.ecarepro.ui.markAttendence

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
 import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StuMarkAttendanceFragment : Fragment(),    ItemListener<StudentAtt> {


    private var editMode: Boolean= false
    private var openPreviousDay: Boolean=false
    private var mIsCurrentDate: Boolean=true
    private lateinit var studentListWithData: NetworkStudentListToMarkAtt
    private lateinit var markAttModel: NetworkStudentListToMarkAtt
    private var from: String= ""
    private var isLateEnable: Boolean = false
     private var studentListArrayList= mutableListOf<StudentListMarkAtt>()
    private var uploadStudentList= mutableListOf<StudentAtt>()

    private var subID: Int = 0
    private var pendingLeave: Int = 0
    private lateinit var mySubjectList: List<MySubject>
    private lateinit var classesForSubTeaches: List<ClassesForSubTeach>
    private lateinit var classesForClsTeaches: List<ClassesForClsTeach>
    private   var _binding: FragmentStuMarkAttendenceBinding? = null
    private var mDate: String  = ""
    private var className: String  = ""
    private var subjectName: String  = ""
    var rbType: Int = 0
    private var rollNoFilterAsc=true
    private var admissionFilterAsc=true
    private var nameFilterAsc=true
    private val binding get() = _binding!!
    private lateinit var studentListMarkAttAdapter: StudentListMarkAttAdapter
    private val stuMarkAttendanceViewModel : StuMarkAttendanceViewModel by viewModels()
    private var classID= 0
    private var p  = 0
    private var a  = 0
    private var l  = 0
    private var approve_leave  = 0
    private var lt  = 0
    private var na  = 0
    private lateinit var   dialog  : Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding  = FragmentStuMarkAttendenceBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = from


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

          dialog = Dialog(requireContext())

        from=  getString(R.string.class_attendance)
        binding.radioGroupWisesubmission.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbClassWise -> {
                    classID=0
                    subID=0
                    binding.recyclerNotice.isVisible = false
                    binding.includeToolbar.btnSave.isVisible=false
                    binding.autoCompleteSub.setText("Select Subject ",false)
                    binding.autoCompleteClass.setText("Select Class ",false)
                    from=getString(R.string.class_attendance)
                    getClassList()
                    binding.includeToolbar.toolbarTitle.text = from
                }
                R.id.rbStudentWise -> {
                    classID=0
                    subID=0
                    binding.recyclerNotice.isVisible = false
                    binding.includeToolbar.btnSave.isVisible=false
                    binding.autoCompleteSub.setText("Select Subject ",false)
                    binding.autoCompleteClass.setText("Select Class ",false)
                    from=getString(R.string.subject_attendance)
                    getClassList()
                    binding.includeToolbar.toolbarTitle.text = from
                }
            }
            binding.autoInputSubInputLayout.isVisible=from==getString(R.string.subject_attendance)
        }

        binding.includeToolbar.toolbarTitle.text = from

        binding.includeToolbar.btnSave.setOnClickListener {
            popUpDetailsMarkAttendance()
        }

        binding.autoCompleteClass.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, pos, id ->

                if (from==getString(R.string.subject_attendance)){
                    classID=classesForSubTeaches[pos].classID
                    className=classesForSubTeaches[pos].className
                    getSubjectList(classID)
                }else{
                    subID=0
                    binding.autoCompleteSub.setText("Select Subject ",false)
                    classID=classesForClsTeaches[pos].classID
                    className=classesForClsTeaches[pos].className
                    getStudentListToMarkAtt(classID,subID)
                }

            }

        binding.autoCompleteSub.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, pos, id ->
                subID=mySubjectList[pos].subID
                subjectName=mySubjectList[pos].subjectName
                getStudentListToMarkAtt(classID,subID)
            }

        getClassList()


        mDate=Constant.currentDate()
        binding.startDate.setText(Constant.currentDate())
        binding.startDate.setOnClickListener {
            ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.startDate.setText(Constant.dateToShow(date.toString()))
                    mDate=  Constant.dateToShow(date.toString())
                    mIsCurrentDate=isCurrentDate
                    if (from==getString(R.string.subject_attendance)){
                        if (classID!=0 && subID!=0){
                            getStudentListToMarkAtt(classID,subID)
                        }

                    }else{
                        if (classID!=0  ){
                            getStudentListToMarkAtt(classID,subID)
                        }
                    }
                }
            }).setMaxDate(Constant.getLongTimeDate(Constant.currentDate()))
        }


        binding.tvSortByRollNo.setOnClickListener {
           try {
               rollNoFilterAsc=!rollNoFilterAsc
               studentListArrayList = if (rollNoFilterAsc) studentListArrayList.sortedBy  { it.otherDTL[1].value  }.toMutableList()
               else  studentListArrayList.sortedByDescending { it.otherDTL[1].value  }.toMutableList()
               setupRecyclerView(studentListArrayList)
           }catch (_:Exception){}

        }
        binding.tvSortByAdmission.setOnClickListener {
            try {
                admissionFilterAsc=!admissionFilterAsc
                studentListArrayList = if (admissionFilterAsc) studentListArrayList.sortedBy  { it.otherDTL[0].value  }.toMutableList()
                else  studentListArrayList.sortedByDescending { it.otherDTL[0].value  }.toMutableList()
                setupRecyclerView(studentListArrayList)
            }catch (_:Exception){}
        }

        binding.tvSortByName.setOnClickListener {
           try {
               nameFilterAsc=!nameFilterAsc
               studentListArrayList = if (nameFilterAsc) studentListArrayList.sortedBy  { it.stName.trim().lowercase() }.toMutableList()
               else  studentListArrayList.sortedByDescending { it.stName.trim().lowercase()  }.toMutableList()
               setupRecyclerView(studentListArrayList)
           }catch (_:Exception){}
        }

    }

    private fun setupRecyclerView(studentListArrayList: MutableList<StudentListMarkAtt>) {
        if (studentListArrayList != null) {
        if (studentListArrayList.isNotEmpty()) {
            studentListMarkAttAdapter = StudentListMarkAttAdapter(
                studentListArrayList,
                markAttModel.isLateEnable,
                markAttModel.hasMarked,
                markAttModel.canEdit,
                this@StuMarkAttendanceFragment
            )

            binding.recyclerNotice.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = studentListMarkAttAdapter
            }
        }
        }
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
                                binding.includeToolbar.btnSave.isVisible = true
                                binding.recyclerNotice.isVisible = true
                                binding.tvNoData.isVisible = false
                                 if (it.data.hasMarked){
                                     binding.includeToolbar.btnSave.isVisible = editMode
                                     binding.includeToolbar.btnSave.text = "Modify"
                                 }else{
                                     binding.includeToolbar.btnSave.text = "Save"
                                 }
                                isLateEnable = it.data.isLateEnable
                                studentListWithData = it.data
                                markAttModel = it.data
                                studentListArrayList.clear()
                                studentListArrayList.addAll(it.data.studentList)

                                studentListMarkAttAdapter = StudentListMarkAttAdapter(
                                studentListArrayList,
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
                                if (dialog.isShowing) {
                                    dialog.dismiss()
                                }
                            if (it.data.pendingLeave > 0) {

                                showPendingAlertDialog(it.data.pendingLeave)
                            }

                        } else {
                            binding.recyclerNotice.isVisible = false
                            binding.tvNoData.isVisible = true
                        }

                    }

                } }  } }
        stuMarkAttendanceViewModel.getStudentListToMarkAtt(classID,subID,Constant.toSystemDate(mDate))



    }


    private fun showPendingAlertDialog(pendingLeave: Int) {
        val tv_pending_leave: TextView
        val cv_yes: CardView
        val cv_no: CardView
        val cv_skip: CardView

        dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.window!!.attributes.windowAnimations = R.style.Animations
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_pendong_leave)
        tv_pending_leave = dialog.findViewById<TextView>(R.id.tv_pending_leave)

        tv_pending_leave.text = "Pending Leave: $pendingLeave"

        cv_yes = dialog.findViewById<CardView>(R.id.cv_yes)
        cv_no = dialog.findViewById<CardView>(R.id.cv_no)
        cv_skip = dialog.findViewById<CardView>(R.id.cv_skip)



        cv_no.setOnClickListener { view: View? ->
            dialog.dismiss()
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            findNavController().popBackStack()
        }
        cv_yes.setOnClickListener { view: View? ->
            dialog.dismiss()
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            findNavController().navigate(R.id.leaveReportFragment, Bundle().apply {
                putString(Constant.TO, Constant.FRA_STU_LEAVE)
            })
        }
        cv_skip.setOnClickListener { view: View? ->
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            dialog.dismiss()
        }
        dialog.show()
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
                                android.R.layout.simple_list_item_1,
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

                             binding.rbClassWise.isVisible=it.data.classAttendance
                             binding.rbStudentWise.isVisible=it.data.subjectAttendance

                             if (!it.data.classAttendance ){
                                 binding.rbStudentWise.isChecked=true
                                 from=getString(R.string.subject_attendance)
                             }

                             editMode=it.data.editMode

                            if (from==getString(R.string.subject_attendance)){
                                if (it.data.classesForSubTeach != null) {
                                    classesForSubTeaches = it.data.classesForSubTeach
                                    openPreviousDay=it.data.openPreviousDay
                                    binding.tilStartDate.isVisible=openPreviousDay
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
                                    openPreviousDay=it.data.openPreviousDay
                                    binding.tilStartDate.isVisible=openPreviousDay
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
        p  = 0
        a  = 0
        l  = 0
        approve_leave=0
        lt  = 0
        na  = 0
        for (i in   studentListArrayList) {

            if (i.status == 1 && i.isLate == 0)
                p++
            else if (i.status == 2)
                a++
            else if (i.status == 3) {
                l++
                if (i.isConstant == 1) {
                    approve_leave += 1
                }
            }else if (i.status == 1 && i.isLate == 1)
                lt++
            else if (i.status == 4)
                na++


        }
        tv_present_count.text = p.toString() + ""
        tv_absent_count.text = a.toString() + ""
        if (approve_leave>0 && (l-approve_leave)>0){
        tv_leave_count.text = buildString {
        append(l)
        append(" (")
        append(l-approve_leave)
        append(" Marked, ")
        append(approve_leave)
        append(" Pre-Approved)")
       } } else if (approve_leave>0){
            tv_leave_count.text = buildString {
                append(approve_leave)
                append(" (Pre-Approved)")
            }
        }else{
            tv_leave_count.text = l.toString() + ""
        }

        tvLateCount.text = lt.toString() + ""
        na_day.text = na.toString() + ""
        if (isLateEnable) llLate.visibility = View.VISIBLE else llLate.visibility =  View.GONE
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
            2
        }else{
            1
        }
         uploadStudentList.clear()
        for (i in studentListArrayList) {
            if (i.isConstant==0){
                uploadStudentList.add(StudentAtt(i.isLate,i.stID,i.status))
            }
        }

        stuMarkAttendanceViewModel.postMarkAttendance(
            classID,
            subID,
            mode ,
            Constant.toSystemDate(mDate),
            uploadStudentList
            ).invokeOnCompletion {
            if ( from!=getString(R.string.subject_attendance) ) {
                if (mIsCurrentDate) {
                    SmsAlertPopup()
                }
                else {
                    SuccessAlertPopup(
                        "",
                        String.format(
                            requireContext().resources.getString(R.string.attendance__alert),
                            className + ""
                        )
                    )
                }
            } else {
                if ( from!=getString(R.string.subject_attendance)) SuccessAlertPopup(
                    "",
                    String.format(
                        requireContext().resources.getString(R.string.attendance__alert),
                        className+ ""
                    )
                )
                else SuccessAlertPopup(
                    "",
                    String.format(
                        requireContext().resources.getString(R.string.attendance__alert_subject),
                        subjectName + ""
                    )
                )
            }
        }

        lifecycleScope.launch {
            stuMarkAttendanceViewModel.postMarkAttendanceStateFlow.collectLatest {  when (it) {
                is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                } is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)
                      } }  } }
    }

    override fun onItemClick(t: StudentAtt, pos: Int, boolean: Boolean) {
       // uploadStudentList.add( t)
//       val data= studentList[pos]
//        data.status=t.status
//        data.isLate=t.isLate
//        studentList[pos] = data

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun SuccessAlertPopup(headingString: String?, subHeading: String?) {
        val tv_done: TextView
        val tv_sub_text: TextView
        val tv_main_text: TextView
        val tv_heading: TextView
        val ll_yes_no: LinearLayout
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.window!!.attributes.windowAnimations = R.style.Animations
        dialog.setContentView(R.layout.custom_popup_attention)
        ll_yes_no = dialog.findViewById(R.id.ll_yes_no)
        tv_done = dialog.findViewById(R.id.tv_done)
        tv_sub_text = dialog.findViewById(R.id.tv_sub_text)
        tv_main_text = dialog.findViewById(R.id.tv_main_text)
        tv_heading = dialog.findViewById(R.id.tv_heading)
        tv_heading.setText(R.string.attention)
        tv_main_text.text = headingString
        tv_sub_text.text = subHeading
        tv_done.visibility = View.VISIBLE
        ll_yes_no.visibility = View.GONE
        tv_done.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    fun SmsAlertPopup() {
        val tvCancel: TextView
        val tvOk: TextView
        val tv_sub_text: TextView
        val tv_done: TextView
        val tv_sub: TextView
        val ll_yes_no: LinearLayout
        val rb1: RadioButton
        val rb2: RadioButton
        val rgSmsApp: RadioGroup
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.Animations
        dialog.setContentView(R.layout.custom_popup_sms_alert)
        tvCancel = dialog.findViewById(R.id.tv_cancel)
        tvOk = dialog.findViewById(R.id.tv_ok)
        tv_done = dialog.findViewById(R.id.tv_done)
        tv_sub_text = dialog.findViewById(R.id.tv_sub_text)
        ll_yes_no = dialog.findViewById(R.id.ll_yes_no)
        tv_sub = dialog.findViewById<TextView>(R.id.tv_sub_text_new)
        rb1 = dialog.findViewById(R.id.rb1)
        rgSmsApp = dialog.findViewById<RadioGroup>(R.id.rgSmsApp)
        rb2 = dialog.findViewById(R.id.rb2)
        if (markAttModel.smsAlertEnable && markAttModel.msgAlertEnable) {

            tv_done.visibility = View.GONE
        } else

            if (markAttModel.smsAlertEnable) {
            rb1.visibility = View.GONE
            rb2.visibility = View.GONE
            tv_sub_text.visibility = View.VISIBLE
            tv_sub_text.text = "Do you want to sent SMS Alert ?"
            rbType = 1
            ll_yes_no.visibility = View.VISIBLE
            tv_done.visibility = View.GONE
        } else if (markAttModel.msgAlertEnable) {
            rb1.visibility = View.GONE
            rb2.visibility = View.GONE
            tv_sub_text.visibility = View.VISIBLE
            tv_sub_text.text = "Do you want to sent App Message Alert ?"
            rbType = 2
            ll_yes_no.visibility = View.VISIBLE
            tv_done.visibility = View.GONE
        } else {
            rb1.visibility = View.GONE
            rb2.visibility = View.GONE
            tv_sub_text.visibility = View.GONE
            ll_yes_no.visibility = View.GONE
            tv_done.visibility = View.VISIBLE
        }
        rb1.setOnCheckedChangeListener { buttonView, isChecked -> if (isChecked) rbType = 1 }
        rb2.setOnCheckedChangeListener { buttonView, isChecked -> if (isChecked) rbType = 2 }
        tvCancel.setOnClickListener {
            dialog.dismiss()
            SuccessAlertPopup(
                "",
                String.format(
                    requireContext().resources.getString(R.string.attendance_locked_alert),
                    className + ""
                )
            )
        }
        tvOk.setOnClickListener(View.OnClickListener {
            if (rbType == 0) {
                Toast.makeText(context, "Please select Notification type", Toast.LENGTH_SHORT)
                    .show()
             } else   {
                 if (rbType==1){
                     if (studentListWithData.templateID!=null){
                         dialog.dismiss()
                         (requireActivity() as MainActivity).showLoader(true)
                         stuMarkAttendanceViewModel.sendMessage(markAttModel,studentListArrayList,className,rbType) {  isSuccess, message ->
                             (requireActivity() as MainActivity).showLoader(false)
                             if (isSuccess){
                                 mainActivity().showMessage("SMS Sent Successfully")
                             }else{
                                 mainActivity().showMessage(message)
                             }
                         }
                     }else{
                         Toast.makeText(context, "Sms Template not defined", Toast.LENGTH_SHORT).show()
                     }
                 }else{
                     dialog.dismiss()
                     (requireActivity() as MainActivity).showLoader(true)
                     stuMarkAttendanceViewModel.sendMessage(markAttModel,studentListArrayList,className,rbType) {  isSuccess, message ->
                         (requireActivity() as MainActivity).showLoader(false)
                         if (isSuccess){
                             mainActivity().showMessage(message)
                         }else{
                             mainActivity().showMessage(message)
                         }
                     }
                 }


            }
            })
        tv_done.setOnClickListener {
            dialog.dismiss()

        }
        dialog.show()
    }

}