package com.app.ecarepro.ui.reportCard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
        var data = noticeList[position]

        bindingm.tvExam.text=data.examName
        bindingm.tvSession.text=session
        bindingm.tvUpdateOn.text= "Updated On : ${data.updatedOn}"

        bindingm.llView.setOnClickListener {
            reportCardDetailsFragment.onItemClick(data,1,false)
        }
        bindingm.llBackView.setOnClickListener {
            reportCardDetailsFragment.onItemClick(data,1,false)
        }
        bindingm.llDownload.setOnClickListener {
            reportCardDetailsFragment.onItemClick(data,2,false)
        }
        bindingm.llBackDownload.setOnClickListener {
            reportCardDetailsFragment.onItemClick(data,2,false)
        }


    }




    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}