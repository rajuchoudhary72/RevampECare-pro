package com.app.ecarepro.ui.timetableviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentClassTimeTableBinding
import com.app.ecarepro.model.Classe
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener


class ClassTimeTableFragment(private val classes: List<Classe>, private val toFragment: String) : Fragment(), ItemListener<Classe> {

    private lateinit var binding : FragmentClassTimeTableBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  FragmentClassTimeTableBinding.inflate(inflater,container,false)
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (classes!=null){

            val classTimeTableAdapter =
                ClassTimeTableAdapter(classes,
                    this@ClassTimeTableFragment)

            binding.rvClassTimeTable.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = classTimeTableAdapter
            }

            binding.rvClassTimeTable.isVisible=true
            binding.tvNoData.isVisible=false


        }else{
            binding.rvClassTimeTable.isVisible=false
            binding.tvNoData.isVisible=true

        }

    }

    override fun onItemClick(t: Classe, pos: Int, boolean: Boolean) {
        when (toFragment) {
            Constant.FRA_TIMETABLE -> {
                this@ClassTimeTableFragment. findNavController().
                navigate(R.id.action_classAndTeacherListFragment_to_timeTableNavHostFragment, Bundle().apply {
                    putString(Constant.ID, t.id)
                    putString(Constant.TIME_TABLE_TYPE, Constant.CLASS_TIME_TABLE)

                })
            }
            Constant.FRA_ASSI -> {
                this@ClassTimeTableFragment. findNavController().
                navigate(R.id.action_classAndTeacherListFragment_to_assignmentNavHostFragment,Bundle( ).apply {
                    putString(Constant.ID, t.id)
                    putString(Constant.ASSIGNMENT_TYPE, Constant.CLASS_ASSIGNMENT)

                })
            }
            Constant.FRA_LESSON_PLAN -> {
                this@ClassTimeTableFragment. findNavController().
                navigate(R.id.action_classAndTeacherListFragment_to_lessonPlanListFragment5,Bundle( ).apply {
                    putString(Constant.STAFF_ID_ARGUMENT, t.id)
                })
            }
        }
    }
}