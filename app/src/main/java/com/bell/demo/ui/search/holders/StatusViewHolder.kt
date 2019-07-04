package com.bell.demo.ui.search.holders


import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import com.bell.demo.R
import com.bell.demo.ui.common.interector.InteractionListener
import com.bell.demo.utils.Utils
import com.bell.demo.utils.loadUrl
import com.bell.demo.utils.visible
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.tweet_basic.view.*

open class StatusViewHolder(container: View, listener: InteractionListener) :
        BaseViewHolder(container, listener) {

    protected var retweetTextView: TextView = container.retweetTextView

    @CallSuper
    override fun setup(tweet: Tweet) {
        val currentTweet: Tweet

        if (tweet.retweeted) {
            currentTweet = tweet.retweetedStatus
            retweetTextView.visible()
            retweetTextView.text = container.context.getString(
                    R.string.retweeted_by, tweet.user.screenName)
        } else {
            currentTweet = tweet
            retweetTextView.visible(false)
        }

        val currentUser = currentTweet.user
        userNameTextView.text = currentUser.name
        userScreenNameTextView.text = userScreenNameTextView.context.getString(R.string.at_user, currentUser.screenName)
        timeTextView.text = Utils.formatTime(currentTweet.createdAt)

        userProfilePicImageView.loadUrl(currentUser.profileImageUrl)

        if (currentTweet.favorited)
            favouriteImageButton.setImageResource(R.drawable.ic_favorite_red)
        else
            favouriteImageButton.setImageResource(R.drawable.ic_favorite)

        if (currentTweet.retweeted)
            retweetImageButton.setImageResource(R.drawable.ic_repeat_green)
        else
            retweetImageButton.setImageResource(R.drawable.ic_repeat)

        favouritesStatsTextView.text = "${tweet.favoriteCount}"
        retweetsStatsTextView.text = "${tweet.retweetCount}"
        statusTextView.text = currentTweet.text

        // it could be a good idea to send the user to Twitter app when Profile image clicked... new interface need to be added
        // userProfilePicImageView.setOnClickListener { listener.showUser(currentUser) }

        favouriteImageButton.setOnClickListener {
            if (currentTweet.favorited)
                listener.unfavorite(currentTweet)
            else
                listener.favorite(currentTweet)
        }

        retweetImageButton.setOnClickListener {
            if (currentTweet.retweeted)
                listener.unretweet(currentTweet)
            else
                listener.retweet(currentTweet)
        }

        container.setOnClickListener { listener.openTweet(currentTweet) }
    }

}
