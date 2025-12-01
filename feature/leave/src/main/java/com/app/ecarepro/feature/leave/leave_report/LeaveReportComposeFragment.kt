package com.app.ecarepro.feature.leave.leave_report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaveReportComposeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                EcareProTheme {
                    LeaveReportScreen(
                        onBackClick = {
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    )
                }
            }
        }
    }
}
