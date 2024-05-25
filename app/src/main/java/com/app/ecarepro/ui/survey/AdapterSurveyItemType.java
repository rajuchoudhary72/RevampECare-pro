package com.app.ecarepro.ui.survey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ecarepro.R;

import java.util.ArrayList;

public class AdapterSurveyItemType extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Option> mData;
    private final int RADIO_VIEW = 0;
    private final int CHECK_VIEW = 1;
    private int mViewType;
    Boolean isResult;
    int totalResponse;

    public AdapterSurveyItemType(Context mContext, int viewType, ArrayList<Option> mData, Boolean isResult, int totalResponsel) {
        this.mContext = mContext;
        this.mData = mData;
        this.mViewType = viewType;
        this.isResult = isResult;
        this.totalResponse = totalResponsel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mViewType == RADIO_VIEW) {
            return new RadioSurveyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.row_survey_item_radio, viewGroup, false));
        } else {
            return new CheckSurveyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.row_survey_item_check, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        double response = mData.get(i).getResponse() * 100.0;
        if (viewHolder instanceof CheckSurveyViewHolder) {
            if (isResult) {
                ((CheckSurveyViewHolder) viewHolder).cb.setEnabled(false);
                ((CheckSurveyViewHolder) viewHolder).cb.setButtonDrawable(android.R.color.transparent);
                ((CheckSurveyViewHolder) viewHolder).cb.setText(mData.get(i).getOption() + "    - " + mData.get(i).getResponse() + " (" + Math.round(((response / totalResponse) * 100.0) / 100.0) + "%)");
            } else
                ((CheckSurveyViewHolder) viewHolder).cb.setText(mData.get(i).getOption());
            ((CheckSurveyViewHolder) viewHolder).cb.setChecked(mData.get(i).isSelected());

            ((CheckSurveyViewHolder) viewHolder).cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mData.get(viewHolder.getAdapterPosition()).isSelected())
                        mData.get(i).setSelected(false);
                    else
                        mData.get(viewHolder.getAdapterPosition()).setSelected(true);
                    notifyDataSetChanged();
                }
            });
        } else {
            if (isResult) {
                ((RadioSurveyViewHolder) viewHolder).rb.setEnabled(false);
                ((RadioSurveyViewHolder) viewHolder).rb.setButtonDrawable(android.R.color.transparent);
                ((RadioSurveyViewHolder) viewHolder).rb.setText(mData.get(i).getOption() + "    - " + mData.get(i).getResponse() + " (" + Math.round(((response / totalResponse) * 100.0) / 100.0) + "%)");
            } else
                ((RadioSurveyViewHolder) viewHolder).rb.setText(mData.get(i).getOption());
            ((RadioSurveyViewHolder) viewHolder).rb.setChecked(mData.get(i).isSelected());
            ((RadioSurveyViewHolder) viewHolder).rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < mData.size(); i++) {
                        if (i == viewHolder.getAdapterPosition())
                            mData.get(i).setSelected(true);
                        else
                            mData.get(i).setSelected(false);
                    }
                    notifyDataSetChanged();
                }
            });

        }


    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class RadioSurveyViewHolder extends RecyclerView.ViewHolder {
        RadioButton rb;

        RadioSurveyViewHolder(@NonNull View itemView) {
            super(itemView);
            rb = itemView.findViewById(R.id.rb);
        }
    }

    public class CheckSurveyViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb;

        CheckSurveyViewHolder(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R.id.cb);
        }
    }
}