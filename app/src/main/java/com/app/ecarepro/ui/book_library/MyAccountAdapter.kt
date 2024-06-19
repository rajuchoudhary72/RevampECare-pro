package com.app.ecarepro.ui.book_library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.data.network.model.MyAccount
import com.app.ecarepro.databinding.MyAccountLibraryItemBinding

class MyAccountAdapter(private var myAccountList: List<MyAccount>,
                       private var libraryMyAccountFragment: LibraryMyAccountFragment ) :
    RecyclerView.Adapter<MyAccountAdapter.ThoughtsViewHolder>() {

        private lateinit var binding :   MyAccountLibraryItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThoughtsViewHolder {
        binding=MyAccountLibraryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ThoughtsViewHolder(binding.root)
    }

    override fun getItemCount(): Int = myAccountList.size

    override fun onBindViewHolder(holder: ThoughtsViewHolder, position: Int) {

        val bindings=DataBindingUtil.getBinding<MyAccountLibraryItemBinding>(holder.itemView)

        bindings?.apply {
            accountDetails=myAccountList[position]
            tvSrNo.text= (position+1 ).toString()
        }

    }

    class ThoughtsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
  }


}