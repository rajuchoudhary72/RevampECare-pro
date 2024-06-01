package com.app.ecarepro.ui.assignment.submit_assignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.app.ecarepro.AddMoreFavouritesBindingModelBuilder
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentSubmitAssignmentBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SubmitAssignmentFragment : Fragment() {

    private lateinit var binding : FragmentSubmitAssignmentBinding
    private val submitAssignmentViewModel : SubmitAssignmentViewModel  by viewModels( )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding = FragmentSubmitAssignmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etAnswer.doAfterTextChanged {
            if (it != null) {
                if (it.isNotEmpty()){
                    binding.postAnswer.isEnabled = true
                    binding.postAnswer.setImageResource(R.drawable.send_icon_enable)

                    

                }else{
                    binding.postAnswer.isEnabled = false
                    binding.postAnswer.setImageResource(R.drawable.send_icon_light)
                }

            }
        }

    }
}