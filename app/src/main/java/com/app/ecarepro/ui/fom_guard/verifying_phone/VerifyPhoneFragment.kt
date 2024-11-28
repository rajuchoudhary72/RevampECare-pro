package com.app.ecarepro.ui.fom_guard.verifying_phone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentVerifyPhoneBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerifyPhoneFragment : Fragment() {

    private lateinit var binding: FragmentVerifyPhoneBinding
    private val viewModel: VerfyPhoneViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentVerifyPhoneBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
           findNavController().popBackStack()
        }
        binding.textUserName.doAfterTextChanged {
            binding.btnContinue.isEnabled=binding.textUserName.text.toString().length==10
        }

        binding.btnContinue.setOnClickListener {
            verifyNumber()
        }

        

    }

    private fun verifyNumber(){
        lifecycleScope.launch {
            viewModel.userdetailsfrommobiletateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    } is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)
                    if (it.data != null) {
                        if (it.data. status) {
                            findNavController().navigate(
                                R.id.appointmentFragment,
                                bundleOf("toAppointment" to  true)
                            )
                        }else{

                            mainActivity().showMessage(it.data.message)
                        }
                    }  } } }  }
        viewModel.getuserdetailsfrommobile(binding.textUserName.text.toString())
    }

}