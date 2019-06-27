package com.bell.demo.ui.search.holders


import android.view.View
import com.bell.demo.ui.common.interector.InteractionListener
import com.bell.demo.utils.getVideoCoverUrl
import com.bell.demo.utils.getVideoUrlType
import com.bell.demo.utils.loadUrlCenterCrop
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.video_cover.view.*

class StatusVideoViewHolder(container: View, listener: InteractionListener) :
        StatusViewHolder(container, listener) {

    private val tweetVideoImageView = container.tweetVideoImageView
    private val playVideoImageButton = container.playVideoImageButton

    override fun setup(tweet: Tweet) {
        super.setup(tweet)

        tweetVideoImageView.loadUrlCenterCrop(tweet.getVideoCoverUrl())

        playVideoImageButton.setOnClickListener {
            val pair = tweet.getVideoUrlType()
            listener.showVideo(pair.first, pair.second)
        }
        tweetVideoImageView.setOnClickListener { playVideoImageButton.callOnClick() }
    }

}
