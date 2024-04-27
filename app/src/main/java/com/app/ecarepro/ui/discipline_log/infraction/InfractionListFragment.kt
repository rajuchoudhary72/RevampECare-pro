package com.app.ecarepro.ui.discipline_log.infraction

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
import com.app.ecarepro.databinding.FragmentInfractionListBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.discipline_log.infraction.adapter.InfractionListAdapter
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class InfractionListFragment : Fragment() {

    private var studentID: Int = 0
    private lateinit var binding: FragmentInfractionListBinding
    private  val infractionListViewModel: InfractionListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentInfractionListBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            studentID=  requireArguments().getInt(Constant.STUDENT_ID_ARGUMENT)

        }catch (_:Exception){}
         if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.apply {
                title = " Infractions"

                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
        }
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

                                val circularAdapter = InfractionListAdapter(
                                    it.data.records,
                                    this@InfractionListFragment
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
        infractionListViewModel.getInfractions(studentID)


    }
}