package com.bell.demo.ui.common.interector

import com.twitter.sdk.android.core.models.Tweet


/**
 * Tweet interaction
 */
interface InteractionListener {

    fun favorite(tweet: Tweet)

    fun retweet(tweet: Tweet)

    fun unfavorite(tweet: Tweet)

    fun unretweet(tweet: Tweet)

    fun openTweet(tweet: Tweet)

    fun showImage(imageUrl: String)

    fun showImages(imageUrls: List<String>, index: Int)

    fun showVideo(videoUrl: String, videoType: String)

}