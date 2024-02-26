package com.app.ecarepro.ui.questionnaire

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
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
        bindingm.dot.setOnClickListener {


            deleteAlert(position)
        }


   }

    private fun deleteAlert(position: Int) {
        val builder = AlertDialog.Builder(answerDetailsFragment.context)
        builder.setTitle(answerDetailsFragment.getString(R.string.delete_alert))
        builder.setMessage(answerDetailsFragment.getString(R.string.delete_alert_are_you_sure))

        builder.setPositiveButton( R.string.yes) { _, _ ->
            answerDetailsFragment.onItemClick(answerList[position],1,false)

        }

        builder.setNegativeButton( R.string.cancel) { _, _ ->

        }


        builder.show()
    }





    class AnswerAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){



    }

    fun setData(thoughtsList : List<Answer>){
         this.answerList= thoughtsList as ArrayList<Answer>
        notifyDataSetChanged()
    }
}