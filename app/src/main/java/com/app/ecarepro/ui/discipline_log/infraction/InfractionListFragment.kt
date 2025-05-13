package com.app.ecarepro.ui.discipline_log.infraction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R

import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentInfractionListBinding
import com.app.ecarepro.model.RecentInfraction
import com.app.ecarepro.model.Staff
import com.app.ecarepro.model.StudentDTL
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.discipline_log.infraction.adapter.InfractionListAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class InfractionListFragment : Fragment(),ItemListener<RecentInfraction> {

    private var userID: Int? = null
    private var uType=0
    private lateinit var binding: FragmentInfractionListBinding
    private  val infractionListViewModel: InfractionListViewModel by viewModels()
    private val sharedViewModel: ShareViewModelDiscipline by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentInfractionListBinding.inflate(inflater,container,false)
        try {
            userID=  requireArguments().getInt(Constant.USER_ID)
            uType=  requireArguments().getInt(Constant.USER_TYPE)

        }catch (e:Exception){}

        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.infractions)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            infractionListViewModel.addInfractionStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.recyclerInfractionList.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerInfractionList.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerInfractionList.isVisible = true

                        if (it.data!=null){

                            if (uType==Constant.STUDENT_TYPE||uType==Constant.PARENT_TYPE){
                                bindStudentDetails(it.data.studentDTL)
                            }else{
                                if (it.data.stafftDTL!=null){
                                    bindStaffDetails(it.data.stafftDTL)
                                }else{
                                    binding.recyclerInfractionList.isVisible=false
                                    binding.tvNoData.isVisible=true
                                }
                            }

                            if (it.data.records!=null){
                                binding.recyclerInfractionList.isVisible=true
                                binding.tvNoData.isVisible=false

                                val circularAdapter = InfractionListAdapter(
                                    it.data.records,
                                    this@InfractionListFragment,
                                    uType
                                )

                                binding.recyclerInfractionList.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = circularAdapter
                                }
                            }else{
                                binding.recyclerInfractionList.isVisible=false
                                 binding.tvNoData.isVisible=true
                            }

                        }

                    }


                }


            }

        }

        if (uType==Constant.STUDENT_TYPE||uType==Constant.PARENT_TYPE){
            infractionListViewModel.getInfractions(userID)
        }else{
            infractionListViewModel.getStaffInfractions(userID)
        }

    }

    private fun bindStaffDetails(staffDTL: Staff) {

        binding.tvStudentName.text= buildString {
            append(staffDTL.name)
        }

        Picasso.get().
        load(staffDTL.photo)
            .placeholder(R.drawable.default_profile)
            .  into(binding.circleImageViewProfile)

        binding.tvAdmissionNo.text= buildString {
            append(getString(R.string.designation_bold))
            append(" ")
            append(staffDTL.designation)
        }
        binding.tvClassName.text= buildString {
            append(getString(R.string.mobile_pun_bold))
            append(" ")
            append(staffDTL.mobile)
        }

        binding.tvFatherName.text= buildString {
            append(getString(R.string.doj_bold))
            append(" ")
            append(staffDTL.doj)
        }

        binding.tvContact.text= buildString {
            append(getString(R.string.email_id_pun_bold))
            append(" ")
            append(staffDTL.emailID)
        }
        binding.tvGender.text= buildString {
            append(getString(R.string.gender_pun_bold))
            append(" ")
            append(staffDTL.gender)
        }


    }

    private fun bindStudentDetails(studentDTL: StudentDTL) {

        Picasso.get().
        load(studentDTL.photo)
            .placeholder(R.drawable.default_profile)
            .  into(binding.circleImageViewProfile)

        binding.tvStudentName.text= buildString {
            append(studentDTL.name)
        }

        binding.tvAdmissionNo.text= buildString {
            append(getString(R.string.admission_no))
            append(studentDTL.admissionNo)
        }
        binding.tvClassName.text= buildString {
            append(getString(R.string.classes))
            append(studentDTL.`class`)
        }
        binding.tvFatherName.text= buildString {
            append(getString(R.string.contact_person))
            append(studentDTL.contactPerson)
        }
        binding.tvContact.text= buildString {
            append(getString(R.string.contact_no))
            append(studentDTL.contactMob)
        }
    }

    override fun onItemClick(t: RecentInfraction, pos: Int, boolean: Boolean) {
        if (pos==1){
            infractionListViewModel.disciplineLogDeleteLog(t.id,1)
            lifecycleScope.launch {
                infractionListViewModel.deleteLogStateFlow.collectLatest {
                    when (it) {

                        is NetworkResult.Loading -> {
                            (requireActivity() as MainActivity).showLoader(true)
                        } is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)

                    } is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        if (it.data!=null){
                            it.data.message?.let { it1 -> mainActivity().showMessage(it1) }
                            if (uType==Constant.STUDENT_TYPE||uType==Constant.PARENT_TYPE){
                                infractionListViewModel.getInfractions(userID)
                            }else{
                                infractionListViewModel.getStaffInfractions(userID)
                            }
                        }

                    }
                    }
                }
            }
        } else if (pos==2){
            sharedViewModel.setRecentInfraction(t)
            findNavController().navigate(
                R.id.addComplianceFragment,
                Bundle().apply {
                    putInt(Constant.USER_TYPE, uType)
                })
            }
        }


}