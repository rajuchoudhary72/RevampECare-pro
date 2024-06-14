package com.app.ecarepro.ui.message.selectRecipients

import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.network.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class SelectRecipientsViewModel @Inject constructor(
) : ViewModel() {

    private val selectedContacts = mutableListOf<Contact>()

    fun getSelectedContacts() = selectedContacts

    fun addContact(contact: Contact) {
        if (selectedContacts.all { it.receiverType == contact.receiverType }.not()) {
            clearAllSelectedContact()
        }
        selectedContacts.add(contact)
    }

    fun addContacts(contact: List<Contact>) {
        if (contact.isNotEmpty() && selectedContacts.all { it.receiverType == contact.firstOrNull()?.receiverType }.not()) {
            clearAllSelectedContact()
        }
        selectedContacts.addAll(contact)
        Log.e("Raju", selectedContacts.size.toString() )
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
        Log.e("RAJU IS Selected", contacts.size.toString() + "  ->  " + selectedContacts.size.toString() )
        if (contacts.isEmpty()) return false
        return selectedContacts.containsAll(contacts)
    }

    fun clearAllSelectedContact() {
        selectedContacts.clear()
    }
}
