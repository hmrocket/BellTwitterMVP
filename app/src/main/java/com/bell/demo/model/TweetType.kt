package com.bell.demo.model

import com.bell.demo.utils.*
import com.twitter.sdk.android.core.models.Tweet

enum class TweetType {
    BASIC, LINK, PHOTO, VIDEO, MULTIPLE_PHOTOS, QUOTE;

    companion object {
        fun getType(tweet: Tweet) = when {
            tweet.hasSingleImage() -> PHOTO
            tweet.hasSingleVideo() -> VIDEO
            tweet.hasMultipleMedia() -> MULTIPLE_PHOTOS
            tweet.hasQuotedStatus() -> QUOTE
            tweet.hasLinks() -> LINK
            else -> BASIC
        }
    }
}