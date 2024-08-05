package com.app.ecarepro.ui.edit_profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentEditProfileBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.edit_profile.model.Profile
import com.app.ecarepro.ui.taskmanager.add.selectDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private lateinit var  binding: FragmentEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()
    private val religionList = listOf("Hindu","Muslim","Sikh","Christian")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentEditProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            viewModel.editProfileStateFlow.collectLatest {
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
                        if (it.data != null) {
                            setupView(it.data.profile)
                        }
                    }

                }
            }
        }

        viewModel.getUserProfileEdit(true)



    }

    private fun setupView(profile: Profile) {

        binding.apply {

            textAnniversaryDate.setOnClickListener {
                selectDate("Select start date") {
                    textAnniversaryDate.setText(it)
                }
            }

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                profile.studentProfile.parentStaus.split("status"))
            binding.parentStatus.setAdapter(adapter)

            binding.parentStatus.setOnItemClickListener { _, _, position, _ ->

            }

            val adapterreligion = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                religionList)
            binding.religion.setAdapter(adapterreligion)

            binding.religion.setOnItemClickListener { _, _, position, _ ->

            }

            textUserName.setText(profile.username)
            textName.setText(profile.name)
            textMobile.setText(profile.studentProfile.contactMobile)
            textUserPOB.setText(profile.studentProfile.birthPlace)
            textUserEmail.setText(profile.studentProfile.contactEmailID)
            textAnniversaryDate.setText(profile.studentProfile.parentAnniversaryDate)
            parentStatus.setText(profile.studentProfile.parentStaus,false)

            //Child's Details

            textStudentName.setText(profile.studentProfile.name)
            textClass.setText(profile.studentProfile.className)
            textDOB.setText(profile.studentProfile.dob)
            textAdmissionDate.setText(profile.studentProfile.admissionDate)
            bloodGroup.setText(profile.studentProfile.bloodGroup,false)
            religion.setText(profile.studentProfile.religion,false)
            textAadhaar.setText(profile.studentProfile.aadhaarNumber)

            textAddress.setText(profile.studentProfile.address)
            textCity.setText(profile.studentProfile.city)
            textState.setText(profile.studentProfile.state)


            textPAddress.setText(profile.studentProfile.permanentAddress)
            textPCity.setText(profile.studentProfile.permanentCity)
            textPState.setText(profile.studentProfile.permanentState)

            textSchoolName.setText(profile.studentProfile.previousSchoolDTL.schoolName)
            textSchoolAddress.setText(profile.studentProfile.previousSchoolDTL.address)
            textSchoolBoard.setText(profile.studentProfile.previousSchoolDTL.board)

            textFatherName.setText(profile.studentProfile.fatherName)
            textFatherDOB.setText(profile.studentProfile.fatherDOB)
            fatherProfession.setText(profile.studentProfile.fatherProfession,false)
            fatherDesignation.setText(profile.studentProfile.fatherDesignation,false)

            textFatherResidentialAddress.setText(profile.studentProfile.fatherResidentialAddress)
            textFatherOfficeAddress.setText(profile.studentProfile.fatherOfficeAddress)
            textFatherEmail1.setText(profile.studentProfile.fatherEmail_1)
            textFatherEmail2.setText(profile.studentProfile.fatherEmail_2)
            textFatherMobile1.setText(profile.studentProfile.fatherMob_1)
            textFatherMobile2.setText(profile.studentProfile.fatherMob_2)
            textFatherAnnualIncome.setText(profile.studentProfile.fatherAnnualIncome)
            textFatherAadharNumber.setText(profile.studentProfile.fatherAadhaarNumber)










        }

    }
}