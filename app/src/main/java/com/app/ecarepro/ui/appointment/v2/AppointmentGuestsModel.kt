package com.app.ecarepro.ui.appointment.v2

import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.Form
import com.app.ecarepro.databinding.ItemGuestUsersChipGroupBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.google.android.material.chip.Chip

class AppointmentGuestsModel(private val form: Form, val addToGuestList:(String) -> Unit, val removeToGuestList:(String) -> Unit) :ViewBindingKotlinModel<ItemGuestUsersChipGroupBinding>(R.layout.item_guest_users_chip_group) {
    override fun ItemGuestUsersChipGroupBinding.bind() {

        filedName.text = form.columnName
        textInputLayoutUserName.hint = form.columnDisplayName
        textInputLayoutUserName.isHintEnabled = true
        imageView.isVisible = form.isrequired == true
        chipGroup.removeAllViews()
        form.guestList?.forEach {name ->
            val chip = Chip(root.context)
            chip.text = name
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                removeToGuestList(name)
            }
            chipGroup.addView(chip)
        }

        textUserName.doAfterTextChanged {
            btnAdd.isEnabled = (it?.length ?: 0) > 0
        }

        btnAdd.setOnClickListener {
            val name = textUserName.text.toString()
            addToGuestList(name)
            textUserName.text?.clear()
        }

    }

    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + form.hashCode()
        result = 31 * result + addToGuestList.hashCode()
        result = 31 * result + removeToGuestList.hashCode()
        return result
    }
}