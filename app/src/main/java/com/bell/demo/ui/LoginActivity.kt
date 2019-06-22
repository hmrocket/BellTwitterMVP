package com.bell.demo.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bell.demo.R
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterLoginButton

class LoginActivity : AppCompatActivity() {


    private val CONSUMER_KEY = "kOqfHbu1ADfc8cJ10RhKYyco2"
    private val CONSUMER_SECRET = "8yLEkdsipFvZTw9gy6HeVTLHm3FbiTpMNPRB1jHXv2DM15yKpB"

    private lateinit var twitterLoginButton: TwitterLoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
            .twitterAuthConfig(
                TwitterAuthConfig(
                    CONSUMER_KEY,
                    CONSUMER_SECRET
                )
            )
            //pass the created app Consumer KEY and Secret also called API Key and Secret
            .debug(true)//enable debug mode
            .build()

        //finally initialize twitter with created configs
        Twitter.initialize(config)

        setContentView(R.layout.activity_login)
        twitterLoginButton = findViewById(R.id.btn_twitterLogin)

        twitterLoginButton.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                // go the main activity
                // startActivity(MainActivity)
                TwitterCore.getInstance().sessionManager.clearActiveSession()
            }

            override fun failure(e: TwitterException) {
                Toast.makeText(this@LoginActivity, R.string.authentication_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
