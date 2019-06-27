package com.bell.demo.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bell.demo.R
import com.bell.demo.ui.common.interector.InteractionListener
import com.bell.demo.ui.search.holders.*
import com.bell.demo.utils.*
import com.twitter.sdk.android.core.models.Tweet
import java.util.*

/**
 * Search adapter (it will show multiple tweets)
 */
open class SearchAdapter(val listener: InteractionListener) :
        RecyclerView.Adapter<BaseViewHolder>() {

    var tweets: ArrayList<Tweet> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when (viewType) {
            R.layout.tweet_basic -> StatusViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.tweet_basic, parent, false), listener)
            R.layout.tweet_photo -> StatusPhotoViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.tweet_photo, parent, false), listener)
            R.layout.tweet_quote -> StatusQuoteViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.tweet_quote, parent, false), listener)
            R.layout.tweet_multiplephotos -> StatusMultiplePhotosViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.tweet_multiplephotos, parent, false), listener)
            R.layout.tweet_video -> StatusVideoViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.tweet_video, parent, false), listener)
            R.layout.tweet_link -> StatusLinkViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.tweet_link, parent, false), listener)
            else -> throw UnsupportedOperationException("No Type found")
        }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.setup(tweets[position])
    }

    override fun getItemCount() = tweets.size

    override fun getItemViewType(position: Int): Int {
        val tweet = tweets[position]
        return when {
            tweet.hasSingleImage() -> R.layout.tweet_photo
            tweet.hasSingleVideo() -> R.layout.tweet_video
            tweet.hasMultipleMedia() -> R.layout.tweet_multiplephotos
            tweet.hasQuotedStatus() -> R.layout.tweet_quote
            tweet.hasLinks() -> R.layout.tweet_link
            else -> R.layout.tweet_basic
        }
    }

}