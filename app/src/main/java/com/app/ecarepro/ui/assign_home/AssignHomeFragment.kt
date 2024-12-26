package com.app.ecarepro.ui.assign_home


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.AssignHouseRequest
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.CustomPopupSelectClassBinding
import com.app.ecarepro.databinding.FragmentAssignHomeBinding
import com.app.ecarepro.model.Dtl
import com.app.ecarepro.model.MyClasseX
import com.app.ecarepro.model.OrderDropDown
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.listener.ItemListener
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AssignHomeFragment : Fragment(), ItemListener<Dtl> {

    private lateinit var binding: FragmentAssignHomeBinding
    private val mViewModel: AssignHomeViewModel by viewModels()
    private val assignHomeList = mutableListOf<Student>()
    private val houseList = mutableListOf<House>()
    private val mAdapter by lazy {
        AssignHomeAdapter(assignHomeList, houseList) { poss, student ->
            getHousePopUp(
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

        binding = FragmentAssignHomeBinding.inflate(inflater, container, false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.assign_house)

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
                        assignHomeList.clear()
                        houseList.clear()
                        mAdapter.notifyDataSetChanged()
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        it.data!!.students?.let { list ->
                            assignHomeList.addAll(list)
                        }

                        it.data.houses?.let { list ->
                            houseList.addAll(list)
                        }

                        mAdapter.notifyDataSetChanged()
                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            mViewModel._assignHouse.collectLatest {
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


    override fun onItemClick(t: Dtl, pos: Int, boolean: Boolean) {
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

    private fun getHousePopUp(poss: Int, student: Student) {
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

        binding.tvHeading.text = "Select House"
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvYear.layoutManager = linearLayoutManager
        val homeSelectAdapter = HomeSelectAdapter(houseList) {}
        binding.rvYear.adapter = homeSelectAdapter
        dialog.show()
        binding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        binding.tvOk.setOnClickListener {

            if (homeSelectAdapter.lastIndex == -1) {
                return@setOnClickListener
            }
            val houseId = houseList[homeSelectAdapter.lastIndex].houseID
            //holder.edtHouseName.text = houseDetails.houseName
            assignHomeList[poss].houseID = houseId
            mAdapter.notifyItemChanged(poss)

            val request = AssignHouseRequest(
                houseID = "$houseId",
                rollNumber = student.rollNumber,
                stID = "${student.stID}"

            )
            val list = mutableListOf<AssignHouseRequest>()
            list.add(request)
            mViewModel.assignHouse(list)
            dialog.dismiss()
        }
    }
}