package com.bell.demo.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import com.bell.demo.R
import com.bell.demo.model.TweetType
import com.bell.demo.ui.common.BaseTweetActivity
import com.bell.demo.ui.search.holders.BaseViewHolder
import com.bell.demo.ui.search.holders.StatusViewHolder
import com.google.android.material.snackbar.Snackbar
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet

class TweetActivity : BaseTweetActivity() {

    companion object {
        private const val KEY_TYPE = "type"
        private const val KEY_ID = "id"

        fun launch(context: Context, tweetId: Long, tweetType: TweetType) {
            val intent = Intent(context, TweetActivity::class.java)
            intent.putExtra(KEY_TYPE, tweetType.toString())
            intent.putExtra(KEY_ID, tweetId)
            context.startActivity(intent)
        }
    }

    lateinit var viewHolder : BaseViewHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val typeString: String? = intent.getStringExtra(KEY_TYPE)
        val id = intent.getLongExtra(KEY_ID, -1L)
        if(typeString == null || id == -1L) {
            finish()
            return
        }

        setContentView(getViewId(typeString))
        viewHolder = getHolder(typeString)

        fetchTweetById(id)

    }

    private fun fetchTweetById(id : Long) {
        TwitterCore.getInstance().apiClient.statusesService
                .show(id, null, null, null)
            .enqueue(object : Callback<Tweet>() {
                override fun success(result: Result<Tweet>?) {
                    result?.let { viewHolder.setup(result.data) }
                }

                override fun failure(exception: TwitterException?) {
                    Snackbar.make(
                        this@TweetActivity.window.decorView,
                        getString(R.string.action_failed), Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
    }

    @LayoutRes
    private fun getViewId(typeString: String): Int = when (TweetType.valueOf(typeString)) {
            TweetType.BASIC -> R.layout.tweet_basic
            TweetType.LINK -> R.layout.tweet_link
            TweetType.PHOTO -> R.layout.tweet_photo
            TweetType.MULTIPLE_PHOTOS -> R.layout.tweet_multiplephotos
            TweetType.QUOTE -> R.layout.tweet_quote
            TweetType.VIDEO -> R.layout.tweet_video
        }

    private fun getHolder(typeString: String) : BaseViewHolder = when (TweetType.valueOf(typeString)) {
        TweetType.BASIC -> StatusViewHolder(window.decorView, this)
        TweetType.LINK -> StatusViewHolder(window.decorView, this)
        TweetType.PHOTO -> StatusViewHolder(window.decorView, this)
        TweetType.MULTIPLE_PHOTOS -> StatusViewHolder(window.decorView, this)
        TweetType.QUOTE -> StatusViewHolder(window.decorView, this)
        TweetType.VIDEO -> StatusViewHolder(window.decorView, this)
    }

    override fun postInteractionSuccessful(tweetBefore: Tweet, tweetAfter: Tweet) {
        // update the tweet
        viewHolder.setup(tweetAfter)
    }


}
