package com.app.ecarepro.ui.discipline_log.infraction.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.DisciplineViewListItemBinding
import com.app.ecarepro.model.RecentInfraction
import com.app.ecarepro.ui.discipline_log.infraction.InfractionListFragment

class InfractionListAdapter(private var recentInfractions: List<RecentInfraction>,
                            private var infractionListFragment: InfractionListFragment
) :
    RecyclerView.Adapter<InfractionListAdapter.CircularViewHolder>() {

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
            append(data.infraction)
        }
        bindingm.tvQuantity.text= buildString {
            append(infractionListFragment.getString(R.string.instance_wit))
            append(data.instance)
        }
        bindingm.tvDate.text= buildString {
            append(infractionListFragment.getString(R.string.infraction_on))
            append(data.infractionOn)
        }


        bindingm.tvReason.text= data.subInfraction
        bindingm.tvDiagnosis.text= data.consequences
        bindingm.tvRemark.text= data.correctiveAction
        bindingm.tvAttdentName.text= data.staffName




    }

    class CircularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}