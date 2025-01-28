package com.app.ecarepro.ui.book_library.eBook

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
import com.app.ecarepro.databinding.FragmentEBookBinding
import com.app.ecarepro.model.Book
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.book_library.eBook.adapter.EBookAdapter
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EBookFragment(val data: List<Book>?) : Fragment(), ItemListener<Book> {

    private lateinit var binding: FragmentEBookBinding
    private val eBookDetailsViewModel: EBookDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (data != null) {
        if (data.isNotEmpty()) {

            binding.rvLatestBook.isVisible = true
            binding.tvNoData.isVisible = false

            val eBookAdapter = EBookAdapter(data, this@EBookFragment)

            binding.rvLatestBook.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = eBookAdapter
            }
        } else {
            binding.rvLatestBook.isVisible = false
            binding.tvNoData.isVisible = true
        }
    } else {
        binding.rvLatestBook.isVisible = false
        binding.tvNoData.isVisible = true
    }


    }

    override fun onItemClick(t: Book, pos: Int, boolean: Boolean) {
        lifecycleScope.launch {
            eBookDetailsViewModel.eBookListStateFlow.collectLatest {
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

                            findNavController().navigate(R.id.openPdfFragment,Bundle().apply {
                                putString(Constant.URL_ARGUMENT,  it.data.message)
                            })

                        }

                    }


                }
            }
        }

        eBookDetailsViewModel.getEBookDetails(t.accessionNo)

    }


}