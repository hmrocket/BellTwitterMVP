package com.bell.demo.ui.search.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bell.demo.R
import com.bell.demo.ui.common.interector.InteractionListener
import com.bell.demo.utils.loadUrl
import com.twitter.sdk.android.core.models.TweetEntities
import kotlinx.android.synthetic.main.photo.view.*


class ImagesAdapter(private val mediaEntities: TweetEntities,
                    private val listener: InteractionListener
) :
        RecyclerView.Adapter<ImagesAdapter.VHItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VHItem(LayoutInflater.from(parent.context).inflate(R.layout.photo, parent, false))

    override fun onBindViewHolder(holder: VHItem, position: Int) {
        holder.tweetPhotoImageView.loadUrl(mediaEntities.media[position].mediaUrl)
        holder.tweetPhotoImageView.setOnClickListener {
            listener.showImages(mediaEntities.media.map { mediaEntity -> mediaEntity.mediaUrl }, position)
        }
    }

    override fun getItemCount() = mediaEntities.media.size

    class VHItem(container: View) : RecyclerView.ViewHolder(container) {
        val tweetPhotoImageView: ImageView = container.tweetPhotoImageView
    }

}
