package com.app.ecarepro.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.BuildConfig
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.sync.SyncManager
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.mainActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var usetDataStore: com.app.ecarepro.data.datastore.UserDataStore

    private val viewModel: SystemViewModel by viewModels()

    @Inject
    lateinit var syncManager: SyncManager
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


            cardSync.setOnClickListener {
                /*sync  manually  from user click sync button  on setting screen */
                lifecycleScope.launch {
                    mainActivity().showLoader(true)
                    syncManager.sync { isSuccess, message ->
                        mainActivity().showLoader(false)
                        if (isSuccess) {
                            setLastSyncTime()
                            viewLifecycleOwner.lifecycleScope.launch {
                                viewModel.sendAnalyticEvent(
                                    AnalyticsConstants.Events.SYNC_SUCCESS,
                                    mapOf(
                                        AnalyticsConstants.Attributes.USER_ID to usetDataStore.getUser()?.userId.toString(),
                                        AnalyticsConstants.Attributes.USER_TYPE to usetDataStore.getUser()?.userType.toString(),
                                        AnalyticsConstants.Attributes.SCHOOL_CODE to usetDataStore.getSchoolData()?.schoolCode.toString(),
                                    )
                                )
                            }
                        }

                        mainActivity().showMessage(message)
                    }
                }
            }

            cardRateUs.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.sendAnalyticEvent(
                        AnalyticsConstants.Events.RATE_US,
                        mapOf(
                            AnalyticsConstants.Attributes.USER_ID to usetDataStore.getUser()?.userId.toString(),
                            AnalyticsConstants.Attributes.USER_TYPE to usetDataStore.getUser()?.userType.toString(),
                            AnalyticsConstants.Attributes.SCHOOL_CODE to usetDataStore.getSchoolData()?.schoolCode.toString(),
                        )
                    )
                }
                launchPlayStore()
            }

            setLastSyncTime()
        }
        generalSettings()


    }

    private fun FragmentSettingsBinding.setLastSyncTime() {
        viewLifecycleOwner.lifecycleScope.launch {
            lastSyncTime.text = "Last Sync : ${usetDataStore.getUser()?.loginTime}"
        }
    }

    private fun launchPlayStore() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    "https://play.google.com/store/apps/details?id=${requireContext().packageName.replace(".dev","")}"
                )
            ).apply {
                putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://com.android.chrome"));
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generalSettings() {
        lifecycleScope.launch {
            viewModel.generalSettingsStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error" + it)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {
                            if (it.data.errorCode == 0) {
                                if (it.data.settings != null) {
                                    for (item in it.data.settings) {
                                        if (item.settingName == "ChangeUserName") {

                                            binding.cardChangeUserName.isVisible = item.isEnabled!!
                                            break

                                        }
                                    }
                                }
                            }
                        }

                    }

                    else -> {}
                }


            }
        }
        viewModel.appGeneralSettings()
    }
}