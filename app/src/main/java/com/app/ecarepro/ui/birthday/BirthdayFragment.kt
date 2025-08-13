package com.app.ecarepro.ui.birthday

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentBirthdayBinding
import com.app.ecarepro.model.MonthModel
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BirthdayFragment : Fragment() {

    private var rptType: Int = 1
    private var userType: Int = Constant.STUDENT_TYPE
    private var monthSelected: Int = 0
    private lateinit var binding: FragmentBirthdayBinding
    private var monthModelArrayList = ArrayList<MonthModel>()

    private val birthdayViewModel: BirthdayViewModel by viewModels()


    private var monthSelectedNew: String = ""
    private var dateSelected: String = ""

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBirthdayBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        /*from dashboard birthday cart  click event */
        try {
            rptType = requireArguments().getInt("rType")
            userType = requireArguments().getInt("uType")
            monthSelected = requireArguments().getString("monthSelected").toString().toInt()
            dateSelected = requireArguments().getString("dateSelected").toString()

            if (userType == Constant.STAFF_TYPE) {
                binding.toggleButtonTypeUser.check(R.id.btn_class_staff)
            } else {
                binding.toggleButtonTypeUser.check(R.id.btn_student)
            }

            if (rptType == 2) {
                binding.autoInputClassInputLayout.isVisible=true
                binding.tvDate.isVisible=false
                binding.rbMonthWise.isChecked=true
            }else{
                binding.autoInputClassInputLayout.isVisible=false
                binding.tvDate.isVisible=true
                binding.tvDate.text = Constant.currentDate()
                binding.rbDateWise.isChecked=true
                if (dateSelected.isEmpty()) {
                    binding.tvDate.text = Constant.currentDate()
                } else {
                    binding.tvDate.text = dateSelected
                }
            }

        } catch (e: Exception) {
        }



        bindMonthArray()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.radioGroupWisesubmission.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbMonthWise -> {
                    rptType = 2
                    binding.autoInputClassInputLayout.isVisible=true
                    binding.tvDate.isVisible=false

                }
                R.id.rbDateWise -> {
                    rptType = 1
                    binding.autoInputClassInputLayout.isVisible=false
                    binding.tvDate.isVisible=true
                }
            }
        }


        binding.toggleButtonTypeUser.addOnButtonCheckedListener { _, _, _ ->
            when (binding.toggleButtonTypeUser.checkedButtonId) {
                R.id.btn_student -> {
                    userType = Constant.STUDENT_TYPE
                    birthdayViewModel.birthday(
                            userType,
                            rptType,
                            monthSelected,
                            Constant.toSystemDate(binding.tvDate.text.toString())
                    )


                }
                R.id.btn_parent -> {
                    userType = Constant.PARENT_TYPE
                    birthdayViewModel.birthday(
                        userType,
                        rptType,
                        monthSelected,
                        Constant.toSystemDate(binding.tvDate.text.toString())
                    )


                }

                else -> {
                    userType = Constant.STAFF_TYPE
                    birthdayViewModel.birthday(
                            userType,
                            rptType,
                            monthSelected,
                        Constant.toSystemDate(binding.tvDate.text.toString())
                    )

                }
            }
        }

        binding.tvDate.setOnClickListener {
            ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.tvDate.text = Constant.dateToShow(date.toString())
                    rptType = 1
                    birthdayViewModel.birthday(
                            userType,
                            rptType,
                            monthSelected,
                        Constant.toSystemDate(binding.tvDate.text.toString())
                    )

                }
            })
        }



        binding.autoCompleteMonth.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, pos, id ->
                    monthSelected = monthModelArrayList[pos].monthID
                    rptType = 2
                     birthdayViewModel.birthday(
                            userType,
                            rptType,
                            monthSelected,
                        Constant.toSystemDate(binding.tvDate.text.toString())
                    )
                }




        lifecycleScope.launch {
            birthdayViewModel.birthdayStateFlow.collectLatest { birthdayNetworkResult ->
                when (birthdayNetworkResult) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.recyclerNotice.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerNotice.isVisible = false
                        Log.d("main", "Error$birthdayNetworkResult")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerNotice.isVisible = true

                        if (birthdayNetworkResult.data != null) {

                            if (birthdayNetworkResult.data.usersBirthday != null) {

                                binding.recyclerNotice.isVisible = true
                                binding.tvNoData.isVisible = false

                                val noticeAdapter = BirthListAdapter(
                                        birthdayNetworkResult.data.usersBirthday,
                                        userType, onItemClick = {
                                        findNavController().navigate(
                                            R.id.photoViewFragmentFragment,
                                            bundleOf(
                                                PhotoViewFragmentFragment.PHOTO to it.photo
                                            )
                                        )
                                    }
                                )

                                binding.recyclerNotice.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = noticeAdapter
                                }
                            } else {
                                binding.recyclerNotice.isVisible = false
                                binding.tvNoData.isVisible = true
                            }

                        }

                    }


                }
            }
        }



            monthModelArrayList.forEach { d ->
                if (monthSelected == d.monthID) {
                    binding.autoCompleteMonth.setText(d.month, false)
                }
            }



            binding.tvDate.text = Constant.currentDate()


        if (userType == Constant.STAFF_TYPE) {
            binding.toggleButtonTypeUser.check(R.id.btn_class_staff)
        } else {
            binding.toggleButtonTypeUser.check(R.id.btn_student)
        }

        birthdayViewModel.birthday(userType, rptType, monthSelected, Constant.toSystemDate(binding.tvDate.text.toString()))



    }


    private fun bindMonthArray() {
        monthModelArrayList = ArrayList<MonthModel>()

        /* val monthMode = MonthModel(0, "Select Month")
         monthModelArrayList.add(monthMode)*/

        val monthModel1 = MonthModel(1, getString(R.string.general_january))
        monthModelArrayList.add(monthModel1)

        val monthModel2 = MonthModel(2, getString(R.string.general_february))
        monthModelArrayList.add(monthModel2)

        val monthModel3 = MonthModel(3, getString(R.string.general_march))
        monthModelArrayList.add(monthModel3)

        val monthModel4 = MonthModel(4, getString(R.string.general_april))
        monthModelArrayList.add(monthModel4)

        val monthModel5 = MonthModel(5, getString(R.string.general_may))
        monthModelArrayList.add(monthModel5)

        val monthModel6 = MonthModel(6, getString(R.string.general_june))
        monthModelArrayList.add(monthModel6)

        val monthModel7 = MonthModel(7, getString(R.string.general_july))
        monthModelArrayList.add(monthModel7)

        val monthMode8 = MonthModel(8, getString(R.string.general_august))
        monthModelArrayList.add(monthMode8)

        val monthMode9 = MonthModel(9, getString(R.string.general_september))
        monthModelArrayList.add(monthMode9)

        val monthMode10 = MonthModel(10, getString(R.string.general_october))
        monthModelArrayList.add(monthMode10)

        val monthMode11 = MonthModel(11, getString(R.string.general_november))
        monthModelArrayList.add(monthMode11)

        val monthMode12 = MonthModel(12, getString(R.string.general_december))
        monthModelArrayList.add(monthMode12)


        val monthDataString: ArrayList<String> = ArrayList()


        monthDataString.clear()
        monthModelArrayList.forEach { data ->
            monthDataString.add(data.month.toString())
        }

        val arrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                monthDataString
        )
        binding.autoCompleteMonth.setAdapter(arrayAdapter)


    }

}