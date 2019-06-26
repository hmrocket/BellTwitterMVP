package com.bell.demo.ui.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bell.demo.R
import com.bell.demo.utils.loadUrlWithoutPlaceholder


/**
 */
class ImageFragment : Fragment() {

    companion object {
        private val TAG_IMAGE = "image"

        fun newInstance(imageUrl: String): ImageFragment {
            val imageFragment = ImageFragment()
            val bundle = Bundle()
            bundle.putString(TAG_IMAGE, imageUrl)
            imageFragment.arguments = bundle
            return imageFragment
        }
    }

    private lateinit var imageUrl: String
//    private var photoViewAttacher: PhotoViewAttacher? = null
    private val imageActivity: ImageActivity by lazy { activity as ImageActivity }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        imageUrl = arguments?.getString(TAG_IMAGE)?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_image, container, false)
        val tweetImageView = rootView?.findViewById(R.id.tweetImageView) as ImageView

        tweetImageView.loadUrlWithoutPlaceholder(imageUrl)
//        tweetImageView.setOnTouchListener { _, _ ->
//            if (photoViewAttacher == null) {
//                photoViewAttacher = PhotoViewAttacher(tweetImageView)
//                (photoViewAttacher as PhotoViewAttacher).setOnPhotoTapListener { _, _, _ ->
//                    imageActivity.showToolbar()
//                    imageActivity.hideToolbarDelay()
//                }
//            }
//
//            true
//        }

        imageActivity.hideToolbarDelay()

        return rootView
    }

}