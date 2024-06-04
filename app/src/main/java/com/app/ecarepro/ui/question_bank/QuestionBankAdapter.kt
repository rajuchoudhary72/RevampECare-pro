package com.app.ecarepro.ui.question_bank

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.QuestionBankListItemBinding
import com.app.ecarepro.model.QBQuestion
import com.app.ecarepro.utils.Constant

class QuestionBankAdapter(private var qbQuestionList: List<QBQuestion>,
                          private var questionBankFragment: QuestionBankFragment) :
    RecyclerView.Adapter<QuestionBankAdapter.NoticeViewHolder>() {

        private lateinit var bindingm:   QuestionBankListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        bindingm=QuestionBankListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoticeViewHolder(bindingm )
    }

    override fun getItemCount(): Int = qbQuestionList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(qbQuestionList[position])


    }




   inner class NoticeViewHolder(val item: QuestionBankListItemBinding) : RecyclerView.ViewHolder(item.root){
        fun bind(qbQuestion: QBQuestion) {
            item.questDetails=qbQuestion
            item.tvClass.text=qbQuestion.`class`
            if (qbQuestion.filename != ""){
                item.llDownload.isVisible=true
            }

            item.llDelete.setOnClickListener {
                questionBankFragment.onItemClick(qbQuestion,Constant.DELETE,false)
            }
            item.llEdit.setOnClickListener {
                questionBankFragment.onItemClick(qbQuestion,Constant.EDIT,false)
            }
            item.llDownload.setOnClickListener {
                questionBankFragment.onItemClick(qbQuestion,Constant.DOWNLOAD,false)
            }

        }
  }


}