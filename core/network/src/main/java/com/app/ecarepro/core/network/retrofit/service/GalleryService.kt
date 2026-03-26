package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.gallery.NetworkGetAlbumDetail
import com.app.ecarepro.core.network.model.gallery.NetworkGetAlbumTypes
import com.app.ecarepro.core.network.model.gallery.NetworkGetAlbums
import com.app.ecarepro.core.network.model.gallery.NetworkGetFavorites
import com.app.ecarepro.core.network.model.gallery.NetworkGetKidsAlbumDetail
import com.app.ecarepro.core.network.model.gallery.NetworkGetKidsAlbums
import com.app.ecarepro.core.network.model.gallery.NetworkGetMediaGallery
import com.app.ecarepro.core.network.model.gallery.NetworkGetVideoAlbumDetail
import com.app.ecarepro.core.network.model.gallery.NetworkGetVideoAlbums
import com.app.ecarepro.core.network.model.gallery.NetworkLikeResponse
import com.app.ecarepro.core.network.model.gallery.NetworkManageFavoritesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GalleryService {

    @GET("Gallery/PhotoAlbumTypes")
    suspend fun getAlbumTypes(): NetworkGetAlbumTypes

    @GET("Gallery/PhotoAlbums")
    suspend fun getAlbums(
        @Query("typeID") typeId: Int = 0,
        @Query("pg") page: Int = 1,
    ): NetworkGetAlbums

    @GET("Gallery/PhotoAlbumDTL")
    suspend fun getAlbumDetail(
        @Query("ID") id: String,
        @Query("pg") page: Int = 1,
    ): NetworkGetAlbumDetail

    @GET("Gallery/VideoAlbums")
    suspend fun getVideoAlbums(
        @Query("pg") page: Int = 1,
    ): NetworkGetVideoAlbums

    @GET("Gallery/Favorites")
    suspend fun getFavorites(
        @Query("pg") page: Int = 1,
    ): NetworkGetFavorites

    @GET("Gallery/VideoAlbumDTL")
    suspend fun getVideoAlbumDetail(
        @Query("ID") id: String,
        @Query("pg") page: Int = 1,
    ): NetworkGetVideoAlbumDetail

    @GET("Gallery/Like")
    suspend fun toggleLike(
        @Query("ID") id: String,
        @Query("GalleryType") galleryType: Int,
        @Query("like") like: Boolean,
    ): NetworkLikeResponse

    @GET("Gallery/ManageFavorites")
    suspend fun manageFavorites(
        @Query("ID") id: String,
        @Query("GalleryType") galleryType: Int,
        @Query("Action") action: String,
    ): NetworkManageFavoritesResponse

    @GET("Gallery/MediaGallery")
    suspend fun getMediaGallery(
        @Query("pg") page: Int = 1,
        @Query("QueryType") queryType: Int = 0,
        @Query("Year") year: Int = 0,
    ): NetworkGetMediaGallery

    @GET("Gallery/KidsCornerAlbums")
    suspend fun getKidsAlbums(
        @Query("pg") page: Int = 1,
        @Query("YrID") yrID: Int = 0,
    ): NetworkGetKidsAlbums

    @GET("Gallery/KidsAlbumDetails")
    suspend fun getKidsAlbumDetail(
        @Query("ID") id: String,
        @Query("pg") page: Int = 1,
    ): NetworkGetKidsAlbumDetail
}
