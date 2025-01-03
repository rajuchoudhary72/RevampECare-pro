package com.app.ecarepro.ui.user_session_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.SessionListItemBinding
import com.app.ecarepro.model.WebSession

class SessionsWebListAdapter(
    private val webSessions: List<WebSession>,
    private val userSessionListFragment: UserSessionListFragment
) :
    RecyclerView.Adapter<SessionsWebListAdapter.SmsReportViewHolder>() {

        private lateinit var bindingm:   SessionListItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsReportViewHolder {
        bindingm=SessionListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SmsReportViewHolder(bindingm.root )
    }

    override fun getItemCount(): Int = webSessions.size

    override fun onBindViewHolder(holder: SmsReportViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<SessionListItemBinding>(holder.itemView)
        binding?.apply {
           val sessionData=webSessions[position]
            tvDeviceName.text= buildString {
                append(sessionData.browser)
                append("(OS:")
                append(sessionData.operatingSystem)
                append(")")
            }
            tvLocation.text=sessionData.locationCity
            tvLastActive.text= buildString {
                append("Last Active: ")
                append(sessionData.lastActivityTime)
            }
           // llCurrentSession.isVisible=sessionData.isThisDevice
            ivLogout.isVisible=!sessionData.isThisDevice
            ivLogout.setOnClickListener {
                userSessionListFragment.onItemClick(sessionData.sessionID,1,true)
            }

        }

    }





    class SmsReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}