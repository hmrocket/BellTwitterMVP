package com.bell.demo.ui.search.holders


import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bell.demo.ui.SpaceLeftItemDecoration
import com.bell.demo.ui.search.InteractionListener
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.tweet_multiplephotos.view.*

class StatusMultiplePhotosViewHolder(container: View, listener: InteractionListener) :
        StatusViewHolder(container, listener) {

    private val tweetPhotosRecyclerView = container.tweetPhotosRecyclerView

    init {
        tweetPhotosRecyclerView.addItemDecoration(SpaceLeftItemDecoration(5))
        tweetPhotosRecyclerView.layoutManager =
                LinearLayoutManager(container.context, LinearLayoutManager.HORIZONTAL, false)
        tweetPhotosRecyclerView.setHasFixedSize(true)
    }

    override fun setup(tweet: Tweet) {
        super.setup(tweet)
        tweetPhotosRecyclerView.adapter = ImagesAdapter(tweet.extendedEntities, listener)
    }
}
