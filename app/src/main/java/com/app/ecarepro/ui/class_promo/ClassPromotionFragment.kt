package com.app.ecarepro.ui.class_promo

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentClassPromotionBinding
import com.app.ecarepro.model.MyClasseX
import com.app.ecarepro.model.Student
import com.app.ecarepro.model.StudentPro
import com.app.ecarepro.model.StudentPromotedClass
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClassPromotionFragment : Fragment() {
    private var classModel: MyClasseX? = null
    private lateinit var binding: FragmentClassPromotionBinding
    private var studentListArrayList = mutableListOf<StudentPro>()
    private val mStudentAdapter by lazy { ClassPromotionsAdapter(studentListArrayList) }
    private val classAdapter by lazy {
        ArrayAdapter<MyClasseX>(requireContext(), R.layout.simple_spinner_item).apply {
            this.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        }
    }
    private val classPromotionModel: ClassPromotionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentClassPromotionBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        with(binding) {
            rvStuAtt.adapter = mStudentAdapter
            spClass.adapter = classAdapter
            btnSubmit.setOnClickListener { submitDetails() }
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinnerAdapter()
        classPromotionModel.getClassList()

        lifecycleScope.launch {

            classPromotionModel._promotionModel.collectLatest {
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
                        if (it.data != null) {
                            if (it.data.students!=null){
                                Log.d("List", "${it.data.students.size}")
                                it.data.students.let { list ->
                                    list.forEach { item ->
                                        studentListArrayList.add(item!!)
                                    }
                                    if (studentListArrayList.isEmpty()) {
                                        binding.tvNoRecord.visibility = View.VISIBLE
                                        binding.rvStuAtt.visibility = View.GONE
                                        binding.btnSubmit.visibility = View.GONE
                                    } else {
                                        binding.tvNoRecord.visibility = View.GONE
                                        binding.rvStuAtt.visibility = View.VISIBLE
                                        binding.btnSubmit.visibility = View.VISIBLE
                                    }

                                    mStudentAdapter.notifyDataSetChanged()
                                }
                            }else{
                                binding.tvNoRecord.visibility = View.VISIBLE
                                binding.rvStuAtt.visibility = View.GONE
                                binding.btnSubmit.visibility = View.GONE
                            }

                        }

                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            classPromotionModel._classList.collectLatest {
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
                        if (it.data != null) {
                            it.data.myClasses?.let { list ->
                                studentListArrayList.clear()
                                classAdapter.addAll(list)
                            }


                        }

                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            classPromotionModel._saveResponse.collectLatest {
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
                        if (it.data != null) {
                            mainActivity().showMessage("${it.data.message}")

                        }

                    }

                    else -> {}
                }
            }
        }


    }

    private fun spinnerAdapter() {
        binding.spClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                classModel = parent.getItemAtPosition(position) as MyClasseX
                classModel?.let {
                    studentListArrayList.clear()
                    mStudentAdapter.notifyDataSetChanged()
                    classPromotionModel.getClassPromotions(it.id ?: "")
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }


    private fun submitDetails() {
        val requestList = mutableListOf<StudentPromotedClass>()
        studentListArrayList.forEach { item ->
            item.selected?.let {
                requestList.add(
                    StudentPromotedClass(
                        newClassID = "${it.classID}",
                        newSectionID = "${it.secID}",
                        stID = "${item.stID}"
                    )
                )
            }
        }

        if (requestList.isEmpty()) {
            mainActivity().showMessage("Add Message here")
            return
        }

        classPromotionModel.submitClassPromotions(requestList)

    }
}