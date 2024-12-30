package com.app.ecarepro.ui.book_library

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentBookDetailsBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.book_library.view_model.BookDetailsViewModel
import com.app.ecarepro.utils.Constant
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BookDetailsFragment : Fragment() {

    private lateinit var binding: FragmentBookDetailsBinding
    private val bookDetailsViewModel: BookDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBookDetailsBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        val bookID = requireArguments().getInt(Constant.BOOK_ID_ARGUMENT)
        bookDetailsViewModel.getBookDetails(bookID, Constant.DEFAULT_ID)

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
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        if (it.data != null) {

                            try {
                                val data = it.data.bookDTL[0]

                                binding.bookDetails = data

                                Picasso.get().load(data.coverImg)
                                    .placeholder(R.drawable.ic_library_big_image)
                                    .into(binding.ivCoverPic)

                                if (data.isIssuable == Constant.TRUE_VALUE) {
                                    binding.tvIssuable.text = getString(R.string.true_value)
                                } else {
                                    binding.tvIssuable.text = getString(R.string.false_value)
                                }
                            } catch (e: NullPointerException) {
                                e.printStackTrace()
                            }

                        }

                    }


                }
            }
        }

        bookDetailsViewModel.getBookDetails(1, 0)


    }
}