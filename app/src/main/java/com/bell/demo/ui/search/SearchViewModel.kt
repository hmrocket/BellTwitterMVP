package com.bell.demo.ui.search

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bell.demo.repo.CacheRepo
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Search
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.params.Geocode

class SearchViewModel : ViewModel() {

    private val tweets: MutableLiveData<List<Tweet>?> = MutableLiveData()
    private var requested = false


    // it's imported to pass LiveData and not MutableLiveData so #setValue(T) and #postValue(T) method aren't exposed.
    fun getObservedTweets(): LiveData<List<Tweet>?> = tweets

    fun onViewInitialized(query: String, location: Location?, radius: Int) {
        // If the view  was rotated, this line would not occur since the call already
        // happened.
        if (!requested) {
            searchTweets(query, location, radius)
        }
    }

    fun searchTweets(query: String, location: Location?, radius: Int) {
        Log.d("search", "query = $query")
        val currentOrMontreal = Geocode(
            location?.latitude ?: 45.5017, location?.longitude ?: 73.5673,
            radius, Geocode.Distance.KILOMETERS
        )

        CacheRepo.getCachedSearch(query, currentOrMontreal)?.let {
            tweets.value = it
            return
        }

        requested = true
        val searchService = TwitterCore.getInstance().apiClient.searchService
        searchService.tweets(
            query, currentOrMontreal, null, null, null, 100, null, null, null, true
        )
            .enqueue(object : Callback<Search>() {
                override fun success(result: Result<Search>) {
                    tweets.value = result.data.tweets
                    CacheRepo.putCachedSearch(query, currentOrMontreal, result.data.tweets)
                    requested = false
                }

                override fun failure(exception: TwitterException) {
                    tweets.value = null
                    Log.e("tweet search", "failed request ${exception.message}")
                    requested = false
                }
            })

    }


}