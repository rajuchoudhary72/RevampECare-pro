package com.app.ecarepro.feature.gallery.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.feature.gallery.photo.detail.PhotoDetailScreen
import com.app.ecarepro.feature.gallery.photo.detail.PhotoDetailViewModel
import com.app.ecarepro.feature.gallery.photo.list.PhotoListScreen
import com.app.ecarepro.feature.gallery.photo.slider.PhotoSliderScreen
import com.app.ecarepro.feature.gallery.photo.slider.PhotoSliderViewModel
import com.app.ecarepro.feature.gallery.favorites.FavoritesScreen
import com.app.ecarepro.feature.gallery.favorites.viewer.FavoriteViewerScreen
import com.app.ecarepro.feature.gallery.favorites.viewer.FavoriteViewerViewModel
import com.app.ecarepro.feature.gallery.kids.list.KidsListScreen
import com.app.ecarepro.feature.gallery.kids.detail.KidsDetailScreen
import com.app.ecarepro.feature.gallery.kids.detail.KidsDetailViewModel
import com.app.ecarepro.feature.gallery.media.detail.MediaDetailScreen
import com.app.ecarepro.feature.gallery.media.detail.MediaDetailViewModel
import com.app.ecarepro.feature.gallery.media.list.MediaListScreen
import com.app.ecarepro.feature.gallery.video.detail.VideoDetailScreen
import com.app.ecarepro.feature.gallery.video.detail.VideoDetailViewModel
import com.app.ecarepro.feature.gallery.video.list.VideoListScreen
import com.app.ecarepro.feature.gallery.video.player.VideoPlayerScreen
import com.app.ecarepro.feature.gallery.video.player.VideoPlayerViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface GalleryNavGraph : NavKey {

    @Serializable
    data object PhotoAlbumList : GalleryNavGraph

    @Serializable
    data class PhotoAlbumDetail(val albumId: String, val albumTitle: String) : GalleryNavGraph

    @Serializable
    data class PhotoSlider(val albumId: String, val albumTitle: String, val initialPhotoIndex: Int) : GalleryNavGraph

    @Serializable
    data object VideoAlbumList : GalleryNavGraph

    @Serializable
    data class VideoAlbumDetail(val albumId: String, val albumTitle: String) : GalleryNavGraph

    @Serializable
    data class VideoPlayer(val albumId: String, val albumTitle: String, val initialVideoIndex: Int) : GalleryNavGraph

    @Serializable
    data object KidsCornerList : GalleryNavGraph

    @Serializable
    data class KidsCornerDetail(
        val kid: String,
        val title: String,
    ) : GalleryNavGraph

    @Serializable
    data object MediaGalleryList : GalleryNavGraph

    @Serializable
    data class MediaGalleryDetail(
        val id: Int,
        val newsName: String,
        val headline: String,
        val publishedOn: String,
        val updatedOn: String,
        val thumbnailUrl: String,
        val fullSizeUrl: String,
        val description: String,
    ) : GalleryNavGraph

    @Serializable
    data object Favorites : GalleryNavGraph

    @Serializable
    data class FavoriteViewer(
        val id: String,
        val galleryType: Int,
        val fileName: String,
        val totalLike: Int,
        val isLike: Boolean,
        val isFavourite: Boolean,
        val isLikeEnabled: Boolean,
        val isShareEnabled: Boolean,
        val isAddFavouriteEnabled: Boolean,
    ) : GalleryNavGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.EntryGalleryNavigation(
    backStack: SnapshotStateList<NavKey>,
    navigateBack: () -> Unit,
) {
    entry<GalleryNavGraph.PhotoAlbumList> {
        PhotoListScreen(
            navigateBack = navigateBack,
            navigateToDetail = { albumId, albumTitle ->
                backStack.add(GalleryNavGraph.PhotoAlbumDetail(albumId, albumTitle))
            }
        )
    }

    entry<GalleryNavGraph.PhotoAlbumDetail> { key ->
        val viewModel: PhotoDetailViewModel = navKeyViewModel(key)
        PhotoDetailScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToSlider = { initialIndex ->
                backStack.add(GalleryNavGraph.PhotoSlider(key.albumId, key.albumTitle, initialIndex))
            }
        )
    }

    entry<GalleryNavGraph.PhotoSlider> { key ->
        val viewModel: PhotoSliderViewModel = navKeyViewModel(key)
        PhotoSliderScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }

    entry<GalleryNavGraph.VideoAlbumList> {
        VideoListScreen(
            navigateBack = navigateBack,
            navigateToDetail = { albumId, albumTitle ->
                backStack.add(GalleryNavGraph.VideoAlbumDetail(albumId, albumTitle))
            }
        )
    }

    entry<GalleryNavGraph.VideoAlbumDetail> { key ->
        val viewModel: VideoDetailViewModel = navKeyViewModel(key)
        VideoDetailScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToPlayer = { initialIndex ->
                backStack.add(GalleryNavGraph.VideoPlayer(key.albumId, key.albumTitle, initialIndex))
            }
        )
    }

    entry<GalleryNavGraph.VideoPlayer> { key ->
        val viewModel: VideoPlayerViewModel = navKeyViewModel(key)
        VideoPlayerScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }

    entry<GalleryNavGraph.KidsCornerList> {
        KidsListScreen(
            navigateBack = navigateBack,
            navigateToDetail = { kid, title ->
                backStack.add(GalleryNavGraph.KidsCornerDetail(kid, title))
            }
        )
    }

    entry<GalleryNavGraph.KidsCornerDetail> { key ->
        val viewModel: KidsDetailViewModel = navKeyViewModel(key)
        KidsDetailScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }

    entry<GalleryNavGraph.MediaGalleryList> {
        MediaListScreen(
            navigateBack = navigateBack,
            navigateToDetail = { item ->
                backStack.add(
                    GalleryNavGraph.MediaGalleryDetail(
                        id = item.id,
                        newsName = item.newsName,
                        headline = item.headline,
                        publishedOn = item.publishedOn,
                        updatedOn = item.updatedOn,
                        thumbnailUrl = item.thumbnailUrl,
                        fullSizeUrl = item.fullSizeUrl,
                        description = item.description,
                    )
                )
            }
        )
    }

    entry<GalleryNavGraph.MediaGalleryDetail> { key ->
        val viewModel: MediaDetailViewModel = navKeyViewModel(key)
        MediaDetailScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }

    entry<GalleryNavGraph.Favorites> {
        FavoritesScreen(
            navigateBack = navigateBack,
            navigateToViewer = { item -> backStack.add(item) }
        )
    }

    entry<GalleryNavGraph.FavoriteViewer> { key ->
        val viewModel: FavoriteViewerViewModel = navKeyViewModel(key)
        FavoriteViewerScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }
}
