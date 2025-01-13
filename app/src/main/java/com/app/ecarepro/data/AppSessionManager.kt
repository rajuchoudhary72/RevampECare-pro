package com.app.ecarepro.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.SystemViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

object AppSessionManager {
    private var currentActivity: WeakReference<Activity>? = null
    private var systemViewModel: WeakReference<SystemViewModel>? = null
    private var coroutineScope: CoroutineScope? = null

    fun setCurrentActivity(
        activity: Activity,
        systemViewModel: SystemViewModel,
        scope: CoroutineScope
    ) {
        currentActivity = WeakReference(activity)
        this.systemViewModel = WeakReference(systemViewModel)
        coroutineScope = scope
    }

    fun logoutAndRestartApp(force:Boolean = false) {
        currentActivity?.get()?.let { activity ->

            coroutineScope?.launch {
                // Clear user session or token
                clearUserSession(activity, force)

                // Restart the app
                val intent = Intent(activity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                activity.startActivity(intent)
                Runtime.getRuntime().exit(0)
            }
        }
    }

    private suspend fun clearUserSession(context: Context, force: Boolean) {
        systemViewModel?.get()?.let { viewModel ->
            viewModel.apply {
                logoutCurrentUser {
                        val currentUsers = dataStore.getUsersFlow().first().sortedBy { it.id }
                        if (force.not() && currentUsers.size > 1) {
                            database.deleteUserById(dataStore.getCurrentUserId()!!)
                            dataStore.setCurrentUserId(currentUsers.first { it.id != dataStore.getCurrentUserId()!! }.id)
                        } else {
                            dataStore.clear()
                            context.databaseList()?.forEach {
                                currentActivity?.get()?.deleteDatabase(it)
                            }
                            // Clear Shared Preferences
                            val sharedPreferences =
                                context.getSharedPreferences(
                                    "SHARED_PREF_NAME_PROMPT",
                                    Context.MODE_PRIVATE
                                )
                            sharedPreferences?.edit()?.clear()?.apply()
                            context.cacheDir?.deleteRecursively()
                        }
                }
            }

        }

    }
}