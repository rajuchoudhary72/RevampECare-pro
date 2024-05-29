package com.app.ecarepro.utils

import java.util.regex.Pattern

class YoutubeURL {


    fun getTIURLFromYoutubeURL(youtubeVideoURL: String?): String? {
        val youtubeThumbURL = "http://img.youtube.com/vi/"
        val youtubeThumbQuality = "/default.jpg"
        return youtubeThumbURL + getIDFromYoutubeURL(youtubeVideoURL) + youtubeThumbQuality
    }

    private fun getIDFromYoutubeURL(youtubeVideoURL: String?): String? {
        var youtubeVideoID: String? = null
        val pattern =
            "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher =
            compiledPattern.matcher(youtubeVideoURL) //url is youtube url for which you want to extract the id.
        return if (matcher.find()) {
            matcher.group().also { youtubeVideoID = it }
        } else null
    }

}