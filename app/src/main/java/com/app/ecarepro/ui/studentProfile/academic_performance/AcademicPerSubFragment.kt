package com.app.ecarepro.ui.studentProfile.academic_performance

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentAcademicPerSubBinding
import com.app.ecarepro.databinding.ItemTransAttendanceBinding
import com.app.ecarepro.model.Subject



class AcademicPerSubFragment() : Fragment() {

    private lateinit var binding: FragmentAcademicPerSubBinding
    private var itemDat: ArrayList<Subject>?=null
    private var isExpanded:Boolean=true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                itemDat = it.getParcelableArrayList(ARG_ITEM_DATA, Subject::class.java)
            }else{
                @Suppress("DEPRECATION")
                itemDat = it.getParcelableArrayList(ARG_ITEM_DATA)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentAcademicPerSubBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.llCollapesExpandButton.setOnClickListener {
            setupList(isExpanded)
            if (isExpanded){
                binding.ivExpandButton.setImageResource(R.drawable.keyboard_arrow_up_24dp)
                binding.tvCollapesExpandText.text= getString(R.string.collapse_all)
                isExpanded=false
            }else{
                binding.ivExpandButton.setImageResource(R.drawable.keyboard_arrow_down_24dp)
                binding.tvCollapesExpandText.text= getString(R.string.expand_all)
                isExpanded=true
            }
        }

        setupList(isExpanded)



    }


    fun setupList(isExpanded: Boolean) {
        if (!itemDat.isNullOrEmpty()) {
            val adapter = AcademicPerfListAdapter(itemDat!!, isExpanded)

            binding.rvExamList.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                this.adapter = adapter


            }
            binding.rvExamList.isVisible = true
            binding.tvNoData.isVisible = false
        } else {
            binding.rvExamList.isVisible = false
            binding.tvNoData.isVisible = true
        }
    }


    companion object {
        private const val ARG_ITEM_DATA = "arg_item_data"

        fun newInstance( itemDat: ArrayList<Subject>?)= AcademicPerSubFragment().apply {
            arguments= Bundle().apply {
                putParcelableArrayList(ARG_ITEM_DATA,itemDat)
            }
        }

    }





}