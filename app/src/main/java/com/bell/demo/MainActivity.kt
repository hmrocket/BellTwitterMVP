package com.bell.demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bell.demo.ui.TweetsMapActivity
import com.bell.demo.ui.search.SearchActivity

class MainActivity : AppCompatActivity() {

    private val RQ_LOCATION_PERMISSION = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButton()

        if (isLocationGranted())
            askForLocationPermission()

        SearchActivity.launch(this)
    }

    private fun setupButton() {
        // we'll use one click listener for both button
        val listener: View.OnClickListener = View.OnClickListener {
            when {
                !isLocationGranted() -> askForLocationPermission()
                it.id == R.id.btn_a -> TweetsMapActivity.launch(this@MainActivity)
                else -> SearchActivity.launch(this@MainActivity)
            }
        }

        findViewById<Button>(R.id.btn_a).setOnClickListener(listener)
        findViewById<Button>(R.id.btn_b).setOnClickListener(listener)
    }

    private fun askForLocationPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            RQ_LOCATION_PERMISSION
        )
    }

    private fun isLocationGranted() =
        ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RQ_LOCATION_PERMISSION -> {
                if ((grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED))
                    permissionNotGranted()
                return
            }
        }
    }

    private fun permissionNotGranted() {
        // Location permission is needed, must explain why and ask again or direct the user to the setting, or else exit the app
        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
    }
}
