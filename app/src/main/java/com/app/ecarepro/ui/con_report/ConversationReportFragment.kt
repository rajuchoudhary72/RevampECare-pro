package com.app.ecarepro.ui.con_report

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentConverReportBinding
import com.app.ecarepro.model.Conversation
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.message.chat.MessageType
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.listener.ItemListener
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class ConversationReportFragment : Fragment(), ItemListener<Conversation> {

    private lateinit var binding: FragmentConverReportBinding
    private val conversationReportViewModel: ConversationReportViewModel by viewModels()
    private val dateFrom: Calendar = Calendar.getInstance()

    private val dateTo: Calendar = Calendar.getInstance()


    var query   = ""
     var senderName  = ""
    var receiverName  = ""
    var sender = 0
    var recipient   = 0
    var hasWord = ""
    var mPage = 1
    var mEndDate = ""
    var mFilterStartDate  = ""
    var mFilterEndDate   = ""
    var mStartDate = ""
    private var pageIndex: Int = 1
    private var pastVisiblesItems: Int = 0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0
    private var isLoading: Boolean = true
    private lateinit var conversationReportAdapter : ConversationReportAdapter
    private var mConversationList  = mutableListOf<Conversation>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConverReportBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            conversationReportAdapter = ConversationReportAdapter( mConversationList , this@ConversationReportFragment,false)

            binding.recyclerSmsUsageReport.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = conversationReportAdapter
            }

            tvDateFrom.text = Constant.currentDate()
            tvDateTo.text = Constant.currentDate()

            tvDateFrom.setOnClickListener { pickDateRange() }
            tvDateTo.setOnClickListener { pickDateRange() }

            btnFilter.setOnClickListener {
                showFilterPopUp()
            }
        }

        getConversationReport()
        setupRecycleViewPager()

    }

    private fun pickDateRange() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setSelection(androidx.core.util.Pair(dateFrom.timeInMillis, dateTo.timeInMillis))

        val picker = builder.build()
        picker.show(activity?.supportFragmentManager!!, picker.toString())

        picker.addOnNegativeButtonClickListener { picker.dismiss() }
        picker.addOnPositiveButtonClickListener {
            dateFrom.timeInMillis = it.first
            dateTo.timeInMillis = it.second
            updateDateFilterText(true)
        }
    }

    private fun updateDateFilterText(setAsFilter: Boolean ) {
        if (setAsFilter){
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            dateFormat.format(Date(dateFrom.timeInMillis))
            val from = dateFormat.format(Date(dateFrom.timeInMillis))
            val to = dateFormat.format(Date(dateTo.timeInMillis))

            binding.apply {
                tvDateFrom.text = from
                tvDateTo.text = to
            }
            pageIndex=1
            getConversationReport()
        }


    }

    private fun getConversationReport() {

        lifecycleScope.launch {
            conversationReportViewModel.convReportStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerSmsUsageReport.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerSmsUsageReport.isVisible = true

                        if (it.data != null) {

                            if (it.data.conversation!=null && it.data.conversation.isNotEmpty() ){

                                binding.recyclerSmsUsageReport.isVisible=true
                                binding.tvNoData.isVisible=false

                                //isLoading=true
                                if (pageIndex==1){
                                    conversationReportAdapter.clearData()
                                }
                                conversationReportAdapter.setData(it.data.conversation.toMutableList(),it.data.canDeleteConv)


                            }else{
                                if (pageIndex==1){
                                    binding.recyclerSmsUsageReport.isVisible=false
                                    binding.tvNoData.isVisible=true
                                }

                            }
                        } else {
                            binding.recyclerSmsUsageReport.visibility=View.GONE
                            binding.tvNoData.visibility=View.VISIBLE
                        }




                    }

                    else -> {}
                }
            }

        }

        conversationReportViewModel.getConversationReport(
            pageIndex,
            Constant.toSystemDate(binding.tvDateFrom.text.toString()),
            Constant.toSystemDate(binding.tvDateTo.text.toString())
        )

    }

    override fun onItemClick(t: Conversation, pos: Int, boolean: Boolean) {
        when(pos){
            1->{
                findNavController().navigate(
                    R.id.chatFragment,
                    bundleOf(
                        "ID" to t.msgID,
                        "MessageType" to MessageType.CONV.value
                    )
                )
            }
            2 ->{
                showDeleteConfirmationDialog(t)
            }
        }
    }

    private fun showDeleteConfirmationDialog(t: Conversation) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Delete")
        builder.setMessage("Are you sure you want to delete")

        builder.setPositiveButton("Delete") { dialog, _ ->
            conversationReportViewModel.deleteConversation(t.msgID,Constant.DEVICE_TYPE).invokeOnCompletion {
                pageIndex=1
                getConversationReport()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    private fun showFilterPopUp() {
        val ivCross: ImageView
        val llFilterStart: LinearLayout
        val llFilterEnd: LinearLayout
        val rlClearAll: RelativeLayout
        val rlApply: RelativeLayout
        val tvFilterStartDate: TextView
        val tvFilterEnd: TextView
        val rgSender: RadioGroup
        val rgRecipient: RadioGroup
        val rgHasWord: RadioGroup
        val etSenderName: EditText
        val etRecipientName: EditText
        val etHasWord: EditText
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.window!!.attributes.windowAnimations = R.style.Animations
        dialog.setContentView(R.layout.custom_popup_msg_filter)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        ivCross = dialog.findViewById(R.id.ivCross)
        llFilterStart = dialog.findViewById(R.id.llFilterStart)
        llFilterEnd = dialog.findViewById(R.id.llFilterEnd)
        tvFilterStartDate = dialog.findViewById(R.id.tvFilterStartDate)
        tvFilterEnd = dialog.findViewById(R.id.tvFilterEnd)
        rlApply = dialog.findViewById(R.id.rlApply)
        rlClearAll = dialog.findViewById(R.id.rlClearAll)
        etSenderName = dialog.findViewById(R.id.etSenderName)
        etRecipientName = dialog.findViewById(R.id.etRecipientName)
        etHasWord = dialog.findViewById(R.id.etHasWord)
        rgSender = dialog.findViewById(R.id.rgSender)
        rgRecipient = dialog.findViewById(R.id.rgRecipient)
        rgHasWord = dialog.findViewById(R.id.rgHasWord)
        val rgSenderRb1 = rgSender.findViewById<RadioButton>(R.id.rgSenderRb1)
        val rgSenderRb2 = rgSender.findViewById<RadioButton>(R.id.rgSenderRb2)
        val rgSenderRb3 = rgSender.findViewById<RadioButton>(R.id.rgSenderRb3)
        val rgSenderRb4 = rgSender.findViewById<RadioButton>(R.id.rgSenderRb4)
        val rgRecipientRb1 = rgRecipient.findViewById<RadioButton>(R.id.rgRecipientRb1)
        val rgRecipientRb2 = rgRecipient.findViewById<RadioButton>(R.id.rgRecipientRb2)
        val rgRecipientRb3 = rgRecipient.findViewById<RadioButton>(R.id.rgRecipientRb3)
        val rgRecipientRb4 = rgRecipient.findViewById<RadioButton>(R.id.rgRecipientRb4)
        val rgAnyRb1 = rgHasWord.findViewById<RadioButton>(R.id.rgAnyRb1)
        val rgSpecificRb2 = rgHasWord.findViewById<RadioButton>(R.id.rgSpecificRb2)
        tvFilterStartDate.text = mFilterStartDate
        tvFilterEnd.text = mFilterEndDate
        ivCross.setOnClickListener { dialog.dismiss() }
        llFilterStart.setOnClickListener {
            ECareDataPicker(
                requireActivity(),
                false,
                object : ECareDataPicker.PickerCallback {
                    override fun onSelect(date: String?, isCurrentDate: Boolean) {
                        tvFilterStartDate.text = date
                        mFilterStartDate = date.toString()
                    }
                }).setMaxDate(Constant.getLongTimeDate(Constant.currentDate()))

        }
        llFilterEnd.setOnClickListener {

            ECareDataPicker(
                requireActivity(),
                false,
                object : ECareDataPicker.PickerCallback {
                    override fun onSelect(date: String?, isCurrentDate: Boolean) {
                        tvFilterEnd.text = date
                        mFilterStartDate = date.toString()
                     }
                }).setMaxDate(Constant.getLongTimeDate(Constant.currentDate()))

        }
        rgSender.setOnCheckedChangeListener { group, checkedId ->
            when (rgSender.checkedRadioButtonId) {
                R.id.rgSenderRb1 -> {
                    sender = 0
                    senderName = ""
                    etSenderName.setText("")
                    etSenderName.visibility = View.GONE
                    rgRecipientRb2.isEnabled = true
                    rgRecipientRb1.isEnabled = true
                    rgRecipientRb3.isEnabled = true
                    rgRecipientRb4.isEnabled = true
                }

                R.id.rgSenderRb2 -> {
                    sender = 3
                    senderName = ""
                    etSenderName.setText("")
                    etSenderName.visibility = View.VISIBLE
                    rgRecipientRb2.isEnabled = true
                    rgRecipientRb1.isEnabled = true
                    rgRecipientRb3.isEnabled = true
                    rgRecipientRb4.isEnabled = true
                }

                R.id.rgSenderRb3 -> {
                    sender = 2
                    senderName = ""
                    etSenderName.setText("")
                    etSenderName.visibility = View.VISIBLE
                    rgRecipientRb2.isChecked = true
                    rgRecipientRb1.isEnabled = false
                    rgRecipientRb3.isEnabled = false
                    rgRecipientRb4.isEnabled = false
                }

                R.id.rgSenderRb4 -> {
                    sender = 1
                    senderName = ""
                    etSenderName.setText("")
                    etSenderName.visibility = View.VISIBLE
                    rgRecipientRb2.isChecked = true
                    rgRecipientRb1.isEnabled = false
                    rgRecipientRb3.isEnabled = false
                    rgRecipientRb4.isEnabled = false
                }
            }
        }
        rgRecipient.setOnCheckedChangeListener { group, checkedId ->
            when (rgRecipient.checkedRadioButtonId) {
                R.id.rgRecipientRb1 -> {
                    recipient = 0
                    senderName = ""
                    etRecipientName.setText("")
                    etRecipientName.visibility = View.GONE
                }

                R.id.rgRecipientRb2 -> {
                    recipient = 3
                    senderName = ""
                    etRecipientName.setText("")
                    etRecipientName.visibility = View.VISIBLE
                }

                R.id.rgRecipientRb3 -> {
                    recipient = 2
                    senderName = ""
                    etRecipientName.setText("")
                    etRecipientName.visibility = View.VISIBLE
                }

                R.id.rgRecipientRb4 -> {
                    recipient = 1
                    senderName = ""
                    etRecipientName.setText("")
                    etRecipientName.visibility = View.VISIBLE
                }
            }
        }
        rgHasWord.setOnCheckedChangeListener { group, checkedId ->
            when (rgHasWord.checkedRadioButtonId) {
                R.id.rgAnyRb1 -> {
                    hasWord = ""
                    etHasWord.visibility = View.GONE
                    etHasWord.setText("")
                }

                R.id.rgSpecificRb2 -> {
                    etHasWord.visibility = View.VISIBLE
                    etHasWord.setText("")
                }
            }
        }
        rlApply.setOnClickListener {
            dialog.dismiss()
            mStartDate = mFilterStartDate
            mEndDate = mFilterEndDate
            binding.tvDateFrom.text = mFilterStartDate
            binding.tvDateTo.text = mFilterEndDate
            hasWord = etHasWord.text.toString()
            senderName = etSenderName.text.toString()
            receiverName = etRecipientName.text.toString()

        }
        rlClearAll.setOnClickListener {
            rgSenderRb1.isChecked = true
            rgRecipientRb1.isChecked = true
            rgAnyRb1.isChecked = true
            etHasWord.setText("")
            etSenderName.setText("")
            etRecipientName.setText("")
            etHasWord.visibility = View.GONE
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }


    private fun setupRecycleViewPager() {
        conversationReportAdapter.clearData()
        binding.recyclerSmsUsageReport.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (linearLayoutManager != null) {
                    if (dy > 0) {
                        visibleItemCount = linearLayoutManager.childCount;
                        totalItemCount = linearLayoutManager.itemCount;
                        pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition()

                        if (isLoading) {
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                isLoading = false
                                pageIndex += 1
                                getConversationReport()

                            }
                        }

                    }
                }
            }
        })



    }


}