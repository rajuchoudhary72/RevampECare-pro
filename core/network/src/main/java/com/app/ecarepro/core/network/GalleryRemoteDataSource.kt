package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.gallery.NetworkAlbum
import com.app.ecarepro.core.network.model.gallery.NetworkAlbumType
import com.app.ecarepro.core.network.model.gallery.NetworkGetAlbumDetail
import com.app.ecarepro.core.network.model.gallery.NetworkGetFavorites
import com.app.ecarepro.core.network.model.gallery.NetworkGetKidsAlbumDetail
import com.app.ecarepro.core.network.model.gallery.NetworkGetKidsAlbums
import com.app.ecarepro.core.network.model.gallery.NetworkGetMediaGallery
import com.app.ecarepro.core.network.model.gallery.NetworkGetVideoAlbumDetail
import com.app.ecarepro.core.network.model.gallery.NetworkVideoAlbum

interface GalleryRemoteDataSource {
    suspend fun getAlbumTypes(): List<NetworkAlbumType>
    suspend fun getAlbums(typeId: Int, page: Int): List<NetworkAlbum>
    suspend fun getAlbumDetail(id: String, page: Int): NetworkGetAlbumDetail
    suspend fun toggleLike(id: String, like: Boolean): Int
    suspend fun manageFavorite(id: String, action: String)

    suspend fun getVideoAlbums(page: Int): List<NetworkVideoAlbum>
    suspend fun getVideoAlbumDetail(id: String, page: Int): NetworkGetVideoAlbumDetail
    suspend fun toggleVideoLike(id: String, like: Boolean): Int
    suspend fun manageVideoFavorite(id: String, action: String)

    suspend fun getFavorites(page: Int): NetworkGetFavorites

    suspend fun getMediaGallery(page: Int, queryType: Int, year: Int): NetworkGetMediaGallery

    suspend fun getKidsAlbums(page: Int, yrID: Int): NetworkGetKidsAlbums
    suspend fun getKidsAlbumDetail(id: String, page: Int): NetworkGetKidsAlbumDetail
}
