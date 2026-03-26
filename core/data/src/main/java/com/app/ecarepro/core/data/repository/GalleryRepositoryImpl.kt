package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.Album
import com.app.ecarepro.core.domain.model.AlbumDetail
import com.app.ecarepro.core.domain.model.AlbumType
import com.app.ecarepro.core.domain.model.FavoritesData
import com.app.ecarepro.core.domain.model.KidsAlbumDetailData
import com.app.ecarepro.core.domain.model.KidsAlbumsData
import com.app.ecarepro.core.domain.model.MediaGalleryData
import com.app.ecarepro.core.domain.model.VideoAlbum
import com.app.ecarepro.core.domain.model.VideoAlbumDetail
import com.app.ecarepro.core.domain.repository.GalleryRepository
import com.app.ecarepro.core.network.GalleryRemoteDataSource
import com.app.ecarepro.core.network.model.gallery.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GalleryRepositoryImpl @Inject constructor(
    private val galleryRemoteDataSource: GalleryRemoteDataSource,
) : GalleryRepository {

    override fun getAlbumTypes(): Flow<Result<List<AlbumType>>> = asResultFlow {
        galleryRemoteDataSource.getAlbumTypes().map { it.toDomainModel() }
    }

    override fun getAlbums(typeId: Int, page: Int): Flow<Result<List<Album>>> = asResultFlow {
        galleryRemoteDataSource.getAlbums(typeId, page).map { it.toDomainModel() }
    }

    override fun getAlbumDetail(id: String, page: Int): Flow<Result<AlbumDetail>> = asResultFlow {
        galleryRemoteDataSource.getAlbumDetail(id, page).toDomainModel()
    }

    override fun toggleLike(id: String, like: Boolean): Flow<Result<Int>> = asResultFlow {
        galleryRemoteDataSource.toggleLike(id, like)
    }

    override fun manageFavorite(id: String, action: String): Flow<Result<Unit>> = asResultFlow {
        galleryRemoteDataSource.manageFavorite(id, action)
    }

    override fun getVideoAlbums(page: Int): Flow<Result<List<VideoAlbum>>> = asResultFlow {
        galleryRemoteDataSource.getVideoAlbums(page).map { it.toDomainModel() }
    }

    override fun getVideoAlbumDetail(id: String, page: Int): Flow<Result<VideoAlbumDetail>> = asResultFlow {
        galleryRemoteDataSource.getVideoAlbumDetail(id, page).toDomainModel()
    }

    override fun toggleVideoLike(id: String, like: Boolean): Flow<Result<Int>> = asResultFlow {
        galleryRemoteDataSource.toggleVideoLike(id, like)
    }

    override fun manageVideoFavorite(id: String, action: String): Flow<Result<Unit>> = asResultFlow {
        galleryRemoteDataSource.manageVideoFavorite(id, action)
    }

    override fun getFavorites(page: Int): Flow<Result<FavoritesData>> = asResultFlow {
        galleryRemoteDataSource.getFavorites(page).toDomainModel()
    }

    override fun getMediaGallery(page: Int, queryType: Int, year: Int): Flow<Result<MediaGalleryData>> = asResultFlow {
        galleryRemoteDataSource.getMediaGallery(page, queryType, year).toDomainModel()
    }

    override fun getKidsAlbums(page: Int, yrID: Int): Flow<Result<KidsAlbumsData>> = asResultFlow {
        galleryRemoteDataSource.getKidsAlbums(page, yrID).toDomainModel()
    }

    override fun getKidsAlbumDetail(id: String, page: Int): Flow<Result<KidsAlbumDetailData>> = asResultFlow {
        galleryRemoteDataSource.getKidsAlbumDetail(id, page).toDomainModel()
    }
}
