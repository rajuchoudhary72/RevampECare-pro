package com.app.ecarepro.ui.notice

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.MyClasseItem
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentNoticeListBinding
import com.app.ecarepro.model.Notice
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class NoticeListFragment : Fragment(), ItemListener<Notice> {
    private val noticeViewModel: NoticeViewModel by viewModels()
    private lateinit var binding: FragmentNoticeListBinding
    private var mMyClass = mutableListOf<MyClasseItem>()
    private var mMyClassDataString: ArrayList<String> = ArrayList()
    private var noticeType = ""
    private var userType = ""

    @Inject
    lateinit var userDataStore: UserDataStore

    private var pageIndex: Int = 1
    private var pastVisiblesItems: Int = 0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0
    private var isLoading: Boolean = true
    private lateinit var noticeListAdapter: NoticeListAdapter
    private var noticeList = mutableListOf<Notice>()
    private var isFirstTimeCall = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNoticeListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            mnoticeViewModel = noticeViewModel

        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        try {
            noticeType = requireArguments().getString(Constant.NOTICE_TYPE).toString()
            userType = requireArguments().getString(Constant.USER_TYPE).toString()
        } catch (_: Exception) {
        }
        pageIndex = 1
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noticeListAdapter = NoticeListAdapter(noticeList, this@NoticeListFragment, noticeType)

        binding.recyclerNotice.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = noticeListAdapter
        }



        lifecycleScope.launch {
            noticeViewModel._noticeStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.recyclerNotice.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerNotice.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerNotice.isVisible = true

                        if (it.data != null) {

                            if (it.data.noticeList != null && it.data.noticeList.isNotEmpty()) {

                                binding.recyclerNotice.isVisible = true
                                binding.tvNoData.isVisible = false
                                binding.toolbar.title =
                                    getString(R.string.all_notices) + "( " + it.data.totalNotice + "/" + it.data.unreadNotice + ")"


                                isLoading = true
                                if (pageIndex == 1) {

                                    noticeListAdapter.clearData()
                                }
                                noticeListAdapter.setData(it.data.noticeList.toMutableList())


                            } else {
                                if (pageIndex == 1) {
                                    binding.recyclerNotice.isVisible = false
                                    binding.tvNoData.isVisible = true
                                }

                            }

                        }

                    }

                    else -> {}
                }
            }
        }



        binding.toggleButtonTypeNoti.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (binding.toggleButtonTypeNoti.checkedButtonId) {
                R.id.btn_noti -> {
                    pageIndex = 1
                    binding.autoInputClassInputLayout.visibility = View.GONE
                    fetchNotices(pageIndex, 0, noticeType == Constant.NOTICE_CLASS)
                }

                else -> {
                    binding.autoInputClassInputLayout.visibility = View.VISIBLE
                }
            }
        }





        binding.autoCompleteClass.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, pos, id ->
                pageIndex = 1
                mMyClass[pos].classID?.let {
                    fetchNotices(
                        pageIndex,
                        it,
                        noticeType == Constant.NOTICE_CLASS
                    )
                }

            }



        if (noticeType == Constant.NOTICE_CLASS) {
            if (userType == Constant.USER_STAFF) {
                binding.autoInputClassInputLayout.isVisible = true
                getMyClass(Constant.SUB_ID, Constant.MY_CLASS_ID)
            } else {
                fetchNotices(pageIndex, 0, noticeType == Constant.NOTICE_CLASS)

            }
        } else {
            fetchNotices(pageIndex, 0, noticeType == Constant.NOTICE_CLASS)

        }

        setupRecycleViewPager()

    }


    private fun fetchNotices(pg: Int, classid: Int, isClassNotice: Boolean) {
        if (noticeType == Constant.NOTICE_CLASS) {
            if (userType == Constant.USER_STAFF) {

                noticeViewModel.getNotice(pg, classid, isClassNotice)

            } else {
                lifecycleScope.launch {
                    userDataStore.getUser()?.run {
                        noticeViewModel.getNotice(pg, classID!!.toInt(), isClassNotice)
                    }
                }

            }


        } else {
            noticeViewModel.getNotice(pg, 0, isClassNotice)
        }


    }

    private fun getMyClass(subID: Int, iD: Int) {

        noticeViewModel.getMyClass(subID, iD)

        lifecycleScope.launch {
            noticeViewModel._myClassStateFlow.collectLatest {
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
                            if (it.data.myClasses != null) {


                                if (mMyClass.isEmpty()) {
                                    mMyClass = it.data.myClasses as MutableList<MyClasseItem>

                                    mMyClass.forEach { data ->
                                        mMyClassDataString.add(data.className.toString())
                                    }
                                }

                                val arrayAdapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_list_item_1,
                                    mMyClassDataString
                                )
                                binding.autoCompleteClass.setAdapter(arrayAdapter)
                                if (!mMyClass.isNullOrEmpty()) {
                                    mMyClass[0].classID?.let { idClass ->
                                        fetchNotices(
                                            pageIndex,
                                            idClass,
                                            noticeType == Constant.NOTICE_CLASS
                                        )
                                    }
                                    binding.autoCompleteClass.setText(mMyClass[0].className, false)
                                }
                            }
                        }


                    }


                }
            }
        }
    }

    override fun onItemClick(t: Notice, pos: Int, boolean: Boolean) {

        findNavController().navigate(
            R.id.action_noticeListFragment_to_noticeDetailsFragment,
            Bundle().apply {
                t.id?.let { putString(Constant.NOTICE_ID_ARGUMENT, it) }
            })

    }

    private fun setupRecycleViewPager() {
        noticeListAdapter.clearData()
        binding.recyclerNotice.addOnScrollListener(object :
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
                                fetchNotices(pageIndex, 0, noticeType == Constant.NOTICE_CLASS)

                            }
                        }

                    }
                }
            }
        })


    }
}