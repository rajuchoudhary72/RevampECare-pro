package com.app.ecarepro.ui.con_report

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ConversationReportItemBinding
import com.app.ecarepro.model.Conversation
import com.app.ecarepro.utils.Constant
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class ConversationReportAdapter(
    private var conversationList: List<Conversation>,
    private val conversationReportFragment: ConversationReportFragment
) :
    RecyclerView.Adapter<ConversationReportAdapter.MedicineIssueViewHolder>() {




        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineIssueViewHolder {
        val binding =
            ConversationReportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicineIssueViewHolder(binding)
    }

    override fun getItemCount(): Int = conversationList.size


    override fun onBindViewHolder(holder: MedicineIssueViewHolder, position: Int) {
         holder.bind(conversationList[position]) }

   inner class MedicineIssueViewHolder(private val binding:   ConversationReportItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(conversation: Conversation) {
            binding.apply {

                cvMain.setOnClickListener {
                    conversationReportFragment.onItemClick(conversation,1,false)
                }


                tvSubject.text = conversation.subject
                var s = ""
                s = when (conversation.senderDTL.senderType) {
                    1 -> {
                        ", " + conversation.senderDTL .className
                    }

                    2 -> {
                        ((", Parent of " + conversation.senderDTL
                            .childName) + "(" + conversation.senderDTL
                            .className) + ")"
                    }

                    else -> {
                        ", " + conversation.senderDTL.designation
                    }
                }

                tvMessage.text = buildString {
                    append(("From : " + conversation.senderDTL.name))
                    append(s)
                }


                tvSubAbbre2.visibility = View.VISIBLE
                when (conversation.msgType) {
                    1 -> {
                        tvSubAbbre2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        tvSubAbbre2.text = conversation.abbreviation
                    }
                    2 -> {
                        tvSubAbbre2.text = "Photo"
                        tvSubAbbre2.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_photo, 0, 0, 0
                        )
                        tvSubAbbre2.compoundDrawablePadding = 5
                    }
                    3 -> {
                        tvSubAbbre2.text = "Audio"
                        tvSubAbbre2.setCompoundDrawablesWithIntrinsicBounds(  R.drawable.ic_audio, 0, 0, 0 )
                        tvSubAbbre2.compoundDrawablePadding = 5
                    }
                    else -> {
                        tvSubAbbre2.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_msg_typ_sms, 0, 0, 0 )
                        tvSubAbbre2.compoundDrawablePadding = 5
                        tvMessage.setTextColor(
                            conversationReportFragment.resources.getColor(R.color.light_gray_gallerytext)
                        )
                        val spannableString =
                            SpannableString("SMS  " + conversation.abbreviation)
                        spannableString.setSpan(
                            ForegroundColorSpan(
                                conversationReportFragment.resources.getColor(R.color.module)
                            ), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        val bss = StyleSpan(Typeface.BOLD)
                        spannableString.setSpan(bss, 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        tvSubAbbre2.text = spannableString
                    }
                }
                if (null != conversation.recipients) {
                    if (null != conversation.recipients[0].name) {
                        if (conversation.recipients .size > 1 )
                            tvSubAbbre.text = (("To : " + conversation.recipients[0] .name) + " and " + (conversation.recipients .size - 1)) + " more"
                        else tvSubAbbre.text =
                            buildString {
                                append("To : ")
                                append(conversation.recipients[0].name)
                            }
                    }
                    ctvReadBy.visibility = View.VISIBLE
                    if (conversation.readCount > 0) {
                        ctvReadBy.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_green_right_double,
                            0,
                            0,
                            0
                        )
                        ctvReadBy.text = ("Read by " + conversation.readCount) + " (" + setCalculatedPercentage(
                            conversation.readCount,
                            conversation.recipients.size
                        ) + ") recipient(s)"
                    } else {
                        ctvReadBy.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        ctvReadBy.text = "Read by 0 recipient"
                        ctvReadBy.setTextColor(
                            conversationReportFragment.resources.getColor(R.color.grey_40)
                        )
                    }
                }

                Picasso.get().
                load(conversation.senderDTL.photo)
                    .placeholder(R.drawable.default_profile)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .  into(ivUserPic)





                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val dt: String = conversation.sentOn
                try {
                    val date = sdf.parse(conversation.sentOn)
                    val destDate = SimpleDateFormat("dd MMM",Locale.getDefault())
                    tvTimeAgo.text = Constant.dateToShowConn(dt)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

            }
        }

       private fun setCalculatedPercentage(readCount: Int, totalUser: Int): String {
           return if (totalUser != 0) ((readCount * 100.00 / totalUser * 100.00).roundToInt() / 100.00).toString() + "%" else "0.0%"
       }

        }


}