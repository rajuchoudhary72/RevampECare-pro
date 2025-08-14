package com.app.ecarepro.ui.survey;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ecarepro.R;

import java.util.ArrayList;

public class AdapterSurveyQuestions extends RecyclerView.Adapter<AdapterSurveyQuestions.SurveyQuestionsViewHolder> {
    private Context mContext;
    private ArrayList<Question> mData;
    private final int RADIO_VIEW = 0;
    private final int CHECK_VIEW = 1;
    Boolean isResult;

    public AdapterSurveyQuestions(Context mContext, ArrayList<Question> mData, Boolean isResult) {
        this.mContext = mContext;
        this.mData = mData;
        this.isResult = isResult;
    }

    public void setData(ArrayList<Question> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    public ArrayList<Question> getData() {
        return mData;
    }

    @NonNull
    @Override
    public SurveyQuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SurveyQuestionsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.survey_question_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyQuestionsViewHolder viewHolder, int position) {
        AdapterSurveyItemType surveyItemType;
        viewHolder.rvSurveyItemType.setLayoutManager(new LinearLayoutManager(mContext));
        if (mData.get(position).isAnsMandatory() && !isResult) {
            SpannableString spannableString = new SpannableString(mData.get(position).getQuestion() + " *");
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.absent_red)), mData.get(position).getQuestion().length(), mData.get(position).getQuestion().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.tvSurvey.setText(spannableString);
        } else
            viewHolder.tvSurvey.setText(mData.get(position).getQuestion());
        if (mData.get(position).isMultiSelect())
            surveyItemType = new AdapterSurveyItemType(mContext, CHECK_VIEW, mData.get(position).getOptions(), isResult, mData.get(position).getResponse());
        else
            surveyItemType = new AdapterSurveyItemType(mContext, RADIO_VIEW, mData.get(position).getOptions(), isResult, mData.get(position).getResponse());
        if (isResult) {
            viewHolder.tvTotalSurveyCount.setText(new StringBuilder().append(mContext.getString(R.string.total_response_s)).append(mData.get(position).getResponse()).toString());
            viewHolder.tvTotalSurveyCount.setVisibility(View.VISIBLE);
        } else
            viewHolder.tvTotalSurveyCount.setVisibility(View.GONE);

        viewHolder.rvSurveyItemType.setAdapter(surveyItemType);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class SurveyQuestionsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSurvey, tvTotalSurveyCount;
        private RecyclerView rvSurveyItemType;

        public SurveyQuestionsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSurvey = itemView.findViewById(R.id.tv_survey);
            rvSurveyItemType = itemView.findViewById(R.id.rv_survey_item_type);
            tvTotalSurveyCount = itemView.findViewById(R.id.tv_total_survey_count);

        }
    }
}