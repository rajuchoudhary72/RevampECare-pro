package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.profile.MyProfileData
import com.app.ecarepro.core.domain.model.profile.StaffProfileUpdateRequest
import com.app.ecarepro.core.domain.repository.MyProfileRepository
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.network.MyProfileRemoteDataSource
import com.app.ecarepro.core.network.model.profile.NetworkStaffProfileRequest
import com.app.ecarepro.core.network.model.profile.NetworkUpdateParentProfileRequest
import com.app.ecarepro.core.network.model.profile.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class MyProfileRepositoryImpl @Inject constructor(
    private val dataSource: MyProfileRemoteDataSource,
    private val userRepository: UserRepository,
) : MyProfileRepository {

    override fun getProfile(): Flow<Result<MyProfileData>> = asResultFlow {
        val user = userRepository.getActiveUser()
        val userType = user?.userType ?: 3
        val response = dataSource.getProfile("false")
        val canEditProfile = response.canEditProfile ?: false
        val profileData = response.profile
            ?: throw Exception(response.message.ifEmpty { "Failed to load profile" })
        profileData.toDomainModel(userType = userType, canEditProfile = canEditProfile)
    }

    override fun sendStaffProfileRequest(request: StaffProfileUpdateRequest): Flow<Result<Unit>> =
        asResultFlow {
            val networkRequest = NetworkStaffProfileRequest(
                fName = request.fName,
                mName = request.mName,
                lName = request.lName,
                mobile = request.mobile,
                emailID = request.emailID,
                alternateEmailID = request.alternateEmailID,
                alternateMobile = request.alternateMobile,
                emergencyContactNo = request.emergencyContactNo,
                fatherHusbandName = request.fatherHusbandName,
                fatherHusbandMob = request.fatherHusbandMob,
                address = request.address,
                pAddress = request.permanentAddress,
                qualification = request.qualification,
                aadharCardNo = request.aadhar,
                paNNumber = request.pan,
                cbseid = request.cbseId,
                uaNNumber = request.uan,
                nationalCode = request.nationalCode,
                dob = request.dob,
                doAnniversary = request.doAnniversary,
                isMaritialStatusID = request.isMaritalStatusChanged,
                isBloodGroupID = request.isBloodGroupChanged,
                isRelegionID = request.isReligionChanged,
                isNationalityID = request.isNationalityChanged,
            )
            dataSource.sendStaffProfileRequest(networkRequest)
            Unit
        }

    override fun updateParentProfile(mobile: String, email: String, address: String): Flow<Result<Unit>> =
        asResultFlow {
            val request = NetworkUpdateParentProfileRequest(
                contactMobile = mobile,
                contactEmailID = email,
                address = address,
            )
            dataSource.updateParentProfile(request)
            Unit
        }
}
