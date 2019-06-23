package com.bell.demo

import android.app.Application
import android.util.Log
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig

class MyDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // init twitter core, juste une fois
        initTwitter()
    }

    private fun initTwitter() {
        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
            .twitterAuthConfig(
                TwitterAuthConfig(
                    BuildConfig.TWITTER_CONSUMER_KEY,
                    BuildConfig.TWITTER_CONSUMER_SECRET
                )
            )
            //pass the created app Consumer KEY and Secret also called API Key and Secret
            .debug(true)//enable debug mode
            .build()

        //finally initialize twitter with created configs
        Twitter.initialize(config)
    }
}