package com.app.ecarepro.core.domain.model

data class AlbumType(
    val typeID: Int,
    val typeName: String,
)

data class Album(
    val id: String,
    val title: String,
    val description: String,
    val totalPhotos: Int,
    val fileName: String,
    val eventDate: String,
)

data class Photo(
    val id: String,
    val title: String,
    val description: String?,
    val photoPath: String,
    val likes: Int,
    val isLike: Boolean,
    val isFavourite: Boolean,
)

data class PhotoSetting(
    val isLikeEnabled: Boolean,
    val isShareEnabled: Boolean,
    val isAddFavouriteEnabled: Boolean,
)

data class AlbumDetail(
    val title: String,
    val description: String,
    val eventDate: String,
    val totalPhotos: Int,
    val setting: PhotoSetting,
    val photos: List<Photo>,
)

data class VideoAlbum(
    val id: String,
    val title: String,
    val description: String,
    val totalVideos: Int,
    val fileName: String,
    val eventDate: String,
)

data class Video(
    val id: String,
    val title: String,
    val url: String,
    val likes: Int,
    val isLike: Boolean,
    val isFavourite: Boolean,
)

data class VideoAlbumDetail(
    val title: String,
    val description: String,
    val eventDate: String,
    val totalVideos: Int,
    val setting: PhotoSetting,
    val videos: List<Video>,
)

data class FavoriteItem(
    val id: String,
    val galleryType: Int,   // 1 = photo, 2 = video
    val fileName: String,   // image URL (photo) or YouTube embed URL (video)
    val totalLike: Int,
    val isLike: Boolean,
    val isFavourite: Boolean,
)

data class FavoritesData(
    val setting: PhotoSetting,
    val items: List<FavoriteItem>,
)

data class MediaItem(
    val id: Int,
    val newsName: String,
    val headline: String,
    val publishedOn: String,
    val updatedOn: String,
    val thumbnailUrl: String,
    val fullSizeUrl: String,
    val description: String,
)

data class MediaGalleryData(
    val years: List<String>,
    val items: List<MediaItem>,
)

data class KidsAcademicYear(
    val yrID: Int,
    val session: String,
    val isCur: Boolean,
)

data class KidsAlbum(
    val kid: String,
    val title: String,
    val description: String,
    val albumIcon: String,
    val yearName: String,
    val totalPhoto: Int,
    val createdOn: String,
    val updatedOn: String,
)

data class KidsAlbumsData(
    val academicYears: List<KidsAcademicYear>,
    val albums: List<KidsAlbum>,
)

data class KidsPhoto(
    val id: String,
    val fileName: String,
)

data class KidsAlbumDetailData(
    val kid: String,
    val title: String,
    val description: String,
    val albumIcon: String,
    val yearName: String,
    val totalPhoto: Int,
    val createdOn: String,
    val updatedOn: String,
    val photos: List<KidsPhoto>,
)
