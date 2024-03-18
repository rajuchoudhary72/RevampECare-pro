package com.app.ecarepro.ui.message.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.ecarepro.databinding.FragmentComposeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ComposeFragment : Fragment() {

    private var _binding: FragmentComposeBinding? = null

    private val binding get() = _binding!!

    private val composeViewModel: ComposeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentComposeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddAttachment.setOnClickListener {
            binding.cardAttachmentOptions.isVisible = binding.cardAttachmentOptions.isVisible.not()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}