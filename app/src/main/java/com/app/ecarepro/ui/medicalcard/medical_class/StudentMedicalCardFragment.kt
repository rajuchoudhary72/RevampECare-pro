package com.app.ecarepro.ui.medicalcard.medical_class

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.StudentMedicalCardBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.medicalcard.MedicineCardViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


@AndroidEntryPoint
class StudentMedicalCardFragment : Fragment() {
    private lateinit var binding: StudentMedicalCardBinding
    private val mViewModel: MedicineCardViewModel by viewModels()


    val bundle by lazy {
        arguments
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
        } catch (e: IllegalStateException) {

        }
        binding = StudentMedicalCardBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.printView.setOnClickListener {

            findNavController().navigate(R.id.studentMedicalReportFragment, bundle)


        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            mViewModel._studentMedicalCardResponse.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        // binding.rvMedicineIssue.isVisible = false
                        Log.d("main", "Error$it")
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        it.data?.let { student ->
                            student.profile?.let { it1 ->
                                student.immunizationRecords?.let { it2 ->
                                    bindElement(
                                        it1,
                                        it2
                                    )
                                }
                            }
                        }

                    }
                }
            }

        }
        bundle?.let {
            val id = it.getString("ID")
            if (id != null) {
                mViewModel.getMedicalCard(id)
            }
        }


    }

    private val DEFAULT_TEXT = ""
    private fun bindElement(details: Profile, immunizationRecords: ImmunizationRecords) {

        Picasso.get()
            .load(details.coverImg)
            .error(R.drawable.shape_rect_trans)
            .placeholder(R.drawable.shape_rect_trans)
            .into(binding.ivBannerImage)
        getIcNoProfileBig(requireContext())?.let {
            Picasso.get()
                .load(details.photo)
                .placeholder(it)
                .error(it)
                .into(binding.ivUserImage)
        }



        binding.tvStudentName.text = details.name ?: DEFAULT_TEXT
        binding.tvClass.text = details.className ?: DEFAULT_TEXT
        binding.tvContactNo.text = details.contactMobile ?: DEFAULT_TEXT
        binding.tvAdmissionNo.text = details.fatherName ?: DEFAULT_TEXT
        binding.tvRollNo.text = details.bloodGroup ?: DEFAULT_TEXT
        binding.tvDateOfAdmission.text = details.admissionDate ?: DEFAULT_TEXT
        binding.tvStudentDob.text = details.dob ?: DEFAULT_TEXT
        binding.tvMotherName.text = details.motherName ?: DEFAULT_TEXT
        binding.tvMohterContact.text = details.contactMobile ?: DEFAULT_TEXT
        binding.resAddress.text = details.address ?: DEFAULT_TEXT
        binding.tvEmerContact.text = details.contactMobile ?: DEFAULT_TEXT

        // Bind immunization details
        binding.tbBgp.text = immunizationRecords.bcg ?: DEFAULT_TEXT
        binding.tvDiphtheria.text = immunizationRecords.diphtheria ?: DEFAULT_TEXT
        binding.tvWhoopingCough.text = immunizationRecords.whoopingCough ?: DEFAULT_TEXT
        binding.tvTetanus.text = immunizationRecords.tetanus ?: DEFAULT_TEXT
        binding.tvMeasles.text = immunizationRecords.measles ?: DEFAULT_TEXT
        binding.tvMMR.text = immunizationRecords.mmr ?: DEFAULT_TEXT
        binding.tvHepatitisB.text = immunizationRecords.hepatitisB ?: DEFAULT_TEXT
        binding.tvTyphoid.text = immunizationRecords.typhoid ?: DEFAULT_TEXT
        binding.tvChickenPox.text = immunizationRecords.chicken_pox ?: DEFAULT_TEXT
        binding.tvHecatitisA.text = immunizationRecords.hepatitisA ?: DEFAULT_TEXT
        binding.tvCovidDose1.text = immunizationRecords.covidDose1 ?: DEFAULT_TEXT
        binding.tvAllergeis.text = immunizationRecords.allergies ?: DEFAULT_TEXT
        binding.tvCovidDose2.text = immunizationRecords.covidDose2 ?: DEFAULT_TEXT
        binding.tvCovidBoosterDose.text = immunizationRecords.covidBoosterDose ?: DEFAULT_TEXT
        binding.tvOther.text = immunizationRecords.dptBooster ?: DEFAULT_TEXT
        binding.tvSpecPast.text = immunizationRecords.specificPastDisease ?: DEFAULT_TEXT
        binding.tvSurgeryPast.text = immunizationRecords.surgeryUndergoneInthePast ?: DEFAULT_TEXT
        binding.tvOther.text = immunizationRecords.childRegularMedication ?: DEFAULT_TEXT
    }

    fun getIcNoProfileBig(context: Context): VectorDrawableCompat? {
        return VectorDrawableCompat.create(context.resources, R.drawable.ic_no_profile_big, null)
    }

    fun date_converter2(s: String?): String? {
        val D_PATTERN_SPLIT_BY_T = "yyyy-MM-dd'T'HH:mm:ss"
        val PATTERN_DAY_SMALL = "dd"
        val PATTERN_MONTH_THREE_LETTER = "MMM"
        val PATTERN_YEAR_FOUR_DIGIT = "yyyy"
        val isoFormat =
            SimpleDateFormat(D_PATTERN_SPLIT_BY_T)
        isoFormat.timeZone = TimeZone.getDefault()
        val isoFormatDate =
            SimpleDateFormat(PATTERN_DAY_SMALL)
        val isoFormatMonth =
            SimpleDateFormat(PATTERN_MONTH_THREE_LETTER)
        val isoFormatYear =
            SimpleDateFormat(PATTERN_YEAR_FOUR_DIGIT)
        var date: Date? = null
        try {
            date = isoFormat.parse(s)
            val a = date.time
            val b = -1577943000000L
            if (a < b) {
                return "N/A"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return isoFormatDate.format(date) + " " + isoFormatMonth.format(date) + ", " + isoFormatYear.format(
            date
        )
    }
}