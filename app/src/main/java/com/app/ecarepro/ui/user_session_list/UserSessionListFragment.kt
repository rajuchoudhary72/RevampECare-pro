package com.app.ecarepro.ui.user_session_list

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentUserSessionListBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UserSessionListFragment : Fragment() , ItemListener<String> {

    private val viewModel: UserSessionListViewModel by viewModels()
    private lateinit var binding: FragmentUserSessionListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserSessionListBinding.inflate(inflater, container, false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.active_sessions)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpActiveSessionList()

        binding.btnLogoutDevice.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Logout All Devices")
            builder.setMessage("Are you sure, you want to logout from all devices?")

            builder.setPositiveButton("Sure") { dialog, _ ->
                removeSession(null)
                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

    }

    private fun setUpActiveSessionList(){
        lifecycleScope.launch {
            viewModel.sessionsStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {

                            binding.tvMobileSession.text= buildString {
                                append(it.data.appSessionCount.toString())
                                append(" sessions in Mobile")
                            }

                            binding.tvMobileSession.text= buildString {
                                append(it.data.webSessionCount.toString())
                                append(" sessions in Computer")
                            }

                            if (it.data.webSessions!=null) {
                                val sessionsListAdapter = SessionsWebListAdapter(
                                        it.data.webSessions,
                                        this@UserSessionListFragment
                                    )
                                binding.recyclerSessionWeb.apply {
                                        setHasFixedSize(true)
                                        layoutManager = LinearLayoutManager(activity)
                                        adapter = sessionsListAdapter
                                    }

                            }

                            if (it.data.appSessions!=null) {
                                val sessionsListAdapter = SessionsMobileListAdapter(
                                    it.data.appSessions,
                                    this@UserSessionListFragment
                                )
                                binding.recyclerSessionWeb.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = sessionsListAdapter
                                }

                            }



                        }
                    }
                }
            }
        }
        viewModel.activeSessionsList()
    }

    override fun onItemClick(t: String, pos: Int, boolean: Boolean) {
        binding.btnLogoutDevice.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Logout This Devices")
            builder.setMessage("Are you sure, you want to logout from this device?")

            builder.setPositiveButton("Sure") { dialog, _ ->
                removeSession(t)
                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
    private fun removeSession(sessionID: String?){
        lifecycleScope.launch {
            viewModel.removeSessionStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }
                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }
                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Toast.makeText(requireContext(), it.data!!.message,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        viewModel.removeSession(sessionID)
    }

}