package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.domain.exception.ApiException
import com.app.ecarepro.core.network.GalleryRemoteDataSource
import com.app.ecarepro.core.network.model.gallery.NetworkAlbum
import com.app.ecarepro.core.network.model.gallery.NetworkAlbumType
import com.app.ecarepro.core.network.model.gallery.NetworkGetAlbumDetail
import com.app.ecarepro.core.network.model.gallery.NetworkGetFavorites
import com.app.ecarepro.core.network.model.gallery.NetworkGetKidsAlbumDetail
import com.app.ecarepro.core.network.model.gallery.NetworkGetKidsAlbums
import com.app.ecarepro.core.network.model.gallery.NetworkGetMediaGallery
import com.app.ecarepro.core.network.model.gallery.NetworkGetVideoAlbumDetail
import com.app.ecarepro.core.network.model.gallery.NetworkVideoAlbum
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.GalleryService
import javax.inject.Inject

internal class GalleryRemoteDataSourceImpl @Inject constructor(
    private val galleryService: GalleryService,
) : GalleryRemoteDataSource {

    override suspend fun getAlbumTypes(): List<NetworkAlbumType> {
        return galleryService.getAlbumTypes().unwrapPayload { albumTypes ?: emptyList() }
    }

    override suspend fun getAlbums(typeId: Int, page: Int): List<NetworkAlbum> {
        return galleryService.getAlbums(typeId = typeId, page = page).unwrapPayload { albums ?: emptyList() }
    }

    override suspend fun getAlbumDetail(id: String, page: Int): NetworkGetAlbumDetail {
        val response = galleryService.getAlbumDetail(id = id, page = page)
        if (response.errorCode != 0) throw ApiException(response.errorCode, response.message)
        return response
    }

    override suspend fun toggleLike(id: String, like: Boolean): Int {
        return galleryService.toggleLike(id = id, galleryType = 1, like = like).unwrapPayload { totalLikes ?: 0 }
    }

    override suspend fun manageFavorite(id: String, action: String) {
        galleryService.manageFavorites(id = id, galleryType = 1, action = action).unwrapPayload { message }
    }

    override suspend fun getVideoAlbums(page: Int): List<NetworkVideoAlbum> {
        return galleryService.getVideoAlbums(page = page).unwrapPayload { albums ?: emptyList() }
    }

    override suspend fun getVideoAlbumDetail(id: String, page: Int): NetworkGetVideoAlbumDetail {
        val response = galleryService.getVideoAlbumDetail(id = id, page = page)
        if (response.errorCode != 0) throw ApiException(response.errorCode, response.message)
        return response
    }

    override suspend fun toggleVideoLike(id: String, like: Boolean): Int {
        return galleryService.toggleLike(id = id, galleryType = 2, like = like).unwrapPayload { totalLikes ?: 0 }
    }

    override suspend fun manageVideoFavorite(id: String, action: String) {
        galleryService.manageFavorites(id = id, galleryType = 2, action = action).unwrapPayload { message }
    }

    override suspend fun getFavorites(page: Int): NetworkGetFavorites {
        val response = galleryService.getFavorites(page = page)
        if (response.errorCode != 0) throw ApiException(response.errorCode, response.message)
        return response
    }

    override suspend fun getMediaGallery(page: Int, queryType: Int, year: Int): NetworkGetMediaGallery {
        val response = galleryService.getMediaGallery(page = page, queryType = queryType, year = year)
        if (response.errorCode != 0) throw ApiException(response.errorCode, response.message)
        return response
    }

    override suspend fun getKidsAlbums(page: Int, yrID: Int): NetworkGetKidsAlbums {
        val response = galleryService.getKidsAlbums(page = page, yrID = yrID)
        if (response.errorCode != 0) throw ApiException(response.errorCode, response.message)
        return response
    }

    override suspend fun getKidsAlbumDetail(id: String, page: Int): NetworkGetKidsAlbumDetail {
        return galleryService.getKidsAlbumDetail(id = id, page = page)
    }
}
