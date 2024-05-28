package com.app.ecarepro.ui.home

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.Carousel
import com.app.ecarepro.R
import com.app.ecarepro.addMoreFavourites
import com.app.ecarepro.cardOption
import com.app.ecarepro.dashboardCard
import com.app.ecarepro.data.network.model.Card
import com.app.ecarepro.data.network.model.Menu
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.Slider
import com.app.ecarepro.databinding.FragmentHomeBinding
import com.app.ecarepro.databinding.LayoutUndertakingBinding
import com.app.ecarepro.labelCenter
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.MainActivityUiState
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.views.carouselNoSnapBuilder
import com.app.ecarepro.utils.imageUrl
import com.app.ecarepro.viewAllWidget
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rubensousa.decorator.ColumnProvider
import com.rubensousa.decorator.DecorationLookup
import com.rubensousa.decorator.GridMarginDecoration
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern


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
        binding.btnMenu.setOnClickListener { systemViewModel.openDrawer(true) }
        binding.imgUserAvatar.setOnClickListener { findNavController().navigate(R.id.profileFragment) }
        binding.recyclerView.addItemDecoration(
            LinearMarginDecoration.create(
                margin = resources.getDimensionPixelOffset(
                    R.dimen.horizontal_margin
                ),
                decorationLookup = object : DecorationLookup {
                    override fun shouldApplyDecoration(position: Int, itemCount: Int): Boolean {
                        return binding.recyclerView.adapter?.getItemViewType(position) == R.layout.item_view_all_widget
                    }
                }
            )
        )
        binding.recyclerView.addItemDecoration(
            GridMarginDecoration.create(
                margin = resources.getDimensionPixelOffset(
                    R.dimen.horizontal_margin
                ),
                columnProvider = object : ColumnProvider {
                    override fun getNumberOfColumns(): Int {
                        return 3
                    }

                },
                decorationLookup = object : DecorationLookup {
                    override fun shouldApplyDecoration(position: Int, itemCount: Int): Boolean {
                        return binding.recyclerView.adapter?.getItemViewType(position) == R.layout.item_card_option
                    }
                }
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
                        uiState.userInfo.let { user ->
                            binding.apply {
                                imgUserAvatar.imageUrl(user.photo)
                                txtUserName.text = user.name
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

    private fun handleUiState(uiState: HomeUiState) {
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
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        if (isSuccess) {
                            builder.dismiss()
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please go throw user undertaking and accept it",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }

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
            Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
        }

        if (uiState is HomeUiState.Success) {
            binding.recyclerView.withModels {
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
                    uiState.cards.forEach { card: Card ->
                        dashboardCard {
                            id(card.link)
                            card(card)
                            clickListener { _ ->
                                (requireActivity() as MainActivity).getFragmentId(card.menuID, card.chmenuID)
                                    ?.let {
                                       /* findNavController().navigate(it)*/
                                    }
                            }
                        }
                    }
                }

                viewAllWidget {
                    id("view_all_widget")
                    spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                    clickListener { _ ->
                        findNavController().navigate(
                            R.id.widgetsFragment,
                            bundleOf("cards" to (mViewModel.uiState.value as HomeUiState.Success).cards)
                        )
                    }
                }

                labelCenter {
                    id("fav")
                    spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                }

                uiState.favourites.forEach { favouriteSlider: Menu ->
                    cardOption {
                        id(favouriteSlider.title)
                        data(favouriteSlider)
                        clickListener { _ ->
                            if (favouriteSlider.chMenuID>0){
                                    (requireActivity() as MainActivity).getFragmentId(favouriteSlider.menuID, favouriteSlider.chMenuID)
                            }else{
                                if (favouriteSlider.title!!.contains(getString(R.string.assessment), true)) {
                                    schoolData?.let {
                                        it.assessmentMarksURL?.let { url ->
                                            webViewCall(url, getString(R.string.assessment_headling))
                                        }
                                    }
                                }else if (favouriteSlider.title.contains(getString(R.string.marks_manager), true)) {
                                    schoolData?.let {
                                        it.marksEntryURL?.let { url ->
                                            webViewCall(url, getString(R.string.marks_entry_heading))
                                        }
                                    }
                                }else if (favouriteSlider.title.contains(getString(R.string.website), true)) {
                                    schoolData?.let {
                                        it.webSite?.let { url ->
                                            webViewCall(url, getString(R.string.website_txt))
                                        }
                                    }
                                }else{
                                    (requireActivity() as MainActivity).getFragmentId(favouriteSlider.menuID)
                                }

                            }
                        }
                /*        clickListener {
                            _ -> navigateToFavourites(favouriteSlider) }*/
                    }
                }
                addMoreFavourites {
                    id("add more")
                    clickListener { _ ->
                        setFragmentResultListener("favourites") { _, bundle ->
                            if (bundle.containsKey("isUpdate")) {
                                systemViewModel.refreshAppLayout()
                            }
                        }
                        findNavController().navigate(R.id.favouritesFragment)
                    }
                }
            }
        }
    }

    private fun navigateToFavourites(favouriteSlider: Slider) {
        if (favouriteSlider.module.contains("notice", true)) {
            findNavController().navigate(R.id.noticeListFragment)
        } else if (favouriteSlider.module.contains("thought", true)) {
            findNavController().navigate(R.id.thoughtsListFragment)
        } else if (favouriteSlider.module.contains("circular", true)) {
            findNavController().navigate(R.id.circularFragment)
        } else if (favouriteSlider.module.contains("library", true)) {
            findNavController().navigate(R.id.bookLibraryFragment)
        } else if (favouriteSlider.module.contains("syllabus", true)) {
            findNavController().navigate(R.id.classSyllabus)
        } else if (favouriteSlider.module.contains("activity", true)) {
            findNavController().navigate(R.id.calenderActivityNavHost)
        } else if (favouriteSlider.module.contains("payslip", true)||favouriteSlider.module.contains("pay slip", true)) {
            findNavController().navigate(R.id.paySlipFragment)
        } else if (favouriteSlider.module.contains("Questionnaire", true)) {
            findNavController().navigate(R.id.questionnaireListFragment)
        } else if (favouriteSlider.module.contains("Leave Request", true)) {
            findNavController().navigate(R.id.leaveHistoryFragment)
        } else if (favouriteSlider.module.contains("Appreciation", true)) {
            findNavController().navigate(R.id.studentListFragment2)
        } else if (favouriteSlider.module.contains("Class Promotion", true)) {
            findNavController().navigate(R.id.classPromotionFragment)
        } else if (favouriteSlider.module.contains("Timetable", true)) {
            findNavController().navigate(R.id.timeTableNavHostFragment)
        } else if (favouriteSlider.module.contains("Birthday", true)) {
            findNavController().navigate(R.id.birthdayFragment)
        } else if (favouriteSlider.module.contains("Assignment", true)) {
            findNavController().navigate(R.id.staffAssignmentsListFragment)
        } else if (favouriteSlider.module.contains("Attendance", true)) {
            findNavController().navigate(R.id.attendanceFragment)
        } else if (favouriteSlider.module.contains("Excellence Award", true)) {
            findNavController().navigate(R.id.excellenceAwardFragment)
        } else if (favouriteSlider.module.contains("Medicine Issue", true)) {
            findNavController().navigate(R.id.medicineIssuedFragment)
        } else if (favouriteSlider.module.contains("Assign House", true)) {
            findNavController().navigate(R.id.assignHomeFragment)
        } else if (favouriteSlider.module.contains("Medical History", true)) {
            findNavController().navigate(R.id.medicalCardFragment)
        } else if (favouriteSlider.module.contains("Id Card", true)) {
            // findNavController().navigate(R.id.medicalClassFragment)
            findNavController().navigate(R.id.studentIDFragment)
        } else if (favouriteSlider.module.contains("SMS Addon", true)) {
            findNavController().navigate(R.id.medicalClassFragment)
        }else if (favouriteSlider.module.contains("Teachers", true)) {
            findNavController().navigate(R.id.subjectTeacherFragment)
        }else if (favouriteSlider.module.contains("Classmates", true)) {
            findNavController().navigate(R.id.classMateFragment)
        }else if (favouriteSlider.module.contains("Survey", true)) {
            findNavController().navigate(R.id.surveyListFragment)
             }
        /*start Web view module call  from here */
        else if (favouriteSlider.module.contains("Website", true)) {
            schoolData?.let {
                it.webSite?.let { url ->
                    webViewCall(url, getString(R.string.website_txt))
                }
            }
        } else if (favouriteSlider.module.contains("Marks Entry", true)) {
            schoolData?.let {
                it.marksEntryURL?.let { url ->
                    webViewCall(url, getString(R.string.marks_entry_heading))
                }
            }
        } else if (favouriteSlider.module.contains("Assessment", true)) {
            schoolData?.let {
                it.assessmentMarksURL?.let { url ->
                    webViewCall(url, getString(R.string.assessment_headling))
                }
            }
        }
        /*end Web view module call  from here */
        else {
            Log.e("Home", favouriteSlider.toString())
        }
    }

    private fun webViewCall(url: String, title: String) {
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("url", url)
        findNavController().navigate(R.id.webViewFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        systemViewModel.refreshAppLayout()
    }
}