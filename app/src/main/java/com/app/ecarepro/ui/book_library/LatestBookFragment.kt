package com.app.ecarepro.ui.book_library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentLatestBookBinding
import com.app.ecarepro.model.LatestBook
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LatestBookFragment(private val latestBook: List<LatestBook> , private val i: Int) : Fragment() , ItemListener<LatestBook> {

    private lateinit var latestBookBinding: FragmentLatestBookBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        latestBookBinding=FragmentLatestBookBinding.inflate(inflater,container,false)
         return latestBookBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (i==0){
            if (latestBook!=null){

                latestBookBinding.rvLatestBook.isVisible=true
                latestBookBinding.tvNoData.isVisible=false

                val noticeAdapter = LatestBookAdapter(latestBook , this@LatestBookFragment)

                latestBookBinding.rvLatestBook.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(activity)
                    adapter = noticeAdapter
                }
            }else{
                latestBookBinding.rvLatestBook.isVisible=false
                latestBookBinding.tvNoData.isVisible=true
            }
        }else if (i==1){
            latestBookBinding.rvLatestBook.isVisible=false
            latestBookBinding.tvNoData.isVisible=true
            latestBookBinding.tvNoData.text="No Account Data"
        }



    }

    override fun onItemClick(t: LatestBook, pos: Int, boolean: Boolean) {
        findNavController().navigate(R.id.action_bookLibraryFragment_to_bookDetailsFragment,Bundle( ).apply {
            putInt(Constant.BOOK_ID_ARGUMENT, t.bookID)
        })
     }
}