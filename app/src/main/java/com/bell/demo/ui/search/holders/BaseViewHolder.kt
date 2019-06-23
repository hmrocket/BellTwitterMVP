package com.bell.demo.ui.search.holders

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bell.demo.ui.search.InteractionListener
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.item_interaction.view.*
import kotlinx.android.synthetic.main.item_userinfo.view.*
import kotlinx.android.synthetic.main.tweet_basic.view.*

abstract class BaseViewHolder(val container: View, val listener: InteractionListener) :
        RecyclerView.ViewHolder(container) {

    protected val userNameTextView: TextView = container.userNameTextView
    protected val userScreenNameTextView: TextView = container.userScreenNameTextView
    protected val statusTextView: TextView = container.statusTextView
    protected val timeTextView: TextView = container.timeTextView
    protected val retweetsStatsTextView: TextView = container.retweetsStatsTextView
    protected val favouritesStatsTextView: TextView = container.favouritesStatsTextView
    protected val userProfilePicImageView: ImageView = container.userProfilePicImageView
    protected val favouriteImageButton: ImageButton = container.favouriteImageButton
    protected val retweetImageButton: ImageButton = container.retweetImageButton

    // TODO use call super and pre setup common value of a tweet
    abstract fun setup(tweet: Tweet)

}
