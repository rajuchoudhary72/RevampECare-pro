package com.app.ecarepro.ui.medicine_issue

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentMedicineIssueBinding
import com.app.ecarepro.model.Dtl
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.listener.ItemListener
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MedicineIssuedFragment : Fragment() , ItemListener<Dtl> {

    private lateinit var binding: FragmentMedicineIssueBinding
    private val mViewModel: MedicineIssueViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            Picasso.setSingletonInstance(
                Picasso.Builder(requireActivity()) // additional settings
                    .build()
            )
        }catch (_:IllegalStateException){

        }

        binding = FragmentMedicineIssueBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            mViewModel.leaveHistoryStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvMedicineIssue.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvMedicineIssue.isVisible = true

                        if (it.data != null) {
                            setHeaderData(it.data)
                            binding.rvMedicineIssue.isVisible = true
                            binding.tvNoData.isVisible = false

                            val leaveHistoryAdapter = MedicineIssueAdapter(
                                it.data.medicineIssued
                            )

                            binding.rvMedicineIssue.apply {
                                setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(activity)
                                adapter = leaveHistoryAdapter
                            }


                        }


                    }
                }
            }

        }

        mViewModel.medicineIssue()

    }

    @SuppressLint("SetTextI18n")
    private fun setHeaderData(data: MedicineIsuueModel)= with(binding){
        tvStudentName.text = data.name
        data.designation?.let {
            tvAdmissionNo.text = getString(R.string.general_designation_pun)+" $it"
            tvClassName.text = ""
            linPro.visibility = View.VISIBLE
        }?:run {
            data.admissionNo?.let {
                tvAdmissionNo.text = getString(R.string.admission_no)+" $it"
            }
            data.className?.let {
                tvClassName.text = getString(R.string.general_classes_pun)+" $it"
            }
            linPro.visibility = View.VISIBLE
        }


        Picasso.get().load(
            data.photo
        ) //.placeholder(getIcNoProfileBig(context))
            //.error(getIcNoProfileBig(context))
            //.memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)
            .into(circleImageViewProfile)
    }

    override fun onItemClick(t: Dtl, pos: Int, boolean: Boolean) {
    }
}