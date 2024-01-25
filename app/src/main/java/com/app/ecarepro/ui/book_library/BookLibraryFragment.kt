package com.app.ecarepro.ui.book_library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentBookLibraryBinding
import com.app.ecarepro.ui.message.inbox.InboxMessageFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BookLibraryFragment : Fragment() {

    private lateinit var bookLibraryBinding: FragmentBookLibraryBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bookLibraryBinding=FragmentBookLibraryBinding.inflate(inflater,container,false)
        return bookLibraryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()

        bookLibraryBinding.ivSearch.setOnClickListener {
             findNavController().navigate(R.id.librarySearchFragment)
        }

        bookLibraryBinding.toggleButtonTypeNoti.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (bookLibraryBinding.toggleButtonTypeNoti.checkedButtonId) {
                R.id.btn_noti -> {


                }

                else -> {

                }
            }
        }

    }


    private fun setUpViewPager() {

        val tabItem = mutableListOf("Inbox", "Sent")

        bookLibraryBinding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return tabItem.size
            }

            override fun createFragment(position: Int): Fragment {
                return LatestBookFragment()
            }

        }

        TabLayoutMediator(
            bookLibraryBinding.tabLayout, bookLibraryBinding.viewPager
        ) { tab, position ->
            tab.text = tabItem[position]
        }.attach()

    }

}