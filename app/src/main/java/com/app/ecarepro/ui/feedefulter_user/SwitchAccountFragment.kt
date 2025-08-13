package com.app.ecarepro.ui.feedefulter_user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.R
import com.app.ecarepro.account
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.databinding.FragmentSwitchAccountBinding
import com.app.ecarepro.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.profileAddAccount

@AndroidEntryPoint
class SwitchAccountFragment : Fragment() {

    private lateinit var binding: FragmentSwitchAccountBinding

    @Inject
    lateinit var userDataStore: UserDataStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return FragmentSwitchAccountBinding.inflate(inflater, container, false).let {
            binding = it
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            userDataStore.getUsersFlow().collectLatest { users ->
                buildModels(users)
            }
        }
    }

    private fun buildModels(users: List<NetworkUserDetailsDto>) {
        binding.recyclerAccounts.withModels {
            users.forEach {
                account {
                    id(it.id)
                    name(
                        if (it.name.isNullOrEmpty()) {
                            "N/A (${it.roleName})"
                        } else {
                            it.name + "(${it.roleName})"
                        }

                    )/*show  child info  if  user is parent*/
                    if (it.stName.isNullOrEmpty().not()) {
                        childName("${it.stName ?: ""} (${it.className ?: ""})")
                    } else {
                        childName(null)
                    }
                    hideEndButton(true)
                    photo(it.photo)
                    school(it.school)
                    changeUser { _ ->
                        lifecycleScope.launch {
                            userDataStore.setCurrentUserId(it.id)
                            restartApp()
                        }
                    }
                }
            }
           /* profileAddAccount {
                id(23)
                clickListener { _ ->
                    findNavController().navigate(
                        R.id.schoolCodeFragment,
                        bundleOf("add_account" to true)
                    )
                }
            }*/

        }
    }

    private fun restartApp() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        Runtime.getRuntime().exit(0)
    }
}