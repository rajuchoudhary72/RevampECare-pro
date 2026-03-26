package com.app.ecarepro.core.network.model.discipline

import com.app.ecarepro.core.domain.model.discipline.AddAppreciationFormData
import com.app.ecarepro.core.domain.model.discipline.AddInfractionFormData
import com.app.ecarepro.core.domain.model.discipline.AppreciationDetails
import com.app.ecarepro.core.domain.model.discipline.AppreciationRecord
import com.app.ecarepro.core.domain.model.discipline.AppreciationReward
import com.app.ecarepro.core.domain.model.discipline.AppreciationType
import com.app.ecarepro.core.domain.model.discipline.DisciplineUserInfo
import com.app.ecarepro.core.domain.model.discipline.InfractionConsequence
import com.app.ecarepro.core.domain.model.discipline.InfractionDetails
import com.app.ecarepro.core.domain.model.discipline.InfractionRecord
import com.app.ecarepro.core.domain.model.discipline.InfractionType

// Infraction mappers
fun NetworkInfractionType.toDomainModel() = InfractionType(
    id = infrTypeID ?: 0,
    name = infraction.orEmpty(),
)

fun NetworkInfractionConsequence.toDomainModel() = InfractionConsequence(
    id = consID ?: 0,
    name = consequences.orEmpty(),
)

fun NetworkInfractionRecord.toDomainModel() = InfractionRecord(
    infractionID = infractionID,
    infraction = infraction,
    consequences = consequences,
    designation = designation,
    stffPhoto = stffPhoto,
    admissionNo = admissionNo,
    correctiveAction = correctiveAction,
    stID = stID,
    studentName = studentName,
    point = point,
    instance = instance,
    infractionOn = infractionOn,
    staffName = staffName,
    issueBy = issueBy,
    recordClass = recordClass,
    photo = photo,
    subInfraction = subInfraction,
    isResolved = isResolved,
    canDelete = canDelete,
    showResolvedButton = showResolvedButton,
    isComplianceActive = isComplianceActive,
    complianceAttachment = complianceAttachment,
    contactMob = contactMob,
    remarks = remarks,
)

fun NetworkStudentDTL.toDisciplineUserInfo() = DisciplineUserInfo(
    id = stID,
    name = studentName,
    className = className,
    admissionNo = admissionNo,
    photo = photo,
    contactPerson = contactPerson,
    contactMob = contactMob,
    designation = null,
    mobile = null,
    qualification = null,
)

fun NetworkStaffDTL.toDisciplineUserInfo() = DisciplineUserInfo(
    id = sID,
    name = staffName,
    className = null,
    admissionNo = null,
    photo = photo,
    contactPerson = null,
    contactMob = null,
    designation = designation,
    mobile = mobile,
    qualification = qualification,
)

fun NetworkInfractionDetailsResponse.toDomainModel() = InfractionDetails(
    records = records?.map { it.toDomainModel() } ?: emptyList(),
    showPoints = showPoints ?: false,
    totalPoints = totalPoints ?: 0,
    userInfo = studentDTL?.toDisciplineUserInfo() ?: staffDTL?.toDisciplineUserInfo(),
)

fun NetworkAddInfractionResponse.toDomainModel() = AddInfractionFormData(
    studentInfo = studentDTL?.toDisciplineUserInfo(),
    staffInfo = staffDTL?.toDisciplineUserInfo(),
    infractionTypes = infractionTypes?.map { it.toDomainModel() } ?: emptyList(),
    infractionConsequences = infractionConsequences?.map { it.toDomainModel() } ?: emptyList(),
)

// Appreciation mappers
fun NetworkAppreciationType.toDomainModel() = AppreciationType(
    id = aprID ?: 0,
    name = appreciation.orEmpty(),
    subCatID = appreciationSubCatID,
)

fun NetworkAppreciationReward.toDomainModel() = AppreciationReward(
    id = rwdID ?: 0,
    name = reward.orEmpty(),
)

fun NetworkAppreciationRecord.toDomainModel() = AppreciationRecord(
    id = id,
    appreciation = appreciation,
    subAppreciation = subAppreciation,
    appreciationOn = appreciationOn,
    admissionNo = admissionNo,
    stID = stID,
    studentName = studentName,
    reward = reward,
    remark = remark,
    point = point,
    instance = instance,
    staffName = staffName,
    stffPhoto = stffPhoto,
    recordClass = recordClass,
    photo = photo,
    designation = designation,
    canDelete = canDelete,
)

fun NetworkAppreciationDetailsResponse.toDomainModel() = AppreciationDetails(
    records = records?.map { it.toDomainModel() } ?: emptyList(),
    showPoints = showPoints ?: false,
    totalPoints = totalPoints ?: 0,
    userInfo = studentDTL?.toDisciplineUserInfo(),
)

fun NetworkAddAppreciationResponse.toDomainModel() = AddAppreciationFormData(
    studentInfo = studentDTL?.toDisciplineUserInfo(),
    appreciationTypes = appreciationTypes?.map { it.toDomainModel() } ?: emptyList(),
    appreciationRewards = appreciationRewards?.map { it.toDomainModel() } ?: emptyList(),
)
