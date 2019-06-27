package com.bell.demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bell.demo.ui.map.TweetsMapActivity
import com.bell.demo.ui.search.SearchActivity
import com.bell.demo.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val RQ_LOCATION_PERMISSION = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButton()

        if (!Utils.isLocationGranted(this))
            askForLocationPermission()

    }

    private fun setupButton() {
        // we'll use one click listener for both button
        val listener: View.OnClickListener = View.OnClickListener {
            when {
                !Utils.isLocationGranted(this) -> askForLocationPermission()
                it.id == R.id.btn_a -> TweetsMapActivity.launch(this@MainActivity)
                else -> SearchActivity.launch(this@MainActivity)
            }
        }

        btn_a.setOnClickListener(listener)
        btn_b.setOnClickListener(listener)
    }


    private fun askForLocationPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            val builder = AlertDialog.Builder(this)
            builder
                .setMessage(getString(R.string.permission_rational_msg))
                .setTitle(getString(R.string.permission_rational_title))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        RQ_LOCATION_PERMISSION
                    )
                }

            val dialog = builder.create()
            dialog.show()

        }
    }


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
        // Location permission is needed, must explain why and ask again or direct the user to the setting
        Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
    }
}
