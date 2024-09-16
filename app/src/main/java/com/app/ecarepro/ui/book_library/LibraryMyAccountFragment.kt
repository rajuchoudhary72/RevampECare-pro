package com.app.ecarepro.ui.book_library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.data.network.model.MyAccount
import com.app.ecarepro.databinding.FragmentLibraryMyAccountBinding


class LibraryMyAccountFragment( private val myAccount: List<MyAccount>?) : Fragment() {

    private lateinit var binding: FragmentLibraryMyAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentLibraryMyAccountBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if (myAccount!=null){

            binding.rvLatestBook.isVisible=true
            binding.tvNoData.isVisible=false

            val myAccountAdapter = MyAccountAdapter(myAccount , this@LibraryMyAccountFragment)

            binding.rvLatestBook.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = myAccountAdapter
            }
        }else{
            binding.rvLatestBook.isVisible=false
            binding.tvNoData.isVisible=true
        }



    }
}