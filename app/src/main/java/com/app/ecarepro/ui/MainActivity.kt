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
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.app.ecarepro.R
import com.app.ecarepro.cardOption
import com.app.ecarepro.data.network.model.Slider
import com.app.ecarepro.databinding.ActivityMainBinding
import com.app.ecarepro.drawerChildItem
import com.app.ecarepro.drawerItem
import com.app.ecarepro.ui.views.bottom_navigation.CbnMenuItem
import com.app.ecarepro.utils.progressDialog
import com.app.ecarepro.utils.slideVisibility
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rubensousa.decorator.ColumnProvider
import com.rubensousa.decorator.GridMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val systemViewModel: SystemViewModel by viewModels()

    private val navController: NavController by lazy {
        findNavController(R.id.nav_host_fragment_content_main)
    }

    private var loader: AlertDialog? = null

    private var expandedMenuId: Int = -1


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
                        buildFavoriteMenusModels(data.favroiteMenus)
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

    private fun buildFavoriteMenusModels(favoriteMenus: List<com.app.ecarepro.data.network.model.Menu>) {
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
                        )?.let { navController.navigate(it) }
                        binding.appBarMain.contentMain.moreItemContainer.slideVisibility(false)
                    }
                }
            }
        }
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
                            getFragmentId(parentMenu.menuID)?.let { navController.navigate(it) }
                        }
                    }
                }

                if (expandedMenuId == parentMenu.menuID) {
                    parentMenu.childMenus?.forEach { menu ->
                        drawerChildItem {
                            id(parentMenu.menuID, menu.menuID)
                            title(menu.title)
                            icon(menu.icon)
                            hasChildMenu(menu.childMenus.isNullOrEmpty().not())
                            clickListener { _ ->
                                getFragmentId(
                                    parentMenu.menuID,
                                    menu.chMenuID
                                )?.let {
                                    systemViewModel.openDrawer(false)
                                    navController.navigate(
                                        it
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private fun getFragmentId(menuID: Int): Int? {
        return when (menuID) {
            3 -> R.id.staffAssignmentsListFragment
            4 -> R.id.timeTableNavHostFragment
            5 -> R.id.classSyllabus
            6 -> R.id.messageFragment    // student  app
            10 -> R.id.calenderActivityNavHost //
            12 -> R.id.bookLibraryFragment
            14 -> R.id.questionnaireListFragment
            15 -> R.id.thoughtsListFragment
            16 -> R.id.calenderActivityNavHost    // student  app
            17 -> R.id.showAttendanceFragment    // student  app
            20 -> R.id.questionnaireListFragment    // student  app
            21 -> R.id.thoughtsListFragment    // student  app
            23 -> R.id.taskManagerFragment
            25 -> R.id.excellenceAwardFragment    // student  app
            32 -> R.id.studentIDFragment    // student  app
            33 -> R.id.surveyListFragment
            51 -> R.id.excellenceAwardFragment
            else -> null
        }
    }

    fun getFragmentId(menuID: Int, childMenuId: Int): Int? {
        return when (menuID) {
            1-> {
                return when (childMenuId) {
                    3 -> R.id.leaveHistoryFragment
                    else -> null
                }
            }
            2-> {
                return when (childMenuId) {
                    6 -> R.id.leaveHistoryFragment
                    else -> null
                }
            }
            6 -> {
                return when (childMenuId) {
                    7 -> R.id.composeFragment
                    8 -> R.id.messageFragment
                    9 -> R.id.messageFragment
                    else -> null
                }
            }

            7 -> {
                return when (childMenuId) {
                    10 -> R.id.circularFragment
                    11 -> R.id.noticeListFragment
                    12 -> R.id.noticeListFragment
                    else -> null
                }
            }
            8-> {
                return when (childMenuId) {
                    45-> R.id.staticalReport
                    46-> R.id.appUserReportFragment
                    47-> R.id.surveyListFragment
                    else -> null
                }
            }

            10 -> {
                return when (childMenuId) {
                    18 -> R.id.attendanceFragment
                    19 -> R.id.leaveHistoryFragment
                    20 -> R.id.paySlipFragment
                    else -> null
                }
            }

            11 -> {
                return when (childMenuId) {
                    18 -> R.id.attendanceFragment
                    20 -> R.id.paySlipFragment
                    else -> null
                }
            }

            18 -> {
                return when (childMenuId) {
                    21 -> R.id.studentListFragment2
                    22 -> R.id.studentListFragment
                    else -> null
                }
            }

            else -> {
                null
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
}