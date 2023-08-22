package com.example.remoteconfigsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings


class MainActivity : AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Firebase.initialize(this)
        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        fetchAndActivateRemoteConfigValues()
    }

    private fun fetchAndActivateRemoteConfigValues() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val isPhotoHidden = remoteConfig.getBoolean("isPhotoHidden")
                    Log.e("aaaaa", isPhotoHidden.toString())

                    if (isPhotoHidden) {
                        val imageView = findViewById<ImageView>(R.id.imageView)
                        imageView.visibility = View.GONE
                    } else {
                        val imageView = findViewById<ImageView>(R.id.imageView)
                        imageView.visibility = View.VISIBLE
                    }

                    val message = remoteConfig.getString("message")
                    val tvMessage = findViewById<TextView>(R.id.tvMessage)
                    Log.e("welcome text", message.toString())
                    tvMessage.text = message

                    val year = remoteConfig.getLong("year")
                    val tvYear = findViewById<TextView>(R.id.tvYear)
                    Log.e("year text", year.toString())
                    tvYear.text = year.toString()

                }
            }
    }
}