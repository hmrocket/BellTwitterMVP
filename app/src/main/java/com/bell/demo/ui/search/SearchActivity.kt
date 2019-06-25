package com.bell.demo.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bell.demo.R
import com.bell.demo.ui.LoginActivity
import com.bell.demo.utils.Utils
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


class SearchActivity : AppCompatActivity(), InteractionListener {

    companion object {
        fun launch(contecxt: Context) {
            contecxt.startActivity(Intent(contecxt, SearchActivity::class.java))
        }
    }

    private lateinit var adapter: SearchAdapter
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupRecyclerView()

        getCurrentLocation()

        search("#food")
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (!Utils.isLocationGranted(this)) {
            finish()
            return // location permission is needed
        }

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)

    }

    fun setupRecyclerView() {
        tweetsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SearchAdapter(this@SearchActivity)
        tweetsRecyclerView.adapter = adapter
        tweetsRecyclerView.setHasFixedSize(true)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)?.actionView as SearchView

        searchItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                search(query)
                supportActionBar?.title = query
                searchItem.onActionViewCollapsed()
                return true
            }

            override fun onQueryTextChange(s: String) = false
        })

        return true

    }

    private fun search(query: String) {
        Log.d("search", "query = $query")

        val searchService = TwitterCore.getInstance().apiClient.searchService
        val montreal = Geocode(
            location?.latitude ?: 45.5017, location?.longitude ?: 73.5673,
            100, Geocode.Distance.KILOMETERS
        )
        searchService.tweets(query, montreal, null, null, null, 100, null, null, null, null
//        val geocode =
//            Geocode(location?.latitude ?: 45.5017, location?.longitude ?: 73.5673,
//                9995, Geocode.Distance.KILOMETERS)
//        TwitterCore.getInstance().apiClient.searchService.tweets(
//            query, geocode, null,
//            null, null, 100, null, null, null, null
        )
            .enqueue(object : Callback<Search>() {
                override fun success(result: Result<Search>) {
                    adapter.tweets = result.data.tweets
                    adapter.notifyDataSetChanged()

                    //Do something with result
                    result.data.tweets.forEach { action: Tweet ->
                        run {
                            Log.d("tweet", action.text)
                        }
                    }
                }

                override fun failure(exception: TwitterException) {
                    //Do something on failure
                    Log.e("tweet search", "failed request ${exception.message}")
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


        // recyclerView.adapter = adapter

    }


    override fun favorite(tweet: Tweet) {
        if(TwitterCore.getInstance().sessionManager.activeSession == null)
            LoginActivity.launch(this)
        else
            TwitterCore.getInstance().apiClient.favoriteService.create(tweet.id, null)
    }

    override fun retweet(tweet: Tweet) {
        if(TwitterCore.getInstance().sessionManager.activeSession == null)
            LoginActivity.launch(this)
        else
            TwitterCore.getInstance().apiClient.statusesService.retweet(tweet.id, null)
    }

    override fun unfavorite(tweet: Tweet) {
        if(TwitterCore.getInstance().sessionManager.activeSession == null)
            LoginActivity.launch(this)
        else
            TwitterCore.getInstance().apiClient.favoriteService.destroy(tweet.id, null)
    }

    override fun unretweet(tweet: Tweet) {
        if(TwitterCore.getInstance().sessionManager.activeSession == null)
            LoginActivity.launch(this)
        else
            TwitterCore.getInstance().apiClient.statusesService.unretweet(tweet.id, null)
    }

    override fun openTweet(tweet: Tweet) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showImage(imageUrl: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showImages(imageUrls: List<String>, index: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showVideo(videoUrl: String, videoType: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
