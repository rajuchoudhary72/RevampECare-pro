package com.app.ecarepro.ui.notice

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
class NoticeListFragment : Fragment() , ItemListener<Notice> {




    private val noticeViewModel: NoticeViewModel by viewModels()
    private lateinit var binding :  FragmentNoticeListBinding
    private   var mMyClass= mutableListOf<MyClasseItem>()
    private var mMyClassDataString: ArrayList<String> = ArrayList()
    private var noticeType=""
    private var userType=""
    @Inject
    lateinit var userDataStore: UserDataStore



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
            noticeType= requireArguments().getString(Constant.NOTICE_TYPE).toString()
            userType= requireArguments().getString(Constant.USER_TYPE).toString()
        }catch (_:Exception){}

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (noticeType==Constant.NOTICE_CLASS){
            if (userType==Constant.USER_STAFF){
                binding.autoInputClassInputLayout.isVisible=true
                getMyClass(Constant.SUB_ID, Constant.MY_CLASS_ID)
            }else{
                fetchNotices(Constant.PAGE_INDEX, 0,noticeType==Constant.NOTICE_CLASS)

            }
         }else{
            fetchNotices(Constant.PAGE_INDEX, 0,noticeType==Constant.NOTICE_CLASS)

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

                        if (it.data!=null){

                            if (it.data.noticeList!=null && it.data.noticeList.isNotEmpty() ){

                                binding.recyclerNotice.isVisible=true
                                binding.tvNoData.isVisible=false

                                val noticeAdapter = NoticeListAdapter(it.data.noticeList , this@NoticeListFragment,noticeType)

                                binding.recyclerNotice.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = noticeAdapter
                                }
                                binding.toolbar.title= "All Notices" + "( " + it.data.totalNotice + "/" + it.data.unreadNotice + ")"
                            }else{
                                binding.recyclerNotice.isVisible=false
                                binding.tvNoData.isVisible=true
                            }

                        }

                    }

                    else -> {}
                }
            }
        }

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
                        if (it.data!=null){
                            if (it.data.myClasses!=null) {

                                mMyClass.clear()
                                mMyClassDataString.clear()
                                mMyClass= it.data.myClasses as MutableList<MyClasseItem>

                                mMyClass.forEach { data ->
                                    mMyClassDataString.add(data.className.toString())
                                }

                                val arrayAdapter= ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,mMyClassDataString)
                                binding.autoCompleteClass.setAdapter(arrayAdapter)
                                 if (!mMyClass.isNullOrEmpty()){
                                     mMyClass[0].classID?.let { idClass -> fetchNotices(Constant.PAGE_INDEX, idClass,noticeType==Constant.NOTICE_CLASS) }
                                     binding.autoCompleteClass.setText(mMyClass[0].className,false)
                                 }
                            }
                        }


                    }


                }
            }
        }

        binding.toggleButtonTypeNoti.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (binding.toggleButtonTypeNoti.checkedButtonId) {
                R.id.btn_noti -> {

                    binding.autoInputClassInputLayout.visibility = View.GONE
                    fetchNotices(Constant.PAGE_INDEX, 0,noticeType==Constant.NOTICE_CLASS)
                }

                else -> {
                    binding.autoInputClassInputLayout.visibility = View.VISIBLE
                }
            }
        }





        binding.autoCompleteClass.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, pos, id ->

                mMyClass[pos].classID?.let { fetchNotices(Constant.PAGE_INDEX, it,noticeType==Constant.NOTICE_CLASS) }

            }



    }


    private fun fetchNotices(pg: Int, classid: Int, isClassNotice: Boolean) {
        if (noticeType==Constant.NOTICE_CLASS){
            if (userType==Constant.USER_STAFF){

                    noticeViewModel.getNotice(pg, classid,isClassNotice)

            }else{
                lifecycleScope.launch {
                    userDataStore.getUser()?.run {
                        noticeViewModel.getNotice(pg,classID!!.toInt(),isClassNotice)
                    }
                }

            }


        }else{
            noticeViewModel.getNotice(pg, 0,isClassNotice)
        }

    }

    private fun getMyClass(subID: Int, iD: Int  ) {

        noticeViewModel.getMyClass(subID, iD)
    }

    override fun onItemClick(t: Notice, pos: Int, boolean: Boolean) {

        findNavController().navigate(R.id.action_noticeListFragment_to_noticeDetailsFragment,Bundle( ).apply {
            t.ntID?.let { putInt(Constant.NOTICE_ID_ARGUMENT, it) }
        })

     }
}