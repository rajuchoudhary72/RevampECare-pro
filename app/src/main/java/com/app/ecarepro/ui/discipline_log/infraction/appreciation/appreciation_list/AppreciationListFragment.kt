package com.app.ecarepro.ui.discipline_log.infraction.appreciation.appreciation_list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R

import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentAppreciationListBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.discipline_log.infraction.appreciation.adapter.AppreciationListAdapter
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AppreciationListFragment : Fragment() {

    private var studentID: Int = 0
    private lateinit var binding: FragmentAppreciationListBinding
    private  val appreciationListViewModel: AppreciationListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentAppreciationListBinding.inflate(inflater,container,false)
        try {
            studentID=  requireArguments().getInt(Constant.STUDENT_ID_ARGUMENT)

        }catch (e: Exception){}
         binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            appreciationListViewModel.addAppreciationStateFlow.collectLatest {
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

                            binding.studentData=it.data.studentDTL

                            binding.tvAdmissionNo.text= buildString {
                                append(getString(R.string.admission_no))
                                append(it.data.studentDTL.admissionNo)
                            }
                            binding.tvClassName.text= buildString {
                                append(getString(R.string.classes))
                                append(it.data.studentDTL.`class`)
                            }
                            binding.tvFatherName.text= buildString {
                                append(getString(R.string.contact_person))
                                append(it.data.studentDTL.contactPerson)
                            }
                            binding.tvContact.text= buildString {
                                append(getString(R.string.contact_no))
                                append(it.data.studentDTL.contactMob)
                            }

                            if (it.data.records!=null){
                                binding.recyclerInfractionList.isVisible=true
                                binding.tvNoData.isVisible=false

                                val appreciationListAdapter = AppreciationListAdapter(
                                    it.data.records,
                                    this@AppreciationListFragment
                                )

                                binding.recyclerInfractionList.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = appreciationListAdapter
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
        appreciationListViewModel.getAppreciations(studentID)


    }
}