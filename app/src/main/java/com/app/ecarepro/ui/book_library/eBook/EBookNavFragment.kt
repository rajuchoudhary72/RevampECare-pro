package com.app.ecarepro.ui.book_library.eBook

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentENavBookBinding
import com.app.ecarepro.model.Book
import com.app.ecarepro.ui.MainActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EBookNavFragment : Fragment() {


    private lateinit var binding: FragmentENavBookBinding
    private val eBookViewModel: EBookViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentENavBookBinding.inflate(inflater, container, false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.e_books)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        lifecycleScope.launch {
            eBookViewModel.eBookListStateFlow.collectLatest {
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

                            setUpViewPager(it.data.books, it.data.megaBookLink)


                        }

                    }


                }
            }
        }

        eBookViewModel.getEBook("%27%27", 0)


    }

    private fun setUpViewPager(data: List<Book>?, megaBookLink: String) {

        val tabItem =
            mutableListOf(getString(R.string.my_school_library), getString(R.string.mega_e_book))

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return tabItem.size
            }

            override fun createFragment(position: Int): Fragment {

                return when (position) {
                    0 -> {
                        EBookFragment(data)
                    } 1 -> {
                        MegaEBookCollectionFragment(megaBookLink)
                    }

                    else -> EBookFragment(data)
                }
            }

        }

        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab, position ->
            tab.text = tabItem[position]
        }.attach()





    }



}