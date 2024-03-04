package com.app.ecarepro.ui.questionnaire

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.QuestionnaireListItemBinding
import com.app.ecarepro.model.Question
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class QuestionnaireAdapter(private var questionsList: ArrayList<Question>,
                           private var questionnaireListFragment: QuestionnaireListFragment) :
    RecyclerView.Adapter<QuestionnaireAdapter.QuestionnaireViewHolder>() {

        private lateinit var bindingm:   QuestionnaireListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionnaireViewHolder {
        bindingm=QuestionnaireListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return QuestionnaireViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = questionsList.size

    override fun onBindViewHolder(holder: QuestionnaireViewHolder, position: Int) {



        val data= questionsList[position]

        var like = !data.isILike

        setLikeDisLikeUi(like, holder)
        setAnswerUi(data.isAnswered, holder)

        holder.tv_que.text=data.que
        holder.updated_by.text=data.updatedBy
        holder.updated_on.text=data.updatedOn
         holder.total_like.text= data.likes.toString()+" "+ questionnaireListFragment.getString(R.string.like)
         holder.tv_total_answer.text= data.totalAnswer.toString()+" "+ questionnaireListFragment.getString(R.string.answer)


        Picasso.get().load(questionsList[position].photo).
        placeholder(R.drawable.default_profile)
            .into(holder.user_img)

        if (!data.isVerified){
            holder.rl_likes.isVisible=false
            holder.ll_anser.isVisible=false
            holder.tv_thoughtStatus.isVisible=true
            holder.tv_thoughtStatus.text=questionnaireListFragment.getString(R.string.pending)
        }else{
            holder.rl_likes.isVisible=true
            holder.ll_anser.isVisible=true
            holder.tv_thoughtStatus.isVisible=false
            holder.tv_thoughtStatus.text=" "
        }

        var likeCount=data.likes


        holder.unlike.setOnClickListener {
            if (like){
                likeCount += 1
                questionnaireListFragment.onItemClick(data,1,like)
                like=false

             }else{
                  likeCount-= 1
                questionnaireListFragment.onItemClick(data,1,like)
                like=true
           }
            setLikeDisLikeUi(like,holder)
            holder.total_like.text = "$likeCount"+" "+ questionnaireListFragment.getString(R.string.like)
         }

        holder.like.setOnClickListener {
            if (like){
                 likeCount+= 1
                questionnaireListFragment.onItemClick(data,1,like)
                like=false

            }else{
                 likeCount-= 1
                questionnaireListFragment.onItemClick(data,1,like)
                like=true

            }
            setLikeDisLikeUi(like, holder)
            holder.total_like.text="$likeCount"+" "+ questionnaireListFragment.getString(R.string.like)
        }

        holder.total_like.setOnClickListener {
            if (data.likes>0){
                questionnaireListFragment.onItemClick(data,2,true)
            }  }

        holder.rel_dot.setOnClickListener {
            questionnaireListFragment.onItemClick(data,3,true)
            }

        holder.ll_main.setOnClickListener {
            questionnaireListFragment.onItemClick(data,4,true)
        }






    }



    private fun setLikeDisLikeUi(likeBoolean: Boolean, holder: QuestionnaireViewHolder){
        if (likeBoolean){
            holder.unlike.isVisible=true
            holder.like.isVisible=false
        }else{
            holder.unlike.isVisible=false
            holder.like .isVisible=true
        }


    }

    private fun setAnswerUi(answerBoolean: Boolean, holder: QuestionnaireViewHolder){
        if (answerBoolean){
            holder.tv_answer.isVisible=true
            holder.tv_unanswer.isVisible=false
        }else{
            holder.tv_answer.isVisible=false
            holder.tv_unanswer .isVisible=true
        }


    }


    class QuestionnaireViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


        val updated_by: TextView = itemView.findViewById(R.id.updated_by)
        val updated_on: TextView = itemView.findViewById(R.id.updated_on)
        val unlike: TextView = itemView.findViewById(R.id.unkike)
        val like: TextView = itemView.findViewById(R.id.like)
        val tv_que: TextView = itemView.findViewById(R.id.tv_que)
        val total_like: TextView = itemView.findViewById(R.id.total_like)
        val tv_total_answer: TextView = itemView.findViewById(R.id.tv_total_answer)
         val rl_likes: LinearLayout = itemView.findViewById(R.id.rl_likes)
        val tv_thoughtStatus: TextView = itemView.findViewById(R.id.tv_thoughtStatus)
        val user_img: ShapeableImageView = itemView.findViewById(R.id.user_img)
        val rel_dot: RelativeLayout = itemView.findViewById(R.id.rel_dot)
        val ll_main: LinearLayout = itemView.findViewById(R.id.ll_main)
        val ll_anser: LinearLayout = itemView.findViewById(R.id.ll_anser)
        val tv_unanswer: TextView = itemView.findViewById(R.id.tv_unanswer)
        val tv_answer: TextView = itemView.findViewById(R.id.tv_answer)

    }

    fun setData(questionList : List<Question>){
         this.questionsList.addAll(questionList)
        notifyDataSetChanged()
    }
    fun clearData( ){
        this.questionsList.clear()
        notifyDataSetChanged()
    }
}