package com.bell.demo.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bell.demo.R
import com.bell.demo.ui.search.SearchActivity
import com.bell.demo.utils.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Coordinates
import com.twitter.sdk.android.core.models.Search
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.params.Geocode
import kotlinx.android.synthetic.main.map_tweet.view.*
import java.util.*

class TweetsMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, TweetsMapActivity::class.java))
        }
    }

    private lateinit var mMap: GoogleMap
    private var mapReady = false
    private var queue: List<Tweet> = Collections.emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweets_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // load tweets
        val geocode = Geocode(45.5017, 73.5673, 9995, Geocode.Distance.KILOMETERS)
        TwitterCore.getInstance().apiClient.searchService.tweets(
            "#food", geocode, null,
            null, null, 100, null, null, null, null
        ).enqueue(object : Callback<Search>() {
            override fun success(result: Result<Search>?) {
                Log.d("request", result?.data?.tweets?.size.toString())
                queue = result?.data?.tweets ?: Collections.emptyList()
                displayTweets()
            }

            override fun failure(exception: TwitterException?) {
                Log.e("request", exception?.cause?.toString() ?: "")
            }

        })
    }

    private fun displayTweets() {
        if (queue.isEmpty() || !mapReady) {
            return
        }
        queue.forEachIndexed {i, it -> run {
            val pos: LatLng? = it.coordinates?.let { it2 -> LatLng(it2.latitude, it2.longitude) }
            /// if coordinate is null check place https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/geo-objects.html
                ?: run {
                    it.place?.boundingBox?.coordinates?.let { it2 ->
                        if (it2.isNotEmpty() && it2[0].isNotEmpty()) LatLng(
                            it2[0][0][Coordinates.INDEX_LATITUDE],
                            it2[0][0][Coordinates.INDEX_LONGITUDE]
                        )
                        else null
                    } ?: run { null }
                }

            // if we have a non null position add marker
            pos?.let { it2 ->
                val text = "${it.user.profileImageUrlHttps}~${it.createdAt}~${it.text}~$i"
                mMap.addMarker(MarkerOptions().position(it2).title(it.user.name).snippet(text))
            }
        }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapReady = true

        // move the camera to current position
        val currentLocation = LatLng(45.5017, 73.5673)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        //val location = CameraUpdateFactory.newLatLngZoom(currentLocation, 12.0f)

        val adapter = InfoAdapter(this)
        mMap.setInfoWindowAdapter(adapter)
        mMap.setOnInfoWindowClickListener(adapter)
//        mMap.animateCamera(location)
        mMap.isMyLocationEnabled = true

        displayTweets()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Current location:\n" + p0, Toast.LENGTH_LONG).show()
    }

    class InfoAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {
        override fun onInfoWindowClick(p0: Marker?) {
            SearchActivity.launch(context)
        }

        override fun getInfoContents(marker: Marker): View {
            val v = LayoutInflater.from(context).inflate(R.layout.map_tweet, null)

            val text = marker.snippet.split("~")

            val photoUrl = text[0]
            val timestamp = text[1]
            val tweet = text[2]

            Picasso.with(context)
                .load(photoUrl)
                .into(v.avatar)

            v.timestamp.text = Utils.formatTime(timestamp)
            v.name.text = marker.title
            v.tweet.text = tweet

            return v
        }



        override fun getInfoWindow(p0: Marker?) = null
    }
}
