package com.app.ecarepro.ui.message.selectRecipients

import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.network.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectRecipientsViewModel @Inject constructor(
) : ViewModel() {

    private val selectedContacts = mutableListOf<Contact>()

    fun getSelectedContacts() = selectedContacts

    fun addContact(contact: Contact) {
        selectedContacts.add(contact)
    }

    fun addContacts(contact: List<Contact>) {
        selectedContacts.addAll(contact)
    }

    fun removeContact(contact: Contact) {
        selectedContacts.remove(contact)
    }

    fun removeContacts(contacts: List<Contact>) {
        selectedContacts.removeAll(contacts)
    }

    fun isContactSelected(contact: Contact): Boolean {
        return selectedContacts.contains(contact)
    }

    fun isContactsSelected(contacts: List<Contact>): Boolean {
        if (contacts.isEmpty()) return false
        return selectedContacts.containsAll(contacts)
    }

    fun clearAllSelectedContact() {
        selectedContacts.clear()
    }
}
