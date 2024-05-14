package com.app.ecarepro.ui.medicalcard.medical_class

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.MedicalClassBinding
import com.app.ecarepro.model.Student
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.medicalcard.MedicineCardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MedicalClassFragment : Fragment() {
    private lateinit var binding: MedicalClassBinding
    private val mViewModel: MedicineCardViewModel by viewModels()
    private val studentList = mutableListOf<Student>()
    private val mAdapter by lazy {
        MedicalClassAdapter(studentList) { poss, student ->
            val bundle=Bundle()
            bundle.putString("Name",student.name)
            bundle.putString("ID","${student.stID}")
            binding.toolbarAdd2.etSearchCtb.text.clear()
            findNavController().navigate(R.id.studentMedicalCardFragment,bundle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MedicalClassBinding.inflate(inflater, container, false)
        with(binding) {
            toolbarAdd2.logo.setImageResource(R.drawable.back)
            toolbarAdd2.toprightIcon.setImageResource(R.drawable.search)
            toolbarAdd2.logo.setOnClickListener {
                findNavController().popBackStack()
            }
            toolbarAdd2.toprightIcon.setOnClickListener {
                searchClick()
            }
            toolbarAdd2.mainToolbarTitle2.text = "Student List"
            toolbarAdd2.mainToolbarTitle2.setTextColor(resources.getColor(R.color.md_theme_light_primary) )
            toolbarAdd2.mainToolbarTitle2.setTextSize(resources.getDimension(R.dimen.size_teenty) )
        }
        return binding.root
    }

    fun searchClick() {
        if (binding.toolbarAdd2.etSearchCtb.visibility == View.GONE) {
            binding.toolbarAdd2.toprightIcon.setImageResource(R.drawable.cross1)
            binding.toolbarAdd2.etSearchCtb.visibility = View.VISIBLE
            binding.toolbarAdd2.etSearchCtb.hint = "Search by Name"
            binding.toolbarAdd2.etSearchCtb.requestFocus()
            binding.toolbarAdd2.etSearchCtb.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(editable: Editable) {
                    mAdapter.filter(editable.toString())
                }
            })
        } else {
            binding.toolbarAdd2.etSearchCtb.setText("")
            binding.toolbarAdd2.toprightIcon.setImageResource(R.drawable.search)
            binding.toolbarAdd2.etSearchCtb.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvStudentList.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity,2)
          //  layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
        lifecycleScope.launch {
            mViewModel.studentListStateFlow.collectLatest {
                studentList.clear()
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
                        it.data?.let { studentResponse ->
                            studentList.addAll(studentResponse.students)


                        }
                        mAdapter.notifyDataSetChanged()

                    }

                    else -> {}
                }
            }

        }

        mViewModel.getStudentList(2, false)

    }

}