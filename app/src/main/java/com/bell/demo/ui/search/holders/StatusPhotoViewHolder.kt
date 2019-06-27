package com.bell.demo.ui.search.holders

import android.view.View
import com.bell.demo.ui.common.interector.InteractionListener
import com.bell.demo.utils.getImageUrl
import com.bell.demo.utils.hasSingleImage
import com.bell.demo.utils.loadUrlCenterCrop
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.tweet_photo.view.*

class StatusPhotoViewHolder(container: View, listener: InteractionListener) :
        StatusViewHolder(container, listener) {

    private val tweetPhotoImageView = container.tweetPhotoImageView

    override fun setup(tweet: Tweet) {
        super.setup(tweet)

        if (tweet.hasSingleImage()) {
            tweetPhotoImageView.loadUrlCenterCrop(tweet.getImageUrl())
            tweetPhotoImageView.setOnClickListener { listener.showImage(tweet.getImageUrl()) }
        }
    }
}
