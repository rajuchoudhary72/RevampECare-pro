package com.app.ecarepro.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var usetDataStore: com.app.ecarepro.data.datastore.UserDataStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

            cardChangePassword.setOnClickListener { findNavController().navigate(R.id.changePasswordFragment) }

            cardChangeUserName.setOnClickListener { findNavController().navigate(R.id.changeUsernameFragment) }

            cardRateUs.setOnClickListener { launchPlayStore() }

            viewLifecycleOwner.lifecycleScope.launch {
                lastSyncTime.text = "Last Sync : ${usetDataStore.getUser()?.loginTime}"
            }

        }
    }


    private fun launchPlayStore() {
        var intent: Intent? = null
        try {
            intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setData(Uri.parse("market://details?id=${requireContext().packageName}"))
            startActivity(intent)
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        "https://play.google.com/store/apps/details?id=${requireContext().packageName}"
                    )
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}