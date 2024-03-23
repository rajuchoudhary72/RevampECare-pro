package com.app.ecarepro.ui.message.compose

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.Contact
import com.app.ecarepro.data.network.model.ContactsDto
import com.app.ecarepro.databinding.FragmentComposeBinding
import com.app.ecarepro.ui.message.selectRecipients.SelectRecipientsFragment
import com.google.android.material.chip.Chip
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

        binding.btnAddRecipient.setOnClickListener {

            setFragmentResultListener(SelectRecipientsFragment.SELECT_CONTACT_REQUEST_KEY) { requestKey, bundle ->
                if (bundle.containsKey(SelectRecipientsFragment.SELECTED_CONTACT)) {
                    val contacts: ContactsDto =
                        bundle.getSerializable(SelectRecipientsFragment.SELECTED_CONTACT) as ContactsDto

                    buildChipGroup(contacts.contacts)
                }
            }

            findNavController().navigate(R.id.selectRecipientsFragment)
        }

        binding.btnAddAttachment.setOnClickListener {
            binding.cardAttachmentOptions.isVisible = binding.cardAttachmentOptions.isVisible.not()
        }

    }

    private fun buildChipGroup(contacts: List<Contact>) {
        binding.recipientChipGroup.apply {
            removeAllViews()
            contacts.forEach { contact ->
                addView(
                    Chip(requireContext()).apply {
                        text = contact.name
                        setTextAppearance(R.style.TextAppearance_ECarePro_BodyMedium)
                        isCloseIconVisible = true
                        /*convertUrlToDrawable(contact.photo) {
                            chipIcon = it
                        }*/
                        setEnsureMinTouchTargetSize(false)
                    }
                )
            }
        }

    }

    private fun convertUrlToDrawable(url: String?, result: (Drawable) -> Unit) {
        val loader = ImageLoader(context = requireContext())
        val req = ImageRequest.Builder(requireContext())
            .data(url)
            .transformations(RoundedCornersTransformation(100f, 100f, 100f, 100f))
            .target { drawable ->
                result(drawable)
            }
            .build()
        loader.enqueue(req)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}