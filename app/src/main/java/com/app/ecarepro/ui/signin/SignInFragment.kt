package com.app.ecarepro.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.runBlocking
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.databinding.FragmentSignInBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.mainActivity
import com.google.android.gms.tasks.OnCompleteListener
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.ExecutionException
import com.app.ecarepro.data.datastore.UserDataStore
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: SignInViewModel by viewModels()

    private var userNameValid = false
    private val systemViewModel: SystemViewModel by activityViewModels()
    @Inject
    lateinit var userDataStore: UserDataStore
    @Inject
    lateinit var userDatabase: UserDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
        }
        return binding.root

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
            findNavController().navigate(R.id.helpFragment, bundleOf("schoolCode" to mViewModel.schoolCode))
        }
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
                            if (arguments?.containsKey("add_account") == true) {
                              //  findNavController().popBackStack()
                                viewLifecycleOwner.lifecycleScope.launch {
                                    userDatabase.getUser(it.userID, mViewModel.schoolCode, it.userType)?.id?.let {
                                        userDataStore.setCurrentUserId(it)
                                    }
                                    restartApp()
                                }
                            } else {
                                FirebaseMessaging.getInstance().token
                                    .addOnCompleteListener(OnCompleteListener { task ->
                                        if (!task.isSuccessful) {
                                            Log.w("FCM Token", "Fetching FCM registration token failed", task.exception)
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

                                findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
                            }

                        } else {
                            mainActivity().showMessage(" " + it.authenticated)

                        }

                    }else    if (it.errorCode == 401) {
                        mainActivity().showMessage("Invalid password")
                    }

                    Log.i("Token Aut", it.authToken.toString())
                }
            } else {
                mViewModel.verifyUser(binding.textUserName.text.toString()) {
                    (requireActivity() as MainActivity).showLoader(false)
                    if (it.errorCode == 0) {
                        if(runBlocking {
                                mViewModel.isUserAlreadyLogin(it.userId, it.userType)
                            }){
                            mainActivity().showMessage("User already login!")
                        }else{
                            userNameValid = true
                            binding.textInputLayoutPassword.isVisible = true
                            binding.textInputLayoutUserName.isEnabled = false
                            binding.textUserName.isEnabled = false
                            binding.textUserName.isClickable = false
                        }
                    }else{
                        mainActivity().showMessage("Invalid username")
                    }
                }
            }
        }
        binding.btnForgotPassword.setOnClickListener {
          //  findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment, bundleOf("schoolCode" to mViewModel.schoolCode))
            findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment, bundleOf("schoolCode" to mViewModel.schoolCode,
                "isStudentLoginBlocked" to mViewModel.isStudentLoginBlocked))
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