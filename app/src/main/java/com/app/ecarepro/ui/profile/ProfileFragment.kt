package com.app.ecarepro.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.ecarepro.BuildConfig
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyController
import com.app.ecarepro.R
import com.app.ecarepro.account
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.Profile
import com.app.ecarepro.databinding.FragmentProfileBinding
import com.app.ecarepro.profileAddAccount
import com.app.ecarepro.profileHeader
import com.app.ecarepro.profileItem
import com.app.ecarepro.profileLogout
import com.app.ecarepro.profileWardDetails
import com.app.ecarepro.space
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.FileAccess
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var photoType: PhotoType




    @Inject
    lateinit var userDataStore: UserDataStore

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                // binding.ivAddedImage.setImageURI(imgUri)

                val bitmap = FileAccess.bitmapFromUri(requireContext(), imgUri)

                val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)

                val imageExt = FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()

                uploadPhoto(imageString, imageExt)

            }
        }

    private fun uploadPhoto(imageString: String, imageExt: String) {
        (requireActivity() as MainActivity).showLoader(true)
        profileViewModel.uploadPhoto(
            photoType = photoType,
            base64Text = imageString,
            ext = imageExt
        ) { _, message ->
            (requireActivity() as MainActivity).showLoader(false)
            mainActivity().showMessage(message)
        }
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap
                    // binding.ivAddedImage.setImageBitmap(bitmap)

                    val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)

                    val imageExt =
                        FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()

                    uploadPhoto(imageString, imageExt)

                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.uiState.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.CREATED
            ).collectLatest { uiState ->
                handleUiState(uiState)
            }
        }

        setUpViews()


    }

    private fun handleUiState(uiState: ProfileUiState) {
        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message?:"")
        }

        if (uiState is ProfileUiState.Success) {
            binding.recyclerView.withModels {
                profileHeader {
                    id(uiState.profile.username)
                    bannerImage(if (profileViewModel.isParent()) uiState.profile.studentProfile?.coverImg else uiState.profile.coverImg)
                    profileImage(uiState.profile.photo)
                    name(uiState.profile.name)
                    designation(if (profileViewModel.isParent()) "Parent" else if (profileViewModel.isStudent()) "Class " + uiState.profile.className else uiState.profile.designation)
                    username(uiState.profile.username)
                    contactNumber(uiState.profile.emergencyContactNo)
                /*    canEditBannerImage(uiState.profile.canChangeCoverImg ?: false && uiState.profile.userImgReq?.coverImg != 1)
                    canEditProfileImage(uiState.profile.canChangeProfileImg ?: false && uiState.profile.userImgReq?.profileImg != 1)*/

                    canEditBannerImage(uiState.profile.canChangeCoverImg ?: true && (uiState.profile.userImgReq == null || uiState.profile.userImgReq?.coverImg != 1))
                    canEditProfileImage(uiState.profile.canChangeProfileImg ?: true && (uiState.profile.userImgReq == null || uiState.profile.userImgReq?.profileImg != 1))

                    profileImageStatus(uiState.profile.userImgReq?.profileImg)
                    bannerImageStatus(uiState.profile.userImgReq?.coverImg)
                    clickListener { v: View ->
                        when (v.id) {
                            R.id.fabBannerImage -> {
                                photoType = PhotoType.COVER_PHOTO
                                selectImageOptionDialog()
                            }

                            R.id.fabProfileImage -> {
                                photoType = PhotoType.PROFILE_PHOTO
                                selectImageOptionDialog()
                            }
                        }
                    }
                }


                if (profileViewModel.isParent()) {
                    buildParentModels(uiState.profile)
                } else if (profileViewModel.isStudent()) {
                    buildStudentModels(uiState.profile)
                } else {
                    buildStaffModels(uiState.profile)
                }

                space {
                    id("space")
                }

                uiState.users.forEach {
                    account {
                        id(it.userId)
                        name(
                            if(it.name.isNullOrEmpty()){
                                "N/A (${it.roleName})"
                            }else {
                                it.name + "(${it.roleName})"
                            }

                        )
                        photo(it.photo)
                        school(it.school)
                        isCurrentUser(it.userId == uiState.currentUserId)
                        changeUser { _ ->
                            lifecycleScope.launch {
                                userDataStore.setCurrentUserId(it.userId)
                                restartApp()
                            }
                        }
                        removeAccountListener { _ ->
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Remove Account")
                                .setMessage("Are you sure to remove ${it.name}")
                                .setPositiveButton("Yes") { _, _ ->
                                    profileViewModel.removeUser(it)
                                    restartApp()
                                }.setNegativeButton("No") { _, _ ->

                                }
                                .show()
                        }
                    }
                }

                profileAddAccount {
                    id(23)
                    clickListener { _ ->
                        findNavController().navigate(
                            R.id.schoolCodeFragment,
                            bundleOf("add_account" to true)
                        )
                    }
                }

                profileLogout {
                    id(131)
                    version("v ${BuildConfig.VERSION_NAME}")
                    clickListener { _ ->
                        (requireActivity() as MainActivity).logout()
                    }
                }
            }
        }
    }

    private fun restartApp() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        Runtime.getRuntime().exit(0)
    }

    private fun selectImageOptionDialog() {
        val items = arrayOf<CharSequence>(
            "Take Photo", "Choose from Library",
            "Cancel"
        )
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Photo!")
        builder.setItems(items, DialogInterface.OnClickListener { dialog, item ->
            FileAccess.checkPermission(this)
            if (items[item] == "Take Photo") {
                cameraLauncher.launch(FileAccess.cameraIntent())
            } else if (items[item] == "Choose from Library") {
                galleryLauncher.launch(FileAccess.galleryIntent())
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
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
            id(R.string.marital_status)
            iconRes(R.drawable.ic_material_status)
            title(getString(R.string.marital_status))
            subTitle(profile.maritalStatus)
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
            id(R.string.cbseid)
            iconRes(R.drawable.pan_card_icon)
            title(getString(R.string.cbseid))
            subTitle(profile.cbseID)
        }
        profileItem {
            id(R.string.club)
            iconRes(R.drawable.pan_card_icon)
            title(getString(R.string.club))
            subTitle(profile.club)
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

    private fun EpoxyController.buildStudentModels(profile: Profile) {
        profileItem {
            id(R.string.admission_number)
            iconRes(R.drawable.ic_baseline_menu_book_24)
            title(getString(R.string.admission_number))
            subTitle(profile.admissionNo)
        }

        profileItem {
            id(R.string.date_of_admission)
            iconRes(R.drawable.ic_date_of_aniversery)
            title(getString(R.string.date_of_admission))
            subTitle(profile.admissionDate)
        }

        profileItem {
            id(R.string.date_of_birth)
            iconRes(R.drawable.ic_date_of_birth)
            title(getString(R.string.date_of_birth))
            subTitle(profile.dob)
        }
        profileItem {
            id(R.string.permanent_education_number)
            iconRes(R.drawable.avd_dashboard)
            title(getString(R.string.permanent_education_number))
            subTitle(profile.admissionNo)
        }
        profileItem {
            id(R.string.fathers_name)
            iconRes(R.drawable.ic_person)
            title(getString(R.string.fathers_name))
            subTitle(profile.fatherName)
        }
        profileItem {
            id(R.string.mothers_name)
            iconRes(R.drawable.ic_person)
            title(getString(R.string.mothers_name))
            subTitle(profile.motherName)
        }
        profileItem {
            id(R.string.blood_group)
            iconRes(R.drawable.ic_chat_bubble)
            title(getString(R.string.blood_group))
            subTitle(profile.bloodGroup)
        }
        profileItem {
            id(R.string.cbseid)
            iconRes(R.drawable.pan_card_icon)
            title(getString(R.string.cbseid))
            subTitle(profile.cbseID)
        }
        profileItem {
            id(R.string.club)
            iconRes(R.drawable.pan_card_icon)
            title(getString(R.string.club))
            subTitle(profile.club)
        }
        profileItem {
            id(R.string.house_name)
            iconRes(R.drawable.outline_help_outline_24)
            title(getString(R.string.house_name))
            subTitle(profile.house)
        }
        profileItem {
            id(R.string.address)
            iconRes(R.drawable.ic_address)
            title(getString(R.string.address))
            subTitle(profile.address)
        }
        profileItem {
            id(R.string.contact_number)
            iconRes(R.drawable.ic_contact_no_)
            title(getString(R.string.contact_number))
            subTitle(profile.contactMobile)
        }
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
            clickListener { _ ->
                photoType = PhotoType.CHILD_PHOTO
                selectImageOptionDialog()
            }
        }
    }
 private fun setUpViews() {
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        }
     viewLifecycleOwner.lifecycleScope.launch {
         userDataStore.getUser()?.run {
             binding.tvEditProfile.isVisible =   Constant.PARENT_TYPE == userType
         }
     }

        binding.tvEditProfile.setOnClickListener {
            findNavController().navigate(R.id.editProfileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class PhotoType(val type: Int) {
    PROFILE_PHOTO(1),
    COVER_PHOTO(2),
    CHILD_PHOTO(3)
}