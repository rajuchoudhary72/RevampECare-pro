package com.app.ecarepro.ui.message.selectRecipients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.app.ecarepro.model.RecipientsType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectRecipientsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val recipientsType = savedStateHandle.getStateFlow(
        SelectRecipientPagerFragment.RECIPIENTS_TYPE,
        RecipientsType.PARENTS
    )
}