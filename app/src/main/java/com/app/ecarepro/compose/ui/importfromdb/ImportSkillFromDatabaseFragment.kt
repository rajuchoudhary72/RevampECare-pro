package com.app.ecarepro.compose.ui.importfromdb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.compose.theme.ECareProTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImportSkillFromDatabaseFragment : Fragment() {

    private val mViewModel: ImportSkillFromDatabaseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ECareProTheme {
                    ImportSkillFromDatabase(
                        viewModel = mViewModel,
                        onClickBack = { findNavController().popBackStack() },
                    )
                }
            }
        }
    }
}