package com.bell.demo.utils

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bell.demo.R
import com.squareup.picasso.Picasso
import com.twitter.sdk.android.core.models.Tweet

////// Views
fun View.visible(show: Boolean = true) {
    this.visibility = if (show) View.VISIBLE else View.GONE
}

fun ImageView.loadUrlWithoutPlaceholder(url: String?) {
    Picasso.with(context).load(url).into(this)
}

fun ImageView.loadUrl(url: String?, @DrawableRes placeholder: Int = R.drawable.placeholder) {
    Picasso.with(context).load(url).placeholder(placeholder).into(this)
}

fun ImageView.loadUrlCenterCrop(url: String?, @DrawableRes placeholder: Int = R.drawable.placeholder) {
    Picasso.with(context).load(url).placeholder(placeholder).fit().centerCrop().into(this)
}

//////// Tweet
// Important docs about entities :
// https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/extended-entities-object
//  https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/entities-object
fun Tweet.hasSingleImage(): Boolean {
    extendedEntities?.media?.size?.let { return it == 1 && extendedEntities.media[0].type == "photo" }
    return false
}

fun Tweet.hasSingleVideo(): Boolean {
    extendedEntities?.media?.size?.let { return it == 1 && extendedEntities.media[0].type != "photo" }
    return false
}

fun Tweet.hasMultipleMedia(): Boolean {
    extendedEntities?.media?.size?.let { return it > 1 }.run { return false }
}

fun Tweet.hasQuotedStatus(): Boolean {
    return quotedStatus != null
}

fun Tweet.hasLinks() : Boolean {
    return extendedEntities?.urls?.isNotEmpty() ?: false
}

fun Tweet.getImageUrl(): String {
    if (hasSingleImage() || hasMultipleMedia())
        return entities.media[0]?.mediaUrl ?: ""
    throw RuntimeException("This Tweet does not have a photo")
}

fun Tweet.getVideoCoverUrl(): String {
    if (hasSingleVideo() || hasMultipleMedia())
        return entities.media[0]?.mediaUrlHttps ?: (entities.media[0]?.mediaUrl ?: "")
    throw RuntimeException("This Tweet does not have a video")
}

fun Tweet.getVideoUrlType(): Pair<String, String> {
    if (hasSingleVideo() || hasMultipleMedia()) {
        val variant =  extendedEntities.media[0].videoInfo.variants
        return Pair(variant[0].url, variant[0].contentType) // ideally we will pick a variant (i.e video quality) based on the screen size or user preferences
    }
    throw RuntimeException("This Tweet does not have a video")
}