package com.bell.demo.repo

import android.util.LruCache
import androidx.annotation.CheckResult
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.params.Geocode


@Suppress("UNCHECKED_CAST")
object CacheRepo {

    private fun generateRadiusKey(radius: Int) : String = "radius=$radius"
    private fun generateSearchKey(query: String, geocode: Geocode?) : String = "query=$query~$geocode"

    private val cache : LruCache<String, Any> = LruCache(2) // max to objects


    @CheckResult
    fun getCachedTweets(radius : Int) : List<Tweet>?
            = cache.get(generateRadiusKey(radius)) as? List<Tweet>
    fun putCachedTweets(radius : Int, list: List<Tweet>): Any = cache.put(generateRadiusKey(radius), list)

    @CheckResult
    fun getTweet(id: Long) = cache.get(id.toString()) as? Tweet
    fun putTweet(tweet: Tweet): Any = cache.put(tweet.id.toString(), tweet)

    fun getCachedSearch(query: String, geocode: Geocode?): List<Tweet>?
            = cache.get(generateSearchKey(query, geocode)) as? List<Tweet>

    fun putCachedSearch(query: String, geocode: Geocode?, tweets: List<Tweet>): Any = cache.put(generateSearchKey(query, geocode), tweets)
}