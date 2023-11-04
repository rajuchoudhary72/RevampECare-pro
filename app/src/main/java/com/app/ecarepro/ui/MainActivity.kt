package com.app.ecarepro.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.app.ecarepro.R
import com.app.ecarepro.cardOption
import com.app.ecarepro.databinding.ActivityMainBinding
import com.app.ecarepro.ui.views.bottom_navigation.CbnMenuItem
import com.app.ecarepro.utils.slideVisibility
import com.rubensousa.decorator.ColumnProvider
import com.rubensousa.decorator.GridMarginDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val navController: NavController by lazy {
        findNavController(R.id.nav_host_fragment_content_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.contentMain.bottomNavigationView.isVisible = destination.id == R.id.homeFragment
        }

        setUpBottomNavigationView()

        setUpMoreOptions()

    }

    private fun setUpMoreOptions() {

        binding.contentMain.recyclerViewMoreOptions.addItemDecoration(GridMarginDecoration.create(
            margin = resources.getDimensionPixelOffset(R.dimen.vertical_margin),
            columnProvider = object : ColumnProvider {
                override fun getNumberOfColumns(): Int {
                    return 3
                }

            }
        ))

        binding.contentMain.recyclerViewMoreOptions.withModels {
            (0..16).forEach {
                cardOption { id(it) }
            }
        }
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
                R.id.signInFragment
            ),
            CbnMenuItem(
                R.drawable.ic_dashboard,
                R.drawable.avd_dashboard,
                R.id.searchInstitutionFragment
            ),
            CbnMenuItem(
                R.drawable.ic_notification,
                R.drawable.avd_notification,
                R.id.forgotPasswordFragment
            ),
            CbnMenuItem(
                R.drawable.ic_profile,
                R.drawable.avd_profile,
                R.id.helpFragment
            )
        )
        binding.contentMain.bottomNavigationView.setMenuItems(menuItems, 2)
        binding.contentMain.bottomNavigationView.setupWithNavController(navController)

        binding.contentMain.bottomNavigationView.setOnMenuItemClickListener { cbnMenuItem, _ ->
            binding.contentMain.moreItemContainer.slideVisibility(cbnMenuItem.icon == R.drawable.ic_dashboard)
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
}