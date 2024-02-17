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

class QuestionnaireAdapter(private var questionsList: List<Question>,
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

        var like = data.isILike

        setLikeDisLikeUi(like, holder)
        setAnswerUi(data.isAnswered, holder)

        holder.tv_que.text=data.que
        holder.updated_by.text=data.updatedBy
        holder.updated_on.text=data.updatedOn
         holder.total_like.text= data.likes.toString()+" Likes"
         holder.tv_total_answer.text= data.totalAnswer.toString()+" Answer"

        Picasso.get().load(questionsList[position].photo).
        placeholder(R.drawable.default_profile)
            .into(holder.user_img)

        if (!data.isVerified){
            holder.rl_likes.isVisible=false
            holder.tv_thoughtStatus.isVisible=true
            holder.tv_thoughtStatus.text="Pending"
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
            holder.total_like.text = "$likeCount Likes"
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
            holder.total_like.text="$likeCount Likes"
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
        val tv_unanswer: TextView = itemView.findViewById(R.id.tv_unanswer)
        val tv_answer: TextView = itemView.findViewById(R.id.tv_answer)

    }

    fun setData(thoughtsList : List<Question>){
         this.questionsList= thoughtsList as ArrayList<Question>
        notifyDataSetChanged()
    }
}