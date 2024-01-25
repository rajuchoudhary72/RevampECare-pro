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
import com.app.ecarepro.databinding.FragmentLibrarySearchBinding
import com.app.ecarepro.model.BookDTL
import com.app.ecarepro.model.LatestBook
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.book_library.view_model.BookSearchViewModel
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LibrarySearchFragment : Fragment(), ItemListener<BookDTL> {


    private lateinit var fragmentLibrarySearchBinding: FragmentLibrarySearchBinding
    private val bookSearchViewModel : BookSearchViewModel  by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {

        fragmentLibrarySearchBinding=FragmentLibrarySearchBinding.inflate(inflater,container,false)
         return fragmentLibrarySearchBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            bookSearchViewModel._bookSearchStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        fragmentLibrarySearchBinding.recyclerBooks.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        fragmentLibrarySearchBinding.recyclerBooks.isVisible = false
                        Log.d("main", "Error" + it )
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        fragmentLibrarySearchBinding.recyclerBooks.isVisible = true

                        if (it.data!=null){

                            if (it.data.bookDTL!=null){

                                fragmentLibrarySearchBinding.recyclerBooks.isVisible=true
                                fragmentLibrarySearchBinding.tvNoData.isVisible=false

                                val noticeAdapter = SearchBookAdapter(it.data.bookDTL , this@LibrarySearchFragment)

                                fragmentLibrarySearchBinding.recyclerBooks.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = noticeAdapter
                                }
                            }else{
                                fragmentLibrarySearchBinding.recyclerBooks.isVisible=false
                                fragmentLibrarySearchBinding.tvNoData.isVisible=true
                            }

                        }

                    }

                    else -> {}
                }
            }
        }

        fragmentLibrarySearchBinding.ivSearch.setOnClickListener {
            if (fragmentLibrarySearchBinding.edSearch.text.isNotEmpty()){
                bookSearchViewModel.getLibrarySearch(fragmentLibrarySearchBinding.edSearch.text.toString(),1)
            }
        }




    }

    override fun onItemClick(t: BookDTL, pos: Int, boolean: Boolean) {
        findNavController().navigate(R.id.action_librarySearchFragment_to_bookDetailsFragment,Bundle( ).apply {
            putInt("bookID", t.bookID)
        })
     }
}