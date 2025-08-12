package com.app.ecarepro.ui.survey

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.SurveyItemBinding
import com.app.ecarepro.utils.getDateTimeFormatted

class SurveyAdapter(
    private var syllabusLST: List<AllSurvey>,
    private var  surveyListFragment: SurveyListFragment,
    val callback: (poss: Int, data: AllSurvey) -> Unit
) : RecyclerView.Adapter<SurveyAdapter.NoticeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding =
            SurveyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding.root)
    }

    override fun getItemCount(): Int = syllabusLST.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<SurveyItemBinding>(holder.itemView)
        val surveyModel = syllabusLST[position]
        // bindingm.attData = syllabusLST[position]
        if (binding != null) {
            binding.tvTest.text = surveyModel.description
            holder.itemView.setOnClickListener {
                callback.invoke(position, surveyModel)
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
                binding.tvPublishedOn.setText(surveyListFragment.getString(R.string.published_on) + surveyModel.publishedOn?.let {
                    getDateTimeFormatted(
                        it
                    )
                })
                if (surveyModel.isOpen) binding.tvOpenClose.setText(surveyListFragment.getString(R.string.open_till) + surveyModel.openEndDate) else binding.tvOpenClose.setText(
                    surveyListFragment.getString(R.string.closed_on) + surveyModel.openEndDate
                )
                if (surveyModel.isResponded) {
                    binding.tvRespondedOn.setText(
                        surveyListFragment.getString(R.string.responded_on) + surveyModel.respondedOn?.let {
                            (
                                    it
                                    )
                        }
                    )
                    binding.tvRespondedOn.setVisibility(View.VISIBLE)
                }
            })


            binding.tvMore.setOnClickListener(View.OnClickListener {
                if (binding.tvMore.getText().toString().equals("more", ignoreCase = true)) {
                    binding.tvMore.setText(surveyListFragment.getString(R.string.less))
                    binding.tvDescription.setLines(binding.tvTest.lineCount)
                    binding.tvDescription.setEllipsize(null)
                } else {
                    binding.tvMore.setText(surveyListFragment.getString(R.string.more))
                    binding.tvDescription.setLines(2)
                    binding.tvDescription.setEllipsize(TextUtils.TruncateAt.END)
                }
                //  notifyDataSetChanged();
            })

            binding.rootContainer.setBackgroundColor(
                ContextCompat.getColor(
                    binding.rootContainer.context,
                    if (surveyModel.isOpen) R.color.grey_10 else R.color.white
                )
            )
        }
    }


    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}