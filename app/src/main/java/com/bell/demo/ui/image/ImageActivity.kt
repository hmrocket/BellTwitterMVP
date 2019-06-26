package com.bell.demo.ui.image

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bell.demo.R
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {

    companion object {
        private val TAG_IMAGES = "images"
        private val TAG_CURRENT_ITEM = "current_item"

        fun launch(context: Context, imageUrls: Array<String>) {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra(TAG_IMAGES, imageUrls)
            context.startActivity(intent)
        }

        fun launch(context: Context, imageUrls: Array<String>, index: Int) {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra(TAG_IMAGES, imageUrls)
                    .putExtra(TAG_CURRENT_ITEM, index)
            context.startActivity(intent)
        }
    }

    private lateinit var images: List<String>
    private var isToolbarVisible = true
    private val handler: Handler = Handler()
    private var hideToolbarRunnable = Runnable { hideToolbar() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        images = intent.getStringArrayExtra(TAG_IMAGES).asList()
        val currentItem = intent.getIntExtra(TAG_CURRENT_ITEM, 0)
        val toolbarTitle = if (images.size > 1)
            getString(R.string.pic_of_n, currentItem + 1, images.size) else ""
        toolbar.title = toolbarTitle
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        photosViewPager.adapter = ImageFragmentPagerAdapter(supportFragmentManager)
        photosViewPager.currentItem = currentItem
        photosViewPager.setPageTransformer(true, { view, position ->
            if (position < -1) {
                view.alpha = 0f
            } else if (position <= 0) {
                view.alpha = 1f
                view.translationX = 0f
                view.scaleX = 1f
                view.scaleY = 1f
            } else if (position <= 1) {
                view.alpha = 1 - position
                view.translationX = view.width * -position

                val scaleFactor = 0.75f + (1 - 0.75f) * (1 - Math.abs(position))
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
            } else view.alpha = 0f
        })

        photosViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (images.size > 1) {
                    supportActionBar?.title = getString(R.string.pic_of_n, position + 1, images.size)
                    showToolbar()
                    hideToolbarDelay()
                }
            }
        })
    }

    fun hideToolbarDelay() {
        handler.removeCallbacks(hideToolbarRunnable)
        handler.postDelayed(hideToolbarRunnable, 2000)
    }

    fun hideToolbar() {
        if (isToolbarVisible) {
            toolbar.animate().translationY(-toolbar.bottom.toFloat()).setInterpolator(AccelerateInterpolator()).start()
            isToolbarVisible = false
        }
    }

    fun showToolbar() {
        if (!isToolbarVisible) {
            toolbar.animate().translationY(0.toFloat()).setInterpolator(DecelerateInterpolator()).start()
            isToolbarVisible = true
        }
    }

    inner class ImageFragmentPagerAdapter(supportFragmentManager: FragmentManager) :
            FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int) = ImageFragment.newInstance(images[position])

        override fun getCount() = images.size
    }

}
