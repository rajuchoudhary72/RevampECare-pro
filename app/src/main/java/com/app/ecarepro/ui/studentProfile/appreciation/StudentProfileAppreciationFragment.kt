package com.app.ecarepro.ui.studentProfile.appreciation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.databinding.FragmentStudentProfileappreciationBinding
import com.app.ecarepro.model.RecentAppreciation
import com.app.ecarepro.ui.discipline_log.infraction.appreciation.adapter.AppreciationListAdapter


class StudentProfileAppreciationFragment(val recentAppreciations: List<RecentAppreciation>) : Fragment() {

    private lateinit var binding: FragmentStudentProfileappreciationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentStudentProfileappreciationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (recentAppreciations!=null){
            binding.recyclerInfractionList.isVisible=true
            binding.tvNoData.isVisible=false

            val appreciationListAdapter = StudentProfileAppreciationListAdapter(
                recentAppreciations,
                this@StudentProfileAppreciationFragment
            )

            binding.recyclerInfractionList.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = appreciationListAdapter
            }
        }else{
            binding.recyclerInfractionList.isVisible=false
            binding.tvNoData.isVisible=true
        }

    }




     
}