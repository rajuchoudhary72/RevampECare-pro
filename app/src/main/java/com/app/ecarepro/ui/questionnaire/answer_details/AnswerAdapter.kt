package com.app.ecarepro.ui.questionnaire

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.AnswerListItemBinding
import com.app.ecarepro.model.Answer
import com.app.ecarepro.ui.questionnaire.answer_details.AnswerDetailsFragment

class AnswerAdapter(private var answerList: List<Answer>,
                    private var answerDetailsFragment: AnswerDetailsFragment) :
    RecyclerView.Adapter<AnswerAdapter.AnswerAdapterViewHolder>() {

        private lateinit var bindingm:   AnswerListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerAdapterViewHolder {
        bindingm=AnswerListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AnswerAdapterViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = answerList.size

    override fun onBindViewHolder(holder: AnswerAdapterViewHolder, position: Int) {

        bindingm.answerData=answerList[position]

   }





    class AnswerAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){



    }

    fun setData(thoughtsList : List<Answer>){
         this.answerList= thoughtsList as ArrayList<Answer>
        notifyDataSetChanged()
    }
}