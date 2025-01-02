package com.app.ecarepro.ui.studentProfile.infraction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.databinding.FragmentStudentProfileInfractionBinding
import com.app.ecarepro.model.RecentInfraction
import com.app.ecarepro.ui.discipline_log.infraction.adapter.InfractionListAdapter
import com.app.ecarepro.ui.studentProfile.share_data.SharedViewModelProfile


class StudentProfileInfractionFragment( ) : Fragment() {

    private lateinit var binding: FragmentStudentProfileInfractionBinding
    private val sharedViewModel: SharedViewModelProfile by activityViewModels()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentStudentProfileInfractionBinding.inflate(inflater,container,false)
        return binding.root
     }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.getNetworkStudentProfile().observe(this.viewLifecycleOwner){
            val recentInfractions=it.recentInfractions
            if (recentInfractions!=null){
                if (recentInfractions.isNotEmpty()){
                    binding.recyclerInfractionList.isVisible=true
                    binding.tvNoData.isVisible=false

                    val circularAdapter = StudentProfileInfractionListAdapter(
                        recentInfractions,
                        this@StudentProfileInfractionFragment
                    )

                    binding.recyclerInfractionList.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(activity)
                        adapter = circularAdapter
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