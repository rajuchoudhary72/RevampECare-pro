package com.app.ecarepro.ui.gallery.video

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentPhotoAlbumBinding
import com.app.ecarepro.model.Album
import com.app.ecarepro.model.AlbumType
import com.app.ecarepro.model.AlbumVideo
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoAlbumFragment : Fragment() , ItemListener<AlbumVideo> {


    private lateinit var videoAlbumAdapter: VideoAlbumAdapter
    private lateinit var binding: FragmentPhotoAlbumBinding
    private val videoAlbumViewModel : VideoAlbumViewModel by viewModels()

    private var pageIndex: Int = 1
    private var pastVisiblesItems: Int = 0
    private var totalItemCount: Int = 0
    private var visibleItemCount: Int = 0
    private var isLoading: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPhotoAlbumBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.toolbar.title= getString(R.string.video_album_title)
        binding.toolbar.isVisible=true
        videoAlbumAdapter =    VideoAlbumAdapter(this@VideoAlbumFragment)

        binding.rvPhotoAlbum.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity,2)
            adapter = videoAlbumAdapter
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /*for  push notification  handling  condition  here where  we redirect  screen  respective  "refID" */
        arguments?.let {args ->
            if (args.getString("ID")=="Menu"){

            }else{
                if(args.getString("ID").isNullOrEmpty().not()){
                    findNavController().navigate(
                        R.id.action_videoAlbumFragment_to_videoAlbumDTLFragment,
                        bundleOf(Constant.ID to args.getString("ID"))
                    )
                    args.remove("ID")
                }
            }
        }
        lifecycleScope.launch {
            videoAlbumViewModel.photoAlbumStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.rvPhotoAlbum.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvPhotoAlbum.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvPhotoAlbum.isVisible = true

                        if (it.data!=null){

                            if (it.data.albums!=null){

                                binding.rvPhotoAlbum.isVisible=true
                                binding.tvNoData.isVisible=false
                                isLoading=true

                                videoAlbumAdapter.setData(it.data.albums.toMutableList())

                            }else{
                                if (pageIndex==1){
                                    binding.rvPhotoAlbum.isVisible=false
                                    binding.tvNoData.isVisible=true
                                }

                            }

                        }

                    }

                    else -> {}
                }
            }


        }

        videoAlbumViewModel.getVideoAlbums( pageIndex)

        setupRecycleViewPager()

    }


    private fun setupRecycleViewPager() {



            binding.rvPhotoAlbum.addOnScrollListener(object :
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
                                    videoAlbumViewModel.getVideoAlbums(  pageIndex)
                                }
                            }

                        }
                    }
                }
            })



    }

    override fun onItemClick(t: AlbumVideo, pos: Int, boolean: Boolean) {
        this@VideoAlbumFragment. findNavController().
        navigate(R.id.action_videoAlbumFragment_to_videoAlbumDTLFragment, Bundle().apply {
            putString(Constant.ID, t.id)


        })
    }

}