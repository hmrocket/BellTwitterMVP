package com.bell.demo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bell.demo.ui.TweetsMapActivity

class MainActivity : AppCompatActivity() {

    private val RQ_LOCATION_PERMISSION = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), RQ_LOCATION_PERMISSION)

        startActivity(Intent(this, TweetsMapActivity::class.java))
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
        // Location permission is needed, must explain why and ask again or direct the user to the setting, or else exit the app
        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
    }
}
