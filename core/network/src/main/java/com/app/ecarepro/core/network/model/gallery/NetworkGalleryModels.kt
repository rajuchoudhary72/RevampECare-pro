package com.app.ecarepro.core.network.model.gallery

import com.app.ecarepro.core.domain.model.Album
import com.app.ecarepro.core.domain.model.AlbumDetail
import com.app.ecarepro.core.domain.model.AlbumType
import com.app.ecarepro.core.domain.model.FavoriteItem
import com.app.ecarepro.core.domain.model.FavoritesData
import com.app.ecarepro.core.domain.model.KidsAcademicYear
import com.app.ecarepro.core.domain.model.KidsAlbum
import com.app.ecarepro.core.domain.model.KidsAlbumDetailData
import com.app.ecarepro.core.domain.model.KidsAlbumsData
import com.app.ecarepro.core.domain.model.KidsPhoto
import com.app.ecarepro.core.domain.model.MediaGalleryData
import com.app.ecarepro.core.domain.model.MediaItem
import com.app.ecarepro.core.domain.model.Photo
import com.app.ecarepro.core.domain.model.PhotoSetting
import com.app.ecarepro.core.domain.model.Video
import com.app.ecarepro.core.domain.model.VideoAlbum
import com.app.ecarepro.core.domain.model.VideoAlbumDetail
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGetAlbumTypes(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("albumTypes") val albumTypes: List<NetworkAlbumType>? = null,
) : NetworkResponse

@Serializable
data class NetworkAlbumType(
    @SerialName("typeID") val typeID: Int,
    @SerialName("typeName") val typeName: String,
)

@Serializable
data class NetworkGetAlbums(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("albums") val albums: List<NetworkAlbum>? = null,
) : NetworkResponse

@Serializable
data class NetworkAlbum(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("totalPhotos") val totalPhotos: Int = 0,
    @SerialName("fileName") val fileName: String? = null,
    @SerialName("eventDate") val eventDate: String? = null,
)

@Serializable
data class NetworkGetAlbumDetail(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("eventDate") val eventDate: String? = null,
    @SerialName("totalPhotos") val totalPhotos: Int? = null,
    @SerialName("setting") val setting: NetworkPhotoSetting? = null,
    @SerialName("photos") val photos: List<NetworkPhoto>? = null,
) : NetworkResponse

@Serializable
data class NetworkPhotoSetting(
    @SerialName("isLikeEnabled") val isLikeEnabled: Boolean = false,
    @SerialName("isShareEnabled") val isShareEnabled: Boolean = false,
    @SerialName("isAddFavouriteEnabled") val isAddFavouriteEnabled: Boolean = false,
)

@Serializable
data class NetworkPhoto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("photoPath") val photoPath: String,
    @SerialName("likes") val likes: Int = 0,
    @SerialName("isLike") val isLike: Boolean = false,
    @SerialName("isFavourite") val isFavourite: Boolean = false,
)

@Serializable
data class NetworkLikeResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("totalLikes") val totalLikes: Int? = null,
) : NetworkResponse

@Serializable
data class NetworkManageFavoritesResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
) : NetworkResponse

@Serializable
data class NetworkGetVideoAlbums(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("albums") val albums: List<NetworkVideoAlbum>? = null,
) : NetworkResponse

@Serializable
data class NetworkVideoAlbum(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("totalVideos") val totalVideos: Int = 0,
    @SerialName("fileName") val fileName: String? = null,
    @SerialName("eventDate") val eventDate: String? = null,
)

@Serializable
data class NetworkGetVideoAlbumDetail(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("eventDate") val eventDate: String? = null,
    @SerialName("totalVideos") val totalVideos: Int? = null,
    @SerialName("setting") val setting: NetworkPhotoSetting? = null,
    @SerialName("videos") val videos: List<NetworkVideo>? = null,
) : NetworkResponse

@Serializable
data class NetworkVideo(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String? = null,
    @SerialName("url") val url: String,
    @SerialName("likes") val likes: Int = 0,
    @SerialName("isLike") val isLike: Boolean = false,
    @SerialName("isFavourite") val isFavourite: Boolean = false,
)

fun NetworkAlbumType.toDomainModel() = AlbumType(typeID = typeID, typeName = typeName)

fun NetworkAlbum.toDomainModel() = Album(
    id = id,
    title = title,
    description = description ?: "",
    totalPhotos = totalPhotos,
    fileName = fileName ?: "",
    eventDate = eventDate ?: "",
)

fun NetworkPhoto.toDomainModel() = Photo(
    id = id,
    title = title ?: "",
    description = description,
    photoPath = photoPath,
    likes = likes,
    isLike = isLike,
    isFavourite = isFavourite,
)

fun NetworkGetAlbumDetail.toDomainModel() = AlbumDetail(
    title = title ?: "",
    description = description ?: "",
    eventDate = eventDate ?: "",
    totalPhotos = totalPhotos ?: 0,
    setting = setting?.let {
        PhotoSetting(
            isLikeEnabled = it.isLikeEnabled,
            isShareEnabled = it.isShareEnabled,
            isAddFavouriteEnabled = it.isAddFavouriteEnabled,
        )
    } ?: PhotoSetting(isLikeEnabled = false, isShareEnabled = false, isAddFavouriteEnabled = false),
    photos = photos?.map { it.toDomainModel() } ?: emptyList(),
)

fun NetworkVideoAlbum.toDomainModel() = VideoAlbum(
    id = id,
    title = title,
    description = description ?: "",
    totalVideos = totalVideos,
    fileName = fileName ?: "",
    eventDate = eventDate ?: "",
)

fun NetworkVideo.toDomainModel() = Video(
    id = id,
    title = title ?: "",
    url = url,
    likes = likes,
    isLike = isLike,
    isFavourite = isFavourite,
)

fun NetworkGetVideoAlbumDetail.toDomainModel() = VideoAlbumDetail(
    title = title ?: "",
    description = description ?: "",
    eventDate = eventDate ?: "",
    totalVideos = totalVideos ?: 0,
    setting = setting?.let {
        PhotoSetting(
            isLikeEnabled = it.isLikeEnabled,
            isShareEnabled = it.isShareEnabled,
            isAddFavouriteEnabled = it.isAddFavouriteEnabled,
        )
    } ?: PhotoSetting(isLikeEnabled = false, isShareEnabled = false, isAddFavouriteEnabled = false),
    videos = videos?.map { it.toDomainModel() } ?: emptyList(),
)

// ── Favorites ────────────────────────────────────────────────────────────────

@Serializable
data class NetworkGetFavorites(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("setting") val setting: NetworkPhotoSetting? = null,
    @SerialName("list") val list: List<NetworkFavoriteItem>? = null,
) : NetworkResponse

@Serializable
data class NetworkFavoriteItem(
    @SerialName("galleryType") val galleryType: Int,
    @SerialName("id") val id: String,
    @SerialName("totalLike") val totalLike: Int = 0,
    @SerialName("islLike") val islLike: Int = 0,   // API typo: islLike (Int 0/1)
    @SerialName("fileName") val fileName: String,
    @SerialName("isFavourite") val isFavourite: Boolean = false,
)

fun NetworkFavoriteItem.toDomainModel() = FavoriteItem(
    id = id,
    galleryType = galleryType,
    fileName = fileName,
    totalLike = totalLike,
    isLike = islLike == 1,
    isFavourite = isFavourite,
)

fun NetworkGetFavorites.toDomainModel() = FavoritesData(
    setting = setting?.let {
        PhotoSetting(
            isLikeEnabled = it.isLikeEnabled,
            isShareEnabled = it.isShareEnabled,
            isAddFavouriteEnabled = it.isAddFavouriteEnabled,
        )
    } ?: PhotoSetting(isLikeEnabled = false, isShareEnabled = false, isAddFavouriteEnabled = false),
    items = list?.map { it.toDomainModel() } ?: emptyList(),
)

// ── Media Gallery ─────────────────────────────────────────────────────────────

@Serializable
data class NetworkGetMediaGallery(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("years") val years: List<String>? = null,
    @SerialName("albums") val albums: List<NetworkMediaItem>? = null,
) : NetworkResponse

@Serializable
data class NetworkMediaItem(
    @SerialName("id") val id: Int,
    @SerialName("newsName") val newsName: String? = null,
    @SerialName("headline") val headline: String? = null,
    @SerialName("publishedOn") val publishedOn: String? = null,
    @SerialName("updatedOn") val updatedOn: String? = null,
    @SerialName("fileName") val fileName: String? = null,
    @SerialName("fileNameFullSize") val fileNameFullSize: String? = null,
    @SerialName("description") val description: String? = null,
)

fun NetworkMediaItem.toDomainModel() = MediaItem(
    id = id,
    newsName = newsName.orEmpty(),
    headline = headline.orEmpty(),
    publishedOn = publishedOn.orEmpty(),
    updatedOn = updatedOn.orEmpty(),
    thumbnailUrl = fileName.orEmpty(),
    fullSizeUrl = fileNameFullSize.orEmpty(),
    description = description.orEmpty(),
)

fun NetworkGetMediaGallery.toDomainModel() = MediaGalleryData(
    years = years ?: emptyList(),
    items = albums?.map { it.toDomainModel() } ?: emptyList(),
)

// ── Kids Corner ───────────────────────────────────────────────────────────────

@Serializable
data class NetworkGetKidsAlbums(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("academicYears") val academicYears: List<NetworkKidsAcademicYear>? = null,
    @SerialName("albums") val albums: List<NetworkKidsAlbum>? = null,
) : NetworkResponse

@Serializable
data class NetworkKidsAcademicYear(
    @SerialName("yrID") val yrID: Int,
    @SerialName("session") val session: String,
    @SerialName("isCur") val isCur: Boolean = false,
)

@Serializable
data class NetworkKidsAlbum(
    @SerialName("kid") val kid: String,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("albumIcon") val albumIcon: String? = null,
    @SerialName("yearName") val yearName: String? = null,
    @SerialName("totalPhoto") val totalPhoto: Int = 0,
    @SerialName("createdOn") val createdOn: String? = null,
    @SerialName("updatedOn") val updatedOn: String? = null,
)

@Serializable
data class NetworkGetKidsAlbumDetail(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("albumDetail") val albumDetail: NetworkKidsAlbum? = null,
    @SerialName("albumDetails") val albumDetails: List<NetworkKidsPhoto>? = null,
) : NetworkResponse

@Serializable
data class NetworkKidsPhoto(
    @SerialName("id") val id: String? = null,
    @SerialName("fileName") val fileName: String? = null,
)

fun NetworkKidsAcademicYear.toDomainModel() = KidsAcademicYear(
    yrID = yrID, session = session, isCur = isCur,
)

fun NetworkKidsAlbum.toDomainModel() = KidsAlbum(
    kid = kid,
    title = title.orEmpty(),
    description = description.orEmpty(),
    albumIcon = albumIcon.orEmpty(),
    yearName = yearName.orEmpty(),
    totalPhoto = totalPhoto,
    createdOn = createdOn.orEmpty(),
    updatedOn = updatedOn.orEmpty(),
)

fun NetworkKidsPhoto.toDomainModel() = KidsPhoto(
    id = id.orEmpty(),
    fileName = fileName.orEmpty(),
)

fun NetworkGetKidsAlbums.toDomainModel() = KidsAlbumsData(
    academicYears = academicYears?.map { it.toDomainModel() } ?: emptyList(),
    albums = albums?.map { it.toDomainModel() } ?: emptyList(),
)

fun NetworkGetKidsAlbumDetail.toDomainModel(): KidsAlbumDetailData {
    val detail = albumDetail
    return KidsAlbumDetailData(
        kid = detail?.kid.orEmpty(),
        title = detail?.title.orEmpty(),
        description = detail?.description.orEmpty(),
        albumIcon = detail?.albumIcon.orEmpty(),
        yearName = detail?.yearName.orEmpty(),
        totalPhoto = detail?.totalPhoto ?: 0,
        createdOn = detail?.createdOn.orEmpty(),
        updatedOn = detail?.updatedOn.orEmpty(),
        photos = albumDetails?.map { it.toDomainModel() } ?: emptyList(),
    )
}
