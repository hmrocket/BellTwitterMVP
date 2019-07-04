package com.bell.demo.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bell.demo.R
import com.bell.demo.model.TweetType
import com.bell.demo.ui.common.BaseTweetActivity
import com.bell.demo.ui.search.holders.*
import com.bell.demo.ui.tweet.TweetViewModel
import com.google.android.material.snackbar.Snackbar
import com.twitter.sdk.android.core.models.Tweet

/**
 * Show a single tweet screen
 */
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
    lateinit var model: TweetViewModel

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

        model = ViewModelProviders.of(this).get(TweetViewModel::class.java)

        val observer = Observer<Tweet?> {
            if (it == null)
                Snackbar.make(
                    this@TweetActivity.window.decorView,
                    getString(R.string.action_failed), Snackbar.LENGTH_SHORT
                ).show()
            else
                viewHolder.setup(it)
        }

        model.getObseravale().observe(this, observer)

        model.onViewInitialized(id)
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
        TweetType.LINK -> StatusLinkViewHolder(window.decorView, this)
        TweetType.PHOTO -> StatusPhotoViewHolder(window.decorView, this)
        TweetType.MULTIPLE_PHOTOS -> StatusMultiplePhotosViewHolder(window.decorView, this)
        TweetType.QUOTE -> StatusQuoteViewHolder(window.decorView, this)
        TweetType.VIDEO -> StatusVideoViewHolder(window.decorView, this)
    }

    override fun postInteractionSuccessful(tweetBefore: Tweet, tweetAfter: Tweet) {
        // update the tweet
        viewHolder.setup(tweetAfter)
    }


}
