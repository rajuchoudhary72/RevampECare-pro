package com.app.ecarepro.ui.thought.add_thoughts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentAddThoughtsBlankBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.thought.ThoughtsViewModel
import com.app.ecarepro.utils.ResponseState
import com.app.ecarepro.utils.ResponseStateCreateTou
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddThoughtsBlankFragment : Fragment() {

    private lateinit var binding: FragmentAddThoughtsBlankBinding

    private val thoughtsViewModel : ThoughtsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentAddThoughtsBlankBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textFiledThoughts.doAfterTextChanged {
            if (it != null) {
                binding.btnAdd.isEnabled = it.isNotEmpty() && binding.textFiledAuther.text!!.isNotEmpty()
            }
        }
        binding.textFiledAuther.doAfterTextChanged {
            if (it != null) {
                binding.btnAdd.isEnabled = it.isNotEmpty() && binding.textFiledThoughts.text!!.isNotEmpty()
            }

        }

        binding.btnAdd.setOnClickListener {
            runCatching {
                thoughtsViewModel.thoughtsCreate(
                    binding.textFiledThoughts.text.toString(),
                    binding.textFiledAuther.text.toString()
                )
            }
        }

        lifecycleScope.launch {
            thoughtsViewModel._createTouStateFlow.collectLatest {
                when(it){

                    is ResponseStateCreateTou.Loading->{
                        (requireActivity() as MainActivity).showLoader(true)

                    }
                    is ResponseStateCreateTou.Failure->{
                        (requireActivity() as MainActivity).showLoader(false)
                        Toast.makeText(activity,it.msg.toString(),Toast.LENGTH_LONG).show()
                        Log.d("main", "Error"+it.msg.toString())
                    }
                    is ResponseStateCreateTou.Success->{
                        (requireActivity() as MainActivity).showLoader(false)
                        Toast.makeText(activity,"Successfully!!!",Toast.LENGTH_LONG).show()
                        findNavController().popBackStack()
                    }

                    else -> {}
                }
            }
        }




    }
}