package com.app.ecarepro.ui.thought

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentThoughtsListBinding
import com.app.ecarepro.model.Thoughts
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ResponseState
import com.app.ecarepro.utils.ResponseStateCreateTou
import com.app.ecarepro.utils.listener.ItemListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ThoughtsListFragment : Fragment(), ItemListener<Thoughts> {

    private lateinit var fragmentThoughtsListBinding: FragmentThoughtsListBinding
    private val thoughtsViewModel: ThoughtsViewModel by viewModels()
    private lateinit var thoughtsAdapter: ThoughtsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentThoughtsListBinding =
            FragmentThoughtsListBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = viewLifecycleOwner
                mThoughtsViewModel = thoughtsViewModel

            }
        fragmentThoughtsListBinding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        initRecycleView()

        return fragmentThoughtsListBinding.root
    }

    private fun initRecycleView() {


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentThoughtsListBinding.toggleButtonTypeNoti.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (fragmentThoughtsListBinding.toggleButtonTypeNoti.checkedButtonId) {
                R.id.btn_all -> {
                    getThoughts(Constant.PAGE_INDEX, Constant.THOUGHTS_DIR, false)
                }

                else -> {
                    getThoughts(Constant.PAGE_INDEX, Constant.THOUGHTS_DIR,true)
                }
            }
        }

        fragmentThoughtsListBinding.fbAdd.setOnClickListener {

            findNavController().navigate(R.id.addThoughtsBlankFragment)
        }

        getThoughts(Constant.PAGE_INDEX, Constant.THOUGHTS_DIR,false)

        lifecycleScope.launch {
            thoughtsViewModel._postStateFlow.collectLatest {
                when (it) {

                    is ResponseState.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        fragmentThoughtsListBinding.recyclerThoughts.isVisible = false
                    }

                    is ResponseState.Failure -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        fragmentThoughtsListBinding.recyclerThoughts.isVisible = false
                        Log.d("main", "Error" + it.msg.toString())
                    }

                    is ResponseState.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        fragmentThoughtsListBinding.recyclerThoughts.isVisible = true

                        thoughtsAdapter = ThoughtsAdapter(it.data.list, this@ThoughtsListFragment)

                        fragmentThoughtsListBinding.recyclerThoughts.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(activity)
                            adapter = thoughtsAdapter
                        }
                    }

                    else -> {}
                }
            }
        }

        // Thoughts Delete flow state collection
        lifecycleScope.launch {
            thoughtsViewModel._thoughtsDeleteStateFlow.collectLatest {
                when (it) {
                    is ResponseStateCreateTou.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }
                    is ResponseStateCreateTou.Failure -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error" + it.msg.toString())
                    }
                    is ResponseStateCreateTou.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        fragmentThoughtsListBinding.recyclerThoughts.isVisible = true

                    } else -> {}
                } } }


    }

    private fun getThoughts(pg: Int, dir: Int, mythoughts: Boolean) {
        thoughtsViewModel.getThoughts(pg, dir, mythoughts)
    }

    override fun onItemClick(t: Thoughts, pos: Int, boolean: Boolean) {
        when (pos) {
            1 -> {
                thoughtsViewModel.like(t.thID, boolean)
            }

            2 -> {
                thoughtsViewModel.whoLiked(t.thID)
                whoLikedBottomSheet()
            }

            3 -> {
                thoughtsViewModel.thoughtsDelete(t.thID)
            }
        }
    }


    private fun whoLikedBottomSheet() {

        (requireActivity() as MainActivity).showLoader(true)

        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.who_liked_bottom_sheet, null)

        val ivClose = view.findViewById<ImageView>(R.id.iv_close)
        val like = view.findViewById<TextView>(R.id.like)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        lifecycleScope.launch {

            lifecycleScope.launch {
                thoughtsViewModel._whoLikeStateFlow.collectLatest {
                    when (it) {

                        is NetworkResult.Loading -> {
                            (requireActivity() as MainActivity).showLoader(true)
                            recyclerView.isVisible = false
                        }

                        is NetworkResult.Error -> {
                            (requireActivity() as MainActivity).showLoader(false)
                            recyclerView.isVisible = false
                            Log.d("main", "Error$it")
                        }

                        is NetworkResult.Success -> {
                            (requireActivity() as MainActivity).showLoader(false)
                            recyclerView.isVisible = true

                            if (it.data != null) {
                                val whoLikedAdapter = WhoLikedAdapter(it.data.likeBy)
                                recyclerView.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = whoLikedAdapter
                                }
                                like.text = it.data.likeBy.size.toString() + " Likes"

                            }
                        }

                        else -> {}
                    }
                }
            }
        }


        dialog.setCancelable(false)
        dialog.setContentView(view)

        dialog.show()
    }


}