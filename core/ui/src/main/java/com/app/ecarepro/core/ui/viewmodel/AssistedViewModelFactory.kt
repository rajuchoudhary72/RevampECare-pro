package com.app.ecarepro.core.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel

/**
 * A generic factory that any Hilt @AssistedFactory can implement.
 *
 * @param P The type of the assisted parameter (e.g., a navigation key).
 * @param VM The type of the ViewModel to be created.
 */
interface AssistedViewModelFactory<in P : Any, out VM : ViewModel> {
    fun create(param: P): VM
}

/**
 * A composable helper that delegates the creation of an assisted ViewModel.
 *
 * It uses the generic [AssistedViewModelFactory] to create a ViewModel instance,
 * providing the required assisted parameter (`param`).
 *
 * @param param The assisted parameter to pass to the ViewModel's constructor (e.g., a navKey).
 */
@Composable
inline fun <reified VM : ViewModel, P : Any> navKeyViewModel(
    param: P,
): VM {
    // The 'creationCallback' is where the "delegation" happens.
    // We call the 'create' method from our standardized factory interface.
    return hiltViewModel<VM, AssistedViewModelFactory<P, VM>>(
        creationCallback = { factory ->
            factory.create(param)
        }
    )
}