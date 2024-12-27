package com.app.ecarepro.ui.studentProfile.appreciation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.databinding.FragmentStudentProfileappreciationBinding
import com.app.ecarepro.model.RecentAppreciation
import com.app.ecarepro.ui.discipline_log.infraction.appreciation.adapter.AppreciationListAdapter
import com.app.ecarepro.ui.studentProfile.share_data.SharedViewModelProfile
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StudentProfileAppreciationFragment : Fragment() {

    private lateinit var binding: FragmentStudentProfileappreciationBinding
    private val sharedViewModel: SharedViewModelProfile by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentStudentProfileappreciationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.getNetworkStudentProfile().observe(this.viewLifecycleOwner){
            val recentAppreciations=it.recentAppreciations
            if (recentAppreciations!=null   ){
                if (recentAppreciations.isNotEmpty()   ){
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
            }else{
                binding.recyclerInfractionList.isVisible=false
                binding.tvNoData.isVisible=true
            }
        }


    }




     
}