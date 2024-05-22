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
import com.app.ecarepro.data.network.model.MyClasseItem
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentNoticeListBinding
import com.app.ecarepro.epoxy_controler.NoticeEpoxyController
import com.app.ecarepro.model.Notice
import com.app.ecarepro.model.Thoughts
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.thought.ThoughtsAdapter
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ResponseState
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NoticeListFragment : Fragment() , ItemListener<Notice> {




    private val noticeViewModel: NoticeViewModel by viewModels()
    private lateinit var binding :  FragmentNoticeListBinding
    private lateinit var mMyClass: List<MyClasseItem>
    private var mMyClassDataString: ArrayList<String> = ArrayList()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNoticeListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            mnoticeViewModel = noticeViewModel

        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

                            if (it.data.noticeList!=null){

                                binding.recyclerNotice.isVisible=true
                                binding.tvNoData.isVisible=false

                                val noticeAdapter = NoticeListAdapter(it.data.noticeList , this@NoticeListFragment)

                                binding.recyclerNotice.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = noticeAdapter
                                }
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
                                mMyClass=it.data.myClasses

                                mMyClass.forEach { data ->
                                    mMyClassDataString.add(data.className.toString())
                                }

                                val arrayAdapter= ArrayAdapter(requireContext(), R.layout.view_drop_down_menu,mMyClassDataString)
                                binding.autoCompleteClass.setAdapter(arrayAdapter)
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
                    fetchNotices(Constant.PAGE_INDEX, Constant.DEFAULT_ID)
                }

                else -> {
                    binding.autoInputClassInputLayout.visibility = View.VISIBLE
                }
            }
        }





        binding.autoCompleteClass.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, pos, id ->

                mMyClass[pos].classID?.let { fetchNotices(0, it) }

            }

        fetchNotices(Constant.PAGE_INDEX, Constant.DEFAULT_ID)
        getMyClass(Constant.SUB_ID, Constant.MY_CLASS_ID)

    }


    private fun fetchNotices(pg: Int, classID: Int) {

        noticeViewModel.getNotice(pg, classID)
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