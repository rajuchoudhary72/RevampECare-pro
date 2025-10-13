package com.app.ecarepro.ui.studentProfile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.model.LibraryTransactionX
import com.app.ecarepro.model.PaidHistory
import com.app.ecarepro.utils.listener.ItemListener

class PopUpListAdapterLibTrans(private var paidHistories: List<LibraryTransactionX>,

                               ) :
    RecyclerView.Adapter<PopUpListAdapterLibTrans.PopUpListViewHolder>() {


     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopUpListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.library_trans_list_item, parent, false)

        return PopUpListViewHolder(view)
    }

    override fun getItemCount(): Int = paidHistories.size

    override fun onBindViewHolder(holder: PopUpListViewHolder, pos: Int) {

        holder.tvFine.text=paidHistories[holder.bindingAdapterPosition].fineAmount.toString()
        holder.tvStatus.text=paidHistories[holder.bindingAdapterPosition].status
        holder.tvBookName.text=paidHistories[holder.bindingAdapterPosition].bookName


    }

    class PopUpListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val tvFine: TextView = itemView.findViewById(R.id.tv_fine)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
        val tvBookName: TextView = itemView.findViewById(R.id.tv_book_name)


    }


}