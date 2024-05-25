package com.app.ecarepro.ui.assignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.databinding.FragmentAssignmentListBinding
import com.app.ecarepro.model.Assignment
import com.app.ecarepro.utils.listener.ItemListener


class AssignmentListFragment(private val assignments: List<Assignment>?) : Fragment(),
    ItemListener<Assignment> {

    private lateinit var binding: FragmentAssignmentListBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (assignments != null) {


            val assignmentListAdapter =
                AssignmentListAdapter(
                    assignments,
                    this@AssignmentListFragment
                )

            binding.rvAssignment.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = assignmentListAdapter
            }
            binding.rvAssignment.isVisible = true
            binding.tvNoData.isVisible = false


        } else {
            binding.rvAssignment.isVisible = false
            binding.tvNoData.isVisible = true

        }


    }

    override fun onItemClick(t: Assignment, pos: Int, boolean: Boolean) {

        if (pos == 1) {

        }


    }
}