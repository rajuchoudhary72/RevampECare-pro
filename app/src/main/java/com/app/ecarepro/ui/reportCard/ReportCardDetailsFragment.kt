package com.app.ecarepro.ui.reportCard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.databinding.FragmentReportCardDetailsBinding
import com.app.ecarepro.model.ReportCard
import com.app.ecarepro.model.ReportClasse
import com.app.ecarepro.model.Student
import com.app.ecarepro.utils.listener.ItemListener


class ReportCardDetailsFragment(private val itemDat: ReportClasse) : Fragment(),
    ItemListener<ReportCard> {

    private lateinit var binding: FragmentReportCardDetailsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentReportCardDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (itemDat.reportCards!=null){



            val reportCardListAdapter =
                ReportCardListAdapter(itemDat.reportCards,itemDat.academicYear,
                    this@ReportCardDetailsFragment)

            binding.rvTimeReportCard.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = reportCardListAdapter
            }
            binding.rvTimeReportCard.isVisible=true
            binding.tvNoData.isVisible=false


        }else{
            binding.rvTimeReportCard.isVisible=false
            binding.tvNoData.isVisible=true

        }

    }

    override fun onItemClick(t: ReportCard, pos: Int, boolean: Boolean) {
        /*if (pos==1){
            openFile(t.asgFile)
        }else if (pos==2){
            downloadFile(t.asgFile)
        }*/
    }
}