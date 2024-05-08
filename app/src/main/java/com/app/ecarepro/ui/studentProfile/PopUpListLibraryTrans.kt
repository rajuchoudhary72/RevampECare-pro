package com.app.ecarepro.ui.studentProfile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.model.PaidHistory
import com.app.ecarepro.utils.listener.ItemListener

class PopUpListLibraryTrans(private var paidHistories: List<PaidHistory>,

                            ) :
    RecyclerView.Adapter<PopUpListLibraryTrans.PopUpListViewHolder>() {


     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopUpListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.paid_history_list_item, parent, false)

        return PopUpListViewHolder(view)
    }

    override fun getItemCount(): Int = paidHistories.size

    override fun onBindViewHolder(holder: PopUpListViewHolder, pos: Int) {

        holder.tvInstallment.text=paidHistories[holder.bindingAdapterPosition].installment
        holder.tvReceiptNo.text=paidHistories[holder.bindingAdapterPosition].recNo
        holder.tvDate.text=paidHistories[holder.bindingAdapterPosition].paidOn
        holder.tvAmount.text=paidHistories[holder.bindingAdapterPosition].amount.toString()
        holder.tvPaymentMode.text=paidHistories[holder.bindingAdapterPosition].payMode


    }

    class PopUpListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val tvInstallment: TextView = itemView.findViewById(R.id.tvInstallment)
        val tvReceiptNo: TextView = itemView.findViewById(R.id.tvReceiptNo)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvPaymentMode: TextView = itemView.findViewById(R.id.tvPaymentMode)

    }


}