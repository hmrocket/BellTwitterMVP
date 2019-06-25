package com.bell.demo.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bell.demo.R
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterLoginButton

class LoginActivity : AppCompatActivity() {


    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    private lateinit var twitterLoginButton: TwitterLoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result to the login button.
        twitterLoginButton.onActivityResult(requestCode, resultCode, data)
    }
}
