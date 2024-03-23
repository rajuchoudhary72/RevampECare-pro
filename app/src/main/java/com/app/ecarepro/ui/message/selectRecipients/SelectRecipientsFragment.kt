package com.app.ecarepro.ui.message.selectRecipients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.ecarepro.data.network.model.ContactsDto
import com.app.ecarepro.databinding.FragmentSelectRecipientsBinding
import com.app.ecarepro.model.RecipientsType
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectRecipientsFragment : Fragment() {

    private var _binding: FragmentSelectRecipientsBinding? = null
    private val binding get() = _binding!!

    private val selectRecipientsViewModel: SelectRecipientsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectRecipientsViewModel.clearAllSelectedContact()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectRecipientsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.btnDone.setOnClickListener {
            setFragmentResult(
                SELECT_CONTACT_REQUEST_KEY, bundleOf(
                    SELECTED_CONTACT to ContactsDto(selectRecipientsViewModel.getSelectedContacts())
                )
            )
            findNavController().popBackStack()
        }
    }

    private fun setUpViewPager() {

        val recipientsTypes = RecipientsType.values()

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return recipientsTypes.size
            }

            override fun createFragment(position: Int): Fragment {
                return SelectRecipientPagerFragment.getInstance(recipientsTypes[position])
            }

        }

        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab, position ->
            tab.text = recipientsTypes[position].title
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SELECTED_CONTACT = "selected_contact"
        const val SELECT_CONTACT_REQUEST_KEY = "select_contact"
    }
}

