package com.app.ecarepro.ui.edit_profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentEditProfileBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.edit_profile.model.Profile
import com.app.ecarepro.ui.edit_profile.model.update_profile.PreviousSchoolDTL
import com.app.ecarepro.ui.edit_profile.model.update_profile.StudentProfile
import com.app.ecarepro.ui.edit_profile.model.update_profile.UpdateProfileModel
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.taskmanager.add.selectDate
import com.app.ecarepro.ui.taskmanager.add.selectDatePro
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : Fragment() {


    private lateinit var binding: FragmentEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()
    private var fatherDesignationID = 0
    private var fatherProfessionID = 0
    private var motherDesignationID = 0
    private var motherProfessionID = 0
    private var parentStausID = 0
    private var stuBloodGroupID = 0
    private var stuReligionID = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSave.setOnClickListener {
            updateProfile()
        }

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


                    else -> {}
                }
            }
        }

        viewModel.getUserProfileEdit(true)
binding.cbSameAddress.setOnCheckedChangeListener {
        _, isChecked ->
    if (isChecked){
        binding.textPAddress.text= binding.textAddress.text
        binding.textPCity.text= binding.textCity.text
        binding.textPState.text= binding.textState.text

    }
}

    }

    private fun setupView(profile: Profile) {

        binding.apply {

            tvSave.isVisible = true


            fatherDesignationID = profile.fatherDesignationID
            fatherProfessionID = profile.fatherProfessionID
            motherDesignationID = profile.motherDesignationID
            motherProfessionID = profile.motherProfessionID
            parentStausID = profile.parentStausID
            stuBloodGroupID = profile.stuBloodGroupID
            stuReligionID = profile.stuReligionID

            textUserName.isEnabled = false
            textName.isEnabled = false
            textStudentName.isEnabled = false
            textClass.isEnabled = false
            textDOB.isEnabled = false
            textAdmissionDate.isEnabled = false
            textFatherName.isEnabled = false
            textMotherName.isEnabled = false


            textAnniversaryDate.setOnClickListener {
                selectDatePro("Select Anniversary date") {
                    textAnniversaryDate.setText(it)
                }
            }
            textAdmissionDate.setOnClickListener {
                selectDatePro("Select Admission date") {
                    textAdmissionDate.setText(it)
                }
            }

            val adapterparentsStatus = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                profile.parentsStatusLST.map { it.status })
            binding.parentStatus.setAdapter(adapterparentsStatus)
            binding.parentStatus.setOnItemClickListener { _, _, position, _ ->
                parentStausID = profile.parentsStatusLST[position].id
            }

            val adapterReligion = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                profile.relegionLST.map { it.relegion })
            binding.religion.setAdapter(adapterReligion)
            binding.religion.setOnItemClickListener { _, _, position, _ ->
                stuReligionID = profile.relegionLST[position].id
            }

            val adapterFatherProfession =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                    profile.professionLST.map { it.profession })
            binding.fatherProfession.setAdapter(adapterFatherProfession)
            binding.fatherProfession.setOnItemClickListener { _, _, position, _ ->
                fatherProfessionID = profile.professionLST[position].id
            }

            val adapterFatherDesignation =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                    profile.designationLST.map { it.designation })
            binding.fatherDesignation.setAdapter(adapterFatherDesignation)
            binding.fatherDesignation.setOnItemClickListener { a, e, position, c ->
                fatherDesignationID = profile.designationLST[position].id
            }

            val adapterMotherDesignation =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                    profile.designationLST.map { it.designation })
            binding.MotherDesignation.setAdapter(adapterMotherDesignation)
            binding.MotherDesignation.setOnItemClickListener { _, _, position, _ ->
                motherDesignationID = profile.designationLST[position].id
            }

            val adapterMotherProfession =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                    profile.professionLST.map { it.profession })
            binding.MotherProfession.setAdapter(adapterMotherProfession)
            binding.MotherProfession.setOnItemClickListener { _, _, position, _ ->
                motherProfessionID = profile.professionLST[position].id
            }

            val adapterBloodGroup =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                    profile.bloodGroupLST.map { it.groupName })
            binding.bloodGroup.setAdapter(adapterBloodGroup)
            binding.bloodGroup.setOnItemClickListener { _, _, position, _ ->
                stuBloodGroupID = profile.bloodGroupLST[position].id
            }


            textUserName.setText(profile.username)
            textName.setText(profile.name)
            textMobile.setText(profile.studentProfile.contactMobile)
            textUserPOB.setText(profile.studentProfile.birthPlace)
            textUserEmail.setText(profile.studentProfile.contactEmailID)
            textAnniversaryDate.setText(profile.studentProfile.parentAnniversaryDate)
            parentStatus.setText(profile.studentProfile.parentStaus, false)

            //Child's Details

            textStudentEmail.setText(profile.studentProfile.studentEmail)
            textClass.setText(profile.studentProfile.className)
            textDOB.setText(profile.studentProfile.dob)
            textAdmissionDate.setText(profile.studentProfile.admissionDate)
            bloodGroup.setText(profile.studentProfile.bloodGroup, false)
            religion.setText(profile.studentProfile.religion, false)
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
            fatherProfession.setText(profile.studentProfile.fatherProfession, false)
            fatherDesignation.setText(profile.studentProfile.fatherDesignation, false)

            textFatherResidentialAddress.setText(profile.studentProfile.fatherResidentialAddress)
            textFatherOfficeAddress.setText(profile.studentProfile.fatherOfficeAddress)
            textFatherEmail1.setText(profile.studentProfile.fatherEmail_1)
            textFatherEmail2.setText(profile.studentProfile.fatherEmail_2)
            textFatherMobile1.setText(profile.studentProfile.fatherMob_1)
            textFatherMobile2.setText(profile.studentProfile.fatherMob_2)
            textFatherAnnualIncome.setText(profile.studentProfile.fatherAnnualIncome)
            textFatherAadharNumber.setText(profile.studentProfile.fatherAadhaarNumber)

            textMotherName.setText(profile.studentProfile.motherName)
            textMotherDOB.setText(profile.studentProfile.motherDOB)
            MotherProfession.setText(profile.studentProfile.motherProfession, false)
            MotherDesignation.setText(profile.studentProfile.motherDesignation, false)

            textMotherResidentialAddress.setText(profile.studentProfile.motherResidentialAddress)
            textMotherOfficeAddress.setText(profile.studentProfile.motherOfficeAddress)
            textMotherEmail1.setText(profile.studentProfile.motherEmail_1)
            textMotherEmail2.setText(profile.studentProfile.motherEmail_2)
            textMotherMobile1.setText(profile.studentProfile.motherMob_1)
            textMotherMobile2.setText(profile.studentProfile.motherMob_2)
            textMotherAnnualIncome.setText(profile.studentProfile.motherAnnualIncome)
            textMotherAadharNumber.setText(profile.studentProfile.motherAadhaarNumber)


            val adapterRecord = UpdateRecordListAdapter(profile.profileUpdationRecord)

            rvUpdateRecord.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = adapterRecord
            }

        }


    }

    fun updateProfile() {
        binding.apply {

            /*

            modelEditProfile.studentProfile.aadhaarNumber = textAadhaar.text.toString()

            modelEditProfile.studentProfile.birthPlace = textUserPOB.text.toString()

            modelEditProfile.studentProfile.parentAnniversaryDate =
                textAnniversaryDate.text.toString()


            modelEditProfile.studentProfile.city = textCity.text.toString()
            modelEditProfile.studentProfile.state = textState.text.toString()
            modelEditProfile.studentProfile.address = textAddress.text.toString()

            modelEditProfile.studentProfile.permanentCity = textPCity.text.toString()
            modelEditProfile.studentProfile.permanentState = textPState.text.toString()
            modelEditProfile.studentProfile.permanentAddress = textPAddress.text.toString()

            modelEditProfile.studentProfile.contactEmailID = textUserEmail.text.toString()
            modelEditProfile.studentProfile.contactMobile = textMobile.text.toString()



            modelEditProfile.studentProfile.permanentAddress = textPAddress.text.toString()
            modelEditProfile.studentProfile.permanentCity = textPCity.text.toString()

            modelEditProfile.studentProfile.previousSchoolDTL.address =
                textSchoolAddress.text.toString()

            modelEditProfile.studentProfile.previousSchoolDTL.board =
                textSchoolBoard.text.toString()
            modelEditProfile.studentProfile.previousSchoolDTL.schoolName =
                textSchoolName.text.toString()


            modelEditProfile.studentProfile.fatherEmail_1 = textFatherEmail1.text.toString()
            modelEditProfile.studentProfile.fatherEmail_2 = textFatherEmail2.text.toString()
            modelEditProfile.studentProfile.motherEmail_1 = textMotherEmail1.text.toString()
            modelEditProfile.studentProfile.motherEmail_2 = textMotherEmail2.text.toString()

            modelEditProfile.studentProfile.fatherMob_1 = textFatherMobile1.text.toString()
            modelEditProfile.studentProfile.fatherMob_1 = textFatherMobile2.text.toString()
            modelEditProfile.studentProfile.motherMob_1 = textMotherMobile2.text.toString()

            modelEditProfile.studentProfile.motherResidentialAddress =
                textMotherResidentialAddress.text.toString()
            modelEditProfile.studentProfile.motherOfficeAddress =
                textMotherOfficeAddress.text.toString()
            modelEditProfile.studentProfile.fatherResidentialAddress =
                textFatherResidentialAddress.text.toString()
            modelEditProfile.studentProfile.fatherOfficeAddress =
                textFatherOfficeAddress.text.toString()



            modelEditProfile.studentProfile.fatherDOB = textFatherDOB.text.toString()
            modelEditProfile.studentProfile.motherDOB = textMotherDOB.text.toString()
            modelEditProfile.studentProfile.fatherAnnualIncome =
                textFatherAnnualIncome.text.toString()
            modelEditProfile.studentProfile.motherAnnualIncome =
                textMotherAnnualIncome.text.toString()
            modelEditProfile.studentProfile.fatherAadhaarNumber =
                textFatherAadharNumber.text.toString()
            modelEditProfile.studentProfile.motherAadhaarNumber =
                textMotherAadharNumber.text.toString()*/

            /*modelEditProfile.fatherDesignationID = fatherDesignationID

            modelEditProfile.fatherProfessionID=  fatherProfessionID
            modelEditProfile.motherDesignationID=  motherDesignationID
            modelEditProfile.motherProfessionID=  motherProfessionID
            modelEditProfile.parentStausID=       parentStausID
            modelEditProfile.stuBloodGroupID=     stuBloodGroupID
            modelEditProfile.stuReligionID=       stuReligionID*/


            val modelEditProfile = UpdateProfileModel(
                fatherDesignationID,
                fatherProfessionID,
                motherDesignationID,
                motherProfessionID,
                parentStausID,
                stuBloodGroupID,
                stuReligionID,
                StudentProfile(
                    textAadhaar.text.toString(),
                    textAddress.text.toString(),
                    textUserPOB.text.toString(),
                    textCity.text.toString(),
                    textUserEmail.text.toString(),
                    textMobile.text.toString(),
                    textFatherAadharNumber.text.toString(),
                    textFatherAnnualIncome.text.toString(),
                    textFatherDOB.text.toString().ifEmpty { null },
                    textFatherEmail1.text.toString(),
                    textFatherEmail2.text.toString(),
                    textFatherMobile1.text.toString(),
                    textFatherMobile2.text.toString(),
                    textFatherOfficeAddress.text.toString(),
                    textFatherResidentialAddress.text.toString(),
                    textMotherAadharNumber.text.toString(),
                    textMotherAnnualIncome.text.toString(),
                    textMotherDOB.text.toString().ifEmpty { null },
                    textMotherEmail1.text.toString(),
                    textMotherEmail2.text.toString(),
                    textMotherMobile1.text.toString(),
                    textMotherMobile2.text.toString(),
                    textMotherOfficeAddress.text.toString(),
                    textMotherResidentialAddress.text.toString(),
                    textAnniversaryDate.text.toString().ifEmpty { null },
                    textPAddress.text.toString(),
                    textPCity.text.toString(),
                    textPState.text.toString(),
                    textState.text.toString(),
                    PreviousSchoolDTL(
                        textSchoolAddress.text.toString(),
                        textSchoolBoard.text.toString(),
                        textSchoolName.text.toString()
                    ),
                    textStudentEmail.text.toString()
                )
            )

            viewModel.updateParentProfile(modelEditProfile)

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
                              viewModel.getUserProfileEdit(true)
                          }
                      }


                      else -> {}
                  }
              }
         }


    }
}