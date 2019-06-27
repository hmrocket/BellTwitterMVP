package com.bell.demo.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bell.demo.R
import com.bell.demo.repo.AppConfig
import com.bell.demo.repo.CacheRepo
import com.bell.demo.ui.common.BaseTweetActivity
import com.bell.demo.utils.Utils
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Search
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.params.Geocode
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*


class SearchActivity : BaseTweetActivity() {

    companion object {
        fun launch(contecxt: Context) {
            contecxt.startActivity(Intent(contecxt, SearchActivity::class.java))
        }
    }

    private val radius: Int by lazy { AppConfig(this).radius }
    private lateinit var adapter: SearchAdapter
    private var location: Location? = null
    private var searchQuery: String = "#food"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupRecyclerView()

        getCurrentLocation()

        search()
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
        val layoutManager = LinearLayoutManager(this)
        tweetsRecyclerView.layoutManager = layoutManager
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

    private fun search(query: String = searchQuery) {
        Log.d("search", "query = $query")
        val currentOrMontreal = Geocode(
            location?.latitude ?: 45.5017, location?.longitude ?: 73.5673,
            radius, Geocode.Distance.KILOMETERS
        )

        CacheRepo.getCachedSearch(query, currentOrMontreal)?.let {
            displayTweetsSearch(it)
            return
        }

        val searchService = TwitterCore.getInstance().apiClient.searchService
        searchService.tweets(
            query, currentOrMontreal, null, null, null, 100, null, null, null, true
        )
            .enqueue(object : Callback<Search>() {
                override fun success(result: Result<Search>) {
                    displayTweetsSearch(result.data.tweets)
                    CacheRepo.putCachedSearch(query, currentOrMontreal, result.data.tweets)
                }

                override fun failure(exception: TwitterException) {
                    Log.e("tweet search", "failed request ${exception.message}")
                }
            })


    }

    private fun displayTweetsSearch(it: List<Tweet>) {
        adapter.tweets = ArrayList(it)
        adapter.notifyDataSetChanged()
    }


    override fun postInteractionSuccessful(tweetBefore: Tweet, tweetAfter: Tweet) {

        val index = adapter.tweets.asSequence()
            .indexOfFirst { it == tweetBefore || (it.retweeted && it.retweetedStatus == it)}

        if (index != -1) {
            adapter.tweets.removeAt(index)
            adapter.tweets.add(index, tweetAfter)
            // notify just i
            adapter.notifyItemChanged(index)
        }
    }
}
