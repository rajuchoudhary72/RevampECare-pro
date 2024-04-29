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
import com.app.ecarepro.databinding.FragmentTeacherTimeTableBinding
import com.app.ecarepro.model.Teacher
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener


class TeacherTimeTableFragment(private val teachers: List<Teacher>, private val toFragment: String) : Fragment(), ItemListener<Teacher> {

    private lateinit var binding : FragmentTeacherTimeTableBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentTeacherTimeTableBinding.inflate(inflater,container,false)
         return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (teachers!=null){

            val classTimeTableAdapter =
                TeacherTimeTableAdapter(teachers,
                    this@TeacherTimeTableFragment)

            binding.rvTeacherTimeTable.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = classTimeTableAdapter
            }

            binding.rvTeacherTimeTable.isVisible=true
            binding.tvNoData.isVisible=false


        }else{
            binding.rvTeacherTimeTable.isVisible=false
            binding.tvNoData.isVisible=true

        }

    }

    override fun onItemClick(t: Teacher, pos: Int, boolean: Boolean) {

        when (toFragment) {
            Constant.FRA_TIMETABLE -> {
                this@TeacherTimeTableFragment. findNavController().
                navigate(R.id.action_classAndTeacherListFragment_to_timeTableNavHostFragment, Bundle().apply {
                    putString(Constant.STAFF_ID_ARGUMENT, t.id)
                })
            }
            Constant.FRA_ASSI -> {
                this@TeacherTimeTableFragment. findNavController().
                navigate(R.id.action_classAndTeacherListFragment_to_staffAssignmentsListFragment,Bundle( ).apply {
                    putString(Constant.STAFF_ID_ARGUMENT, t.id)
                })
            }
            Constant.FRA_LESSON_PLAN -> {
                this@TeacherTimeTableFragment. findNavController().
                navigate(R.id.action_classAndTeacherListFragment_to_lessonPlanListFragment5,Bundle( ).apply {
                    putString(Constant.STAFF_ID_ARGUMENT, t.id)
                })
            }
        }


    }


}