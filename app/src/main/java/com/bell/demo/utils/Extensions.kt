package com.bell.demo.utils

import android.net.Uri
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

fun ImageView.loadUri(uri: Uri?, @DrawableRes placeholder: Int = R.drawable.placeholder) {
    Picasso.with(context).load(uri).placeholder(placeholder).into(this)
}

fun ImageView.loadUrl(url: String?, @DrawableRes placeholder: Int = R.drawable.placeholder) {
    Picasso.with(context).load(url).placeholder(placeholder).into(this)
}

fun ImageView.loadUrlCenterCrop(url: String?, @DrawableRes placeholder: Int = R.drawable.placeholder) {
    Picasso.with(context).load(url).placeholder(placeholder).centerCrop().into(this)
}

//////// Tweet
fun Tweet.hasSingleImage(): Boolean {
    extendedEntities?.media?.size?.let { return it == 1 && extendedEntities.media[0].videoInfo == null }
    return false
}

fun Tweet.hasSingleVideo(): Boolean {
    extendedEntities?.media?.size?.let { return it == 1 && extendedEntities.media[0].videoInfo != null }
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
        return entities.media[0]?.mediaUrl ?: ""
    throw RuntimeException("This Tweet does not have a video")
}

fun Tweet.getVideoUrlType(): Pair<String, String> {
    if (hasSingleVideo() || hasMultipleMedia()) {
        val mediaEntities =  entities.media
        return Pair(mediaEntities[0].mediaUrl, mediaEntities[0].type)
    }
    throw RuntimeException("This Tweet does not have a video")
}