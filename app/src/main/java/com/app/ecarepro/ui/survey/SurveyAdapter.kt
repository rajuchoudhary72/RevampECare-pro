package com.app.ecarepro.ui.survey

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.SurveyItemBinding
import com.app.ecarepro.databinding.TeacherItemBinding
import com.app.ecarepro.utils.getDateTimeFormatted

class SurveyAdapter(private var syllabusLST: List<AllSurvey>) : RecyclerView.Adapter<SurveyAdapter.NoticeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
       val binding =
            SurveyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding.root)
    }

    override fun getItemCount(): Int = syllabusLST.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<SurveyItemBinding>(holder.itemView)
        val surveyModel=syllabusLST[position]
       // bindingm.attData = syllabusLST[position]
        if (binding!=null){
            binding.tvTest.text = surveyModel.description
            holder.itemView.setOnClickListener {
                /* if (surveyModel.isOpen && !surveyModel.isResponded
                 )
                     mContext.startActivity(
                     Intent(
                         mContext,
                         ActivitySurveyQuestions::class.java
                     ).putExtra("SurveyObj", mData.get(holder.adapterPosition))
                 ) else if (mData.get(holder.adapterPosition)
                         .getResultDeclared()
                 ) mContext.startActivity(
                     Intent(
                         mContext,
                         ActivitySurveyResult::class.java
                     ).putExtra("surId", surveyModel.getSurID())
                 ) else if (mData.get(holder.adapterPosition).getResponded()) Toast.makeText(
                     mContext,
                     "Thanks for your response. Your response has already been recorded.",
                     Toast.LENGTH_SHORT
                 ).show() else Toast.makeText(mContext, "Survey Closed", Toast.LENGTH_SHORT).show()*/
            }


            // Log.v("abc", holder.tvTest.getLineCount() + "");


            // Log.v("abc", holder.tvTest.getLineCount() + "");
            binding.tvTest.post(Runnable { // Log.v("abc", holder.tvTest.getLineCount() + "");
                if (binding.tvTest.lineCount > 2) {
                    binding.tvMore.visibility = View.VISIBLE
                    binding.tvDescription.ellipsize = TextUtils.TruncateAt.END
                } else {
                    binding.tvMore.setVisibility(View.GONE)
                    binding.tvDescription.setEllipsize(null)
                }
                binding.tvDescription.setText(surveyModel.description)
                binding.tvTitle.setText(surveyModel.title)
                binding.tvPublishedOn.setText("Published on: " + surveyModel.publishedOn?.let {
                    getDateTimeFormatted(
                        it
                    )
                })
                if (surveyModel.isOpen) binding.tvOpenClose.setText("Open till: " + surveyModel.openEndDate) else binding.tvOpenClose.setText(
                    "Closed on: " + surveyModel.openEndDate
                )
                if (surveyModel.isResponded) {
                    binding.tvRespondedOn.setText(
                        "RESPONDED ON: " + surveyModel.respondedOn?.let {
                            getDateTimeFormatted(
                                it
                            )
                        }
                    )
                    binding.tvRespondedOn.setVisibility(View.VISIBLE)
                }
            })


            binding.tvMore.setOnClickListener(View.OnClickListener {
                if (binding.tvMore.getText().toString().equals("more", ignoreCase = true)) {
                    binding.tvMore.setText("Less")
                    binding.tvDescription.setLines(binding.tvTest.lineCount)
                    binding.tvDescription.setEllipsize(null)
                } else {
                    binding.tvMore.setText("More")
                    binding.tvDescription.setLines(2)
                    binding.tvDescription.setEllipsize(TextUtils.TruncateAt.END)
                }
                //  notifyDataSetChanged();
            })
        }



    }


    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}