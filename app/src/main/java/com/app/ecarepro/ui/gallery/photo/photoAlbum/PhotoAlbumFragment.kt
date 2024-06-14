package com.app.ecarepro.ui.gallery.photo.photoAlbum

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.app.ecarepro.databinding.FragmentPhotoAlbumTypeNavHostBinding
import com.app.ecarepro.model.Album
import com.app.ecarepro.model.AlbumType
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.notice.NoticeListAdapter
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PhotoAlbumFragment(private val albumType: AlbumType) : Fragment() , ItemListener<Album> {


    private lateinit var photoAlbumAdapter: PhotoAlbumAdapter
    private lateinit var binding: FragmentPhotoAlbumBinding
    private val photoAlbumViewModel : PhotoAlbumViewModel by viewModels()

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
        photoAlbumAdapter =    PhotoAlbumAdapter(this@PhotoAlbumFragment)
        binding.rvPhotoAlbum.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity,2)
            adapter = photoAlbumAdapter
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            photoAlbumViewModel.photoAlbumStateFlow.collectLatest {
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

                                if (pageIndex==1){
                                    photoAlbumAdapter.clearData()
                                }

                                photoAlbumAdapter.setData(it.data.albums.toMutableList())

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

        photoAlbumViewModel.getPhotoAlbums(albumType.typeID ,pageIndex)

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
                                    photoAlbumViewModel.getPhotoAlbums(albumType.typeID,pageIndex)
                                }
                            }

                        }
                    }
                }
            })



    }

    override fun onItemClick(t: Album, pos: Int, boolean: Boolean) {
        this@PhotoAlbumFragment. findNavController().
        navigate(R.id.action_photoAlbumTypeNavHostFragment_to_photoAlbumDTLFragment, Bundle().apply {
            putString(Constant.ID, t.id)


        })
    }

}