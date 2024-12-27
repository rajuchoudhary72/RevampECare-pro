package com.app.ecarepro.ui.reportCard

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentReportCardDetailsBinding
import com.app.ecarepro.model.ReportCard
import com.app.ecarepro.model.ReportClasse
import com.app.ecarepro.model.Student
import com.app.ecarepro.model.Subject
import com.app.ecarepro.ui.studentProfile.academic_performance.AcademicPerSubFragment
import com.app.ecarepro.ui.studentProfile.academic_performance.AcademicPerSubFragment.Companion
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener


class ReportCardDetailsFragment : Fragment(),
        ItemListener<ReportCard> {

    private lateinit var binding: FragmentReportCardDetailsBinding
    private var itemDat: ReportClasse? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                itemDat = it.getParcelable(ARG_ITEM_DATA, ReportClasse::class.java)
            }else{
                @Suppress("DEPRECATION")
                itemDat = it.getParcelable(ARG_ITEM_DATA)
            }

        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportCardDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (itemDat?.reportCards != null) {
        if (itemDat!!.reportCards!!.isNotEmpty()) {


            val reportCardListAdapter =
                    ReportCardListAdapter(
                        itemDat!!.reportCards!!, itemDat!!.academicYear!!,
                            this@ReportCardDetailsFragment)

            binding.rvTimeReportCard.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = reportCardListAdapter
            }
            binding.rvTimeReportCard.isVisible = true
            binding.tvNoData.isVisible = false


        } else {
            binding.rvTimeReportCard.isVisible = false
            binding.tvNoData.isVisible = true

        }
        } else {
            binding.rvTimeReportCard.isVisible = false
            binding.tvNoData.isVisible = true

        }

    }

    override fun onItemClick(t: ReportCard, pos: Int, boolean: Boolean) {
        if (pos == 1) {
            if (boolean) {

                     if (t.viewMode==1){
                         findNavController().navigate(R.id.openPdfFragment, Bundle().apply {
                             putString(Constant.URL_ARGUMENT, t.fileName)
                         })
                     }else{
                         findNavController().navigate(R.id.openPdfFragment, Bundle().apply {
                             putString(Constant.URL_ARGUMENT, t.frontFileName)
                         })
                     }

                 } else {
                if (t.viewMode==1) {
                    findNavController().navigate(R.id.openPdfFragment, Bundle().apply {
                        putString(Constant.URL_ARGUMENT, t.fileName)
                    })

                }else{
                    findNavController().navigate(R.id.openPdfFragment, Bundle().apply {
                        putString(Constant.URL_ARGUMENT, t.backFileName)
                    })
                }

            }
        }

        else if (pos == 2) {
            try {
                try {
                    if (boolean) {

                        if (t.viewMode==1) {
                            if (t.fileName!=null){
                                val androidDownloader = AndroidDownloader(requireContext())
                                androidDownloader.downloadFile(t.fileName, getString(R.string.report_card))
                            }

                        }
                        else{
                            if (t.frontFileName!=null){
                                val androidDownloader = AndroidDownloader(requireContext())
                                androidDownloader.downloadFile(t.frontFileName, getString(R.string.report_card))
                            }

                        }


                    } else {
                        if (t.viewMode==1) {
                            if (t.fileName!=null){
                                val androidDownloader = AndroidDownloader(requireContext())
                                androidDownloader.downloadFile(t.fileName, getString(R.string.report_card))
                            }

                        }else{
                            if (t.backFileName!=null){
                                val androidDownloader = AndroidDownloader(requireContext())
                                androidDownloader.downloadFile(t.backFileName, getString(R.string.report_card))
                            }

                        }

                    }
                }catch (e:SecurityException){
                    e.message
                }
            }catch (e:NullPointerException){
                e.message
            }


        }
    }

    companion object {
        private const val ARG_ITEM_DATA = "arg_item_data"

        fun newInstance( itemDat: ReportClasse?)= ReportCardDetailsFragment().apply {
            arguments= Bundle().apply {
                putParcelable(ARG_ITEM_DATA,itemDat)
            }
        }
    }

}