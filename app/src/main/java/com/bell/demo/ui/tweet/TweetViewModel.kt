package com.bell.demo.ui.tweet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bell.demo.repo.CacheRepo
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet

class TweetViewModel : ViewModel() {

    private val liveData : MutableLiveData<Tweet> = MutableLiveData()
    // we don't want to expose setValue and postValue
    fun getObseravale() : LiveData<Tweet> = liveData

    private var fetchTweetRequested : Boolean = false


    fun onViewInitialized(id: Long) {
        if (!fetchTweetRequested)
            fetchTweetById(id)
    }

    fun fetchTweetById(id : Long) {
        // check the cache to avoid double fetch on rotation
        CacheRepo.getTweet(id)?.let {
            liveData.value = it
            return
        }

        fetchTweetRequested = true

        TwitterCore.getInstance().apiClient.statusesService
            .show(id, null, null, null)
            .enqueue(object : Callback<Tweet>() {
                override fun success(result: Result<Tweet>?) {
                    result?.let {
                        CacheRepo.putTweet(it.data)
                        liveData.value = result.data
                    }
                }

                override fun failure(exception: TwitterException?) {
                    liveData.value = null
                }
            })
    }
}