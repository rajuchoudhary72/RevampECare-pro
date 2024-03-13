package com.app.ecarepro.ui.discipline_log.infraction.appreciation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.DisciplineViewListItemBinding
import com.app.ecarepro.model.RecentAppreciation
 import com.app.ecarepro.ui.discipline_log.infraction.appreciation.appreciation_list.AppreciationListFragment

 class AppreciationListAdapter(private var recentInfractions: List<RecentAppreciation>,
                              private var infractionListFragment: AppreciationListFragment
) :
    RecyclerView.Adapter<AppreciationListAdapter.CircularViewHolder>() {

        private lateinit var bindingm:   DisciplineViewListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularViewHolder {
        bindingm=DisciplineViewListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CircularViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = recentInfractions.size

    override fun onBindViewHolder(holder: CircularViewHolder, position: Int) {
         val data=recentInfractions[position]

        bindingm.tvMedicineName.text= buildString {
            append(infractionListFragment.getString(R.string.category))
            append(data.appreciation)
        }
        bindingm.tvQuantity.text= buildString {
            append(infractionListFragment.getString(R.string.instance_wit))
            append(data.instance)
        }
        bindingm.tvDate.text= buildString {
            append(infractionListFragment.getString(R.string.appreciatin_on))
            append(data.appreciationOn)
        }


        bindingm.tvReason.text= data.subAppreciation
        bindingm.tvDiagnosis.text= data.reward
        bindingm.tvRemark.text= data.remark
        bindingm.tvAttdentName.text= data.staffName




    }

    class CircularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}