package com.app.ecarepro.ui.question_paper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.data.network.model.QuestionPaper
import com.app.ecarepro.databinding.DayWiseTimeTableItemBinding
import com.app.ecarepro.databinding.PrevExamItemBinding

class QuestionPaperAdapter(
    private var questionPaperList: List<QuestionPaper>,
    private var questionPaperSubFragment: QuestionPaperSubFragment
) :
    RecyclerView.Adapter<QuestionPaperAdapter.AssignmentListAdapter>() {

    private lateinit var bindingm: PrevExamItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListAdapter {
        bindingm =
            PrevExamItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentListAdapter(bindingm )
    }

    override fun getItemCount(): Int = questionPaperList.size

    override fun onBindViewHolder(holder: AssignmentListAdapter, position: Int) {
         val binding = DataBindingUtil.getBinding<PrevExamItemBinding>(holder.itemView)
        val data= questionPaperList[position]
        binding?.apply {
            termsName.text=data.examName
            name.text=data.examName
            size.text=data.fileSize
            updatedOn.text=data.updatedOn

            llView.setOnClickListener {
                questionPaperSubFragment.onItemClick(data,1,false)
            }
            llDownload.setOnClickListener {
                questionPaperSubFragment.onItemClick(data,2,false)
            }
        }



     }


    class AssignmentListAdapter(itemView: PrevExamItemBinding) : RecyclerView.ViewHolder(itemView.root) {
    }


}