package com.bell.demo.ui.map

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.bell.demo.R
import com.bell.demo.model.TweetType
import com.bell.demo.ui.TweetActivity
import com.bell.demo.utils.Utils
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.map_tweet.view.*

internal class InfoAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    companion object {
        fun parseTweetInfo(marker: Marker): List<String> = marker.snippet.split("~")
        fun encodeTweetInfo(it: Tweet): String =
            "${it.user.profileImageUrlHttps}~${it.createdAt}~${it.text}~${it.id}~${TweetType.getType(it)}"

        const val INDEX_PHOTO_URL = 0
        const val INDEX_TIME = 1
        const val INDEX_TWEET_TEXT = 2
        const val INDEX_TWEET_ID = 3
        const val INDEX_TWEET_TYPE = 4
    }

    override fun onInfoWindowClick(p0: Marker) {
        val text = parseTweetInfo(p0)
        val id = text[INDEX_TWEET_ID].toLong()
        val type = text[INDEX_TWEET_TYPE]

        TweetActivity.launch(context, id, TweetType.valueOf(type))
    }

    override fun getInfoContents(marker: Marker): View {
        val text = parseTweetInfo(marker)
        val photoUrl = text[INDEX_PHOTO_URL]
        val timestamp = text[INDEX_TIME]
        val tweet = text[INDEX_TWEET_TEXT]

        val v = LayoutInflater.from(context).inflate(R.layout.map_tweet, null)
        Picasso.get()
            .load(photoUrl)
            .into(v.avatar)

        v.timestamp.text = Utils.formatTime(timestamp)
        v.name.text = marker.title
        v.tweet.text = tweet

        return v
    }


    override fun getInfoWindow(p0: Marker?) = null
}