package com.bell.demo.ui.map

import android.location.Location
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bell.demo.repo.AppConfig
import com.bell.demo.repo.CacheRepo
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.params.Geocode
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// TODO we can add fetch location and get radius to ViewModel, that way we don't need them as params
// XXX MapState ? observable to inform the view of the current state
class TweetsMapViewModel : ViewModel() {

    private val tweets: MutableLiveData<List<Tweet>?> = MutableLiveData()
    private var location : Location? = null
    private var radius: Int = AppConfig.MIN_RADIUS.toInt()

    var runnable : Runnable? = null
    private val timeFrameBetweenRequestsInMilliseconds: Long = 30000 // 30 sec
    private var lastRequestTimeFrame: Long = 0L

    private val backThread: ExecutorService = Executors.newSingleThreadExecutor()
    private val handler : Handler = Handler(Looper.getMainLooper())

    // it's imported to pass LiveData and not MutableLiveData so #setValue(T) and #postValue(T) method aren't exposed.
    fun getObservedTweets(): LiveData<List<Tweet>?> = tweets


    fun onViewInitialized(location: Location?, radius: Int) {
        //
        // If the lastRequestTimeFrame field is 0 then it means that the view  was
        // just initialized. Thus, a new call is being made for quotes.
        // If the view  was rotated, this line would not occur since the call already
        // happened.
        if (lastRequestTimeFrame == 0L) {
            fetchGeoTweets(location, radius)
        }
    }

    fun fetchGeoTweets(location: Location?, radius: Int) {
        CacheRepo.getCachedTweets(radius)?.let {
            postUpdate(radius, it)
            this.location = location
            this.radius = radius
            Log.d("tweet", "cached tweet returned")
            return // check the cache first, post and return
        }

        if (runnable != null) {
            if (this.radius != radius || this.location != location) {
                updateTimeFrame()
                backThread.execute { // user changed radius or location, fetch tweets right away
                    loadTweets(location, radius)
                }
                this.radius = radius
                this.location = location
            }
            return // if the user didn't change location neither radius, NO need to update we'll just wait for the next refresh
        } else {

            // here we'll setup a lopper of 30 sec

            runnable = object : Runnable {
                override fun run() {
                    backThread.execute {
                        if (!backThread.isShutdown) {
                            val time = getRemainingTimeUntilNextRequest()
                            if (time > 0) // it's possible we just updated the tweet because of radius or location change
                                TimeUnit.MILLISECONDS.sleep(time) // wait, we just updated
                            loadTweets(location, radius)
                        }
                    }
                    handler.postDelayed(this, timeFrameBetweenRequestsInMilliseconds)
                }
            }

            handler.post(runnable!!)
        }

    }

    @WorkerThread
    private fun loadTweets(location: Location?, radius: Int) {
        Log.d("tweets", "loadTweets")
        // load tweets if location is null take montreal
        val geocode =
            Geocode(
                location?.latitude ?: 45.5017, location?.longitude ?: 73.5673,
                radius, Geocode.Distance.KILOMETERS
            )
        try {
            val response = TwitterCore.getInstance().apiClient.searchService.tweets(
                "#food", geocode, null,
                null, null, 100, null, null, null, null
            ).execute()

            if (response.isSuccessful) {
                val tweets = response.body()?.tweets ?: Collections.emptyList()
                postUpdate(radius, tweets)
            } else
                postUpdate(radius, null)

        } catch (e: Exception) {
            postUpdate(radius, null)
        }

    }

    private fun postUpdate(radius: Int, tweets: List<Tweet>?) {
        CacheRepo.putCachedTweets(radius, tweets)
        this.tweets.postValue(tweets)
        //tweetsRequested = false
    }

    override fun onCleared() {
        backThread.shutdown() // stop updates
        runnable?.let { handler.removeCallbacks(runnable!!) }
        super.onCleared()
    }

    private fun getRemainingTimeUntilNextRequest() =
        lastRequestTimeFrame - System.currentTimeMillis() + timeFrameBetweenRequestsInMilliseconds

    private fun updateTimeFrame() {
        lastRequestTimeFrame = System.currentTimeMillis()
    }
}