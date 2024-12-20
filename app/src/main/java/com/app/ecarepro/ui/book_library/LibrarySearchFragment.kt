package com.app.ecarepro.ui.book_library

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentLibrarySearchBinding
import com.app.ecarepro.model.BookDTL
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.book_library.view_model.BookSearchViewModel
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LibrarySearchFragment : Fragment(), ItemListener<BookDTL> {

    private lateinit var binding: FragmentLibrarySearchBinding
    private val bookSearchViewModel: BookSearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibrarySearchBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchInputListener()
        observeSearchResults()
    }

    private fun setupSearchInputListener() {
        binding.edSearch.doOnTextChanged { text, _, _, _ ->
            bookSearchViewModel.setSearchQuery(text.toString().trim().replace(Regex("\\s+"), " "))
        }
    }

    private fun observeSearchResults() {
//        lifecycleScope.launch {
//            bookSearchViewModel.searchQuery.collectLatest { query ->
//                // Show loader only if query is not empty, and results are still loading
//                val shouldShowLoader = query.isNotEmpty() && bookSearchViewModel.searchResults.value.isEmpty()
//                (requireActivity() as MainActivity).showLoader(shouldShowLoader)
//            }
//        }

        lifecycleScope.launch {
            bookSearchViewModel.searchResults.collectLatest { bookList ->
                // If the book list is empty, show the "no data" view
                if (bookList.isNotEmpty()) {
                    binding.recyclerBooks.isVisible = true
                    binding.tvNoData.isVisible = false
                    val bookAdapter = SearchBookAdapter(bookList, this@LibrarySearchFragment)
                    binding.recyclerBooks.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(activity)
                        adapter = bookAdapter
                    }
                } else {
                    // Show "No Data" when the list is empty
                    binding.recyclerBooks.isVisible = false
                    binding.tvNoData.isVisible = true
                }
            }
        }
    }


    override fun onItemClick(t: BookDTL, pos: Int, boolean: Boolean) {
        findNavController().navigate(
            R.id.action_librarySearchFragment_to_bookDetailsFragment,
            Bundle().apply {
                putInt(Constant.BOOK_ID_ARGUMENT, t.bookID)
            }
        )
    }
}
