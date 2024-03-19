package com.app.ecarepro.ui.message.selectRecipients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.ecarepro.databinding.FragmentPagerSelectRecipientsBinding
import com.app.ecarepro.model.RecipientsType
import com.app.ecarepro.selectableClassView
import com.app.ecarepro.selectableRecipient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectRecipientPagerFragment : Fragment() {

    private var _binding: FragmentPagerSelectRecipientsBinding? = null
    private val binding get() = _binding!!

    private val selectRecipientsViewModel: SelectRecipientsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPagerSelectRecipientsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.withModels {
            (0..3).forEach {
                selectableClassView {
                    id(it)
                }
            }
            (4..6).forEach {
                selectableRecipient {
                    id(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val RECIPIENTS_TYPE = "recipientsType"
        fun getInstance(recipientsType: RecipientsType): SelectRecipientPagerFragment {
            return SelectRecipientPagerFragment().apply {
                arguments = bundleOf(
                    RECIPIENTS_TYPE to recipientsType
                )
            }
        }
    }
}