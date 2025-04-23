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
    private lateinit var stickyHeaderBinding: ItemTransAttendanceBinding



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
                binding.tvCollapesExpandText.text="Collapes All"
                isExpanded=false
            }else{
                binding.tvCollapesExpandText.text="Expand All"
                isExpanded=true
            }
        }

        setupList(isExpanded)



    }


    fun setupList(isExpanded: Boolean) {
        if (!itemDat.isNullOrEmpty()) {
            val adapter = AcademicPerfListAdapter(itemDat!!, isExpanded)

            setupStickyHeader()


            binding.rvExamList.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                this.adapter = adapter


            }

            binding.rvExamList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisiblePos = layoutManager.findFirstVisibleItemPosition()

                    if (firstVisiblePos != RecyclerView.NO_POSITION) {
                        val subject = adapter.getSubject(firstVisiblePos)
                        val subjectData = itemDat?.get(firstVisiblePos)

                        // Update sticky header text and toggle
                        stickyHeaderBinding.tvStopName.text = subjectData?.subjectName ?: ""

                        // Update layout
                        stickyHeaderBinding.llView.removeAllViews()
                        adapter.addLayout(stickyHeaderBinding.llView, subjectData?.marks, firstVisiblePos)

                        // Toggle functionality
                        stickyHeaderBinding.ivExpandButton.visibility = View.GONE

                    }
                }
            })


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

    private fun setupStickyHeader() {
        val inflater = LayoutInflater.from(requireContext())
        stickyHeaderBinding = ItemTransAttendanceBinding.inflate(inflater)
        binding.stickyHeaderContainer.removeAllViews()
        binding.stickyHeaderContainer.addView(stickyHeaderBinding.root)

        // Initially hide the content
        stickyHeaderBinding.llView.visibility = View.GONE
    }



}