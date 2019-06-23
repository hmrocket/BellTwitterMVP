package com.bell.demo.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bell.demo.BuildConfig.TWITTER_CONSUMER_KEY
import com.bell.demo.BuildConfig.TWITTER_CONSUMER_SECRET
import com.bell.demo.R
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.sdk.android.tweetui.SearchTimeline
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {

    companion object {
        fun launch(contecxt : Context) {
            contecxt.startActivity(Intent(contecxt, SearchActivity::class.java))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null)
            initTwitter()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)?.actionView as SearchView

        searchItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(s: String) = false
        })

        return true

    }

    private fun search(query: String) {
        Log.d("search", "query = $query")

        val recyclerView: RecyclerView = findViewById(R.id.tweetsRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val searchTimeline = SearchTimeline.Builder()
            .query("#hiking")
            .maxItemsPerRequest(50)
            .build()

        val adapter = TweetTimelineRecyclerViewAdapter.Builder(this)
            .setTimeline(searchTimeline)
            .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
            .build()

        recyclerView.adapter = adapter

    }

    private fun initTwitter() {
        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
            .twitterAuthConfig(
                TwitterAuthConfig(
                    TWITTER_CONSUMER_KEY,
                    TWITTER_CONSUMER_SECRET
                )
            )
            //pass the created app Consumer KEY and Secret also called API Key and Secret
            .debug(true)//enable debug mode
            .build()

        //finally initialize twitter with created configs
        Twitter.initialize(config)
    }
}
