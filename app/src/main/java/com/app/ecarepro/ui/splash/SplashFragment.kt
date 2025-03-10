package com.app.ecarepro.ui.splash

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.UserDataOldApp
import com.app.ecarepro.databinding.FragmentSplashBinding
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.utils.imageUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    val splashViewModel: SplashViewModel by viewModels()
    val systemViewModel: SystemViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimation()

        splashViewModel.school.observe(viewLifecycleOwner) { school ->
            school?.let {
                binding.apply {
                    logo.imageUrl(
                        school.logo,
                        requireContext().getDrawable(R.drawable.img_school_logo)
                    )
                    textInstituteName.text = school.schoolName
                    textInstituteAddress.text = school.city
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val oldUsers: List<UserDataOldApp> = fetchOldUserData(requireContext())

// Example: Print user details
                for (user in oldUsers) {
                    Toast.makeText(
                        requireContext(),
                        ("ID: " + user.userId).toString() + ", Password: " + user.userPassword,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "UserData",
                        ("ID: " + user.userId).toString() + ", Password: " + user.userPassword
                    )
                }


                if (splashViewModel.isUserAuthenticated()) {
                    systemViewModel.refreshAppLayout()
                    delay(2000)
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                } else {
                    //   splashViewModel.getSliders()
                    //   findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

    }

    fun fetchOldUserData(context: Context): List<UserDataOldApp> {
        val userList = mutableListOf<UserDataOldApp>()
        var oldDb: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            // Get the old SQLite database file
            val dbFile = context.getDatabasePath("palmboard.db") // Ensure correct old DB name
            if (!dbFile.exists()) return userList // Return empty list if old DB is missing

            // Open the old database in read-only mode
            oldDb = SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READONLY)

            // Query user_id and password from old database
            cursor = oldDb.rawQuery("SELECT user_name_id, password FROM user_info_table_db", null)

            cursor?.use {
                while (it.moveToNext()) {
                    val userId = it.getString(it.getColumnIndexOrThrow("user_name_id"))
                    val password = it.getString(it.getColumnIndexOrThrow("password"))

                    // Add to list
                    userList.add(UserDataOldApp(userId, password))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            oldDb?.close()
        }

        return userList
    }
    /* fun fetchOldUserData(context: Context): List<UserDataOldApp> {
         val userList: MutableList<UserDataOldApp> = ArrayList<UserDataOldApp>()
         var oldDb: SQLiteDatabase? = null
         var cursor: Cursor? = null

         try {
             // Get the old SQLite database file
             val dbFile: File =
                 context.getDatabasePath("user_info_table_db") // Change to your old DB name
             if (!dbFile.exists()) return userList // Return empty list if old DB is missing


             // Open the old database in read-only mode
             oldDb =
                 SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY)

             // Query user_id and user_password
             cursor = oldDb.rawQuery("SELECT user_name_id, password FROM users", null)

             if (cursor != null && cursor.moveToFirst()) {
                 do {
                     val userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_name_id"))
                     val password = cursor.getString(cursor.getColumnIndexOrThrow("password"))

                     // Add to ArrayList
                     userList.add(UserDataOldApp(userId, password))
                 } while (cursor.moveToNext())
             }
         } catch (e: java.lang.Exception) {
             e.printStackTrace()
         } finally {
             cursor?.close()
             oldDb?.close()
         }
         return userList
     }*/


    private fun startAnimation() {
        val anim: AnimationDrawable = binding.backgroundView.drawable as AnimationDrawable
        val run = Runnable { anim.start() }
        binding.backgroundView.post(run)

    }

    override fun onResume() {
        super.onResume()
        fetchOldUserData(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}