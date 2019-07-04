package com.bell.demo.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bell.demo.R
import com.bell.demo.repo.AppConfig
import com.bell.demo.repo.CacheRepo
import com.bell.demo.utils.Utils
import com.bell.demo.utils.visible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.twitter.sdk.android.core.models.Coordinates
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.activity_tweets_map.*
import kotlin.math.ln
import kotlin.math.roundToInt

class TweetsMapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, TweetsMapActivity::class.java))
        }
    }

    private lateinit var model: TweetsMapViewModel
    private lateinit var mMap: GoogleMap
    private var mapReady = false
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

        model = ViewModelProviders.of(this).get(TweetsMapViewModel::class.java)

        val observer = Observer<List<Tweet>?> { tweets ->
            // Update the UI
            tweets?.let {
                // cache and display, (it's good to have cache, specially when offline mode is not implemented, sometime the user press back intentionally and expect the content already there and not to wait again )
                CacheRepo.putCachedTweets(appConfig.radius, tweets)
                displayTweets()
            }

        }

        model.getObservedTweets().observe(this, observer)

        model.onViewInitialized(location, appConfig.radius)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (R.id.action_radius == item.itemId) {
            seekBar_radius.progress = appConfig.radius
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
        if (model.getObservedTweets().value.isNullOrEmpty() || !mapReady) {
            return
        }
        val queue = model.getObservedTweets().value!!
        // remove all old markers
        mMap.clear()
        addCircle()

        queue
            .map { Pair(it, getTweetLatLng(it)) }
            .filter { it.second != null}  // if we have a non null position add marker
            .forEach {
                val text = InfoAdapter.encodeTweetInfo(it.first)
                mMap.addMarker(MarkerOptions()
                    .position(it.second!!)
                    .title(it.first.user.name)
                    .snippet(text))
            }

        seekBar_radius.visible(false)
    }

    /**
     *  we can get location from two places from coordinates or place
     *  @see <a href="https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/geo-objects.html">API</a>
     */
    private fun getTweetLatLng(it: Tweet): LatLng? {
        return (it.coordinates?.let { it2 -> LatLng(it2.latitude, it2.longitude) }
        /// if coordinate is null check place https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/geo-objects.html
            ?: run {
                it.place?.boundingBox?.coordinates?.let { it2 ->
                    if (it2.isNotEmpty() && it2[0].isNotEmpty()) LatLng(
                        it2[0][0][Coordinates.INDEX_LATITUDE],
                        it2[0][0][Coordinates.INDEX_LONGITUDE]
                    )
                    else null
                } ?: run { null }
            })
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,
            getZoomLevel(appConfig.radius.toDouble() * 1000)))
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
                .radius(appConfig.radius.toDouble() * 1000) // Converting KM into Meters...
                .strokeColor(getColor(R.color.colorAccent))
                .strokeWidth(2f)
        )

        seekBar_radius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                circle.radius = p1.toDouble() + AppConfig.MIN_RADIUS * 1000
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, getZoomLevel(circle)))
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                circle.isVisible = true
            }

            override fun onStopTrackingTouch(p0: SeekBar) {
                // circle.isVisible = false
                model.fetchGeoTweets(location, (circle.radius / 1000).toInt())
                appConfig.radius = p0.progress + AppConfig.MIN_RADIUS.toInt()// ad
            }

        })
    }

    // Credit: https://stackoverflow.com/questions/39386140/zooming-google-map-to-specific-radius-in-miles-in-android
    private fun getZoomLevel(circle: Circle?): Float {
        if (circle == null) return 0.5f

        val radius = circle.radius
        return getZoomLevel(radius)
    }

    /**
     * im Meters not KM
     */
    private fun getZoomLevel(radius: Double): Float {
        val scale = radius / 500
        val zoomLevel = (16 - ln(scale) / ln(2.0)).roundToInt().toFloat()
        return zoomLevel + 0.5f
    }

}
