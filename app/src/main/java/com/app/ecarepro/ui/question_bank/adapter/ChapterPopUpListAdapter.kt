package com.app.ecarepro.ui.question_bank.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.question_bank.Chapter
import com.app.ecarepro.data.network.model.question_bank.MyClasse
import com.app.ecarepro.data.network.model.question_bank.QuestionType
import com.app.ecarepro.model.InfractionType
import com.app.ecarepro.utils.listener.ItemListener

class ChapterPopUpListAdapter(private var infractionTypeList: List<Chapter>,
                              private var itemListener: ItemListener<Chapter>
) :
    RecyclerView.Adapter<ChapterPopUpListAdapter.PopUpListViewHolder>() {


    private var lastIndex = 1000
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopUpListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pop_up_list_item, parent, false)

        return PopUpListViewHolder(view)
    }

    override fun getItemCount(): Int = infractionTypeList.size

    override fun onBindViewHolder(holder: PopUpListViewHolder, pos: Int) {

        holder.itemName.text=infractionTypeList[holder.bindingAdapterPosition].chapterName
        holder.llMain.setOnClickListener {
            lastIndex=holder.bindingAdapterPosition
             itemListener.onItemClick(infractionTypeList[holder.bindingAdapterPosition],1,true)
            notifyDataSetChanged()
        }

        if (lastIndex == holder.bindingAdapterPosition) {
           holder. itemName.setTextColor(Color.parseColor(R.color.brand_color.toString()))
        } else {
            holder.itemName.setTextColor(Color.parseColor("#000000"))
        }


    }

    class PopUpListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val itemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val llMain: LinearLayout = itemView.findViewById(R.id.ll_main)

    }


}