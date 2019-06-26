package com.bell.demo.ui.video

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.bell.demo.R
import com.bell.demo.utils.visible
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity() {

    companion object {
        private const val TAG_URL = "video"
        private const val TAG_TYPE = "type"

        fun launch(context: Context, videoUrl: String, videoType: String) {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra(TAG_URL, videoUrl)
            intent.putExtra(TAG_TYPE, videoType)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        Log.d("Video", "url = ${intent.getStringExtra(TAG_URL)}")

        val mc = MediaController(this)
        mc.setAnchorView(videoView)
        mc.setMediaPlayer(videoView)

        videoView.setOnPreparedListener { loadingProgressBar.visible(false) }
        if ("animated_gif" == intent.getStringExtra(TAG_TYPE))
            videoView.setOnCompletionListener { videoView.start() }

        videoView.setMediaController(mc)
        videoView.setVideoURI(Uri.parse(intent.getStringExtra(TAG_URL)))
        videoView.requestFocus()
        videoView.start()
    }

}
