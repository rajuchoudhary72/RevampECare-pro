package com.app.ecarepro.ui.gallery.favorites

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
import com.app.ecarepro.data.network.model.FavList
import com.app.ecarepro.data.network.model.NetworkFavorites
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentPhotoAlbumBinding
import com.app.ecarepro.model.AlbumVideo
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.YoutubeURL
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesListFragment : Fragment() , ItemListener<FavList> {


    private lateinit var networkFavorites: NetworkFavorites
    private lateinit var favoritesListAdapter: FavoritesListAdapter
    private lateinit var binding: FragmentPhotoAlbumBinding
    private val favoritesViewModel : FavoritesViewModel by viewModels()

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
        binding.toolbar.title= "Favorites"
        binding.toolbar.isVisible=true
        favoritesListAdapter =    FavoritesListAdapter(this@FavoritesListFragment)

        binding.rvPhotoAlbum.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity,2)
            adapter = favoritesListAdapter
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            favoritesViewModel.favStateFlow.collectLatest {
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

                            if (it.data.list!=null){
                            if (it.data.list.isNotEmpty()){

                                binding.rvPhotoAlbum.isVisible=true
                                binding.tvNoData.isVisible=false
                                isLoading=true
                                networkFavorites=it.data

                                if (pageIndex==1){
                                    favoritesListAdapter.clearData()
                                }
                                favoritesListAdapter.setData(it.data.list.toMutableList())

                            }else{
                                if (pageIndex==1){
                                    binding.rvPhotoAlbum.isVisible=false
                                    binding.tvNoData.isVisible=true
                                }

                            }

                        }else{
                                if (pageIndex==1){
                                    binding.rvPhotoAlbum.isVisible=false
                                    binding.tvNoData.isVisible=true
                                }
                        }
                        }else{
                            if (pageIndex==1){
                                binding.rvPhotoAlbum.isVisible=false
                                binding.tvNoData.isVisible=true
                            }
                        }

                    }

                    else -> {}
                }
            }


        }

        favoritesViewModel.getFavorites( pageIndex)

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
                                    favoritesViewModel.getFavorites(  pageIndex)
                                }
                            }

                        }
                    }
                }
            })



    }

    override fun onItemClick(t: FavList, pos: Int, boolean: Boolean) {

//        findNavController().navigate(R.id.photoSliderFragment,
//            Bundle().apply {
//                putString(Constant.ID, t.id)
//                putString(Constant.URL_ARGUMENT, YoutubeURL().getTIURLFromYoutubeURL(t.fileName))
//                putString(Constant.FULL_URL_ARGUMENT, t.fileName)
//                putInt(Constant.GALLERY_TYPE, t.galleryType)
//                putBoolean("isLiked", t.islLike == 1 )
//                putBoolean("isFav", t.isFavourite)
//                putInt("likes", t.totalLike)
//            })

        findNavController().navigate(R.id.favoriteSliderNavHostFragment ,
            Bundle().apply {
                putParcelable("favDetails", networkFavorites)
                putInt("photoPosition", pos)

            })

    }

}