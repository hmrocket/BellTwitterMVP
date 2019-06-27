package com.bell.demo.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bell.demo.R
import com.bell.demo.model.TweetType
import com.bell.demo.repo.AppConfig
import com.bell.demo.utils.Utils
import com.bell.demo.utils.visible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Coordinates
import com.twitter.sdk.android.core.models.Search
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.params.Geocode
import kotlinx.android.synthetic.main.activity_tweets_map.*
import kotlinx.android.synthetic.main.map_tweet.view.*
import java.util.*
import kotlin.math.ln
import kotlin.math.roundToInt

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
    private var location: Location? = null
    private val appConfig by lazy { AppConfig(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweets_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // get location
        getCurrentLocation()

        fetchGeoTweets(appConfig.radius)
    }

    private fun fetchGeoTweets(radius: Int) {
        // load tweets if location is null take montreal
        val geocode =
            Geocode(
                location?.latitude ?: 45.5017, location?.longitude ?: 73.5673,
                radius, Geocode.Distance.KILOMETERS
            )
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (R.id.action_radius == item.itemId) {
            seekBar_radius.visible(!seekBar_radius.isVisible) // toggle visibility
            true
        } else
            super.onOptionsItemSelected(item)
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

    private fun displayTweets() {
        if (queue.isEmpty() || !mapReady) {
            return
        }
        // remove all old markers
        mMap.clear()
        addCircle()


        queue.forEachIndexed { i, it ->
            run {
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
                    val text = InfoAdapter.encodeTweetInfo(it)
                    mMap.addMarker(MarkerOptions().position(it2).title(it.user.name).snippet(text))
                }
            }
        }

        seekBar_radius.visible(false)
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
        val currentLocation =
            location?.let { LatLng(location!!.latitude, location!!.longitude) } ?: LatLng(45.5017, 73.5673)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        //val location = CameraUpdateFactory.newLatLngZoom(currentLocation, 12.0f)

        val adapter = InfoAdapter(this)
        mMap.setInfoWindowAdapter(adapter)
        mMap.setOnInfoWindowClickListener(adapter)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12.0f))
        mMap.isMyLocationEnabled = true

        displayTweets()

        addCircle()

    }

    private fun addCircle() {
        val currentLocation = location?.let { LatLng(location!!.latitude, location!!.longitude) }
            ?: LatLng(45.5017, 73.5673)

        val circle: Circle = mMap.addCircle(
            CircleOptions()
                .center(currentLocation)
                .radius(appConfig.radius.toDouble()) // Converting Miles into Meters...
                .strokeColor(getColor(R.color.colorAccent))
                .strokeWidth(2f)
        )

        seekBar_radius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                circle.radius = p1.toDouble()
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, getZoomLevel(circle)))
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                circle.isVisible = true
            }

            override fun onStopTrackingTouch(p0: SeekBar) {
                // circle.isVisible = false
                fetchGeoTweets((circle.radius / 1000).toInt())
                appConfig.radius = p0.progress
            }

        })
    }

    // Credit: https://stackoverflow.com/questions/39386140/zooming-google-map-to-specific-radius-in-miles-in-android
    private fun getZoomLevel(circle: Circle?): Float {
        if (circle == null) return 0.5f

        val radius = circle.radius
        val scale = radius / 500
        val zoomLevel = (16 - ln(scale) / ln(2.0)).roundToInt().toFloat()
        return zoomLevel - 0.5f
    }


    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Current location:\n" + p0, Toast.LENGTH_LONG).show()
    }

    class InfoAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

        companion object {
            fun parseTweetInfo(marker: Marker): List<String> = marker.snippet.split("~")
            fun encodeTweetInfo(it: Tweet): String =
                "${it.user.profileImageUrlHttps}~${it.createdAt}~${it.text}~${it.id}~${TweetType.getType(it)}"

            const val INDEX_PHOTO_URL = 0
            const val INDEX_TIME = 1
            const val INDEX_TWEET_TEXT = 2
            const val INDEX_TWEET_ID = 3
            const val INDEX_TWEET_TYPE = 4
        }

        override fun onInfoWindowClick(p0: Marker) {
            val text = parseTweetInfo(p0)
            val id = text[INDEX_TWEET_ID].toLong()
            val type = text[INDEX_TWEET_TYPE]

            TweetActivity.launch(context, id, TweetType.valueOf(type))
        }

        override fun getInfoContents(marker: Marker): View {
            val v = LayoutInflater.from(context).inflate(R.layout.map_tweet, null)

            val text = parseTweetInfo(marker)

            val photoUrl = text[INDEX_PHOTO_URL]
            val timestamp = text[INDEX_TIME]
            val tweet = text[INDEX_TWEET_TEXT]

            Picasso.get()
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
