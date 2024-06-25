package com.app.ecarepro.ui.gallery.videoPlay

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.app.ecarepro.R
import android.view.WindowManager
import com.app.ecarepro.databinding.FragmentYouTubeVideoPlayerBinding
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.AndroidDownloader
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class YouTubeVideoPlayerFragment : Fragment() {

    private lateinit var binding : FragmentYouTubeVideoPlayerBinding

    var videoID = " "



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentYouTubeVideoPlayerBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString(VIDEO_ID)?.let { videoID ->
            this.videoID =videoID
        }

       /* requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.youTubePlayerView.enterFullScreen()
        binding.youTubePlayerView.toggleFullScreen()*/


        lifecycle.addObserver(binding.youTubePlayerView)


       // binding.youTubePlayerView.getPlayerUiController()

       // binding.youTubePlayerView.enterFullScreen()
       // binding.youTubePlayerView.toggleFullScreen()

        binding.youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {

                youTubePlayer.loadVideo(videoID, 0f)
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                // this method is called if video has ended,
                super.onStateChange(youTubePlayer, state)
            }
        })
    }

    companion object {
        const val VIDEO_ID = "videoID"
    }
}