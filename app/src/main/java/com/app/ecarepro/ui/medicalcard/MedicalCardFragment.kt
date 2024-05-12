package com.app.ecarepro.ui.medicalcard

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
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentMedicalCardBinding
import com.app.ecarepro.model.Dtl
import com.app.ecarepro.model.UpdateMedicalCardRequest
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MedicalCardFragment : Fragment(), ItemListener<Dtl> {

    private lateinit var binding: FragmentMedicalCardBinding
    private val mViewModel: MedicineCardViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMedicalCardBinding.inflate(inflater, container, false)
        readOnlyData()
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.edit.setOnClickListener {
            editModeData()
            binding.edit.visibility = View.GONE
            binding.btnSubmit.visibility = View.VISIBLE
        }
        binding.btnSubmit.setOnClickListener {
            var isYesNo = false
            if (binding.rb1.isChecked)
                isYesNo = true
            val medicalCardModel = UpdateMedicalCardRequest(
                drugAllergy = binding.etDrugName.text.toString(),
                emrAddress = binding.etAddress.text.toString(),
                emrContact = binding.etMobNo.text.toString(),
                emrName = binding.etName.text.toString(),
                isPhysicallyFitForGame = isYesNo,
                sufferingFrom = binding.etSufferingFrom.text.toString()
            )
            mViewModel.medicineCardUpdate(medicalCardModel)
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            mViewModel.medicineCard.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        // binding.rvMedicineIssue.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        it.data?.let { medicalData ->
                            medicalData.medicalCard?.let { medicalCard ->
                                binding.etSufferingFrom.setText(
                                    medicalCard.sufferingFrom
                                )
                                binding.etName.setText(medicalCard.emrName)
                                binding.etAddress.setText(
                                    medicalCard.emrAddress
                                )
                                binding.etMobNo.setText(
                                    medicalCard.emrContact
                                )
                                binding.etDrugName.setText(
                                    medicalCard.drugAllergy
                                )
                                if (medicalCard.isPhysicallyFitForGame) binding.rgYesNo.check(R.id.rb1) else binding.rgYesNo.check(
                                    R.id.rb2
                                )
                            }

                        }


                    }
                }
            }

        }
        lifecycleScope.launch {
            mViewModel._updateMedicalCardStateFlow.collectLatest {
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

                        Toast.makeText(requireContext(), "Updated successfully", Toast.LENGTH_SHORT).show()


                    }
                }
            }

        }
        mViewModel.medicineCard()

    }

    override fun onItemClick(t: Dtl, pos: Int, boolean: Boolean) {

    }

    private fun readOnlyData() = with(binding) {
        etName.setFocusable(false)
        etName.setClickable(false)
        etMobNo.setFocusable(false)
        etMobNo.setClickable(false)
        etSufferingFrom.setFocusable(false)
        etSufferingFrom.setClickable(false)
        etAddress.setFocusable(false)
        etAddress.setClickable(false)
        etDrugName.setFocusable(false)
        etDrugName.setClickable(false)
        for (i in 0 until rgYesNo.getChildCount()) {
            rgYesNo.getChildAt(i).setClickable(false)
        }

        /*rgYesNo.setEnabled(false);
        rgYesNo.setFocusable(false);
        rgYesNo.setClickable(false);*/
    }

    private fun editModeData() = with(binding) {
        etName.setFocusable(true)
        etName.setFocusableInTouchMode(true)
        etName.setClickable(true)
        etMobNo.setFocusable(true)
        etMobNo.setFocusableInTouchMode(true)
        etMobNo.setClickable(true)
        etSufferingFrom.setFocusable(true)
        etSufferingFrom.setFocusableInTouchMode(true)
        etSufferingFrom.setClickable(true)
        etAddress.setFocusable(true)
        etAddress.setFocusableInTouchMode(true)
        etAddress.setClickable(true)
        etDrugName.setFocusable(true)
        etDrugName.setFocusableInTouchMode(true)
        etDrugName.setClickable(true)
        for (i in 0 until rgYesNo.getChildCount()) {
            rgYesNo.getChildAt(i).setClickable(true)
        }
    }
}