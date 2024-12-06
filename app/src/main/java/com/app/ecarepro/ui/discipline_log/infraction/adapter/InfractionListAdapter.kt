package com.app.ecarepro.ui.discipline_log.infraction.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.DisciplineViewListItemBinding
import com.app.ecarepro.databinding.ProfileUpdateRecordItemBinding
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

        val binding = DataBindingUtil.getBinding<DisciplineViewListItemBinding>(holder.itemView)

         if (binding!=null){

             var isMaxLineOne=true
             val data=recentInfractions[position]

             binding.ivDelete.isVisible=data.canDelete

             binding.tvMedicineName.text= buildString {
                 append(infractionListFragment.getString(R.string.category))
                 append(data.infraction)
             }
             binding.tvQuantity.text= buildString {
                 append(infractionListFragment.getString(R.string.instance_wit))
                 append(data.instance)
             }
             binding.tvDate.text= buildString {
                 append(infractionListFragment.getString(R.string.infraction_on))
                 append(data.infractionOn)
             }


             binding.tvReason.text= data.subInfraction
             binding.tvDiagnosis.text= data.consequences
             binding.tvRemark.text= data.correctiveAction
             binding.tvAttdentName.text= data.staffName

             binding.tvRemark.setOnClickListener {

                 if (isMaxLineOne) {
                     binding.tvRemark.maxLines = Int.MAX_VALUE
                     isMaxLineOne=false
                 } else {
                     binding.tvRemark.maxLines = 1
                     isMaxLineOne=true
                 }
             }

             binding.ivDelete.setOnClickListener {
                 val builder = AlertDialog.Builder(infractionListFragment.requireContext())
                 builder.setTitle("Are you sure ?")
                 builder.setMessage("Are you sure, You want to delete it")

                 builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                     infractionListFragment.onItemClick(data,1,false)
                 }

                 builder.setNegativeButton(android.R.string.no) { dialog, which ->

                 }

                 builder.show()

             }
         }




    }

    class CircularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}