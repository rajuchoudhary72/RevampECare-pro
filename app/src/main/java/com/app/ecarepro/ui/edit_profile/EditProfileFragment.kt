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
import com.app.ecarepro.ui.taskmanager.add.selectDatePro
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import android.widget.Button
import android.widget.TextView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkTransportEditProfile
import com.app.ecarepro.data.network.model.TransportVehicle
import com.app.ecarepro.ui.edit_profile.model.update_profile.UpdateTransportProfileModel
import com.app.ecarepro.ui.staff.FutureDateValidator
import com.app.ecarepro.ui.staff.getFormatedDate
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.text.Editable
import android.text.TextWatcher

import android.widget.EditText

@AndroidEntryPoint
class EditProfileFragment : Fragment() {
    private var isParentUserModified = false
    private var isTransportUserModified = false
    private var isScreenLoaded = false
    private var isUpdatingFromApi = false  // Flag to differentiate programmatic updates

    private lateinit var binding: FragmentEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()
    private var fatherDesignationID = 0
    private var fatherProfessionID = 0
    private var motherDesignationID = 0
    private var motherProfessionID = 0
    private var parentStausID = 0
    private var vehicleTypeID = 0
    private var stuBloodGroupID = 0
    private var stuReligionID = 0
    private var transportID = 0
    private var gouradHelper: Boolean = false
    private var helperSlectedName: String = ""
    private var dateVechileFrom: String = ""
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
        binding.btnSummit.setOnClickListener {
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
                            viewModel.getUserTransportProfile()
                            setupView(it.data.profile)
                        }
                    }


                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            viewModel.editTransportProfileStateFlow.collectLatest {
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
                            isUpdatingFromApi = true  // Set flag before updating UI
                            setupTransView(it.data)

                        }
                    }


                    else -> {}
                }
            }
        }
        viewModel.getUserProfileEdit(true)
        binding.cbSameAddress.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.textPAddress.text = binding.textAddress.text
                binding.textPCity.text = binding.textCity.text
                binding.textPState.text = binding.textState.text

            }
        }
        // Apply to all EditTexts inside the root layout
        addTextWatchers(binding.nestedScrollView)
        addTransportTextWatchers(binding.llTransportView)
        binding.btnSummit.alpha = 0.5f
    }

    private fun setupView(profile: Profile) {

        binding.apply {
            fatherDesignationID = profile.fatherDesignationID!!
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
                selectDatePro(getString(R.string.select_anniversary_date)) {
                    textAnniversaryDate.setText(it)
                }
            }
            textAdmissionDate.setOnClickListener {
                selectDatePro(getString(R.string.select_admission_date)) {
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
            textStudentName.setText(profile.studentProfile.name)
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

    private fun selectDate() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(FutureDateValidator())
                    .setOpenAt(MaterialDatePicker.todayInUtcMilliseconds())
                    .setStart(Calendar.getInstance().apply { add(Calendar.YEAR, -10) }.timeInMillis)
                    .setEnd(Calendar.getInstance().timeInMillis)
                    .build()
            )
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedTime: Long ->
            dateVechileFrom = getFormatedDate(Date(selectedTime))
            binding.btnSelectDate.setText(dateVechileFrom) // ✅ Correct way to set text
        }

        datePicker.show(childFragmentManager, "datePicker")
    }

    fun getFormatedDate(date: Date = Date()): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun setupTransView(profile: NetworkTransportEditProfile?) {
        if (profile == null) return
        binding.apply {
            binding.btnSelectDate.setOnClickListener {
                selectDate()
            }
            if (profile.transDetails != null) {
                binding.vechLL.isVisible = true
            }

            val adapterparentsStatus = profile.transVehicles.let {
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    it.map { it.vehicleType })
            }
            binding.vechicleTypes.setAdapter(adapterparentsStatus)
            binding.vechicleTypes.setOnItemClickListener { _, _, position, _ ->
                if (profile != null) {
                    vehicleTypeID = profile.transVehicles[position].vehicleTypeID
                }
            }
            binding.transType.setOnItemClickListener { _, _, position, _ ->
                transportID = position

                if (transportID != 0) {
                    if (transportID == 1) {
                        binding.vechLL.isVisible = false
                    } else {
                        binding.vechLL.isVisible = true
                    }
                } else {
                    binding.vechLL.isVisible = false
                }
            }

            val transDetails = profile.transDetails
            tvDriver.setText(transDetails?.driverName)
            driverMob.setText(transDetails?.driverMob)
            driverVehicleNo.setText(transDetails?.vehicleNumber)
            driverLicese.setText(transDetails?.driverDrivingLNo)
            driverAddress.setText(transDetails?.driverAdd)
            driverAadhar.setText(transDetails?.driverAadharNumber)
            driverClearanceCert.setText(transDetails?.driverClearanceNo)
            vehicleTypeID = transDetails?.vehicleTypeID!!


            if (transDetails?.vehicleUsingFrom != null) {
                dateVechileFrom = transDetails?.vehicleUsingFrom
                binding.btnSelectDate.setText(dateVechileFrom) // ✅ Correct way to set text
            } else {
                dateVechileFrom = getFormatedDate()
                binding.btnSelectDate.setText(dateVechileFrom) // ✅ Correct way to set text
            }

            /*guard  helper in  bus*/
            // Get the string array from resources
            val taskArray = resources.getStringArray(R.array.task3)
            // Create an ArrayAdapter
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                taskArray
            )

            // Set the adapter to AutoCompleteTextView
            availablityHelper.setAdapter(adapter)

            // Optional: Set default selection

            if (transDetails?.isLadyGuardAvailabile == true) {
                gouradHelper = true
                availablityHelper.setText(taskArray[1], false)
            } else {
                gouradHelper = false
                availablityHelper.setText(taskArray[2], false)
            }
            // Handle gourd  helper item click
            availablityHelper.setOnItemClickListener { parent, _, position, _ ->
                helperSlectedName = parent.getItemAtPosition(position).toString()
                if (helperSlectedName.equals("Yes")) {
                    gouradHelper = true
                } else {
                    gouradHelper = false
                }
            }
            /*transport Type  ==*/
            // Get the string array from resources
            val TransportArray = resources.getStringArray(R.array.task2)
            // Create an ArrayAdapter
            val transporAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                TransportArray
            )

            // Set the adapter to AutoCompleteTextView
            transType.setAdapter(transporAdapter)

            // Optional: Set default selection

            if (profile.transportType == 1) {
                transType.setText(TransportArray[1], false)
            } else if (profile.transportType == 2) {
                transType.setText(TransportArray[2], false)
            } else if (profile.transportType == 3) {
                transType.setText(TransportArray[3], false)
            } else if (profile.transportType == 4) {
                transType.setText(TransportArray[4], false)
            }
        }

        // Filter for vehicleTypeID
        val selectedVehicle =
            profile.transDetails?.let {
                profile.transVehicles.let { it1 ->
                    it.vehicleTypeID.let { it2 ->
                        filterVehicleById(
                            it1,
                            it2
                        )
                    }
                }
            }

        // Set the filtered value into AutoCompleteTextView
        selectedVehicle?.let {
            binding.vechicleTypes.setText(it, false)
        }
        isUpdatingFromApi = false  // Reset flag after UI update

        isScreenLoaded = true  // Now we start tracking user changes

    }

    fun filterVehicleById(vehicles: List<TransportVehicle>, targetId: Int): String? {
        return vehicles.find { it.vehicleTypeID == targetId }?.vehicleType
    }


    fun updateProfile() {
        if (isTransportUserModified){
            binding.apply {
                val modelEditProfile = UpdateTransportProfileModel(
                    transportID,
                    vehicleTypeID,
                    driverVehicleNo.text.toString(),
                    tvDriver.text.toString(),
                    driverMob.text.toString(),
                    driverAddress.text.toString(),
                    driverAadhar.text.toString(),
                    driverLicese.text.toString(),
                    driverClearanceCert.text.toString(),
                    dateVechileFrom,
                    gouradHelper
                )
                viewModel.updateTransportProfile(modelEditProfile)

            }
            lifecycleScope.launch {
                viewModel.updateTransportProfileStateFlow.collectLatest {
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
                                isTransportUserModified=false
                                isUpdatingFromApi=false
                                binding.btnSummit.alpha = 0.5f
                               // viewModel.getUserProfileEdit(true)
                            }
                        }
                    }
                }
            }
        }
        if (isParentUserModified){
            binding.apply {
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
                               // viewModel.getUserProfileEdit(true)
                                isParentUserModified=false
                                isUpdatingFromApi=false
                                binding.btnSummit.alpha = 0.5f
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addTextWatchers(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)

            if (view is EditText) {
                view.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (isScreenLoaded && !isUpdatingFromApi) {
                            binding.btnSummit.alpha = 1.0f
                            isParentUserModified = true  // Set flag only if user changes text manually
                            Log.d("TextChange", "User modified data: $s")
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
            } else if (view is ViewGroup) {
                addTextWatchers(view)  // Recursively check all child views
            }
        }
    }
    private fun addTransportTextWatchers(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)

            if (view is EditText) {
                view.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (isScreenLoaded && !isUpdatingFromApi) {
                            binding.btnSummit.alpha = 1.0f
                            isTransportUserModified = true  // Set flag only if user changes text manually
                            Log.d("TextChange", "Transport modified data: $s")
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
            } else if (view is ViewGroup) {
                addTransportTextWatchers(view)  // Recursively check all child views
            }
        }

    }
}