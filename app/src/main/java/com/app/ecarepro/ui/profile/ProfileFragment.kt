package com.app.ecarepro.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.Profile
import com.app.ecarepro.databinding.FragmentProfileBinding
import com.app.ecarepro.profileAddAccount
import com.app.ecarepro.profileHeader
import com.app.ecarepro.profileItem
import com.app.ecarepro.profileLogout
import com.app.ecarepro.profileWardDetails
import com.app.ecarepro.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.uiState.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.CREATED
            ).collectLatest { uiState ->
                handleUiState(uiState)
            }
        }


    }

    private fun handleUiState(uiState: ProfileUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
        }

        if (uiState is ProfileUiState.Success) {
            binding.recyclerView.withModels {
                profileHeader {
                    id(uiState.profile.username)
                    bannerImage(uiState.profile.coverImg)
                    profileImage(uiState.profile.photo)
                    name(uiState.profile.name)
                    designation(if (profileViewModel.isParent()) "Parent" else if (profileViewModel.isStudent()) uiState.profile.className else uiState.profile.designation)
                    username(uiState.profile.username)
                    contactNumber(uiState.profile.emergencyContactNo)
                    canEditBannerImage(uiState.profile.canChangeCoverImg)
                    canEditProfileImage(uiState.profile.canChangeProfileImg)
                }

                if (profileViewModel.isParent()) {
                    buildParentModels(uiState.profile)
                } else if (profileViewModel.isStudent()) {
                    //buildStudentModels()
                    buildStaffModels(uiState.profile)
                } else {
                    buildStaffModels(uiState.profile)
                }

                profileAddAccount {
                    id(23)
                }

                profileLogout {
                    id(131)
                    clickListener { _ ->
                        (requireActivity() as MainActivity).logout()
                    }
                }
            }
        }
    }

    private fun EpoxyController.buildStaffModels(profile: Profile) {

        profileItem {
            id(R.string.date_of_birth)
            iconRes(R.drawable.ic_date_of_birth)
            title(getString(R.string.date_of_birth))
            subTitle(profile.dob)
        }
        profileItem {
            id(R.string.date_of_joining)
            iconRes(R.drawable.ic_date_of_aniversery)
            title(getString(R.string.date_of_joining))
            subTitle(profile.doj)
        }
        profileItem {
            id(R.string.spouse_name)
            iconRes(R.drawable.ic_profile)
            title(getString(R.string.spouse_name))
            subTitle(profile.fatherHusbandName)
        }
        profileItem {
            id(R.string.date_of_anniversary)
            iconRes(R.drawable.ic_date_of_aniversery)
            title(getString(R.string.date_of_anniversary))
            subTitle(profile.doAnniversary)
        }
        profileItem {
            id(R.string.gender)
            iconRes(R.drawable.ic_gender)
            title(getString(R.string.gender))
            subTitle(profile.gender)
        }
        profileItem {
            id(R.string.nationality)
            iconRes(R.drawable.ic_nationality)
            title(getString(R.string.nationality))
            subTitle(profile.nationality)
        }
        profileItem {
            id(R.string.religion)
            iconRes(R.drawable.religin_icon)
            title(getString(R.string.religion))
            subTitle(profile.religion)
        }
        profileItem {
            id(R.string.qualification)
            iconRes(R.drawable.ic_baseline_menu_book_24)
            title(getString(R.string.qualification))
            subTitle(profile.qualification)
        }
        profileItem {
            id(R.string.address)
            iconRes(R.drawable.ic_address)
            title(getString(R.string.address))
            subTitle(profile.address)
        }
        profileItem {
            id(R.string.pAddress)
            iconRes(R.drawable.ic_permanent_address)
            title(getString(R.string.pAddress))
            subTitle(profile.pAddress)
        }
        profileItem {
            id(R.string.aadharCardNo)
            iconRes(R.drawable.adhar_card_icon)
            title(getString(R.string.aadharCardNo))
            subTitle(profile.aadharCardNo)
        }
        profileItem {
            id(R.string.paNNumber)
            iconRes(R.drawable.pan_card_icon)
            title(getString(R.string.paNNumber))
            subTitle(profile.paNNumber)
        }
        profileItem {
            id(R.string.bank_account_number)
            iconRes(R.drawable.ic_bank_account)
            title(getString(R.string.bank_account_number))
        }
        profileItem {
            id(R.string.uan_account_number)
            iconRes(R.drawable.ic_uan)
            title(getString(R.string.uan_account_number))
        }
        profileItem {
            id(R.string.contact_number)
            iconRes(R.drawable.ic_contact_no_)
            title(getString(R.string.contact_number))
            subTitle(profile.emergencyContactNo)
        }
        profileItem {
            id(R.string.spouse_contact_number)
            iconRes(R.drawable.ic_contact_no_)
            title(getString(R.string.spouse_contact_number))
            subTitle(profile.fatherHusbandMob)
        }
        profileItem {
            id(R.string.email_id)
            iconRes(R.drawable.ic_email_id)
            title(getString(R.string.email_id))
            subTitle(profile.emailID)
        }

    }

    private fun buildStudentModels() {
    }

    private fun EpoxyController.buildParentModels(profile: Profile) {
        profileItem {
            id(R.string.address)
            iconRes(R.drawable.ic_address)
            title(getString(R.string.address))
            subTitle(profile.studentProfile?.address)
        }
        profileItem {
            id(R.string.contact_number)
            iconRes(R.drawable.ic_contact_no_)
            title(getString(R.string.contact_number))
            subTitle(profile.studentProfile?.contactMobile)
        }

        profileWardDetails {
            id(profile.name)
            studentProfile(profile.studentProfile)
        }
    }

    private fun setUpViews() {
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}