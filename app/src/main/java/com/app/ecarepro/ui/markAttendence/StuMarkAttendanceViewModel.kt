package com.app.ecarepro.ui.markAttendence

import android.content.Context
import android.net.wifi.WifiManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.BulkMessageRequestDto
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.Data
import com.app.ecarepro.data.network.model.NetworkMarkAttendance
import com.app.ecarepro.data.network.model.NetworkMySubjects
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStudentListToMarkAtt
import com.app.ecarepro.data.network.model.SendSpecificMsg.PostDataSendSpecificMsg
import com.app.ecarepro.data.network.model.SendSpecificMsg.Recipient
import com.app.ecarepro.data.network.model.post_mark_attedance.StudentAtt
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.ComposeMessageType
import com.app.ecarepro.model.StudentListMarkAtt
import com.app.ecarepro.ui.message.chat.getDeviceIpAddress
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StuMarkAttendanceViewModel  @Inject constructor(
    @ApplicationContext private val context: Context,

    private val  userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val userDataStore: UserDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val myClassMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMarkAttendance>> = MutableStateFlow(
        NetworkResult.Loading())
    val myClassStateFlow: StateFlow<NetworkResult<NetworkMarkAttendance>> = myClassMutableStateFlow

    private val stuListToMarkAttMutableStateFlow: MutableStateFlow<NetworkResult<NetworkStudentListToMarkAtt>> = MutableStateFlow(
        NetworkResult.Loading())
    val stuListToMarkAttStateFlow: StateFlow<NetworkResult<NetworkStudentListToMarkAtt>> = stuListToMarkAttMutableStateFlow


    private val subjectsMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMySubjects>> = MutableStateFlow(
        NetworkResult.Loading())
    val subjectsStateFlow: StateFlow<NetworkResult<NetworkMySubjects>> = subjectsMutableStateFlow

    private val postMarkAttendanceMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val postMarkAttendanceStateFlow: StateFlow<NetworkResult<CommonResponse>> = postMarkAttendanceMutableStateFlow


    val composeMessageType =
        savedStateHandle.getStateFlow("composeMessageType", ComposeMessageType.ONLY_APP_MESSAGE)

    fun getMarkAttendance( )=viewModelScope.launch {
        runCatching {
            myClassMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.markAttendance( )
        }.onSuccess {
            myClassMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            myClassMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun mySubjects( classID :Int)=viewModelScope.launch {
        runCatching {
            subjectsMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.mySubjects(classID)
        }.onSuccess {
            subjectsMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            subjectsMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun getStudentListToMarkAtt(
        classID: Int,
        subID: Int,
        attDate: String,
    )=viewModelScope.launch {
        runCatching {
            stuListToMarkAttMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.getStudentListToMarkAtt(classID, subID, attDate)
        }.onSuccess {
            stuListToMarkAttMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            stuListToMarkAttMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun  postMarkAttendance(
        classID:Int,
        subID:Int,
        mode:Int,
        attDate:String,
        stuList:List<StudentAtt>
    )=viewModelScope.launch {
        runCatching {
            postMarkAttendanceMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.postMarkAttedance(classID, subID, mode, attDate, stuList)
        }.onSuccess {
            postMarkAttendanceMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            postMarkAttendanceMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun sendMessage(
        markAttModel: NetworkStudentListToMarkAtt,
        uploadStudentList: MutableList<StudentListMarkAtt>,
        className: String,
        rbType: Int,
        attDate:String,
        result: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            if (rbType==1) {
                messageRepository
                    .sendBulkMessage(
                        BulkMessageRequestDto(
                            data = generateDataFromSelectedContacts(markAttModel,uploadStudentList,className,attDate),
                            iPAddress = context.getDeviceIpAddress(),
                            schCode = userDataStore.getSchoolData()?.schoolCode,
                            isBulk = null,
                            sMSType = markAttModel.smsType,
                            geoCoordinate = null,

                            uID = userDataStore.getUser()?.userId,
                            uType = userDataStore.getUser()?.userType
                        )
                    )
                    .collectLatest { response ->
                        if (response.isSuccess) {
                            result(true, response.getOrNull() ?: "")
                        } else {
                            result(
                                false,
                                response.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE
                            )
                        }

                    }
            }
            else {
                if (uploadStudentList.isEmpty()){
                    result(false,"Please Select recipient")
                }else{
                    val wifiManager = context.getSystemService(FirebaseMessagingService.WIFI_SERVICE) as WifiManager
                    val wInfo = wifiManager.connectionInfo
                    val macAddress = wInfo.macAddress
                    messageRepository.sendSpecificMsg(
                        PostDataSendSpecificMsg(
                            attachment=null,
                            device = 1,
                            ipAddress = context.getDeviceIpAddress(),
                            msgType =1,
                            recipient = generateDataFromSelectedMessage(markAttModel,uploadStudentList,className,attDate),
                            subject ="Absentee Message"

                        )
                    )
                        .collectLatest { response ->
                            if (response.isSuccess) {
                                result(true, response.getOrNull() ?: "")
                            } else {
                                result(
                                    true,
                                    response.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE
                                )
                            }
                        }
                }

            }
        }

    }


    private fun  generateDataFromSelectedMessage(
        markAttModel: NetworkStudentListToMarkAtt,
        uploadStudentList: MutableList<StudentListMarkAtt>,
        className: String,
        attDate: String
    ): List<Recipient>  {
        val data = mutableListOf<Recipient>()

        uploadStudentList. forEach { contact ->
            if (contact.status==2){
                data.add(
                    Recipient(
                        receiverID = contact.stID,
                        body = markAttModel.smS_Temp .replace("S____", contact.stName ?: "")
                            .replace("Date____", attDate ?: "")
                            .replace("C____", className ?: ""),
                        receiverType = 2
                    )
                )
            }

        }

        return data
    }

    private fun generateDataFromSelectedContacts   (
        markAttModel: NetworkStudentListToMarkAtt,
        uploadStudentList: MutableList<StudentListMarkAtt>,
        className: String,
        attDate: String
    ): List<Data>? {
        val data = mutableListOf<Data>()

        uploadStudentList. forEach { contact ->
             if (contact.status==2){
                 data.add(
                     Data(
                         mobile = contact.contactMob,
                         rCPTID = contact.stID.toString(),
                         rCPTType = 2,
                         templateID = markAttModel.templateID.toString(),
                         sMS = markAttModel.smS_Temp .replace("S____", contact.stName ?: "")
                             .replace("C____", className ?: "")
                             .replace("Date____", attDate ?: "")
                     )
                 )
             }
        }

        return data
    }

}