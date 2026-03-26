package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.Album
import com.app.ecarepro.core.domain.model.AlbumDetail
import com.app.ecarepro.core.domain.model.AlbumType
import com.app.ecarepro.core.domain.model.FavoritesData
import com.app.ecarepro.core.domain.model.KidsAlbumDetailData
import com.app.ecarepro.core.domain.model.KidsAlbumsData
import com.app.ecarepro.core.domain.model.MediaGalleryData
import com.app.ecarepro.core.domain.model.VideoAlbum
import com.app.ecarepro.core.domain.model.VideoAlbumDetail
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
    fun getAlbumTypes(): Flow<Result<List<AlbumType>>>
    fun getAlbums(typeId: Int = 0, page: Int = 1): Flow<Result<List<Album>>>
    fun getAlbumDetail(id: String, page: Int = 1): Flow<Result<AlbumDetail>>
    fun toggleLike(id: String, like: Boolean): Flow<Result<Int>>
    fun manageFavorite(id: String, action: String): Flow<Result<Unit>>

    fun getVideoAlbums(page: Int = 1): Flow<Result<List<VideoAlbum>>>
    fun getVideoAlbumDetail(id: String, page: Int = 1): Flow<Result<VideoAlbumDetail>>
    fun toggleVideoLike(id: String, like: Boolean): Flow<Result<Int>>
    fun manageVideoFavorite(id: String, action: String): Flow<Result<Unit>>

    fun getFavorites(page: Int = 1): Flow<Result<FavoritesData>>

    fun getMediaGallery(page: Int = 1, queryType: Int = 0, year: Int = 0): Flow<Result<MediaGalleryData>>

    fun getKidsAlbums(page: Int = 1, yrID: Int = 0): Flow<Result<KidsAlbumsData>>
    fun getKidsAlbumDetail(id: String, page: Int = 1): Flow<Result<KidsAlbumDetailData>>
}
