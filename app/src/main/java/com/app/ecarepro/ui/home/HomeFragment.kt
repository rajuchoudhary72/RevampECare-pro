package com.app.ecarepro.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.Carousel
import com.app.ecarepro.R
import com.app.ecarepro.addMoreFavourites
import com.app.ecarepro.cardOption
import com.app.ecarepro.dashboardCard
import com.app.ecarepro.data.network.model.Card
import com.app.ecarepro.data.network.model.Menu
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.databinding.FragmentHomeBinding
import com.app.ecarepro.databinding.LayoutUndertakingBinding
import com.app.ecarepro.emptyFav
import com.app.ecarepro.labelCenter
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.MainActivityUiState
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.views.carouselNoSnapBuilder
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.imageUrl
import com.app.ecarepro.viewAllWidget
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rubensousa.decorator.ColumnProvider
import com.rubensousa.decorator.DecorationLookup
import com.rubensousa.decorator.GridMarginDecoration
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.util.regex.Matcher
import java.util.regex.Pattern
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var schoolData: NetworkSchool? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mViewModel: HomeViewModel by viewModels()
    private val systemViewModel: SystemViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObservers()

    }

    private fun setUpViews() {

        binding.imgSync.setOnClickListener {
            mViewModel.refresh()
        }
        binding.swipeRefresh.setOnRefreshListener {
            mViewModel.refresh()
            binding.swipeRefresh.isRefreshing = false
        }
      binding.imgUserAvatar.setOnClickListener { findNavController().navigate(R.id.profileFragment) }
      binding.txtUserName.setOnClickListener { findNavController().navigate(R.id.profileFragment) }
        binding.recyclerView.addItemDecoration(
            LinearMarginDecoration.create(
                margin = 8,
                decorationLookup = object : DecorationLookup {
                    override fun shouldApplyDecoration(position: Int, itemCount: Int): Boolean {
                        return binding.recyclerView.adapter?.getItemViewType(position) == R.layout.item_view_all_widget
                    }
                }
            )
        )
        binding.recyclerView.addItemDecoration(
            GridMarginDecoration.create(
                margin =  8,
                columnProvider = object : ColumnProvider {
                    override fun getNumberOfColumns(): Int {
                        return 4
                    }

                },
                decorationLookup = object : DecorationLookup {
                    override fun shouldApplyDecoration(position: Int, itemCount: Int): Boolean {
                        return binding.recyclerView.adapter?.getItemViewType(position) == R.layout.item_card_option
                    }
                },
            )
        )
    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                mViewModel
                    .uiState
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
                    .collectLatest { uiState ->
                        handleUiState(uiState)

                    }
            }

            launch {
                systemViewModel.uiState.collectLatest { uiState ->
                    if (uiState is MainActivityUiState.Success) {
                        mViewModel.setFavourite(uiState.favroiteMenus)
                        uiState.userInfo?.let { user ->
                            binding.apply {
                                imgUserAvatar.imageUrl(user.photo)
                                // txtUserName.text = user.name
                                txtUserName.text = user.getFullHomeScreenName()
                                profilePrompt()
                            }
                        }
                    }
                }

            }

        }
        mViewModel.schoolData.observe(viewLifecycleOwner) {
            schoolData = it
        }

    }

    private fun handleUiState(uiState: Any) {
        (requireActivity() as MainActivity).showLoader(uiState is HomeUiState.Loading)
        if (uiState is HomeUiState.Success) {
            handleUndertaking(uiState.underTaking)
            buildUiModels(uiState)
        }
    }

    private fun handleUndertaking(underTaking: String) {
        val jsonObject = JSONObject(underTaking)
        if (jsonObject.getBoolean("showUserUndertaking")) {
            val string = removeUTFCharacters(jsonObject.getString("htmlDecription"))
            val spannedString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(string.toString(), Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(string.toString())
            }
            val binding =
                LayoutUndertakingBinding.inflate(LayoutInflater.from(requireContext()), null, false)
            binding.text.text = spannedString

            val builder = MaterialAlertDialogBuilder(requireContext())
                .setView(binding.root)
                .setCancelable(false)
                .show()

            binding.btnSubmit.setOnClickListener {
                if (binding.checkbox.isChecked) {
                    (requireActivity() as MainActivity).showLoader(true)
                    mViewModel.submitUserUndertaking(jsonObject.getString("utID")) { isSuccess, message ->
                        (requireActivity() as MainActivity).showLoader(false)
                        mainActivity().showMessage(message)
                        if (isSuccess) {
                            builder.dismiss()
                        }
                    }
                } else {
                    mainActivity().showMessage("Please go throw user undertaking and accept it")
                }
            }


        }

    }
    private fun startLocationFetch() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 120
            )
            return
        }
        if (isGPSEnabled().not()) {
            MaterialAlertDialogBuilder(requireContext()).setTitle("Turn On GPS")
                .setCancelable(false)
                .setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setPositiveButton("No") { d, _ ->
                    d.dismiss()
                    findNavController().popBackStack()
                }.setPositiveButton("Goto Settings, To Enable GPS") { d, _ ->
                    d.dismiss()
                    val callGPSSettingIntent = Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    )
                    startActivity(callGPSSettingIntent)
                }.show()
        } else {
            fusedLocationClient
                .lastLocation
                .addOnSuccessListener { location: Location? ->
                    mViewModel.currentLocation =
                        Pair(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                }
                .addOnFailureListener {
                    Log.e("MSG", "startLocationFetch: " + it.message)
                }
        }
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }
    private fun isGPSEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    private fun removeUTFCharacters(data: String): StringBuffer {
        val p: Pattern = Pattern.compile("\\\\u(\\p{XDigit}{4})")
        val m: Matcher = p.matcher(data)
        val buf = StringBuffer(data.length)
        while (m.find()) {
            val ch = m.group(1).toInt(16).toChar().toString()
            m.appendReplacement(buf, Matcher.quoteReplacement(ch))
        }
        m.appendTail(buf)
        return buf
    }

    private fun buildUiModels(uiState: HomeUiState) {

        (requireActivity() as MainActivity).showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message?:"")
        }

        if (uiState is HomeUiState.Success) {
            binding.recyclerView.withModels {
                try {
                    carouselNoSnapBuilder {
                        id("carousel")
                        numViewsToShowOnScreen(1.2f)
                        spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                        padding(
                            Carousel.Padding(
                                150,
                                resources.getDimensionPixelOffset(
                                    R.dimen.horizontal_margin
                                ), 150,
                                resources.getDimensionPixelOffset(
                                    R.dimen.horizontal_margin
                                ),
                                resources.getDimensionPixelOffset(
                                    R.dimen.horizontal_margin
                                )
                            )
                        )
                        uiState.cards.forEach { favouriteSlider: Card ->
                            dashboardCard {
                                id(favouriteSlider.link)
                                card(favouriteSlider)
                                clickListener { _ ->
                                    if (favouriteSlider.menuID > 0 && favouriteSlider.chMenuID > 0 && favouriteSlider.sbChMenuID > 0) {
                                        (requireActivity() as MainActivity).getFragmentId(
                                            favouriteSlider.menuID,
                                            favouriteSlider.chMenuID,
                                            favouriteSlider.sbChMenuID
                                        )
                                    } else if (favouriteSlider.menuID > 0 && favouriteSlider.chMenuID > 0) {
                                        (requireActivity() as MainActivity).getFragmentId(
                                            favouriteSlider.menuID,
                                            favouriteSlider.chMenuID
                                        )
                                    } else if (favouriteSlider.menuID > 0) {
                                        (requireActivity() as MainActivity).getFragmentId(
                                            favouriteSlider.menuID
                                        )
                                    }
                                    systemViewModel.sendAnalyticEvent(
                                        AnalyticsConstants.Events.SHOW_CARD_CLICK,
                                        mapOf(
                                            AnalyticsConstants.Attributes.SCREEN_NAME to AnalyticsConstants.Screens.HOME_SCREEN,
                                            AnalyticsConstants.Attributes.HEADLINE to favouriteSlider.link.toString(),
                                            AnalyticsConstants.Attributes.MENU_ID to favouriteSlider.menuID.toString(),
                                            AnalyticsConstants.Attributes.CH_MENU_ID to favouriteSlider.chMenuID.toString(),
                                            AnalyticsConstants.Attributes.SB_CH_MENU_ID to favouriteSlider.sbChMenuID.toString(),
                                        )
                                    )
                                }
                            }
                        }
                    }
                }catch (E:IllegalStateException){

                }


                viewAllWidget {
                    id("view_all_widget")
                    spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                    clickListener { _ ->
                        /*findNavController().navigate(
                            R.id.homeViewPagerFragment,
                           // bundleOf("cards" to (mViewModel.uiState.value as HomeUiState.Success).cards)
                        )*/
                        lifecycleScope.launch {
                            var showDashboard=false
                            var showAttendance=false
                            var showFeeds=false
                            for ( i in mViewModel.dashboardButtons.value!!){
                                when (i.buttonName) {
                                    "Dashboard" -> {
                                        showDashboard= i.isShow!!
                                    }
                                    "Attendance" -> {
                                        showAttendance= i.isShow!!
                                    }
                                    "Feed" -> {
                                        showFeeds= i.isShow!!
                                    }
                                }
                            }
                            findNavController().navigate(R.id.homeViewPagerFragment,Bundle( ).apply {
                                putBoolean( "Dashboard",showDashboard)
                                putBoolean( "Attendance",showAttendance)
                                putBoolean( "Feed",showFeeds)
                            })
                        }



                        //systemViewModel.showDashboard(true)
                    }


                }

                labelCenter {
                    id("fav")
                    spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                }
                if (uiState.favourites.isEmpty()) {
                    emptyFav {
                        id("fave")
                        spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                    }
                } else {
                    uiState.favourites.forEach { favouriteSlider: Menu ->
                        cardOption {
                            id(favouriteSlider.title)
                            data(favouriteSlider)
                            clickListener { _ ->
                                if (favouriteSlider.menuID > 0 && favouriteSlider.chMenuID > 0 && favouriteSlider.sbChMenuID > 0) {
                                    (requireActivity() as MainActivity).getFragmentId(
                                        favouriteSlider.menuID,
                                        favouriteSlider.chMenuID,
                                        favouriteSlider.sbChMenuID
                                    )
                                } else if (favouriteSlider.menuID > 0 && favouriteSlider.chMenuID > 0) {
                                    (requireActivity() as MainActivity).getFragmentId(
                                        favouriteSlider.menuID,
                                        favouriteSlider.chMenuID
                                    )
                                } else if (favouriteSlider.menuID > 0) {
                                    (requireActivity() as MainActivity).getFragmentId(
                                        favouriteSlider.menuID
                                    )
                                } else {
                                    if (favouriteSlider.title!!.contains(
                                            getString(R.string.assessment),
                                            true
                                        )
                                    ) {
                                        schoolData?.let {
                                            it.assessmentMarksURL?.let { url ->
                                                webViewCall(
                                                    url,
                                                    getString(R.string.assessment_headling)
                                                )
                                            }
                                        }
                                    } else if (favouriteSlider.title.contains(
                                            getString(R.string.marks_manager),
                                            true
                                        )
                                    ) {
                                        schoolData?.let {
                                            it.marksEntryURL?.let { url ->
                                                webViewCall(
                                                    url,
                                                    getString(R.string.marks_entry_heading)
                                                )
                                            }
                                        }
                                    } else if (favouriteSlider.title.contains(
                                            getString(R.string.website),
                                            true
                                        )
                                    ) {
                                        schoolData?.let {
                                            it.webSite?.let { url ->
                                                webViewCall(url, getString(R.string.website_txt))
                                            }
                                        }
                                    } else {
                                        (requireActivity() as MainActivity).getFragmentId(
                                            favouriteSlider.menuID
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
                addMoreFavourites {
                    id("add more")
                    clickListener { _ ->
                        setFragmentResultListener("favourites") { _, bundle ->
                            if (bundle.containsKey("isUpdate")) {
                                systemViewModel.refreshAppLayout()
                                mViewModel.refresh()

                            }
                        }
                        findNavController().navigate(R.id.favouritesFragment)
                    }
                }
            }
        }
    }
    fun openCustomTab(customTabsIntent: CustomTabsIntent, uri: Uri?) {
        val packageName = "com.android.chrome"
        if (packageName != null) {
            customTabsIntent.intent.setPackage(packageName)
            customTabsIntent.launchUrl(requireContext(), uri!!)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    private fun webViewCall(url: String, title: String) {
        val tabIntent = CustomTabsIntent.Builder().setToolbarColor(requireContext().getColor(R.color.green)).build()
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("url", url)
        openCustomTab(tabIntent, Uri.parse(url))
      //  findNavController().navigate(R.id.webViewFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        systemViewModel.refreshAppLayout()
        systemViewModel.fetchSettings()
        //startLocationFetch()

    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 120) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               // startLocationFetch()
            } else {
                mainActivity().showMessage("GPS permission denied")
            }
        }
    }

    private fun dashboardPrompt( ) {
        MaterialTapTargetPrompt.Builder(requireActivity())
            .setTarget(R.id.ll_dashboard_link)
            .setPrimaryText("Dashboard")
            .setBackgroundColour(requireContext().getColor(R.color.brand_color))
            .setSecondaryText("Click here to access Dashboards")
            .setPromptStateChangeListener { prompt, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                {
                    addMorePrompt()
                }
            }
            .show()
    }

    private fun addMorePrompt( ) {
        MaterialTapTargetPrompt.Builder(requireActivity())
            .setTarget(R.id.ll_add_more)
            .setPrimaryText("Favourites")
            .setBackgroundColour(requireContext().getColor(R.color.brand_color))
            .setSecondaryText("Click here to add your Favourite menus ")
            .setPromptStateChangeListener { prompt, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                {
                    systemViewModel.startShowPrompt(true)
                }
            }
            .show()
    }

    private fun cardPrompt( ) {
        MaterialTapTargetPrompt.Builder(requireActivity())
            .setTarget(R.id.cv_dashboard_card)
            .setPrimaryText(" Information Cards")
            .setSecondaryText("Slide left to check out all the cards")
            .setBackgroundColour(requireContext().getColor(R.color.brand_color))
            .setPromptStateChangeListener { prompt, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                {
                    dashboardPrompt()
                }
            }
            .show()
    }

    private fun profilePrompt() {

        val sharedPreference = requireActivity(). getSharedPreferences(Constant.SHARED_PREF_NAME_PROMPT,
            Context.MODE_PRIVATE)

        if (!sharedPreference.getBoolean(Constant.SHARED_PREF_SHOW_PROMPT, false)) {
            MaterialTapTargetPrompt.Builder(requireActivity())
                .setTarget(binding.imgUserAvatar)
                .setPrimaryText("Profile")
                .setSecondaryText("Click here to check out your profile and Transport Details")
                .setBackgroundColour(requireContext().getColor(R.color.brand_color))
                .setPromptStateChangeListener { prompt, state ->
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                    {
                        cardPrompt()

                        try {

                            val editor = sharedPreference.edit()
                            editor.putBoolean(Constant.SHARED_PREF_SHOW_PROMPT, true)
                            editor.apply()
                        }catch (e: Exception){}

                    }
                }
                .show()
        }


    }


}