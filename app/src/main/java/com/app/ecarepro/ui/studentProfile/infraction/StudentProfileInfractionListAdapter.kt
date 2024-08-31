package com.app.ecarepro.ui.studentProfile.infraction

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

class StudentProfileInfractionListAdapter(private var recentInfractions: List<RecentInfraction>,
                                          private var infractionListFragment: StudentProfileInfractionFragment
) :
    RecyclerView.Adapter<StudentProfileInfractionListAdapter.CircularViewHolder>() {

        private lateinit var bindingm:   DisciplineViewListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularViewHolder {
        bindingm=DisciplineViewListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CircularViewHolder(bindingm.root)
    }

    override fun getItemCount(): Int = recentInfractions.size

    override fun onBindViewHolder(holder: CircularViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<DisciplineViewListItemBinding>(holder.itemView)

         if (binding!=null){
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
             binding.ivDelete.visibility=View.GONE

         }




    }

    class CircularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}