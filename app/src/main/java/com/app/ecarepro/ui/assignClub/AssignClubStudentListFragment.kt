package com.app.ecarepro.ui.assignClub

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.CustomPopupSelectClassBinding
import com.app.ecarepro.databinding.FragmentAssignClubStudentListBinding
import com.app.ecarepro.model.MyClasseX
import com.app.ecarepro.model.OrderDropDown
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.assign_home.Student
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AssignClubStudentListFragment : Fragment() {

    private lateinit var binding: FragmentAssignClubStudentListBinding
    private val mViewModel: AssignClubViewModel by viewModels()
    private val studentMutableList = mutableListOf<Student>()
    private val clubsMutableList = mutableListOf<Clubs>()
    private val mAdapter by lazy {
        AssignClubStudentsAdapter(studentMutableList, clubsMutableList) { poss, student ->
            getClubPopUp(
                poss,
                student
            )
        }
    }

    private var classModel: MyClasseX? = null
    private val classAdapter by lazy {
        ArrayAdapter<MyClasseX>(requireContext(), android.R.layout.simple_spinner_item).apply {
            this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private var selectedOrderModel = OrderDropDown(0, "Student Name")
    private val selectedAdapter by lazy {
        ArrayAdapter<OrderDropDown>(
            requireContext(),
            android.R.layout.simple_spinner_item
        ).apply {
            this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            Picasso.setSingletonInstance(
                Picasso.Builder(requireActivity()) // additional settings
                    .build()
            )
        }catch (e:IllegalStateException){

        }

        binding = FragmentAssignClubStudentListBinding.inflate(inflater, container, false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.assign_club)

        with(binding) {
            spClass.adapter = classAdapter
            spOrder.adapter = selectedAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerAdapter()
        binding.rvStuAtt.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
        lifecycleScope.launch {
            mViewModel._classList.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        it.data!!.myClasses?.let { list ->
                            //list[0].id?.let { it1 -> mViewModel.getStudentList(it1, "0") }
                            classAdapter.addAll(list)
                        }


                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            mViewModel._studentList.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        studentMutableList.clear()
                        clubsMutableList.clear()
                        mAdapter.notifyDataSetChanged()
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        if (it.data!=null){
                            if (!it.data.students.isNullOrEmpty()){
                                it.data.students.let { list ->
                                    studentMutableList.addAll(list)
                                }
                                binding.rvStuAtt.visibility=View.VISIBLE
                                binding.tvNoRecord.visibility=View.GONE
                            }else{
                                binding.rvStuAtt.visibility=View.GONE
                                binding.tvNoRecord.visibility=View.VISIBLE
                            }

                            it.data.clubs?.let { list ->
                                clubsMutableList.addAll(list)
                            }

                            mAdapter.notifyDataSetChanged()
                        }
                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            mViewModel._assignClub.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                    }

                    else -> {}
                }
            }
        }
        mViewModel.getClassList()


        // orderAdapter.add(new OrderModel(-1, "select"));
        selectedAdapter.add(OrderDropDown(0, "Student Name"))
        selectedAdapter.add(OrderDropDown(1, "Admission Number"))
    }



    private fun spinnerAdapter() {
        binding.spClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                classModel = parent.getItemAtPosition(position) as MyClasseX
                classModel?.let {
                    it.id?.let { it1 ->
                        mViewModel.getStudentList(
                            it1,
                            "${selectedOrderModel.id}"
                        )
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.spOrder.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedOrderModel = parent.getItemAtPosition(position) as OrderDropDown
                classModel?.let {
                    it.id?.let { it1 ->
                        mViewModel.getStudentList(
                            it1,
                            "${selectedOrderModel.id}"
                        )
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }


    private fun getClubPopUp(poss: Int, student: Student) {
        val binding: CustomPopupSelectClassBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.custom_popup_select_class,
            null,
            false
        )
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.Animations
        dialog.setContentView(binding.root)

        binding.tvHeading.text = "Select Club"
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvYear.layoutManager = linearLayoutManager
        val homeSelectAdapter = ClubSelectAdapter(clubsMutableList) {}
        binding.rvYear.adapter = homeSelectAdapter
        dialog.show()
        binding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        binding.tvOk.setOnClickListener {

            if (homeSelectAdapter.lastIndex == -1) {
                return@setOnClickListener
            }
            val clubsId = clubsMutableList[homeSelectAdapter.lastIndex].clubID
            val clubsName = clubsMutableList[homeSelectAdapter.lastIndex].clubName
            //holder.edtHouseName.text = houseDetails.houseName
            studentMutableList[poss].clubID = clubsId
            studentMutableList[poss].clubName = clubsName


            val request = AssignClubRequest(
                rollNumber = student.rollNumber,
                clubID =  student.clubID,
                stID = "${student.stID}"

            )

            mAdapter.notifyItemChanged(poss)

            val list = mutableListOf<AssignClubRequest>()
            list.add(request)
            mViewModel.assignClub(list)
            dialog.dismiss()
        }
    }


}