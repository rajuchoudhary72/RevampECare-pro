package com.app.ecarepro.ui.lessonPlan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.MyClasseItem
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentLessionPlanListBinding
import com.app.ecarepro.model.LessonPlan
import com.app.ecarepro.model.MySubject
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LessonPlanListFragment : Fragment(), MenuProvider, ItemListener<LessonPlan> {

    private   var teacherID: String = ""
    private var isNotFilterList: Boolean = true
    private lateinit var selectedSubject: MySubject
    private lateinit var subjectFilterer: List<MySubject>
    private lateinit var selectedClass: MyClasseItem
    private lateinit var classesFilter: List<MyClasseItem>
    private lateinit var binding: FragmentLessionPlanListBinding
    private val lessonPlanListViewModel: LessonPlanListViewModel by viewModels()
    private var lessonArrayList = mutableListOf<LessonPlan>()
    private var pageIndex: Int = 1
    private var pastVisiblesItems: Int = 0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0
    private var isLoading: Boolean = true

    private lateinit var lessonPlanListAdapter: LessonPlanListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLessionPlanListBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        lessonPlanListAdapter = LessonPlanListAdapter(lessonArrayList, this,lessonPlanListViewModel.userType)

        with(binding) {
            recyclerLessonPlan.adapter = lessonPlanListAdapter
            if (activity is AppCompatActivity) {
                (activity as AppCompatActivity).setSupportActionBar(toolbar)
            }

        }
        try {
            teacherID = requireArguments().getString(Constant.STAFF_ID_ARGUMENT).toString()

        }catch (_:Exception){}
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            fbAdd.setOnClickListener {
                findNavController().navigate(R.id.action_lessonPlanListFragment_to_addLessonFragment)
            }
            llFromDate.setOnClickListener {
                ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                    override fun onSelect(date: String?, isCurrentDate: Boolean) {
                        tvFromDate.text = date

                        llToDate.setOnClickListener {
                            ECareDataPicker(
                                requireActivity(),
                                false,
                                object : ECareDataPicker.PickerCallback {
                                    override fun onSelect(date: String?, isCurrentDate: Boolean) {
                                        tvToDate.text = date

                                        getLessonPlanFilter(
                                            "date",
                                            tvFromDate.text.toString(),
                                            tvToDate.text.toString(),
                                            "", "", 0
                                        )

                                    }
                                })
                        }
                    }
                })
            }
        }

        binding.autoCompleteClass.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, pos, id ->
                selectedClass = classesFilter[pos]
                getLessonPlanFilter(
                    "class",
                    "",
                    "",
                    selectedClass.classID.toString(), "", 0
                )

            }

        binding.autoCompleteSub.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, pos, id ->
                selectedSubject = subjectFilterer[pos]
                getLessonPlanFilter(
                    "subject",
                    "",
                    "",
                    "", selectedSubject.subID.toString(), 0
                )
            }





        setupRecycleViewPager()
        getLessonPlanList()
        setUpClassFilter()
        setSubjectFilter()
        setStatusFilter()

        if ( lessonPlanListViewModel. userType == Constant.PRINCIPAL || lessonPlanListViewModel. userType ==  Constant.MANAGEMENT) {
            binding.fbAdd.isVisible=false
        }

    }

    private fun setStatusFilter() {

        val spinnerItems = resources.getStringArray(R.array.array_search_leave_status)
        val adapter = activity?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_spinner_item,
                spinnerItems
            )
        }
        binding.spLeaveStatus.adapter = adapter

        binding.spLeaveStatus.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

                getLessonPlanFilter(
                    "status",
                    "",
                    "",
                    "", "", position
                )
                isNotFilterList = false

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

    }

    private fun setSubjectFilter() {

        lifecycleScope.launch {
            lessonPlanListViewModel.subjectsStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {
                            if (it.data.mySubjects != null) {
                                subjectFilterer = it.data.mySubjects
                                val subjectDataString: ArrayList<String> = ArrayList()
                                subjectDataString.clear()
                                it.data.mySubjects.forEach { data ->
                                    subjectDataString.add(data.subjectName)
                                }
                                val arrayAdapter = ArrayAdapter(
                                    requireContext(),
                                    R.layout.view_drop_down_menu,
                                    subjectDataString
                                )
                                binding.autoCompleteSub.setAdapter(arrayAdapter)
                            } else {
                                mainActivity().showMessage("No Subject Assign")

                            }
                        }
                    }
                }
            }
        }
        lessonPlanListViewModel.mySubjects(0)


    }

    private fun setUpClassFilter() {

        lifecycleScope.launch {
            lessonPlanListViewModel.myClassStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerLessonPlan.isVisible = true

                        if (it.data != null) {

                            if (it.data.myClasses != null) {

                                classesFilter = it.data.myClasses
                                val classesDataString: ArrayList<String> = ArrayList()
                                classesDataString.clear()
                                it.data.myClasses.forEach { data ->
                                    classesDataString.add(data.className.toString())
                                }
                                val arrayAdapter = ArrayAdapter(
                                    requireContext(),
                                    R.layout.view_drop_down_menu,
                                    classesDataString
                                )
                                binding.autoCompleteClass.setAdapter(arrayAdapter)

                            } else {
                                mainActivity().showMessage("No Class Assign")
                            }

                        }

                    }
                }
            }
        }
        lessonPlanListViewModel.getMyClass(0, 0)


    }

    private fun setupRecycleViewPager() {
        if (isNotFilterList) {
            binding.recyclerLessonPlan.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                    if (linearLayoutManager != null) {
                        if (dy > 0) {
                            visibleItemCount = linearLayoutManager.childCount;
                            totalItemCount = linearLayoutManager.itemCount;
                            pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition()

                            if (isLoading) {
                                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                    isLoading = false
                                    pageIndex += 1
                                    getLessonPlanList()
                                }
                            }

                        }
                    }
                }
            })
        }


    }


    private fun getLessonPlanList() {

        lifecycleScope.launch {
            lessonPlanListViewModel.lessonPlanListStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerLessonPlan.isVisible = true

                        if (it.data != null) {

                            if (it.data.lessonPlans != null) {

                                binding.recyclerLessonPlan.isVisible = true
                                binding.tvNoData.isVisible = false

                                lessonPlanListAdapter.setData(it.data.lessonPlans.toMutableList())

                                lessonPlanListAdapter.notifyDataSetChanged()

                            } else {
                                binding.recyclerLessonPlan.isVisible = false
                                binding.tvNoData.isVisible = true
                            }

                        }

                    }
                }
            }
        }
        lessonPlanListViewModel.getLessonPlanList(pageIndex,teacherID)
        isNotFilterList = true


    }

    override fun onItemClick(t: LessonPlan, pos: Int, boolean: Boolean) {
        if (pos == 3) {
            lessonPlanListViewModel.lessonPlanAction(t.lPlnID, 3, "").invokeOnCompletion {
                lessonPlanListViewModel.getLessonPlanList(pageIndex,teacherID)
                lessonPlanListAdapter.clearData()
            }
        } else if (pos == 1) {

            findNavController().navigate(
                R.id.action_lessonPlanListFragment_to_viewLessonPlanFragment,
                Bundle().apply {
                    putString(Constant.LESSON_ID_ARGUMENT, t.id)
                })

        }
        else if (pos == 2) {

            findNavController().navigate(
                R.id.action_lessonPlanListFragment_to_addLessonFragment,
                Bundle().apply {
                    putString(Constant.LESSON_ID_ARGUMENT, t.id)
                })

        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_filter, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_filter -> {
                popupFilter()
                true
            }

            else -> false
        }
    }

    private fun popupFilter() {
        val menuItemView = requireView().findViewById<View>(R.id.menu_filter)
        val popupMenu = PopupMenu(requireContext(), menuItemView)
        popupMenu.menuInflater.inflate(R.menu.filter_menu_lesson_plan, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            setFilterAction(item.itemId)
            true
        }
        popupMenu.show()
    }

    private fun setFilterAction(type: Int) {
        when (type) {
            R.id.menu_by_date_range -> {
                binding.llDateRange.visibility = View.VISIBLE
                binding.llClasses.visibility = View.GONE
                binding.llSubject.visibility = View.GONE
                binding.llStatus.visibility = View.GONE

            }

            R.id.menu_by_class -> {
                binding.llDateRange.visibility = View.GONE
                binding.llClasses.visibility = View.VISIBLE
                binding.llSubject.visibility = View.GONE
                binding.llStatus.visibility = View.GONE

            }

            R.id.menu_by_subject -> {
                binding.llDateRange.visibility = View.GONE
                binding.llClasses.visibility = View.GONE
                binding.llSubject.visibility = View.VISIBLE
                binding.llStatus.visibility = View.GONE

            }

            R.id.menu_by_status -> {
                binding.llDateRange.visibility = View.GONE
                binding.llClasses.visibility = View.GONE
                binding.llSubject.visibility = View.GONE
                binding.llStatus.visibility = View.VISIBLE

            }
        }
    }


    fun getLessonPlanFilter(
        filter: String,
        from: String,
        till: String,
        classIds: String,
        subIds: String,
        status: Int,

        ) {

        lessonPlanListAdapter.clearData()

        lessonPlanListViewModel.getLessonPlanFilter(
            filter,
            from,
            till,
            classIds,
            subIds,
            status,

            )

        isNotFilterList = false
    }

}