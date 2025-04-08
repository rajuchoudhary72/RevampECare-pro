package com.app.ecarepro.ui.discipline_log.infraction.appreciation.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
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

        val binding = DataBindingUtil.getBinding<DisciplineViewListItemBinding>(holder.itemView)
        if (binding!=null){
            binding.rlCompilance.isVisible=false
            var isMaxLineOne=true
            val data=recentInfractions[position]
            binding.ivDelete.isVisible=data.canDelete
            binding.tvMedicineName.text= buildString {
                append(infractionListFragment.getString(R.string.category))
                append(data.appreciation)
            }
            binding.tvInstance.text= buildString {
                append(data.instance)
            }
            binding.tvDate.text= buildString {
                append(infractionListFragment.getString(R.string.appreciatin_on))
                append(data.appreciationOn)
            }


            binding.tvReason.text= data.subAppreciation
            binding.tvDiagnosis.text= data.reward
            binding.tvRemark.text= data.remark
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
                    infractionListFragment.onItemClick(data,2,false)
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