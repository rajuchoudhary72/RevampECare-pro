package com.app.ecarepro.ui.message.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.Recipient
import com.app.ecarepro.data.network.model.RecipientDto
import com.app.ecarepro.databinding.DialogRecipientsBinding
import com.app.ecarepro.recipients
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RecipientsDialog : BottomSheetDialogFragment() {

    private var _binding: DialogRecipientsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DialogRecipientsBinding.inflate(inflater, container, false).let {
            _binding = it
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { dismiss() }

        val recipientDto: RecipientDto = arguments?.getSerializable(RECIPIENT) as RecipientDto

        binding.toolbar.title = "${recipientDto.recipients.size} ${getString(R.string.recipient_s)}"
        binding.recyclerView.withModels {
            recipientDto.recipients.forEach { recipient ->
                recipients {
                    id(recipient.name)
                    recipient(recipient)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val RECIPIENT = "recipients"
        fun getInstance(recipients: List<Recipient>): RecipientsDialog {
            return RecipientsDialog().apply {
                arguments = bundleOf(
                    RECIPIENT to RecipientDto(recipients)
                )
            }
        }
    }
}