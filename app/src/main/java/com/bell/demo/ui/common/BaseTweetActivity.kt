package com.bell.demo.ui.common

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bell.demo.ui.LoginActivity
import com.bell.demo.ui.common.interector.InteractionListener
import com.bell.demo.ui.image.ImageActivity
import com.bell.demo.ui.video.VideoActivity
import com.google.android.material.snackbar.Snackbar
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import retrofit2.Call

abstract class BaseTweetActivity : AppCompatActivity(), InteractionListener {
    /**
     * Called after success any of these operation favorite, retweet, destroy(retweet, favorite)
     */
    abstract fun postInteractionSuccessful(tweetBefore: Tweet, tweetAfter: Tweet)

    // redundant code
    private fun enqueue(tweet: Tweet, call: Call<Tweet>) {
        call.enqueue(object : Callback<Tweet>() {
            override fun success(result: Result<Tweet>?) {
                result?.let {
                    postInteractionSuccessful(tweet, result.data)
                }

            }

            override fun failure(exception: TwitterException?) {
                Snackbar.make(
                    this@BaseTweetActivity.window.decorView,
                    "Action failed, we don't know why", Snackbar.LENGTH_SHORT
                ).show()
            }

        })
    }

    override fun favorite(tweet: Tweet) {
        if (TwitterCore.getInstance().sessionManager.activeSession == null)
            LoginActivity.launch(this)
        else {
            val call = TwitterCore.getInstance().apiClient.favoriteService.create(tweet.id, null)
            enqueue(tweet, call)
        }
    }

    override fun retweet(tweet: Tweet) {
        if (TwitterCore.getInstance().sessionManager.activeSession == null)
            LoginActivity.launch(this)
        else {
            val call = TwitterCore.getInstance().apiClient.statusesService.retweet(tweet.id, null)
            enqueue(tweet, call)
        }

    }

    override fun unfavorite(tweet: Tweet) {
        if (TwitterCore.getInstance().sessionManager.activeSession == null)
            LoginActivity.launch(this)
        else {
            val call = TwitterCore.getInstance().apiClient.favoriteService.destroy(tweet.id, null)
            enqueue(tweet, call)
        }
    }

    override fun unretweet(tweet: Tweet) {
        if (TwitterCore.getInstance().sessionManager.activeSession == null)
            LoginActivity.launch(this)
        else {
            val call = TwitterCore.getInstance().apiClient.statusesService.unretweet(tweet.id, null)
            enqueue(tweet, call)
        }
    }

    override fun openTweet(tweet: Tweet) {
        Toast.makeText(this, "open tweet", Toast.LENGTH_SHORT).show()
        startActivity(
            Intent.createChooser(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/${tweet.user.screenName}/status/${tweet.id}")
                ), "tweet"
            )
        )
    }

    override fun showImage(imageUrl: String) {
        ImageActivity.launch(this, arrayOf(imageUrl))
    }

    override fun showImages(imageUrls: List<String>, index: Int) {
        ImageActivity.launch(this, imageUrls.toTypedArray(), index)
    }

    override fun showVideo(videoUrl: String, videoType: String) {
        VideoActivity.launch(this, videoUrl, videoType)
    }
}