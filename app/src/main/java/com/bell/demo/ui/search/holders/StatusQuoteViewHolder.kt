package com.bell.demo.ui.search.holders

import android.view.View
import com.bell.demo.ui.common.interector.InteractionListener
import com.twitter.sdk.android.core.models.Tweet

class StatusQuoteViewHolder(container: View, listener: InteractionListener) :
        StatusViewHolder(container, listener) {

//    private val quotedUserNameTextView = container.quotedUserNameTextView
//    private val quotedUserScreenNameTextView = container.quotedUserScreenNameTextView
//    private val quotedStatusTextView = container.quotedStatusTextView
//    private val photoImageView = container.photoImageView
//    private val quotedStatusLinearLayout = container.quotedStatus

    override fun setup(tweet: Tweet) {
        super.setup(tweet)

//        if (tweet.quotedStatus) {
//            val quotedStatus = tweet.getQuotedTweet()
//            quotedUserNameTextView.text = quotedStatus.user.name
//            quotedUserScreenNameTextView.text = "@${quotedStatus.user.screenName}"
//
//            if (quotedStatus.mediaEntities.isNotEmpty()) {
//                photoImageView.visible()
//                photoImageView.loadUrl(quotedStatus.mediaEntities[0].mediaURL)
//
//                quotedStatusTextView.text = quotedStatus.getTextWithoutMediaURLs()
//            } else {
//                photoImageView.visible(false)
//                quotedStatusTextView.text = quotedStatus.text
//            }
//
//            quotedStatusLinearLayout.setOnClickListener { listener.openTweet(quotedStatus) }
//        } else quotedStatusLinearLayout.visible(false)
    }

}
