package com.app.ecarepro.ui.classmate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentClassmateBinding
import com.app.ecarepro.model.ClassmateLST
import com.app.ecarepro.ui.MainActivity
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ClassMateFragment : Fragment() {

    private val classmateLSTS = mutableListOf<ClassmateLST>()
    private val classMateAdapter by lazy { ClassMateAdapter(classmateLSTS) { allTeacher, poss -> } }
    private lateinit var binding: FragmentClassmateBinding
    private val viewModel: ClassMateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentClassmateBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            Picasso.setSingletonInstance(
                Picasso.Builder(requireActivity()) // additional settings
                    .build()
            )
        } catch (e: IllegalStateException) {

        }


        with(binding) {
            gridview.adapter=classMateAdapter
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {

            viewModel._classMateResponseMutableStateFlow.collectLatest {
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

                        classmateLSTS.clear()
                        if (it.data != null) {
                            it.data.let {respose->
                                classmateLSTS.addAll(respose.classmateLST)
                            }


                        }
                        classMateAdapter.notifyDataSetChanged()

                    }

                }
            }
        }
        viewModel.getClassMate()

    }


}