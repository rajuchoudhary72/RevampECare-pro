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
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentBookDetailsBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.book_library.view_model.BookDetailsViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BookDetailsFragment : Fragment() {

    private lateinit var binding: FragmentBookDetailsBinding
    private val bookDetailsViewModel : BookDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {

        binding= FragmentBookDetailsBinding.inflate(inflater,container,false)

        val bookID=  requireArguments().getInt("bookID")
        bookDetailsViewModel.getBookDTL(bookID,0)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            bookDetailsViewModel._bookDetailsStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                     }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                         Log.d("main", "Error" + it )
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        if (it.data!=null){

                            val data=it.data.bookDTL[0]

                            binding.bookDetails=data

                            Picasso.get().load(data.coverImg).
                            placeholder(R.drawable.ic_library_big_image)
                                .into(binding.ivCoverPic)


                            if (data.isIssuable==1){
                                binding.tvIssuable.text=  "True"
                            }else{
                                binding.tvIssuable.text=  "False"
                            }
                        }

                    }

                    else -> {}
                }
            }
        }

        bookDetailsViewModel.getBookDTL(1,0)


    }
}