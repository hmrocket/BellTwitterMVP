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
import com.bell.demo.R
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Search
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.params.Geocode
import com.twitter.sdk.android.tweetui.SearchTimeline
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {

    companion object {
        fun launch(contecxt: Context) {
            contecxt.startActivity(Intent(contecxt, SearchActivity::class.java))
        }
    }

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        recyclerView = findViewById(R.id.tweetsRecyclerView)

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

        recyclerView.layoutManager = LinearLayoutManager(this)

        val searchService = TwitterCore.getInstance().apiClient.searchService
        val montreal = Geocode(45.5017, 73.5673, 100, null)
        searchService.tweets(query, montreal, "en", "ca", "tweet", 100, null, null, null, true)
            .enqueue(object : Callback<Search>() {
                override fun success(result: Result<Search>) {
                    //Do something with result
                    result.data.tweets.forEach { action: Tweet ->
                        run {
                            Log.d("tweet", action.text)
                        }
                    }
                }

                override fun failure(exception: TwitterException) {
                    //Do something on failure
                    Log.e("tweet search", "failed request")
                }
            })


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

}
