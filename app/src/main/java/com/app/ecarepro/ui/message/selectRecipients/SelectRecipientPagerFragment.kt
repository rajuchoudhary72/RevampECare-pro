package com.app.ecarepro.ui.message.selectRecipients

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.airbnb.epoxy.EpoxyController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.ClassContact
import com.app.ecarepro.data.network.model.StaffType
import com.app.ecarepro.databinding.FragmentPagerSelectRecipientsBinding
import com.app.ecarepro.model.RecipientsType
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.selectableClassView
import com.app.ecarepro.selectableRecipient
import com.app.ecarepro.ui.MainActivity
import com.rubensousa.decorator.LinearDividerDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectRecipientPagerFragment : Fragment() {

    private var _binding: FragmentPagerSelectRecipientsBinding? = null
    private val binding get() = _binding!!

    private val selectRecipientsPagerViewModel: SelectRecipientsPagerViewModel by viewModels()

    private val selectRecipientsViewModel: SelectRecipientsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPagerSelectRecipientsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = selectRecipientsPagerViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.addItemDecoration(
            LinearDividerDecoration.create(
                color = ContextCompat.getColor(
                    requireContext(),
                    R.color.green_10
                ),
                size = resources.getDimensionPixelSize(R.dimen.divider_size)
            )
        )

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                selectRecipientsPagerViewModel.uiState.flowWithLifecycle(
                    viewLifecycleOwner.lifecycle,
                    Lifecycle.State.CREATED
                )
                    .collectLatest { uiState ->
                        handleUiState(uiState)
                    }
            }

            launch {
                selectRecipientsPagerViewModel.staffTypes.flowWithLifecycle(
                    viewLifecycleOwner.lifecycle,
                    Lifecycle.State.CREATED
                )
                    .collectLatest { result ->
                        result.getOrNull()?.let { staffTypes ->
                            setUpStaffTypeSpinner(staffTypes)
                        }
                    }
            }

        }
    }

    private fun setUpStaffTypeSpinner(staffTypes: List<StaffType>) {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            staffTypes.map { it.staffType }
        )
        adapter.setDropDownViewResource(
            android.R.layout
                .simple_spinner_dropdown_item
        )
        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun handleUiState(uiState: SelectRecipientsUiState) {
        Log.e("handleUiState: ", uiState.toString())
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.withModels {
            when (uiState) {
                SelectRecipientsUiState.EmptyContact -> {
                    noDataFoundView {
                        id(R.id.empty_view)
                    }
                }

                is SelectRecipientsUiState.StudentContact -> {
                    buildClassWithContactModels(
                        selectRecipientsPagerViewModel.getSelectedClassId(),
                        uiState.contacts,
                        false
                    )
                }

                is SelectRecipientsUiState.ParentContact -> {
                    buildClassWithContactModels(
                        selectRecipientsPagerViewModel.getSelectedClassId(),
                        uiState.contacts,
                        true
                    )
                }

                is SelectRecipientsUiState.StaffContact -> {
                    uiState.contacts.forEach { contact ->
                        selectableRecipient {
                            id(contact.receiverID)
                            isSelected(selectRecipientsViewModel.isContactSelected(contact))
                            photo(contact.photo)
                            name(contact.name)
                            textLine1(contact.designation)
                            clickListener { _ ->
                                if (selectRecipientsViewModel.isContactSelected(contact)) {
                                    selectRecipientsViewModel.removeContact(contact)
                                } else {
                                    selectRecipientsViewModel.addContact(contact)
                                }
                                this@withModels.requestModelBuild()
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }

    private fun EpoxyController.buildClassWithContactModels(
        selectedClassID: Int?,
        contacts: List<ClassContact>,
        isParent: Boolean
    ) {
        if (selectedClassID == null) {
            contacts.forEach { classContact ->
                selectableClassView {
                    id(classContact.classID)
                    className(classContact.className)
                    isSelected(
                        selectRecipientsViewModel.isContactsSelected(
                            classContact.contacts ?: emptyList()
                        )
                    )
                    onClickViewAll { _ ->
                        selectRecipientsPagerViewModel.setSelectedClassId(classId = classContact.classID)
                        this@buildClassWithContactModels.requestModelBuild()
                    }
                    selectClass { _ ->
                        if (selectRecipientsViewModel.isContactsSelected(
                                classContact.contacts ?: emptyList()
                            )
                        ) {
                            selectRecipientsViewModel.removeContacts(
                                classContact.contacts ?: emptyList()
                            )
                        } else {
                            selectRecipientsViewModel.addContacts(
                                classContact.contacts ?: emptyList()
                            )
                        }

                        this@buildClassWithContactModels.requestModelBuild()
                    }
                }
            }
        } else {
            contacts
                .firstOrNull { it.classID == selectedClassID }
                ?.contacts?.forEach { contact ->
                    selectableRecipient {
                        id(contact.receiverID)
                        isSelected(selectRecipientsViewModel.isContactSelected(contact))
                        photo(contact.photo)
                        name(contact.name)
                        textLine1(
                            if (isParent.not())
                                "Class: - ${contact.className}"
                            else
                                "P/o: - ${contact.childName}, ${contact.className}"
                        )
                        textLine2("Roll No: - ${contact.rollNumber}")
                        textLine3("Admission No: - ${contact.admissionNo}")
                        clickListener { _ ->
                            if (selectRecipientsViewModel.isContactSelected(contact)) {
                                selectRecipientsViewModel.removeContact(contact)
                            } else {
                                selectRecipientsViewModel.addContact(contact)
                            }
                            this@buildClassWithContactModels.requestModelBuild()
                        }
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