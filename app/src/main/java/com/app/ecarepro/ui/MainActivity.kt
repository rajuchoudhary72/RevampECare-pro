package com.app.ecarepro.ui

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.AppSessionManager
import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.sync.SyncManager
import com.app.ecarepro.databinding.ActivityMainBinding
import com.app.ecarepro.menuCard
import com.app.ecarepro.model.AppLayout
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
import com.app.ecarepro.ui.message.inbox.InboxMessageViewModel
import com.app.ecarepro.ui.notification.NotificationViewModel
import com.app.ecarepro.ui.views.PaymentWebViewActivity
import com.app.ecarepro.ui.views.bottom_navigation.CbnMenuItem
import com.app.ecarepro.ui.views.menu.ECareDrawerMenu
import com.app.ecarepro.ui.views.menu.MenuHeader
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.progressDialog
import com.app.ecarepro.utils.slideVisibility
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.messaging.FirebaseMessaging
import com.rubensousa.decorator.ColumnProvider
import com.rubensousa.decorator.GridMarginDecoration
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.io.IOException
import java.util.concurrent.ExecutionException
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: NotificationViewModel by viewModels()
    private val viewMessageModel: InboxMessageViewModel by viewModels()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var userData: NetworkUserDetailsDto
    private val systemViewModel: SystemViewModel by viewModels()
    private val navController: NavController by lazy {
        findNavController(R.id.nav_host_fragment_content_main)
    }
    private var loader: AlertDialog? = null

    private var expandedMenuId: Int = -1
    private var listenMenuItemClickEvent = true
    private var isActivityPaused = false
    private val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(this)
    }

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var userDataStore: UserDataStore

    @Inject
    lateinit var userDatabase: UserDatabase

    @Inject
    lateinit var syncManager: SyncManager
    private val topLevelFragments = mutableListOf(
        R.id.homeFragment,
        R.id.profileFragment,
        R.id.notificationFragment,
        R.id.messageFragment,
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun enableNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                Log.e("TestFCM", "onCreate: PERMISSION GRANTED")

            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                if (this::binding.isInitialized.not()) return
                Snackbar.make(
                    binding.appBarMain.contentMain.bottomNavigationView,
                    "Please Enable Notification Permission",
                    Snackbar.LENGTH_LONG
                ).setAction("Settings") {
                    // Responds to click on the action
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri: Uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }.show()
            }

            else -> {
                // The registered ActivityResultCallback gets the result of this request
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
        } else {
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* *//*open profile page after click on  profile *//*
        binding.itemDrawerHeader.imgUserAvatar.setOnClickListener {
            navController.navigate(R.id.profileFragment)
            systemViewModel.openDrawer(false)
        }*/

        setSupportActionBar(binding.appBarMain.toolbar)

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        analyticsManager.trackScreen(AnalyticsConstants.Screens.HOME_SCREEN)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.appBarMain.contentMain.bottomNavigationView.isVisible =
                topLevelFragments.contains(destination.id)
            binding.appBarMain.contentMain.rlBottomNavigation.isVisible =
                topLevelFragments.contains(destination.id)
        }


        setUpDrawer()

        setUpBottomNavigationView()

        setUpMoreOptions()
        try {
            Picasso.setSingletonInstance(Picasso.Builder(this).build())
        } catch (e: RuntimeException) {
            e.toString()
        }

        /* checking  for update version  */
        checkAppVersion()

        lifecycleScope.launch {
            systemViewModel.user.collectLatest {
                if (it != null) {
                    userData = it
                }
            }
        }
        lifecycleScope.launch {
            systemViewModel.bottomNavPosition.collectLatest { v ->
                when (v) {
                    0 -> {
                        binding.appBarMain.contentMain.bottomNavigationView.menu.findItem(R.id.menu)
                            .setChecked(true)

                    }

                    1 -> {
                        binding.appBarMain.contentMain.bottomNavigationView.menu.findItem(R.id.profile)
                            .setChecked(true)

                    }

                    2 -> {
                        binding.appBarMain.contentMain.bottomNavigationView.menu.findItem(R.id.home)
                            .setChecked(true)

                    }

                    3 -> {
                        binding.appBarMain.contentMain.bottomNavigationView.menu.findItem(R.id.notification)
                            .setChecked(true)

                    }

                    4 -> {
                        binding.appBarMain.contentMain.bottomNavigationView.menu.findItem(R.id.message)
                            .setChecked(true)

                    }


                }
            }
        }
        // Check for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                1
            )
        } else {
            // Permission is already granted, get the IMEI
            getIMEINumber()
        }
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM Token", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                Log.d("FCM Token", token)
                systemViewModel.registerDeviceToken(token)
            })
            .addOnFailureListener { e ->
                if (e is IOException) {
                    Log.e("FCM Token", "Network error", e)
                } else if (e is ExecutionException) {
                    Log.e("FCM Token", "Execution error", e)
                } else {
                    Log.e("FCM Token", "Unknown error", e)
                }
            }
        intent?.extras?.let { data ->
            handleNotificationClick(data)
        }
        // throw NullPointerException("Test crash for logging")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            enableNotificationPermission()
        }
        askNotificationPermission()
        observeBadgeCount()
        observeBadgeMessageCount()
    }

    private fun observeBadgeCount() {
        lifecycleScope.launchWhenStarted {
            viewModel.badgeCountFlow.collect { count: Int ->
                showBadgeCount1(count)
            }
        }
    }

    private fun observeBadgeMessageCount() {
        lifecycleScope.launchWhenStarted {
            viewMessageModel.badgeCountFlow.collect { count: Int ->
                showBadgeCount2(count)
            }
        }
    }

    private fun showBadgeCount1(count: Int) {
        binding.appBarMain.contentMain.bottomNavigationView.apply {
            if (count > 0) {
                getOrCreateBadge(R.id.notification).apply {
                    isVisible = true
                    number = count
                }
            } else {
                removeBadge(R.id.notification)
            }
        }
    }

    private fun showBadgeCount2(messageCount: Int) {
        binding.appBarMain.contentMain.bottomNavigationView.apply {
            if (messageCount > 0) {
                getOrCreateBadge(R.id.message).apply {
                    isVisible = true
                    number = messageCount
                }
            } else {
                removeBadge(R.id.message)
            }
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Handle the permission request response
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getIMEINumber()
            }
        }
    }

    private fun getIMEINumber() {
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        val imei: String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // On Android 10 and above, getting IMEI directly is restricted
            "Access Restricted"
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager.imei // For Android 8.0 and above
        } else {
            @Suppress("DEPRECATION")
            telephonyManager.deviceId // Deprecated in Android O and above
        }

        imei?.let {
            // Do something with the IMEI number
            println("IMEI Number: $imei")


        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.extras?.let { data ->
            handleNotificationClick(data)
        }

    }

    private fun handleNotificationClick(data: Bundle) {
        lifecycleScope.launch {
            showLoader(true)
            delay(2000)

            Log.e(
                "Note",
                data.keySet().joinToString() { key -> "$key -> ${data.get(key).toString()}" })
            val schCode = data.getString("SchCode") ?: return@launch
            val userID = data.getString("UserID")?.toInt() ?: return@launch
            val userType = data.getString("UserType")?.toInt() ?: return@launch
            val menuId = data.getString("MenuId")?.toInt()
            val childMenuId = data.getString("ChMenuID")?.toInt()
            val SubCildMenuId = data.getString("SbChMenuID")?.toInt()
            val refId = data.getString("refID")
            Log.e("Note", "$schCode $userID $menuId $childMenuId $SubCildMenuId  $refId")

            if (userDataStore.getUsersFlow().first()
                    .firstOrNull { it.userId == userID && it.schoolCode == schCode } == null
            ) {
                Log.e("Note", "return@launch")
                return@launch
            }

            val currentSchool = userDataStore.getSchoolData()
            if (currentSchool?.schoolCode != schCode) {
                Log.e("Note", "userDataStore.setCurrentSchoolCode(schCode)")
                userDataStore.setCurrentSchoolCode(schCode)
            }

            val currentUser = userDataStore.getUser()
            if (currentUser?.userId != userID) {
                Log.e("Note", "userDataStore.setCurrentUserId(userID)")
                userDatabase.getUser(userID, schCode, userType)?.id?.let {
                    userDataStore.setCurrentUserId(it)
                }
            }

            if (menuId != null) {
                if (childMenuId != null) {
                    if (SubCildMenuId != null) {
                        if (SubCildMenuId > 0) {
                            getFragmentId(
                                menuId, childMenuId, SubCildMenuId, refId,
                                userID.toString()
                            )
                        } else {
                            Log.e("Note", "getFragmentId(menuId, SubCildMenuId)")
                            getFragmentId(menuId, childMenuId, refId)
                        }
                    } else {
                        Log.e("Note", "getFragmentId(menuId, childMenuId)")
                        getFragmentId(menuId, childMenuId, refId)
                    }
                }
            }
            showLoader(false)
        }

    }

    /*
        private fun handleNotificationClick(data: Bundle) {
            val menuId = data.getString("MenuId")?.toInt()
            val childMenuId = data.getString("ChMenuID")?.toInt()
            if (menuId != null) {
                if (childMenuId != null) {
                    getFragmentId(menuId, childMenuId)
                }
            }
        }*/
    fun checkAppVersion() {
        lifecycleScope.launch {
            systemViewModel.appVersionStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        showLoader(false)
                        Log.d("main", "Error" + it)
                    }

                    is NetworkResult.Success -> {
                        showLoader(false)
                        if (it.data != null) {

                            var versionCode = 0
                            var versionName: String? = ""
                            try {
                                val pInfo: PackageInfo = packageManager
                                    .getPackageInfo(packageName, 0)
                                versionName = pInfo.versionName
                                versionCode = pInfo.versionCode
                            } catch (e: PackageManager.NameNotFoundException) {
                                e.printStackTrace()
                            }
                            Log.v("okhttp", "versionCode $versionCode")
                            Log.v("okhttp", "versionName $versionName")


                            try {
                                if (it.data.android.currentVersion != null) {
                                    if (versionName != null) {
                                        if (versionName < it.data.android.currentVersion) {
                                            // open  dialog
                                            if (versionName > it.data.android.criticalVersion && it.data.android.normalVersion < versionName) {
                                                //soft  update
                                                checkIsUpdateAvailable(false)
                                                /*    UpdateAppVersionDialog(
                                                                                    0,
                                                                                    it.data.android.title,
                                                                                    it.data.android.description
                                                                                )*/
                                            } else {
                                                //force update
                                                checkIsUpdateAvailable(true)
                                                /*  UpdateAppVersionDialog(
                                                                                  1,
                                                                                  it.data.android.title,
                                                                                  it.data.android.description
                                                                              )*/
                                            }
                                        } else {
                                            // nothing  open  version  dialog
                                        }
                                    }
                                }
                            } catch (e: NullPointerException) {
                                e.message
                            }


                            /* if (versionCode < it.data.android.versionCode) {


                                 if (versionName == it.data.android.criticalVersion.trim()
                                 ) // force update
                                     UpdateAppVersionDialog(
                                         1,
                                         it.data.android.title,
                                         it.data.android.description
                                     )
                                 else  // normal update
                                     UpdateAppVersionDialog(
                                         0,
                                         it.data.android.title,
                                         it.data.android.description
                                     )
                             }*/
                        }

                    }

                    else -> {}
                }


            }
        }
        systemViewModel.checkAppVersion()
    }

    /*in app  update */
    private fun checkIsUpdateAvailable(forceUpdate: Boolean) {
        try {
            // isImmediatepopup =forceUpdate
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                val isAppUpdateAllowed = if (forceUpdate) {
                    appUpdateInfo.isImmediateUpdateAllowed
                } else {
                    appUpdateInfo.isFlexibleUpdateAllowed
                }

                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate()
                } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && isAppUpdateAllowed) {
                    appUpdateManager.startUpdateFlowForResult(
                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,
                        // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                        if (forceUpdate) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE,
                        // The current activity making the update request.
                        this,
                        // Include a request code to later monitor this update request.
                        MY_REQUEST_CODE
                    )
                }
            }
            appUpdateManager.registerListener(installStateUpdatedListener)
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate()
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            binding.root, "An app update is ready to install.", Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("INSTAll") { appUpdateManager.completeUpdate() }
            setActionTextColor(resources.getColor(R.color.brand_color))
            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.e("In App Update", "onActivityResult: RESULT_OK")
            } else if (resultCode == Activity.RESULT_CANCELED) {
                /* if (isImmediatepopup){
                     Log.e("In force  App Update", "onActivityResult: RESULT_CANCELED")
                     checkAppVersion()
                 }else{
                     Log.e("In soft   App Update", "onActivityResult: RESULT_CANCELED")
                     isImmediatepopup=false
                 }*/
                Log.e("In App Update", "onActivityResult: RESULT_CANCELED")
            } else if (resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
                Log.e("In App Update", "onActivityResult: RESULT_IN_APP_UPDATE_FAILED")
            } else {
                Log.e("In App Update", "onActivityResult: else")
            }
        }
    }

    fun hideKeyBoard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /*fun setUpDrawer() {
        systemViewModel.openNavigationDrawer.observe(this) { open ->
            if (open) {
                binding.drawerLayout.open()
            } else {
                binding.drawerLayout.close()
            }
        }

        lifecycleScope.launch {
            launch {
                systemViewModel.logout.collectLatest { logout ->
                    if (logout) {
                        logout(forceLogout = true)
                    }
                }
            }
            systemViewModel.uiState
                .flowWithLifecycle(lifecycle)
                .collectLatest { uiState ->
                    uiState.getValueOrNull()?.let { data ->
                        buildDrawerModels(data.menus)
                        buildFavoriteMenusModels(data.menus)
                        binding.itemDrawerHeader.user = data.userInfo
                        showBadgeCount(data.appLayoutDto)

                    }
                }
        }

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {
                if (expandedMenuId != -1) {
                    expandedMenuId = -1
                    binding.recyclerViewNavView.requestModelBuild()
                }
            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })

        binding.itemDrawerFooter.appVersion = "App Version:${BuildConfig.VERSION_NAME}"
        binding.itemDrawerFooter.setClickListener {
            logout()
        }

        binding.drawerLayout.open()
    }*/

    /*new bind with LMS module */
    private fun setUpDrawer() {

        binding.navView.apply {

            setListener(object : ECareDrawerMenu.EcareDrawerClickListener {
                override fun onClickHeader() {
                    navController.navigate(R.id.profileFragment)
                    systemViewModel.openDrawer(false)
                }

                override fun onClickMenu(
                    menuId: Int,
                    childMenuId: Int?,
                    childChildMenuId: Int?,
                    refId: String?
                ) {
                    systemViewModel.openDrawer(false)
                    lifecycleScope.launch {
                        if (userDataStore.isLMSEnabled().first()) {
                            if (menuId == 1 ) {
                                navController.navigate(R.id.pedagogyFragment)
                            } else
                                navController.navigate(R.id.defineSkillFragment)
                        } else {
                            if (childMenuId == null && childChildMenuId == null) {
                                getFragmentId(menuId, refId.orEmpty())
                            } else if (childMenuId != null && childChildMenuId == null) {
                                getFragmentId(
                                    menuID = menuId,
                                    childMenuId = childMenuId,
                                    refId = refId.orEmpty()
                                )
                            } else {
                                getFragmentId(
                                    menuId,
                                    childMenuId!!,
                                    childChildMenuId!!,
                                    refId.orEmpty()
                                )
                            }
                        }
                    }

                }

                override fun onClickFooter() {
                    logout()

                }

            })
        }

        systemViewModel.openNavigationDrawer.observe(this) { open ->
            if (open) {
                binding.drawerLayout.open()
            } else {
                binding.drawerLayout.close()
            }
        }

        lifecycleScope.launch {
            launch {
                systemViewModel.logout.collectLatest { logout ->
                    if (logout) {
                        logout(forceLogout = true)
                    }
                }
            }
            systemViewModel.uiState
                .flowWithLifecycle(lifecycle)
                .collectLatest { uiState ->
                    uiState.getValueOrNull()?.let { data ->
                        //buildDrawerModels(data.menus)
                        binding.navView.setMenuItems(data.menus)
                        binding.navView.setHeaderMenu(
                            MenuHeader(
                                userPhoto = data.userInfo?.photo,
                                fullName = data.userInfo?.getFullName(),
                                otherInfo = data.userInfo?.otherInfo
                            )
                        )
                        showBadgeCount(data.appLayoutDto)
                    }
                }
        }

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {
                if (expandedMenuId != -1) {
                    expandedMenuId = -1
                    binding.navView.requestMenuModelBuild()
                }
            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })

        binding.drawerLayout.open()
    }

    private fun showBadgeCount(appLayoutDto: AppLayout) {
        binding.appBarMain.contentMain.bottomNavigationView.apply {
            val notificationCount = appLayoutDto.notificationCount ?: 0
            if (notificationCount > 0) {
                getOrCreateBadge(R.id.notification).apply {
                    isVisible = true
                    number = notificationCount
                }
            } else {
                removeBadge(R.id.notification)
            }
            val messageCount = appLayoutDto.unreadMessageCount ?: 0
            if (messageCount > 0) {
                getOrCreateBadge(R.id.message).apply {
                    isVisible = true
                    number = messageCount
                }
            } else {
                removeBadge(R.id.message)
            }
        }
    }

    /*private fun buildFavoriteMenusModels(favoriteMenus: List<com.app.ecarepro.data.network.model.Menu>) {
        binding.appBarMain.contentMain.recyclerViewMoreOptions.withModels {
            favoriteMenus.forEach { menu ->
                cardOption {
                    id(menu.menuID)
                    data(
                        Slider(
                            imgPath = menu.icon,
                            module = menu.title ?: ""
                        )
                    )
                    clickListener { _ ->
                        getFragmentId(
                            menu.menuID,
                            menu.chMenuID ?: 0
                        )
                        binding.appBarMain.contentMain.moreItemContainer.slideVisibility(false)
                    }
                }
            }
        }
    }*/

    private fun buildFavoriteMenusModels(favoriteMenus: List<com.app.ecarepro.data.network.model.Menu>) {
        binding.appBarMain.contentMain.recyclerViewMoreOptions.withModels {
            favoriteMenus.forEach { menu ->
                if (menu.childMenus.isNullOrEmpty()) {
                    menuCard {
                        id(menu.menuID)
                        title(menu.title)
                        icon(menu.icon)
                        clickListener { _ ->
                            hideMoreItemMenu()
                            getFragmentId(menu.menuID)
                        }
                    }
                } else {
                    menu.childMenus.forEach { childMenu ->
                        if (childMenu.childMenus.isNullOrEmpty()) {
                            menuCard {
                                id(menu.menuID, childMenu.menuID)
                                title(childMenu.title)
                                icon(childMenu.icon)
                                parentMenuIcon(menu.icon)
                                clickListener { _ ->
                                    hideMoreItemMenu()
                                    getFragmentId(menu.menuID, childMenu.chMenuID)
                                }
                            }
                        } else {
                            childMenu.childMenus.forEach { childChildMenu ->
                                menuCard {
                                    id(menu.menuID, childMenu.chMenuID, childChildMenu.menuID)
                                    title(childChildMenu.title)
                                    icon(childChildMenu.icon)
                                    parentMenuIcon(childMenu.icon)
                                    clickListener { _ ->
                                        hideMoreItemMenu()
                                        getFragmentId(
                                            menu.menuID,
                                            childMenu.chMenuID,
                                            childChildMenu.sbChMenuID
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun hideMoreItemMenu() {
        binding.appBarMain.contentMain.moreItemContainer.slideVisibility(false)
        //  listenMenuItemClickEvent = false
    }

    /*   private fun buildDrawerModels(menu: List<com.app.ecarepro.data.network.model.Menu>) {
           binding.recyclerViewNavView.withModels {
               menu.forEach { parentMenu ->
                   drawerItem {
                       id(parentMenu.menuID)
                       title(parentMenu.title)
                       icon(parentMenu.icon)
                       hasChildMenu(parentMenu.childMenus.isNullOrEmpty().not())
                       clickListener { _ ->
                           if (parentMenu.childMenus.isNullOrEmpty().not()) {
                               expandedMenuId = if (expandedMenuId == parentMenu.menuID) {
                                   -1
                               } else {
                                   parentMenu.menuID
                               }
                               this@withModels.requestModelBuild()
                           } else {
                               systemViewModel.openDrawer(false)
                               getFragmentId(parentMenu.menuID, "Menu")
                           }
                       }
                   }

                   if (expandedMenuId == parentMenu.menuID) {
                       parentMenu.childMenus?.forEach { menu ->
                           drawerChildItem {
                               id(parentMenu.menuID, menu.menuID)
                               title(menu.title)
                               icon(menu.icon)
                               clickListener { _ ->
                                   systemViewModel.openDrawer(false)
                                   getFragmentId(parentMenu.menuID, menu.chMenuID, "Menu")
                               }
                           }

                           menu.childMenus?.forEach { childChildMenu ->
                               drawerChildChildItem {
                                   id(parentMenu.menuID, childChildMenu.menuID)
                                   title(childChildMenu.title)
                                   icon(childChildMenu.icon)
                                   clickListener { _ ->
                                       systemViewModel.openDrawer(false)
                                       getFragmentId(
                                           parentMenu.menuID,
                                           menu.chMenuID,
                                           childChildMenu.sbChMenuID,
                                           "Menu"
                                       )
                                   }
                               }
                           }
                       }
                   }
               }
           }
       }*/

    fun getFragmentId(menuID: Int, from: String = "other") {
        lifecycleScope.launch {
            userDataStore.getUser()?.let {
                systemViewModel.UType = userDataStore.getUserType()!!
            }
        }
        systemViewModel.sendAnalyticEvent(
            AnalyticsConstants.Events.MODULE_OPEN, mapOf(
                AnalyticsConstants.Attributes.FROM to from.orEmpty(),
                AnalyticsConstants.Attributes.MENU_ID to menuID.toString()
            )
        )
        when (menuID) {
            3 -> {
                lifecycleScope.launch {
                    userDataStore.getUser()?.run {
                        try {
                            if (userType == Constant.STAFF_TYPE) {
                                navController.navigate(R.id.staffAssignmentsListFragment)
                            } else {
                                navController.navigate(R.id.assignmentNavHostFragment)
                            }
                        } catch (_: Exception) {
                        }
                    }
                }


            }

            4 -> {

                lifecycleScope.launch {
                    userDataStore.getUser()?.run {
                        try {
                            if (userType == Constant.STAFF_TYPE) {

                                navController.navigate(R.id.timeTableNavHostFragment)
                            } else {
                                navController.navigate(
                                    R.id.timeTableNavHostFragment,
                                    Bundle().apply {
                                        putString(
                                            Constant.TIME_TABLE_TYPE,
                                            Constant.CLASS_TIME_TABLE
                                        )
                                    })
                            }
                        } catch (e: Exception) {
                        }
                    }
                }


            }

            5 -> {
                lifecycleScope.launch {
                    userDataStore.getUser()?.run {
                        try {
                            if (userType == Constant.STAFF_TYPE) {
                                navController.navigate(R.id.teacherSyllabusFragment)
                            } else {
                                navController.navigate(R.id.classSyllabus)
                            }

                        } catch (e: Exception) {
                        }
                    }
                }


            }


            10 -> navController.navigate(R.id.calenderActivityNavHost)
            //11 ->  navController.navigate(R.id.feeModule)
            // 12 ->  navController.navigate(R.id.conversationReportFragment)
            12 -> navController.navigate(R.id.bookLibraryFragment)
            13 -> navController.navigate(R.id.EBookNavFragment)
            14 -> navController.navigate(R.id.academicPerformanceHostFragment)
            15 -> navController.navigate(R.id.questionPaperFragment)
            16 -> navController.navigate(R.id.calenderActivityNavHost)

            17 -> {
                lifecycleScope.launch {
                    userDataStore.getUser()?.run {
                        try {
                            if (userType == Constant.STAFF_TYPE) {
                                navController.navigate(R.id.attendanceFragment)
                            } else {
                                val intent1 =
                                    Intent(this@MainActivity, TryAttendanceTest2::class.java)
                                startActivity(intent1)

                                //    navController.navigate(R.id.showAttendanceFragment)
                            }
                        } catch (_: Exception) {
                        }
                    }
                }
            }

            18 -> navController.navigate(R.id.reportCardDetailsNavHostFragment)
            19 -> navController.navigate(R.id.leaveHistoryFragment)
            20 -> navController.navigate(R.id.questionnaireListFragment)
            21 -> navController.navigate(R.id.thoughtsListFragment)

            22 -> {
                lifecycleScope.launch {
                    userDataStore.getUser()?.run {
                        try {
                            if (userType == Constant.STAFF_TYPE) {
                                navController.navigate(R.id.appointmentReportFragment)
                            } else {
                                navController.navigate(R.id.appointmentFragment)
                            }


                        } catch (e: Exception) {
                        }
                    }
                }
            }

            24 -> {
                if (systemViewModel.UType == Constant.STUDENT_TYPE) {
                    navController.navigate(R.id.infractionSelectFragment)
                } else {
                    navController.navigate(R.id.appointmentReportFragment)
                }

            }

            25 -> navController.navigate(R.id.excellenceAwardFragment)
            26 -> navController.navigate(R.id.stuMarkAttendanceFragment)


            27 -> {
                lifecycleScope.launch {
                    userDataStore.getUser()?.run {
                        try {
                            if (userType == Constant.STAFF_TYPE) {
                                lifecycleScope.launch {
                                    userDataStore.getSchoolData()?.let {
                                        it.marksEntryURL?.let { url ->
                                            webViewCall(
                                                url,
                                                getString(R.string.marks_entry_heading)
                                            )
                                        }
                                    }
                                }
                            } else {
                                navController.navigate(R.id.lessonPlanListFragment)
                            }
                        } catch (e: Exception) {
                        }
                    }
                }

            }

            /* 14 -> {
                 try {
                     lifecycleScope.launch {
                         userDataStore.getSchoolData()?.let {
                             if (it.assessmentMarksURL == null) {
                                 showMessage(getString(R.string.assessments_are_currently_unavailable_for_you))
                             } else {
                                 it.assessmentMarksURL?.let { url ->
                                     webViewCall(
                                         url,
                                         getString(R.string.assessment_headling)
                                     )
                                 }
                             }
                         }
                     }
                 } catch (e: Exception) {
                 }

             }*/

            37 -> {
                try {
                    lifecycleScope.launch {
                        userDataStore.getSchoolData()?.let {
                            if (it.webSite == null) {
                                showMessage("Website are currently unavailable for you!")
                            } else {
                                it.webSite?.let { url ->
                                    webViewCall(
                                        url,
                                        getString(R.string.website_txt)
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                }

            }

            28 -> navController.navigate(R.id.lessonPlanListFragment)
            23 -> navController.navigate(R.id.taskManagerFragment)
            30 -> navController.navigate(R.id.selectTransportTypeFragment)
            32 -> navController.navigate(R.id.studentIDFragment)
            33 -> navController.navigate(R.id.surveyListFragment)
            35 -> navController.navigate(R.id.busLocationFragment)
            39 -> navController.navigate(R.id.fomGuardFragment)
            40 -> navController.navigate(R.id.teacherListFragment)

            51 -> navController.navigate(R.id.excellenceAwardFragment)

        }
    }

    fun openCustomTab(customTabsIntent: CustomTabsIntent, uri: Uri?) {
        if (isChromeInstalled(this)) {
            val packageName = "com.android.chrome"
            customTabsIntent.intent.setPackage(packageName)
            try {
                if (uri != null) {
                    customTabsIntent.launchUrl(this, uri)
                }
            } catch (e: ActivityNotFoundException) {
                showMessage("Chrome cannot open this link")
            }
        } else {
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(fallbackIntent)
            } catch (e: ActivityNotFoundException) {
                showMessage("No browser available to handle the URL")
            }
        }
    }

    private fun webViewCall(url: String, title: String) {
        val tabIntent = CustomTabsIntent.Builder()
            .setToolbarColor(getColor(R.color.green)).build()
        if (title.contains("Mark")) {
            systemViewModel.getTokenKey { token ->
                if (token.isNullOrEmpty()) {
                    showMessage("Something went wrong")
                } else {
                    val bundle = Bundle()
                    bundle.putString("title", title)
                    bundle.putString("url", "$url?token=$token")
                    Log.d("WebURL", "$url?token=$token")
                    //navController.navigate(R.id.webViewFragment, bundle)
                    Log.d("WebURL", "$url?token=$token")
                    openCustomTab(tabIntent, Uri.parse("$url?token=$token"))
                }
            }

        } else {
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("url", url)
            Log.d("WebURL", url)
            navController.navigate(R.id.webViewFragment, bundle)
            Log.d("WebURL", url)
            // openCustomTab(tabIntent, Uri.parse(url))
        }
    }

    private fun webViewCallForPayment(feePaymentURL: String) {
        showLoader(true)
        systemViewModel.getTokenKey { token ->
            if (token.isNullOrEmpty()) {
                showLoader(false)
                showMessage("Something went wrong")
            } else {
                showLoader(false)
                Log.d("WebURL", "$feePaymentURL?token=$token")
                val tabIntent = CustomTabsIntent.Builder()
                    .enableUrlBarHiding()
                    .setToolbarColor((this).getColor(R.color.green)).build()
                openCustomTabForPayment(tabIntent, Uri.parse("$feePaymentURL?token=$token"))
            }
        }
    }

    private fun openCustomTabForPayment(customTabsIntent: CustomTabsIntent, uri: Uri?) {
        if (uri == null || uri.scheme.isNullOrEmpty()) {
            showMessage("Invalid or missing URL")
            return
        }
        if (isChromeInstalled(this)) {
            val packageName = "com.android.chrome"
            customTabsIntent.intent.setPackage(packageName)
            try {
                customTabsIntent.launchUrl(this, uri)
            } catch (e: ActivityNotFoundException) {
                showMessage("Chrome cannot open this link")
            }
        } else {
            try {
                // Fallback to WebView if Chrome is not installed or fails
                val webViewIntent = Intent(this, PaymentWebViewActivity::class.java).apply {
                    putExtra("payment_url", uri.toString())
                }
                startActivity(webViewIntent)

                /* val fallbackIntent = Intent(Intent.ACTION_VIEW, uri)
                 startActivity(fallbackIntent)*/
            } catch (e: ActivityNotFoundException) {
                showMessage("No browser available to handle the URL")
            }
        }
    }

    private fun isChromeInstalled(context: Context): Boolean {
        val chromePackageName = "com.android.chrome"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
        intent.setPackage(chromePackageName)

        val resolveInfoList =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfoList.isNotEmpty()
    }


    fun getFragmentId(
        menuID: Int,
        childMenuId: Int,
        refId: String? = null,
        from: String = "other"
    ) {
        lifecycleScope.launch {
            userDataStore.getUser()?.let {
                systemViewModel.UType = userDataStore.getUserType()!!
            }
        }
        systemViewModel.sendAnalyticEvent(
            AnalyticsConstants.Events.MODULE_OPEN, mapOf(
                AnalyticsConstants.Attributes.FROM to from.orEmpty(),
                AnalyticsConstants.Attributes.MENU_ID to menuID.toString(),
                AnalyticsConstants.Attributes.CH_MENU_ID to childMenuId.toString()
            )
        )
        when (menuID) {
            1 -> {
                when (childMenuId) {
                    1 -> {
                        navController.navigate(R.id.studentListFragment2, Bundle().apply {
                            putString(Constant.TO, Constant.PROFILE_FRA_STU)
                        })
                    }

                    3 -> {
                        navController.navigate(R.id.leaveReportFragment, Bundle().apply {
                            putString(Constant.TO, Constant.FRA_STU_LEAVE)
                        })
                    }
                }
            }

            2 -> {
                when (childMenuId) {

                    4 -> {
                        navController.navigate(R.id.staffListFragment, Bundle().apply {
                            putString(Constant.TO, Constant.PROFILE_FRA_STAFF)
                        })

                    }

                    5 -> {
                        navController.navigate(R.id.classTeacherFragment)
                    }

                    6 -> {
                        navController.navigate(R.id.leaveReportFragment, Bundle().apply {
                            putString(Constant.TO, Constant.FRA_STAFF_LEAVE)
                        })
                    }

                    62 -> {
                        navController.navigate(R.id.staffAttendanceFragment)
                    }
                }
            }


            6 -> {
                when (childMenuId) {
                    7 -> navController.navigate(R.id.composeFragment)
                    8 -> navController.navigate(R.id.messageFragment, bundleOf("ID" to refId))
                    9 -> navController.navigate(R.id.messageFragment, bundleOf("openSend" to true))

                }
            }

            7 -> {
                when (childMenuId) {
                    10 -> {
                        Log.e("refId1", "" + refId)

                        if (refId != null) {
                            if (refId == "Menu") {
                                Log.e("refId2", "" + refId)
                                navController.navigate(R.id.circularFragment)
                            } else {
                                Log.e("refId3", "" + refId)
                                navController.navigate(
                                    R.id.circularDetailsFragment,
                                    bundleOf(Constant.CIRCULAR_ID to refId)
                                )
                            }
                        } else {
                            navController.navigate(R.id.circularFragment)
                        }

                    }

                    11 -> {
                        if (refId.isNullOrEmpty() || refId == "Menu") {
                            navController.navigate(R.id.noticeListFragment, Bundle().apply {
                                putString(Constant.NOTICE_TYPE, Constant.NOTICE_SCHOOL)
                            })
                        } else {
                            navController.navigate(R.id.noticeDetailsFragment, Bundle().apply {
                                putString(Constant.NOTICE_ID_ARGUMENT, refId)
                            })
                        }

                    }

                    12 -> {
                        lifecycleScope.launch {
                            userDataStore.getUser()?.run {
                                if (userType == Constant.STAFF_TYPE) {
                                    navController.navigate(R.id.noticeListFragment, Bundle().apply {
                                        putString(Constant.NOTICE_TYPE, Constant.NOTICE_CLASS)
                                        putString(Constant.USER_TYPE, Constant.USER_STAFF)
                                    })
                                } else {
                                    navController.navigate(R.id.noticeListFragment, Bundle().apply {
                                        putString(Constant.NOTICE_TYPE, Constant.NOTICE_CLASS)
                                        putString(Constant.USER_TYPE, Constant.USER_PARENT_STUDENT)
                                    })

                                }
                            }
                        }
                    }
                }
            }

            8 -> {
                when (childMenuId) {
                    13 -> navController.navigate(R.id.studentAttendanceReportFragment)
                    14 -> navController.navigate(R.id.birthdayFragment)

                    15 -> {
                        navController.navigate(R.id.allStaffListFragment, bundleOf("ID" to refId))
                        /*  lifecycleScope.launch {
                              userDataStore.getUser()?.run {
                                  if (userType == Constant.STAFF_TYPE) {
                                      if (roleName == "Principal" || roleName == "Management") {
                                          navController.navigate(
                                              R.id.classAndTeacherListFragment,
                                              Bundle().apply {
                                                  putString(Constant.TO, Constant.FRA_LESSON_PLAN)
                                              })
                                      } else {
                                          navController.navigate(R.id.allStaffListFragment)
                                      }
                                  }
                              }
                          }*/
                    }

                    16 -> navController.navigate(R.id.questionPaperFragment)
                    40 -> navController.navigate(R.id.conversationReportFragment)
                    45 -> navController.navigate(R.id.staticalReport)
                    46 -> navController.navigate(R.id.appUserReportFragment)
                    47 -> navController.navigate(R.id.surveyListFragment)
                    64 -> {

                        navController.navigate(
                            R.id.classAndTeacherListFragment,
                            Bundle().apply {
                                putString(Constant.TO, Constant.FRA_TIMETABLE)
                            })

                    }

                    70 -> {
                        navController.navigate(
                            R.id.smsMsgReportFragment,
                            Bundle().apply {
                                putString(Constant.TO, Constant.FRA_APP_MESSAGE)
                            })
                    }

                }
            }

            10 -> {
                when (childMenuId) {
                    18 -> navController.navigate(R.id.attendanceFragment)
                    19 -> navController.navigate(R.id.leaveHistoryFragment)
                    20 -> navController.navigate(R.id.paySlipFragment)
                }
            }

            11 -> {
                when (childMenuId) {
                    18 -> navController.navigate(R.id.attendanceFragment)
                    20 -> navController.navigate(R.id.paySlipFragment)
                    43 -> {
                        lifecycleScope.launch {
                            try {
                                userDataStore.getSchoolData()?.run {
                                    if (feePayemtURL.isNullOrEmpty()) {
                                        showMessage("Fee Payment URL are currently not unavailable!")
                                    } else {
                                        webViewCallForPayment(feePayemtURL)
                                    }
                                }
                            } catch (_: Exception) {
                            }
                        }
                    }

                    44 -> navController.navigate(R.id.feeReceiptFragment)
                    69 -> navController.navigate(R.id.feeCertificateFragment)
                    72 -> navController.navigate(R.id.feeBookFragment)
                }
            }

            18 -> {
                when (childMenuId) {
                    21 -> navController.navigate(R.id.studentListFragment2)
                    22 -> navController.navigate(R.id.studentListFragment)
                }
            }

            24 -> {
                lifecycleScope.launch {
                    userDataStore.getUser()?.run {
                        when (childMenuId) {
                            21 -> if (userType == Constant.STAFF_TYPE) {
                                navController.navigate(R.id.studentListFragment2, Bundle().apply {
                                    putString(Constant.TO, Constant.APPRECIATION_FRAG)
                                })

                            } else {
                                navController.navigate(R.id.appreciationListFragment)

                            }

                            22 ->
                                navController.navigate(R.id.infractionListFragment, Bundle().apply {
                                    putInt(Constant.USER_TYPE, userType)
                                })
                        }
                    }
                }

            }

            29 -> {
                when (childMenuId) {
                    38 -> navController.navigate(R.id.addQuestionBankFragment)
                    39 -> navController.navigate(R.id.questionBankFragment2)

                }
            }

            6 -> {
                when (childMenuId) {
                    7 -> navController.navigate(R.id.composeFragment)
                    8 -> navController.navigate(R.id.messageFragment, bundleOf("ID" to refId))
                    9 -> navController.navigate(R.id.messageFragment, bundleOf("openSend" to true))

                }
            }
            /*gallery*/
            34 -> {
                when (childMenuId) {
                    48 -> navController.navigate(
                        R.id.photoAlbumTypeNavHostFragment,
                        bundleOf("ID" to refId)
                    )

                    49 -> navController.navigate(R.id.videoAlbumFragment, bundleOf("ID" to refId))
                    50 -> navController.navigate(R.id.favoritesListFragment)
                    51 -> navController.navigate(R.id.mediaGalleryFragment)
                    73 -> navController.navigate(R.id.kidCornerFragment, bundleOf("ID" to refId))
                }
            }

            31 -> {
                when (childMenuId) {
                    40 -> navController.navigate(R.id.calenderActivityNavHost)
                }
            }
            /* *//*gallery*//*
            34 -> {
                when (childMenuId) {
                    48 -> navController.navigate(R.id.photoAlbumTypeNavHostFragment)
                    49 -> navController.navigate(R.id.videoAlbumFragment)
                    50 -> navController.navigate(R.id.videoAlbumFragment)
                }
            }*/
        }
    }


    fun getFragmentId(
        menuID: Int,
        childMenuId: Int,
        childChildMenuId: Int,
        refId: String? = null,
        userID: String? = null,
        from: String = "other"
    ) {
        systemViewModel.sendAnalyticEvent(
            AnalyticsConstants.Events.MODULE_OPEN, mapOf(
                AnalyticsConstants.Attributes.FROM to from.orEmpty(),
                AnalyticsConstants.Attributes.MENU_ID to menuID.toString(),
                AnalyticsConstants.Attributes.CH_MENU_ID to childMenuId.toString(),
                AnalyticsConstants.Attributes.SB_CH_MENU_ID to childChildMenuId.toString(),
            )
        )
        when (menuID) {
            6 -> {
                when (childMenuId) {
                    7 -> navController.navigate(R.id.composeFragment)
                    8 -> navController.navigate(R.id.messageFragment)
                    9 -> navController.navigate(R.id.messageFragment)
                }
            }

            1 -> {
                when (childMenuId) {
                    41 -> {
                        when (childChildMenuId) {
                            1 -> {
                                navController.navigate(R.id.classPromotionFragment)
                            }

                            2 -> {
                                navController.navigate(R.id.assignRollNoFragment)
                            }

                            3 -> {
                                navController.navigate(R.id.assignHomeFragment)
                            }

                            10 -> {
                                navController.navigate(R.id.updateStudentsProfileFragment)
                            }
                        }
                    }


                    2 -> {
                        when (childChildMenuId) {
                            7 -> {
                                navController.navigate(R.id.studentAttendanceReportFragment)
                            }

                            8 -> {
                                navController.navigate(R.id.stuMarkAttendanceFragment)
                            }

                        }
                    }
                }
            }


            24 -> {

                when (childMenuId) {

                    21 -> {
                        when (childChildMenuId) {
                            23 -> {
                                navController.navigate(R.id.studentListFragment2, Bundle().apply {
                                    putString(Constant.TO, Constant.APPRECIATION_FRAG)
                                })
                            }
                        }

                    }

                    22 -> {
                        when (childChildMenuId) {
                            17 -> {
                                navController.navigate(R.id.studentListFragment2, Bundle().apply {
                                    putString(Constant.TO, Constant.INFRECTION_FRAG)
                                })
                            }

                            18 -> {
                                navController.navigate(R.id.staffListFragment, Bundle().apply {
                                    putString(Constant.TO, Constant.PROFILE_FRA_STAFF_INFRACTION)
                                })
                            }

                            20 -> {
                                navController.navigate(R.id.infractionListFragment, Bundle().apply {
                                    putInt(Constant.USER_TYPE, Constant.STAFF_TYPE)
                                })
                            }
                        }
                    }
                }

            }

            8 -> {
                when (childMenuId) {
                    /*sms report*/
                    42 -> {
                        when (childChildMenuId) {
                            4 -> {
                                navController.navigate(R.id.smsReportFragment)
                            }

                            5 -> {
                                navController.navigate(R.id.SMSConsumptionFragment)
                            }

                            6 -> {
                                navController.navigate(R.id.rechargeLogFragment)
                            }

                            14 -> {
                                navController.navigate(
                                    R.id.smsMsgReportFragment,
                                    Bundle().apply {
                                        putString(Constant.TO, Constant.FRA_APP_SMS)
                                    })
                            }
                        }
                    }
                    /* fee report*/
                    67 -> {
                        when (childChildMenuId) {
                            11 -> {
                                navController.navigate(R.id.collectionReport)
                            }

                            12 -> {
                                navController.navigate(R.id.defaulterReportFragment)
                            }

                            13 -> {
                                navController.navigate(R.id.estimateReportFragment)
                            }
                        }
                    }

                    0 -> {
                        when (childChildMenuId) {
                            15 -> {
                                if (refId != null) {
                                    Constant.LESSONPLAN_HARDCCODE_KEY = "LessonList"
                                    navController.navigate(
                                        R.id.viewLessonPlanFragment,
                                        bundleOf(Constant.LESSON_ID_ARGUMENT to refId)
                                    )
                                } else {
                                    navController.navigate(R.id.allStaffListFragment)
                                }
                            }
                        }
                    }
                }
            }


            /*for new development Infraction and  Appreciation */
            /* 24 -> {
                 when (childMenuId) {

                     21 -> {
                         when (childChildMenuId) {
                            23 -> {
                                *//*add to student for Appreciation *//*
                                navController.navigate(R.id.smsReportFragment)
                            }
                        }
                    }

                    22 -> {
                        when (childChildMenuId) {
                            17 -> {
                                *//*add to student for Infraction *//*
                                navController.navigate(R.id.collectionReport)
                            }
                        }
                    }

                }
            }*/
        }
    }

    fun clearAppCache() {
        try {
            val cacheDir = cacheDir
            cacheDir.deleteRecursively()
            appOut()
        } catch (e: Exception) {
            e.printStackTrace()
            appOut()
        }
    }

    fun clearAppData() {
        try {
            // Delete all app databases
            val databases = databaseList()
            for (dbName in databases) {
                deleteDatabase(dbName)
            }
            // Clear Shared Preferences
            val sharedPreferences =
                getSharedPreferences("SHARED_PREF_NAME_PROMPT", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
            // Clear cache as well
            clearAppCache()
        } catch (e: Exception) {
            e.printStackTrace()
            appOut()
        }
    }

    private fun appOut() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        Runtime.getRuntime().exit(0)
    }

    fun logout(forceLogout: Boolean = false) {

        if (forceLogout) {
            lifecycleScope.launch {
                try {
                    systemViewModel.logout {
                        clearAppData()
                    }
                } catch (e: Exception) {
                }
            }
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.are_you_sure_to_logout))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    try {
                        systemViewModel.logout {
                            clearAppData()
                        }
                    } catch (e: Exception) {
                    }
                }
                .setNegativeButton(getString(R.string.no)) { _, _ ->
                }
                .show()
        }

    }

    private fun setUpMoreOptions() {

        binding.appBarMain.contentMain.recyclerViewMoreOptions.addItemDecoration(
            GridMarginDecoration.create(
                margin = resources.getDimensionPixelOffset(R.dimen.vertical_margin),
                columnProvider = object : ColumnProvider {
                    override fun getNumberOfColumns(): Int {
                        return 3
                    }

                }
            ))
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (binding.appBarMain.contentMain.moreItemContainer.isVisible) {
            val viewRect = Rect()
            binding.appBarMain.contentMain.moreItemContainer.getGlobalVisibleRect(viewRect)
            if (!viewRect.contains(ev!!.rawX.toInt(), ev.rawY.toInt())) {
                hideMoreItemMenu()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun setUpBottomNavigationView() {

        binding.appBarMain.contentMain.rlMainSearch.setOnClickListener {
            navController.navigate(
                R.id.searchFragment,
                bundleOf("searchOptions" to systemViewModel.getSearchOptions().filter { it.show })
            )
        }
        binding.appBarMain.contentMain.ivSetting.setOnClickListener {
            navController.navigate(R.id.settingsFragment)
        }

        systemViewModel._showPrompt.observe(this) { open ->
            if (open) {
                SearchPrompt()
            }
        }


        val menuItems = arrayOf(
            CbnMenuItem(
                R.drawable.ic_home,
                R.drawable.avd_home,
                R.id.homeFragment
            ),
            CbnMenuItem(
                R.drawable.ic_settings,
                R.drawable.avd_settings,
                R.id.dashboardFragment
            ),
            CbnMenuItem(
                R.drawable.ic_dashboard,
                R.drawable.avd_dashboard,
                R.id.searchInstitutionFragment
            ),
            CbnMenuItem(
                R.drawable.ic_notification,
                R.drawable.avd_notification,
                R.id.notificationFragment
            ),
            CbnMenuItem(
                R.drawable.ic_profile,
                R.drawable.avd_profile,
                R.id.messageFragment
            )
        )
        //   binding.appBarMain.contentMain.bottomNavigationView.setMenuItems(menuItems, 0)
        binding.appBarMain.contentMain.bottomNavigationView.setupWithNavController(navController)

        binding.appBarMain.contentMain.bottomNavigationView.menu.findItem(R.id.home)
            .setChecked(true)

        binding.appBarMain.contentMain.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu -> {
                    systemViewModel.openDrawer(true)
                    true
                }

                R.id.profile -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }

                R.id.home -> {
                    // loadFragment(SettingFragment())
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.notification -> {
                    navController.navigate(R.id.notificationFragment)
                    true
                }

                R.id.message -> {
                    navController.navigate(R.id.messageFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }

        lifecycleScope.launch {
            systemViewModel.showDashboardValue.collectLatest { v ->
                if (v) {
                    navController.navigate(R.id.action_homeFragment_to_homeViewPagerFragment)
                }
            }

        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun showLoader(show: Boolean) {
        loader?.apply {
            dismiss()
            loader = null
        }
        if (show)
            loader = progressDialog()
    }

    fun showMessage(message: String) {
        Snackbar.make(
            binding.appBarMain.contentMain.bottomNavigationView,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun SearchPrompt() {
        MaterialTapTargetPrompt.Builder(this@MainActivity)
            .setTarget(binding.appBarMain.contentMain.searchBar)
            .setPrimaryText("Global Search")
            .setSecondaryText(" Click here to search Globally in Modules/Students/Staff ")
            .setBackgroundColour(getColor(R.color.brand_color))
            .setPromptStateChangeListener { prompt, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                    settingPrompt()
                }
            }
            .show()
    }

    private fun settingPrompt() {
        MaterialTapTargetPrompt.Builder(this@MainActivity)
            .setTarget(binding.appBarMain.contentMain.ivSetting)
            .setPrimaryText("Setting")
            .setSecondaryText("Click here to access quick settings ")
            .setBackgroundColour(getColor(R.color.brand_color))
            .setPromptStateChangeListener { prompt, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                    menuPrompt()
                }
            }
            .show()
    }

    private fun menuPrompt() {
        MaterialTapTargetPrompt.Builder(this@MainActivity)
            .setTarget(R.id.menu)
            .setPrimaryText("Menu")
            .setSecondaryText("Click here to access Menu Bar")
            .setBackgroundColour(getColor(R.color.brand_color))
            .setFocalColour(getColor(R.color.brand_color))
            .setPromptStateChangeListener { prompt, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                    try {
                        val sharedPreference = getSharedPreferences(
                            Constant.SHARED_PREF_NAME_PROMPT,
                            Context.MODE_PRIVATE
                        )

                        val editor = sharedPreference.edit()
                        editor.putBoolean(Constant.SHARED_PREF_SHOW_PROMPT, true)
                        editor.apply()
                    } catch (e: Exception) {
                    }
                }
            }
            .show()
    }

    private fun UpdateAppVersionDialog(dialog_value: Int, title: String, message: String) {
        val tv_title: TextView
        val tv_description: TextView
        val rel_critical_update_update: RelativeLayout
        val rel_normal_update_update: RelativeLayout
        val rel_normal_update_cancel: RelativeLayout
        val ll_critical_update: LinearLayout
        val ll_normal_update: LinearLayout
        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.window!!.attributes.windowAnimations = R.style.Animations
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_app_version_update)
        tv_title = dialog.findViewById(R.id.tv_title)
        tv_description = dialog.findViewById(R.id.tv_description)
        rel_critical_update_update = dialog.findViewById(R.id.rel_critical_update_update)
        ll_critical_update = dialog.findViewById(R.id.ll_critical_update)
        rel_normal_update_update = dialog.findViewById(R.id.rel_normal_update_update)
        rel_normal_update_cancel = dialog.findViewById(R.id.rel_normal_update_cancel)
        ll_normal_update = dialog.findViewById(R.id.ll_normal_update)
        tv_title.text = title
        tv_description.text = message
        if (dialog_value == 1) {
            ll_critical_update.visibility = View.VISIBLE
            ll_normal_update.visibility = View.GONE
        } else {
            ll_normal_update.visibility = View.VISIBLE
            ll_critical_update.visibility = View.GONE
        }
        rel_normal_update_cancel.setOnClickListener { dialog.dismiss() }
        rel_normal_update_update.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                viewInBrowser(
                    this@MainActivity,
                    "https://play.google.com/store/apps/details?id=$packageName"
                )
            }
        }
        rel_critical_update_update.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                viewInBrowser(
                    this@MainActivity,
                    "https://play.google.com/store/apps/details?id=$packageName"
                )
            }
        }
        dialog.show()
    }

    private fun viewInBrowser(context: Context, url: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (null != intent.resolveActivity(context.packageManager)) {
            context.startActivity(intent)
        }
    }

    private fun syncData(forceSync: Boolean) {
        lifecycleScope.launch {
            showLoader(true)
            syncManager.sync(forceSync) { _, _ ->
                showLoader(false)
            }
        }
    }

    override fun onPause() {
        try {
            isActivityPaused = true
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        AppSessionManager.setCurrentActivity(this, systemViewModel, lifecycleScope)
        showLoader(false)

        if (isActivityPaused) {
            syncData(false)
            isActivityPaused = false
        } else {
            lifecycleScope.launch {
                delay(20.seconds)
                syncData(true)
            }
        }
    }

    companion object {
        private const val MY_REQUEST_CODE = 123
    }
}

fun Fragment.mainActivity(): MainActivity {
    return requireActivity() as MainActivity
}