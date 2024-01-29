package com.app.ecarepro.ui.book_library

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentLatestBookBinding
import com.app.ecarepro.model.LatestBook
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.book_library.view_model.LatestBookViewModel
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LatestBookFragment : Fragment() , ItemListener<LatestBook> {

    private lateinit var latestBookBinding: FragmentLatestBookBinding
    private val latestBookViewModel : LatestBookViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        latestBookBinding=FragmentLatestBookBinding.inflate(inflater,container,false)
         return latestBookBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            latestBookViewModel._latestBookStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        latestBookBinding.rvLatestBook.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        latestBookBinding.rvLatestBook.isVisible = false
                        Log.d("main", "Error" + it )
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        latestBookBinding.rvLatestBook.isVisible = true

                        if (it.data!=null){

                            if (it.data.latestBook!=null){

                                latestBookBinding.rvLatestBook.isVisible=true
                                latestBookBinding.tvNoData.isVisible=false

                                val noticeAdapter = LatestBookAdapter(it.data.latestBook , this@LatestBookFragment)

                                latestBookBinding.rvLatestBook.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = noticeAdapter
                                }
                            }else{
                                latestBookBinding.rvLatestBook.isVisible=false
                                latestBookBinding.tvNoData.isVisible=true
                            }

                        }

                    }

                    else -> {}
                }
            }
        }

        latestBookViewModel.getLibraryDTL()


    }

    override fun onItemClick(t: LatestBook, pos: Int, boolean: Boolean) {
        findNavController().navigate(R.id.action_bookLibraryFragment_to_bookDetailsFragment,Bundle( ).apply {
            putInt("bookID", t.bookID)
        })
     }
}