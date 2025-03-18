package com.app.ecarepro.ui.gallery.kid_corner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.ecarepro.ui.gallery.kid_corner.model.AlbumDetailX

class KidCornerShareViewModel : ViewModel() {

    private val listMutableLiveData = MutableLiveData<List<AlbumDetailX>?>()
    val liveData: MutableLiveData<List<AlbumDetailX>?> = listMutableLiveData

    fun setSelectedAlbum(albumDetailX: List<AlbumDetailX>) {
        listMutableLiveData.value = albumDetailX
    }

    fun getSelectedAlbum(): MutableLiveData<List<AlbumDetailX>?> {
        return liveData
    }

    fun clearList(){
        listMutableLiveData.value=null

    }


}