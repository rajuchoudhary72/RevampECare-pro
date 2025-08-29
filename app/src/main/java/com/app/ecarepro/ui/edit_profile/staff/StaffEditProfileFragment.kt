package com.app.ecarepro.ui.edit_profile.staff

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.edit_profile.model.update_profile.PreviousSchoolDTL
import com.app.ecarepro.ui.edit_profile.model.update_profile.StudentProfile
import com.app.ecarepro.ui.edit_profile.model.update_profile.UpdateProfileModel
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.taskmanager.add.selectDatePro
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.app.ecarepro.R
import com.app.ecarepro.ui.staff.FutureDateValidator
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import com.app.ecarepro.databinding.FragmentEditStaffProfileBinding
import com.app.ecarepro.ui.edit_profile.UpdateRecordListAdapter
import com.app.ecarepro.ui.edit_profile.staff.model.MaritialStatus
import com.app.ecarepro.ui.edit_profile.staff.model.payload.StaffUpdateModel

@AndroidEntryPoint
class StaffEditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditStaffProfileBinding
    private val viewModel: StaffEditProfileViewModel by viewModels()
    private var titleID = 0
    private var maritalStatusID = 0
    private var relationshipStatusID = 0
    private var nationalityID = 0
    private var stuBloodGroupID = 0
    private var stuReligionID = 0
    var martialStatus = listOf<MaritialStatus>()
    var relationshipStatus = listOf<MaritialStatus>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditStaffProfileBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSummit.setOnClickListener {
            updateProfile()
        }

          martialStatus = listOf(MaritialStatus(getString(R.string.married), 0), MaritialStatus(
            getString(
                R.string.unmarried
            ), 1),MaritialStatus(getString(R.string.other), 2))

          relationshipStatus = listOf(MaritialStatus(getString(R.string.father), 0), MaritialStatus(
            getString(
                R.string.spouse
            ), 1))

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
                            it.data.profile?.let { it1 -> setupView(it1) }
                        }
                    }


                    else -> {}
                }
            }
        }

        viewModel.getUserProfileEdit(true)


    }

    private fun setupView(profile: com.app.ecarepro.ui.edit_profile.staff.model.Profile) {

        binding.apply {
            relationshipStatusID = profile.relationshipWithMemberId ?: 0
            nationalityID = profile.nationalityID ?: 0
            titleID = profile.titleID ?: 0
            maritalStatusID= profile.maritialStatusID ?: 0
            stuBloodGroupID = profile.bloodGroupID ?: 0
            stuReligionID = profile.relegionID ?: 0

            textUserName.isEnabled = false
            textRoleName.isEnabled = false
            textDesignation.isEnabled = false



            textDateofBirth.setOnClickListener {
                selectDatePro(getString(R.string.select_anniversary_date)) {
                    textDateofBirth.setText(it)
                }
            }
            textDateOfJoining.setOnClickListener {
                selectDatePro(getString(R.string.select_admission_date)) {
                    textDateOfJoining.setText(it)
                }
            }
            textDateOfAnniversary.setOnClickListener {
                selectDatePro(getString(R.string.select_admission_date)) {
                    textDateOfAnniversary.setText(it)
                }
            }

            profile.titles?.let {
                val adapterparentsStatus = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        profile.titles.map { it.text})

                binding.title.setAdapter(adapterparentsStatus)
                binding.title.setOnItemClickListener { _, _, position, _ ->
                    titleID = profile.titles[position].value ?: 0
                }
            }

            profile.relegionLST?.let {
                val adapterReligion = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    profile.relegionLST.map { it.relegion })
                binding.religion.setAdapter(adapterReligion)
                binding.religion.setOnItemClickListener { _, _, position, _ ->
                    stuReligionID = profile.relegionLST[position].id?:0
                }
            }



            val adapterFatherProfession =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                    martialStatus.map{ it.name })
            binding.maritalStatus.setAdapter(adapterFatherProfession)
            binding.maritalStatus.setOnItemClickListener { _, _, position, _ ->
                maritalStatusID = martialStatus[position].value ?:0
            }

            val adapterrelationshipStatus =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                    relationshipStatus.map{ it.name })
            binding.fatherSpouseRelation.setAdapter(adapterrelationshipStatus)
            binding.fatherSpouseRelation.setOnItemClickListener { _, _, position, _ ->
                relationshipStatusID = relationshipStatus[position].value ?:0
            }


            profile.nationalityLST?.let {
                val adapterNationality =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                        profile.nationalityLST.map { it.nationality })
                binding.nationality.setAdapter(adapterNationality)
                binding.nationality.setOnItemClickListener { a, e, position, c ->
                    nationalityID = profile.nationalityLST[position].id ?: 0
                }
            }

            profile.bloodGroupLST?.let {
                val adapterBloodGroup =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                        profile.bloodGroupLST.map { it.groupName })
                binding.bloodGroup.setAdapter(adapterBloodGroup)
                binding.bloodGroup.setOnItemClickListener { _, _, position, _ ->
                    stuBloodGroupID = profile.bloodGroupLST[position].id ?: 0
                }
            }





            textUserName.setText(profile.username)
            textRoleName.setText(profile.roleName)
            title.setText(profile.title,false)
            textFirstName.setText(profile.fName)
            textMiddleName.setText(profile.mName)
            textLastName.setText(profile.lName)
            textDateofBirth.setText(profile.dob)
            textDateOfJoining.setText(profile.doj)
            textDesignation.setText(profile.designation)
            bloodGroup.setText(profile.bloodGroup, false)
            textUserQualification.setText(profile.qualification)
            textDateOfAnniversary.setText(profile.doAnniversary)
            textAadharNumber.setText(profile.aadharCardNo)
            textUserUAN.setText(profile.uaN_Number)
            textNationalTeacherID.setText(profile.nationalCode)
            textSateTeacherID.setText(profile.stateCode)
            textPAN.setText(profile.paN_Number)
            textCBSEID.setText(profile.cbseid)
            maritalStatus.setText(profile.maritalStatus,false)
            fatherSpouseRelation.setText(if (relationshipStatusID == 0) getString(R.string.father) else getString(R.string.spouse), false)
            textFatherSpouseName.setText(profile.fatherHusbandName)
            textFatherSpouseMobile.setText(profile.fatherHusbandMob)
            textMobile.setText(profile.mobile)
            textAlternateMobile.setText(profile.alternateMobile)
            textEmergencyContactNo.setText(profile.emergencyContactNo)
            nationality.setText(profile.nationality, false)
            religion.setText(profile.religion, false)
            textAddress.setText(profile.address)
            textPermanentAddress.setText(profile.p_Address)


            val adapterRecord = StaffUpdateRecordListAdapter(profile.profileUpdationRecord)

            rvUpdateRecord.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = adapterRecord
            }

        }


    }


    fun updateProfile() {
            binding.apply {
                val modelEditProfile = StaffUpdateModel(
                    address = textAddress.text.toString(),
                    alternateEmailID = textAlternateEmailAddress.text.toString(),
                    alternateMobile = textAlternateMobile.text.toString(),
                    bloodGroupID = stuBloodGroupID,
                    cbseid = textCBSEID.text.toString(),
                    doAnniversary = textDateOfAnniversary.text.toString(),
                    dob = textDateofBirth.text.toString(),
                    doj = textDateOfJoining.text.toString(),
                    emailID = textEmailAddress.text.toString(),
                    emergencyContactNo = textEmergencyContactNo.text.toString(),
                    fName = textFirstName.text.toString(),
                    fatherHusbandMob = textFatherSpouseMobile.text.toString(),
                    fatherHusbandName = textFatherSpouseName.text.toString(),
                    isMaritialStatusID = true,
                    isNationalityID = true,
                    isRelationshipWithMemberId = true,
                    isRelegionID = true,
                    isTitleID = true,
                    lName = textLastName.text.toString(),
                    mName = textMiddleName.text.toString(),
                    maritialStatusID = maritalStatusID,
                    nationalCode = textNationalTeacherID.text.toString(),
                    nationalityID = nationalityID,
                    p_Address = textPermanentAddress.text.toString(),
                    paN_Number = textPAN.text.toString(),
                    qualification = textUserQualification.text.toString(),
                    relationshipWithMemberId = relationshipStatusID,
                    relegionID = stuReligionID,
                    stateCode = textSateTeacherID.text.toString(),
                    titleID = titleID,
                    uaN_Number = textUserUAN.text.toString()


                )
                viewModel.updateStaffProfile(modelEditProfile)
            }
            lifecycleScope.launch {
                viewModel.updateParentProfileStateFlow.collectLatest {
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
                                it.data.message?.let { it1 -> mainActivity().showMessage(it1) }
                               // viewModel.getUserProfileEdit(true)
                                viewModel.getUserProfileEdit(true)
                                binding.btnSummit.isEnabled=false
                            }
                        }
                    }
                }
            }

    }


}