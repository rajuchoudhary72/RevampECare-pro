package com.app.ecarepro.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.databinding.ActivityMainBinding
import com.app.ecarepro.drawerChildItem
import com.app.ecarepro.drawerItem
import com.app.ecarepro.ui.views.bottom_navigation.CbnMenuItem
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.progressDialog
import com.app.ecarepro.utils.slideVisibility
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rubensousa.decorator.ColumnProvider
import com.rubensousa.decorator.GridMarginDecoration
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.app.ecarepro.drawerChildChildItem
import com.app.ecarepro.menuCard
import com.app.ecarepro.drawerChildChildItem
import com.app.ecarepro.menuCard
import com.google.android.material.snackbar.Snackbar


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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


    private val topLevelFragments = mutableListOf(
        R.id.homeFragment,
        R.id.settingsFragment,
        R.id.notificationFragment,
        R.id.messageFragment,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

/*open profile page after click on  profile */
        binding.itemDrawerHeader.imgUserAvatar.setOnClickListener {
            navController.navigate(R.id.profileFragment)
            systemViewModel.openDrawer(false)
        }

        setSupportActionBar(binding.appBarMain.toolbar)

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.appBarMain.contentMain.bottomNavigationView.isVisible =
                topLevelFragments.contains(destination.id)
        }

        setUpDrawer()

        setUpBottomNavigationView()

        setUpMoreOptions()

        Picasso.setSingletonInstance(Picasso.Builder(this).build())

        lifecycleScope.launch {
            systemViewModel.user.collectLatest {
                if (it != null) {
                    userData = it
                }
            }
        }
    }



    fun setUpDrawer() {
        systemViewModel.openNavigationDrawer.observe(this) { open ->
            if (open) {
                binding.drawerLayout.open()
            } else {
                binding.drawerLayout.close()
            }
        }

        lifecycleScope.launch {
            systemViewModel.uiState
                .flowWithLifecycle(lifecycle)
                .collectLatest { uiState ->
                    uiState.getValueOrNull()?.let { data ->
                        buildDrawerModels(data.menus)
                        buildFavoriteMenusModels(data.menus)
                        binding.itemDrawerHeader.user = data.userInfo
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

        binding.itemDrawerFooter.appVersion = "App Version 1.0.0"
        binding.itemDrawerFooter.setClickListener {
            logout()
        }

        binding.drawerLayout.open()
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
                if(menu.childMenus.isNullOrEmpty()){
                    menuCard {
                        id(menu.menuID)
                        title(menu.title)
                        icon(menu.icon)
                        clickListener { _ ->
                            hideMoreItemMenu()
                            getFragmentId(menu.menuID)
                        }
                    }
                }else{
                    menu.childMenus.forEach { childMenu ->
                        if(childMenu.childMenus.isNullOrEmpty()){
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
                        }else{
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
        binding.appBarMain.contentMain.bottomNavigationView.onMenuItemClick(0)
        listenMenuItemClickEvent = false
    }
    private fun buildDrawerModels(menu: List<com.app.ecarepro.data.network.model.Menu>) {
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
                            getFragmentId(parentMenu.menuID)
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
                                getFragmentId(
                                    parentMenu.menuID,
                                    menu.chMenuID
                                )
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

      fun getFragmentId(menuID: Int) {
        when (menuID) {
            3 -> {
                if (userData.userType == Constant.STAFF_TYPE) {
                    if (systemViewModel.userRoleName == "Principal" || systemViewModel.userRoleName == "Management") {
                        navController.navigate(R.id.classAndTeacherListFragment, Bundle().apply {
                            putString(Constant.TO, Constant.FRA_ASSI)
                        })
                    } else {
                        navController.navigate(R.id.staffAssignmentsListFragment)
                    }

                }else{
                    navController.navigate(R.id.assignmentNavHostFragment)
                }

            }

            4 -> {
                try {
                    if (userData.userType == Constant.STAFF_TYPE) {
                        if (systemViewModel.userRoleName == "Principal" || systemViewModel.userRoleName == "Management") {
                            navController.navigate(R.id.classAndTeacherListFragment, Bundle().apply {
                                putString(Constant.TO, Constant.FRA_TIMETABLE)
                            })
                        } else {
                            navController.navigate(R.id.timeTableNavHostFragment)
                        }

                    } else {
                        navController.navigate(R.id.timeTableNavHostFragment, Bundle().apply {
                            putString(Constant.TIME_TABLE_TYPE, Constant.CLASS_TIME_TABLE)
                        })
                    }
                }catch (e:Exception){

                }


            }

            5 -> navController.navigate(R.id.classSyllabus)
            10 ->  navController.navigate(R.id.calenderActivityNavHost)
            //11 ->  navController.navigate(R.id.feeModule)
           // 12 ->  navController.navigate(R.id.conversationReportFragment)
            12 ->  navController.navigate(R.id.bookLibraryFragment )
            13 ->  navController.navigate(R.id.bookLibraryFragment)
            16 ->  navController.navigate(R.id.calenderActivityNavHost)

            17 -> {
                try {
                    if (userData.userType == Constant.STAFF_TYPE) {
                        navController.navigate(R.id.attendanceFragment)
                    }  else{
                        navController.navigate(R.id.showAttendanceFragment)
                    }
                }catch (_:Exception){}
            }
            18 ->  navController.navigate(R.id.reportCardDetailsNavHostFragment)
            19 ->  navController.navigate(R.id.leaveHistoryFragment)
            20 ->  navController.navigate(R.id.questionnaireListFragment)
            21 ->  navController.navigate(R.id.thoughtsListFragment)
            22 ->  navController.navigate(R.id.appointmentReportFragment)
            24 -> {
                if (userData.userType == Constant.STUDENT_TYPE) {
                        navController.navigate(R.id.infractionSelectFragment)
                } else {
                    navController.navigate(R.id.appointmentReportFragment)
                }

            }
            25 ->  navController.navigate(R.id.excellenceAwardFragment)
            26 ->  navController.navigate(R.id.selectMarkAttendanceFragment)
            27 ->  navController.navigate(R.id.lessonPlanListFragment)
            28 ->  navController.navigate(R.id.lessonPlanListFragment)
             23 ->  navController.navigate(R.id.taskManagerFragment)
             30 ->  navController.navigate(R.id.selectTransportTypeFragment)
            32 ->  navController.navigate(R.id.studentIDFragment)
            33 ->  navController.navigate(R.id.surveyListFragment)
            51 ->  navController.navigate(R.id.excellenceAwardFragment)

        }
    }
      fun getFragmentId(menuID: Int, childMenuId: Int) {
        when (menuID) {
            1 -> {
                when (childMenuId) {
                    1 -> {
                        navController.navigate(R.id.studentListFragment2,Bundle( ).apply {
                            putString(Constant.TO,  Constant.PROFILE_FRA_STU)
                        })
                    }
                    2 -> {
                        navController.navigate(R.id.studentAttendanceReportFragment)
                    }
                    3 -> {
                        navController.navigate(R.id.leaveReportFragment,Bundle( ).apply {
                            putString(Constant.TO,  Constant.FRA_STU_LEAVE)
                        })
                    }
                }
            }
            2 -> {
                when (childMenuId) {

                    4 -> {
                        navController.navigate(R.id.staffListFragment,Bundle( ).apply {
                            putString(Constant.TO,  Constant.PROFILE_FRA_STAFF)
                        })

                    }

                    5 -> {
                        navController.navigate(R.id.classTeacherFragment)
                    }

                    6 -> {
                        navController.navigate(R.id.leaveReportFragment,Bundle( ).apply {
                            putString(Constant.TO,  Constant.FRA_STAFF_LEAVE)
                        })
                    }

                }
            }




            6 -> {
                when (childMenuId) {
                    7 -> navController.navigate(R.id.composeFragment)
                    8 -> navController.navigate(R.id.messageFragment)
                    9 -> navController.navigate(R.id.messageFragment)
                }
            }

            7 -> {
                when (childMenuId) {
                    10 -> navController.navigate(R.id.circularFragment)

                    11 -> navController.navigate(R.id.noticeListFragment, Bundle().apply {
                        putString(Constant.NOTICE_TYPE, Constant.NOTICE_SCHOOL)
                    })
                    12 ->if (userData.userType == Constant.STAFF_TYPE) {
                        navController.navigate(R.id.noticeListFragment, Bundle().apply {
                            putString(Constant.NOTICE_TYPE, Constant.NOTICE_CLASS)
                            putString(Constant.USER_TYPE, Constant.USER_STAFF)
                        })
                    }else{
                        navController.navigate(R.id.noticeListFragment, Bundle().apply {
                            putString(Constant.NOTICE_TYPE, Constant.NOTICE_CLASS)
                            putString(Constant.USER_TYPE, Constant.USER_PARENT_STUDENT)
                        })

                    }
                }
            }
            8 -> {
                when (childMenuId) {
                    13 -> navController.navigate(R.id.studentAttendanceReportFragment)
                    14 -> navController.navigate(R.id.birthdayFragment)
                    15 ->   if (userData.userType == Constant.STAFF_TYPE) {
                        if (systemViewModel.userRoleName == "Principal" || systemViewModel.userRoleName == "Management") {
                            navController.navigate(R.id.classAndTeacherListFragment, Bundle().apply {
                                putString(Constant.TO, Constant.FRA_LESSON_PLAN)
                            })
                        }else{
                            navController.navigate(R.id.lessonPlanListFragment)

                        }

                    }

                    16 -> navController.navigate(R.id.questionPaperFragment)
                 //   42 -> navController.navigate(R.id.smsMsgReportFragment)
                    45 -> navController.navigate(R.id.staticalReport)
                    46 -> navController.navigate(R.id.appUserReportFragment)
                     47 -> navController.navigate(R.id.surveyListFragment)


                }
            }

            10 -> {
                when (childMenuId) {
                    18 ->  navController.navigate(R.id.attendanceFragment)
                    19 ->  navController.navigate(R.id.leaveHistoryFragment)
                    20 ->  navController.navigate(R.id.paySlipFragment)
                }
            }

            11 -> {
                when (childMenuId) {
                    18 ->  navController.navigate(R.id.attendanceFragment)
                    20 ->  navController.navigate(R.id.paySlipFragment)
                    43 ->  navController.navigate(R.id.feePaymentFragment)
                    44 ->  navController.navigate(R.id.feeReceiptFragment)
                }
            }

            18 -> {
                when (childMenuId) {
                    21 ->  navController.navigate(R.id.studentListFragment2)
                    22 ->  navController.navigate(R.id.studentListFragment)
                }
            }

            24 -> {
                when (childMenuId) {
                    21 ->  if (userData.userType == Constant.STAFF_TYPE) {
                        navController.navigate(R.id.appreciationSelectionFragment)

                    } else {
                        navController.navigate(R.id.appreciationListFragment)

                    }
                    22 -> if (userData.userType == Constant.STAFF_TYPE) {
                        navController.navigate(R.id.infractionSelectFragment)

                    } else {
                        navController.navigate(R.id.infractionListFragment)

                    }

                }
            }
            29 -> {
                when(childMenuId){
                    38 ->  navController.navigate(R.id.addQuestionBankFragment)
                    39 ->  navController.navigate(R.id.questionBankFragment2)

                }
            }
            34 -> {
                when(childMenuId){
                    48 ->  navController.navigate(R.id.photoAlbumTypeNavHostFragment)
                    49 ->  navController.navigate(R.id.videoAlbumFragment)
                    50 ->  navController.navigate(R.id.favoritesListFragment)
                    51 ->  navController.navigate(R.id.mediaGalleryFragment)
                }
            }

            31 -> {
                when (childMenuId) {
                    40 ->  navController.navigate(R.id.calenderActivityNavHost)
                }
            }
            /*gallery*/
            34 -> {
                when (childMenuId) {
                    48 ->  navController.navigate(R.id.photoAlbumTypeNavHostFragment)
                    49 ->  navController.navigate(R.id.videoAlbumFragment)
                    50 ->  navController.navigate(R.id.videoAlbumFragment)
                }
            }
        }
    }

    fun getFragmentId(menuID: Int, childMenuId: Int, childChildMenuId: Int) {
        when (menuID) {
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
                        }
                    }
                }
            }

            8 -> {
                when (childMenuId) {
                    42 -> {
                        when (childChildMenuId) {
                            4 -> {
                                navController.navigate(R.id.smsMsgReportFragment)
                            }
                            5 -> {
                                navController.navigate(R.id.SMSConsumptionFragment)
                            }
                            6 -> {
                                navController.navigate(R.id.rechargeLogFragment)
                            }
                        }
                    }
                }
            }
        }
    }
    fun logout() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.are_you_sure_to_logout))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                systemViewModel.logout {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    Runtime.getRuntime().exit(0)
                }
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->

            }
            .show()
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

    private fun setUpBottomNavigationView() {
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
        binding.appBarMain.contentMain.bottomNavigationView.setMenuItems(menuItems, 0)
        //binding.appBarMain.contentMain.bottomNavigationView.setupWithNavController(navController)

        binding.appBarMain.contentMain.bottomNavigationView.setOnMenuItemClickListener { cbnMenuItem, position ->
            if (listenMenuItemClickEvent.not()) {
                listenMenuItemClickEvent = true
                return@setOnMenuItemClickListener
            }
            binding.appBarMain.contentMain.moreItemContainer.slideVisibility(cbnMenuItem.icon == R.drawable.ic_dashboard)

            when (position) {
                0 -> {
                    navController.navigate(R.id.homeFragment)
                }

                1 -> {
                    navController.navigate(R.id.settingsFragment)
                }

                3 -> {
                    navController.navigate(R.id.notificationFragment)
                }

                4 -> {
                    navController.navigate(R.id.messageFragment)
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
}
fun Fragment.mainActivity(): MainActivity {
    return requireActivity() as MainActivity
}