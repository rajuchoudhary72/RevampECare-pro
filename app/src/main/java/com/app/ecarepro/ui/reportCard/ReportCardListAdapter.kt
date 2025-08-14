package com.app.ecarepro.ui.reportCard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ReportCardListItemBinding
import com.app.ecarepro.model.ReportCard

class ReportCardListAdapter(
    private var noticeList: List<ReportCard>,
    private val  session: String,
    private var reportCardDetailsFragment:   ReportCardDetailsFragment
) :
    RecyclerView.Adapter<ReportCardListAdapter.NoticeViewHolder>() {

        private lateinit var bindingm:   ReportCardListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm=ReportCardListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoticeViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = noticeList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val binding=DataBindingUtil.getBinding<ReportCardListItemBinding>(holder.itemView)
        binding?.apply {
            val data = noticeList[position]

             tvExam.text=data.examName
             tvSession.text=session
             tvUpdateOn.text= reportCardDetailsFragment.getString(R.string.general_updated_on_pun)+"${data.updatedOn}"

             llBack.isVisible = data.viewMode != 1
            if(data.viewMode == 1){
                tvView.text = reportCardDetailsFragment.getString(R.string.view)
                tvDownload.text=reportCardDetailsFragment.getString(R.string.download)
            }

             llView.setOnClickListener {
                reportCardDetailsFragment.onItemClick(data,1,true)
            }
             llBackView.setOnClickListener {
                reportCardDetailsFragment.onItemClick(data,1,false)
            }
             llDownload.setOnClickListener {
                reportCardDetailsFragment.onItemClick(data,2,true)
            }
             llBackDownload.setOnClickListener {
                reportCardDetailsFragment.onItemClick(data,2,false)
            }
        }



    }




    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}