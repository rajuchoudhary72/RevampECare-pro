package com.app.ecarepro.ui.message.selectRecipients

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.airbnb.epoxy.EpoxyController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.ClassContact
import com.app.ecarepro.data.network.model.Contact
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.StaffType
import com.app.ecarepro.data.network.model.StaffTypeDto
import com.app.ecarepro.databinding.FragmentPagerSelectRecipientsBinding
import com.app.ecarepro.model.RecipientsType
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.selectableClassView
import com.app.ecarepro.selectableRecipient
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.message.MessageViewModel
import com.app.ecarepro.utils.Constant
import com.rubensousa.decorator.LinearDividerDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectRecipientPagerFragment : Fragment() {

    private var _binding: FragmentPagerSelectRecipientsBinding? = null
    private val binding get() = _binding!!

    private val selectRecipientsPagerViewModel: SelectRecipientsPagerViewModel by viewModels()

    private val selectRecipientsViewModel: SelectRecipientsViewModel by activityViewModels()

    private val messageViewModel: MessageViewModel by activityViewModels()

    private var searchQuery: String? = null
    private lateinit var userData: NetworkUserDetailsDto
    private val systemViewModel: SystemViewModel by viewModels()
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

        binding.checkboxSelectAll.setOnClickListener {
            handleSelectAllContacts(binding.checkboxSelectAll.isChecked)
        }

        binding.selectClassId.setOnClickListener {
            selectRecipientsPagerViewModel.setSelectedClassId(null, null)
            binding.recyclerView.requestModelBuild()
        }

        binding.spinnerLayout.setOnClickListener {
            SelectStaffTypesFragment
                .getInstance(
                    StaffTypeDto(
                        staffType = selectRecipientsPagerViewModel.staffTypes.value?.getOrNull()
                            ?: emptyList(),
                        selectedStaffType = selectRecipientsPagerViewModel.getSelectedStaffType()
                    )

                )
                .onContactSelected {
                    binding.spinnerLayout.text = ""
                    binding.spinnerLayout.text = it.joinToString { it.staffType ?: "" }
                    selectRecipientsPagerViewModel.setSelectedStaffType(it)
                }
                .show(childFragmentManager, "")
        }

        binding.recyclerView.addItemDecoration(
            LinearDividerDecoration.create(
                color = ContextCompat.getColor(
                    requireContext(),
                    R.color.green_10
                ),
                size = resources.getDimensionPixelSize(R.dimen.divider_size)
            )
        )
       /* viewLifecycleOwner.lifecycleScope.launch {
            launch {
            systemViewModel.user.collectLatest {
                if (it != null) {
                    userData = it
                }
            }
        }
    }
        if (userData.userType == Constant.STUDENT_TYPE||userData.userType == Constant.PARENT_TYPE) {
            binding.spinnerLayout.isVisible =false
        }else{
            binding.spinnerLayout.isVisible =true
        }*/
        viewLifecycleOwner.lifecycleScope.launch {

            launch {
                messageViewModel.messageSettings.collectLatest { settings ->
                    binding.filterRadioGroup.isVisible =
                        settings?.isBoardingSchool == true && selectRecipientsPagerViewModel.recipientsType.value != RecipientsType.STAFFS
                }
            }

            launch {
                selectRecipientsPagerViewModel
                    .searchQuery
                    .debounce(300)
                    .distinctUntilChanged()
                    .collectLatest { query ->
                        if (query != null) {
                            searchQuery = query
                            binding.recyclerView.requestModelBuild()
                        }
                    }
            }

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

            }

        }
        selectRecipientsPagerViewModel.staffTypes.observe(
            viewLifecycleOwner

        ) { result ->
            result.getOrNull()?.let { staffTypes ->
                //setUpStaffTypeSpinner(staffTypes)
            }
        }
    }

    private fun handleSelectAllContacts(isChecked: Boolean) {
        when (selectRecipientsPagerViewModel.recipientsType.value) {
            RecipientsType.PARENTS -> {
                selectRecipientsPagerViewModel.uiState.value.getParentContactOrNull()
                    ?.let { classContacts ->
                        val selectedClassID =
                            selectRecipientsPagerViewModel.getSelectedClassIds()
                        if (selectedClassID != null) {
                            classContacts.firstOrNull { it.classID == selectedClassID }
                                ?.let { contact ->
                                    if (isChecked) {
                                        selectRecipientsViewModel.addContacts(
                                            contact.contacts ?: emptyList()
                                        )
                                    } else {
                                        selectRecipientsViewModel.removeContacts(
                                            contact.contacts ?: emptyList()
                                        )
                                    }
                                }
                        } else {
                            classContacts.forEach { contact ->
                                if (isChecked) {
                                    selectRecipientsViewModel.addContacts(
                                        contact.contacts ?: emptyList()
                                    )
                                } else {
                                    selectRecipientsViewModel.removeContacts(
                                        contact.contacts ?: emptyList()
                                    )
                                }
                            }
                        }
                    }
            }

            RecipientsType.STUDENTS -> {
                selectRecipientsPagerViewModel.uiState.value.getStudentContactOrNull()
                    ?.let { classContacts ->
                        val selectedClassID =
                            selectRecipientsPagerViewModel.getSelectedClassIds()
                        if (selectedClassID != null) {
                            classContacts.firstOrNull { it.classID == selectedClassID }
                                ?.let { contact ->
                                    if (isChecked) {
                                        selectRecipientsViewModel.addContacts(
                                            contact.contacts ?: emptyList()
                                        )
                                    } else {
                                        selectRecipientsViewModel.removeContacts(
                                            contact.contacts ?: emptyList()
                                        )
                                    }
                                }
                        } else {
                            classContacts.forEach { contact ->
                                if (isChecked) {
                                    selectRecipientsViewModel.addContacts(
                                        contact.contacts ?: emptyList()
                                    )
                                } else {
                                    selectRecipientsViewModel.removeContacts(
                                        contact.contacts ?: emptyList()
                                    )
                                }
                            }
                        }
                    }
            }

            RecipientsType.STAFFS -> {
                selectRecipientsPagerViewModel.uiState.value.getStaffContactOrNull()
                    ?.let { contacts ->
                        if (isChecked) {
                            selectRecipientsViewModel.addContacts(contacts)
                        } else {
                            selectRecipientsViewModel.removeContacts(contacts)
                        }
                    }
            }
        }
        binding.recyclerView.requestModelBuild()
    }

    private fun checkSelectAllButton() {
        when (selectRecipientsPagerViewModel.recipientsType.value) {
            RecipientsType.PARENTS -> {
                selectRecipientsPagerViewModel.uiState.value.getParentContactOrNull()
                    ?.let { classContacts ->
                        val selectedClassID =
                            selectRecipientsPagerViewModel.getSelectedClassIds()
                        if (selectedClassID != null) {
                            classContacts
                                .firstOrNull { it.classID == selectedClassID }
                                ?.let { contact ->
                                    binding.checkboxSelectAll.isChecked =
                                        selectRecipientsViewModel.isContactsSelected(
                                            contact.contacts ?: emptyList()
                                        )
                                }
                        } else {
                            val contacts = mutableListOf<Contact>()
                            classContacts.forEach { contact ->
                                contacts.addAll(contact.contacts ?: emptyList())
                            }
                            binding.checkboxSelectAll.isChecked =
                                selectRecipientsViewModel.isContactsSelected(contacts)
                        }
                    }
            }

            RecipientsType.STUDENTS -> {
                selectRecipientsPagerViewModel.uiState.value.getStudentContactOrNull()
                    ?.let { classContacts ->
                        val selectedClassID =
                            selectRecipientsPagerViewModel.getSelectedClassIds()
                        if (selectedClassID != null) {
                            classContacts
                                .firstOrNull { it.classID == selectedClassID }
                                ?.let { contact ->
                                    binding.checkboxSelectAll.isChecked =
                                        selectRecipientsViewModel.isContactsSelected(
                                            contact.contacts ?: emptyList()
                                        )
                                }
                        } else {
                            val contacts = mutableListOf<Contact>()
                            classContacts.forEach { contact ->
                                contacts.addAll(contact.contacts ?: emptyList())
                            }
                            binding.checkboxSelectAll.isChecked =
                                selectRecipientsViewModel.isContactsSelected(contacts)
                        }
                    }
            }

            RecipientsType.STAFFS -> {
                selectRecipientsPagerViewModel.uiState.value.getStaffContactOrNull()
                    ?.let { contacts ->
                        binding.checkboxSelectAll.isChecked =
                            selectRecipientsViewModel.isContactsSelected(contacts)
                    }
            }
        }
    }

    private fun setUpStaffTypeSpinner(staffTypes: List<StaffType>) {

    }

    private fun handleUiState(uiState: SelectRecipientsUiState) {
        Log.e("handleUiState: ", uiState.toString())
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message?:"")
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
                        selectRecipientsPagerViewModel.getSelectedClassIds(),
                        uiState.contacts,
                        false
                    )
                }

                is SelectRecipientsUiState.ParentContact -> {
                    buildClassWithContactModels(
                        selectRecipientsPagerViewModel.getSelectedClassIds(),
                        uiState.contacts,
                        true
                    )
                }

                is SelectRecipientsUiState.StaffContact -> {
                    uiState
                        .contacts
                        .filter { contact ->
                            contact.name.contains(
                                searchQuery?.toLowerCase() ?: "",
                                true
                            )
                        }
                        .forEachIndexed { index, contact ->
                            selectableRecipient {
                                id(contact.hashCode()+index)
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
                                    checkSelectAllButton()
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
            contacts
                .filter { contact ->
                    contact.className?.contains(
                        searchQuery?.toLowerCase() ?: "",
                        true
                    ) ?: true
                }
                .forEach { classContact ->
                    selectableClassView {
                        id(classContact.classID)
                        className(classContact.className)
                        isSelected(
                            selectRecipientsViewModel.isContactsSelected(
                                classContact.contacts ?: emptyList()
                            )
                        )
                        onClickViewAll { _ ->
                            selectRecipientsPagerViewModel.setSelectedClassId(
                                classId = classContact.classID,
                                className = classContact.className ?: ""
                            )
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
                            checkSelectAllButton()
                        }
                    }
                }
        } else {
            contacts
                .firstOrNull { it.classID == selectedClassID }
                ?.contacts
                ?.filter { contact ->
                    contact.name.contains(
                        searchQuery?.toLowerCase() ?: "",
                        true
                    ) || contact.childName?.contains(
                        searchQuery?.toLowerCase() ?: "",
                        true
                    )?:false
                }
                ?.forEachIndexed {index, contact ->
                    selectableRecipient {
                        id(contact.hashCode()+index)
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
                            checkSelectAllButton()
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