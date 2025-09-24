package com.app.ecarepro.ui.signin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.BuildConfig
import com.app.ecarepro.R
import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.submit_assignment.TwoFactorLoginResponseDto
import com.app.ecarepro.databinding.FragmentSignInBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.otpverification.OtpVerificationFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.Locale
import java.util.concurrent.ExecutionException
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment() {
    val canChangeSchoolCode = BuildConfig.FLAVOR == "Franciscan e-Care"

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: SignInViewModel by viewModels()

    private var userNameValid = false
    private val systemViewModel: SystemViewModel by activityViewModels()

    @Inject
    lateinit var userDataStore: UserDataStore

    @Inject
    lateinit var userDatabase: UserDatabase

    @Inject
    lateinit var analyticsManager: AnalyticsManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
        }
        return binding.root

    }
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    private fun startLocationFetch() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 120
            )
            return
        }
        if (isGPSEnabled().not()) {
            MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.turn_on_gps))
                .setCancelable(false)
                .setMessage(getString(R.string.gps_is_disabled_in_your_device_would_you_like_to_enable_it))
                .setPositiveButton(getString(R.string.no)) { d, _ ->
                    d.dismiss()
                    findNavController().popBackStack()
                }.setPositiveButton(getString(R.string.goto_settings_to_enable_gps)) { d, _ ->
                    d.dismiss()
                    val callGPSSettingIntent = Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    )
                    startActivity(callGPSSettingIntent)
                }.show()
        } else {
            fusedLocationClient
                .lastLocation
                .addOnSuccessListener { location: Location? ->
                    mViewModel.currentLocation =
                        Pair(location?.latitude ?: 0.0, location?.longitude ?: 0.0)


                    lifecycleScope.launch(Dispatchers.IO) {
                        val cityName = if (location != null) {
                            getCityNameSafely(location.latitude, location.longitude)
                        } else {
                            "India"
                        }
                        Log.e("MSG", "startLocationFetch: " + cityName)
                        Log.e("MSG",
                            ("startLocationFetch2: " + cityName) ?: Locale.ENGLISH.displayName
                        )
                        Log.d("startLocationFetch", "startLocationFetch1: $cityName")
                        mViewModel.setCityName(cityName ?: "India ")
                    }

                }
                .addOnFailureListener {
                    Log.e("MSG", "startLocationFetch: " + it.message)
                }
        }
    }


    private suspend fun getCityNameSafely(latitude: Double, longitude: Double): String {
        return try {
            // Check if Geocoder is available
            if (!Geocoder.isPresent()) {
                Log.w("Geocoder", "Geocoder service is not available")
                return "India" // fallback
            }

            val geocoder = Geocoder(requireContext(), Locale.ENGLISH)
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                addresses[0].locality ?: addresses[0].adminArea ?: "India"
            } else {
                "India"
            }
        } catch (e: IOException) {
            Log.e("Geocoder", "Geocoder service unavailable: ${e.message}")
            "India" // fallback to default
        } catch (e: Exception) {
            Log.e("Geocoder", "Unexpected error: ${e.message}")
            "India"
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 120) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationFetch()
            } else {
                mainActivity().showMessage(getString(R.string.gps_permission_denied))
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textUserName.doAfterTextChanged {
            binding.btnContinue.isEnabled = it.isNullOrBlank().not()
        }

        binding.textPassword.doAfterTextChanged {
            binding.btnContinue.isEnabled = it.isNullOrBlank().not()
        }
        binding.btnHelp.setOnClickListener {
            findNavController().navigate(
                R.id.helpFragment,
                bundleOf("schoolCode" to mViewModel.schoolCode)
            )
        }
        binding.btnFindSchoolCollege.isVisible = canChangeSchoolCode
        binding.btnFindSchoolCollege.setOnClickListener {
            findNavController().navigate(
                R.id.schoolCodeFragment,
                bundleOf("add_account" to true, "change_school" to true),
                NavOptions.Builder()
                    .setPopUpTo(R.id.signInFragment, true)
                    .build()
            )
        }

        binding.btnContinue.setOnClickListener {
            (requireActivity() as MainActivity).showLoader(true)
            if (userNameValid) {
                mViewModel.login(
                    binding.textUserName.text.toString(),
                    binding.textPassword.text.toString(),
                ) {
                    (requireActivity() as MainActivity).showLoader(false)
                    if (it.errorCode == 0) {
                        systemViewModel.refresh.tryEmit(true)
                        if (it.authenticated == true) {
                            if (it.isOTPEnabled == true) {
                                mainActivity().showMessage(it.message.toString())
                                try {
                                    setFragmentResultListener(OtpVerificationFragment.REQUEST_TYPE_OPT_VERIFICATION) { requestKey, bundle ->
                                        if (bundle.getBoolean(OtpVerificationFragment.IS_OTP_VERIFIED)) {
                                            launchToNextDesctinationAfterLogin(it)
                                        }
                                    }
                                } catch (e: NullPointerException) {

                                }
                                findNavController().navigate(
                                    R.id.otpVerificationFragment,
                                    bundleOf(
                                        "message" to it.message,
                                        "userName" to binding.textUserName.text.toString(),
                                        "schoolCode" to mViewModel.schoolCode,
                                        "oTPAuthKey" to it.otpAuthKey,
                                    )
                                )
                            } else {
                                launchToNextDesctinationAfterLogin(it)
                            }

                        } else {
                            mainActivity().showMessage(" " + it.authenticated)
                        }
                    } else if (it.errorCode == 401) {
                        mainActivity().showMessage(getString(R.string.invalid_password))
                    } else if (it.errorCode == 429) {
                        mainActivity().showMessage(it.message.toString())
                    } else if (it.errorCode == 404) {
                        mainActivity().showMessage(it.message.toString())
                    }


                    Log.i("Token Aut", it.userDTL?.authToken.toString())
                }
            } else {
                mViewModel.verifyUser(binding.textUserName.text.toString()) {
                    (requireActivity() as MainActivity).showLoader(false)
                    if (it.errorCode == 0) {
                        if (runBlocking {
                                mViewModel.isUserAlreadyLogin(it.userId, it.userType)
                            }) {
                            mainActivity().showMessage(getString(R.string.user_already_login))
                        } else {
                            userNameValid = true
                            binding.textInputLayoutPassword.isVisible = true
                            binding.textInputLayoutUserName.isEnabled = false
                            binding.textUserName.isEnabled = false
                            binding.textUserName.isClickable = false
                        }
                    } else {
                        mainActivity().showMessage(getString(R.string.invalid_username))
                    }
                }
            }
        }
        binding.btnForgotPassword.setOnClickListener {
            try {
                findNavController().navigate(
                    R.id.action_signInFragment_to_forgotPasswordFragment, bundleOf(
                        "schoolCode" to mViewModel.schoolCode,
                        "isStudentLoginBlocked" to mViewModel.isStudentLoginBlocked
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.btnPrevious.setOnClickListener {
            if (userNameValid) {
                binding.textInputLayoutPassword.isVisible = false
                binding.textPassword.setText("")
                binding.textInputLayoutUserName.isEnabled = true
                binding.textUserName.isEnabled = true
                binding.textUserName.isClickable = true
                binding.textUserName.requestFocus()
                binding.textUserName.setText(binding.textUserName.text.toString())
                userNameValid = false
            } else {
                findNavController().popBackStack()
            }
        }
        startLocationFetch()

    }

    private fun launchToNextDesctinationAfterLogin(it: TwoFactorLoginResponseDto) {
        val isAddAccount = arguments?.containsKey("add_account") == true
        analyticsManager.setUserProperties()
        analyticsManager.trackEvent(
            AnalyticsConstants.Events.LOGIN,
            mapOf(
                AnalyticsConstants.Attributes.USER_NAME to _binding?.textUserName?.text.toString(),
                AnalyticsConstants.Attributes.SIGN_IN_TYPE to if (isAddAccount) AnalyticsConstants.Attributes.ADD_ACCOUNT else AnalyticsConstants.Attributes.NORMAL_LOGIN,
            )
        )
        if (isAddAccount) {
            viewLifecycleOwner.lifecycleScope.launch {
                userDatabase.getUser(
                    it.userDTL?.userID ?: 0,
                    mViewModel.schoolCode,
                    it.userDTL?.userType ?: 0
                )?.id?.let {
                    userDataStore.setCurrentUserId(it)
                }
                restartApp()
            }
        } else {
            mainActivity().checkAppVersion()
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(
                            "FCM Token",
                            "Fetching FCM registration token failed",
                            task.exception
                        )
                        return@OnCompleteListener
                    }

                    // Get new FCM registration token
                    val token = task.result

                    // Log and toast
                    Log.d("FCM Token", token)
                    systemViewModel.registerDeviceToken(token)
                })
                .addOnFailureListener { e ->
                    if (e is IOException) {
                        Log.e("FCM Token", "Network error", e)
                    } else if (e is ExecutionException) {
                        Log.e("FCM Token", "Execution error", e)
                    } else {
                        Log.e("FCM Token", "Unknown error", e)
                    }
                }

            try {
                findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
                /*if (it.isDefaulter == true){
                    mainActivity().unPaidClass()
                }*/
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun restartApp() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        Runtime.getRuntime().exit(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}