package com.bell.demo.ui.search.holders

import android.view.View
import com.bell.demo.ui.search.InteractionListener
import com.twitter.sdk.android.core.models.Tweet

/**
 *
 */
class StatusLinkViewHolder(container: View, listener: InteractionListener) :
        StatusViewHolder(container, listener) {

//    private val urlPreviewImageView: ImageView = container.urlPreviewImageView
//    private val urlTitleTextView: TextView = container.urlTitleTextView
//    private val urlTextDescriptionView: TextView = container.urlDescriptionTextView
//    private val urlPreviewLayout = container.urlPreviewLayout
//    private val loadingProgressBar = container.loadingProgressBar
//    private var disposable: Disposable? = null

    override fun setup(tweet: Tweet) {
        super.setup(tweet)

//        setLoading(true)
//        if (tweet.metaData == null) {
//            urlPreviewLayout.setOnClickListener { }
//            disposable?.dispose()
//            disposable = UrlInfo.generatePreview(tweet.getLink())
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({
//                        run {
//                            tweet.metaData =
//                                    MetaData(if (it.images.isNotEmpty()) it.images[0].source
//                                    else null, it.title, it.description, tweet.getLink())
//                            loadPreview(tweet.metaData as MetaData)
//                        }
//                    }, { e -> Timber.e(e, "Error loading url preview") })
//        } else loadPreview(tweet.metaData as MetaData)
    }

//    private fun setLoading(isLoading: Boolean) {
//        loadingProgressBar.visible(isLoading)
//        urlPreviewImageView.visible(!isLoading)
//        urlTitleTextView.visible(!isLoading)
//        urlTextDescriptionView.visible(!isLoading)
//    }

//    private fun loadPreview(metaData: MetaData) {
//        setLoading(false)
//        if (metaData.imageUrl != null)
//            urlPreviewImageView.loadUrl(metaData.imageUrl)
//        else
//            urlPreviewImageView.visibility = View.GONE
//        urlTitleTextView.text = metaData.title
//        urlTextDescriptionView.text = metaData.description
//        urlPreviewLayout.setOnClickListener { openUrl(container.context as Activity, metaData.link) }
//    }
}