package com.app.ecarepro.ui.institutioncode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.databinding.FragmentInstitutionCodeBinding
import com.app.ecarepro.schoolCode
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.searchinstitution.SearchInstitutionFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class InstitutionCodeFragment : Fragment() {

    private var _binding: FragmentInstitutionCodeBinding? = null

    private val binding get() = _binding!!

    private val institutionCodeViewModel: InstitutionCodeViewModel by viewModels()

    @Inject
    lateinit var userDataStore: UserDataStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInstitutionCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            val schoolCode = runBlocking { userDataStore.getCurrentSchoolCode() }
            if (args.containsKey("add_account") && args.containsKey("change_school")
                    .not() && schoolCode.isNullOrEmpty().not()
            ) {
                navigateToSignFragment(schoolCode!!)
            }
        }

        institutionCodeViewModel.schools.observe(viewLifecycleOwner) { schools ->
            lifecycleScope.launch {
                binding.carouselSchool.isVisible = schools.isNullOrEmpty().not() && institutionCodeViewModel.isUserAuthenticated()
            }
            schools.forEach { school ->
                binding.carouselSchool.withModels {
                    schoolCode {
                        id(school.schoolCode)
                        photo(school.logo)
                        code(school.schoolCode)
                        isSelected(school.isSelected)
                        clickListener { _ ->
                            lifecycleScope.launch {
                                userDataStore.setCurrentSchoolCode(school.schoolCode)
                                navigateToSignFragment(school.schoolCode)
                            }
                        }
                    }
                }
            }
        }

        binding.textInstitutionCode.setOtpCompletionListener {
            binding.btnContinue.isEnabled = true
        }

        binding.btnContinue.setOnClickListener {
            (requireActivity() as MainActivity).showLoader(true)
            institutionCodeViewModel.validateSchoolCode(binding.textInstitutionCode.text.toString()) {
                (requireActivity() as MainActivity).showLoader(false)
                if (it?.errorCode == 0) {
                    navigateToSignFragment(it.schoolCode)
                } else {
                    mainActivity().showMessage(it!!.message.toString())
                }
            }
        }
        binding.btnFindSchoolCollege.setOnClickListener {
            setFragmentResultListener(SearchInstitutionFragment.REQUEST_KEY_SCHOOL_CODE) { _, data ->
                data.getString(SearchInstitutionFragment.PRAM_SCHOOL_CODE)?.let {
                    binding.textInstitutionCode.setText(it)
                }
            }
            findNavController().navigate(R.id.searchInstitutionFragment)
        }
        binding.btnHelp.setOnClickListener {
            findNavController().navigate(R.id.helpFragment)
        }
    }

    private fun navigateToSignFragment(schoolCode: String) {
        findNavController().navigate(
            resId = R.id.signInFragment,
            args = if (arguments == null) {
                bundleOf("schoolCode" to schoolCode)
            } else {
                arguments?.apply {
                    putString("schoolCode", schoolCode)
                }
            },
            navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.schoolCodeFragment, true)
                .build()
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}