package com.example.remoteconfigsample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.remoteconfigsample.databinding.FragmentRemoteConfigBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class RemoteConfigFragment : Fragment() {

    private var _binding: FragmentRemoteConfigBinding? = null
    private val binding get() = _binding!!

    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRemoteConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        remoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0

            // Set your desired minimum fetch interval
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Set in-app default parameter values
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        // Fetch and activate values
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("RemoteConfigFragment", "Fetch and activate succeeded")

                    // Values fetched and activated
                    displayFetchedValues()
                } else {
                    // Fetch failed
                    Log.e("RemoteConfigFragment", "Fetch failed")
                }
                displayFetchedValues()
            }

        // Listen for real-time updates
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                // Updated keys
                val updatedKeys = configUpdate.updatedKeys
                Log.d("RemoteConfigFragment", "Updated keys: $updatedKeys")


                // Check if specific keys are updated (you can use your own conditions)
                if (updatedKeys.contains("isPhotoHidden") || updatedKeys.contains("message") || updatedKeys.contains(
                        "year"
                    )
                ) {
                    // Update UI based on updated values
                    Log.d("RemoteConfigFragment", "Update triggered for keys: $updatedKeys")
                    remoteConfig.activate().addOnCompleteListener {
                        displayFetchedValues()
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.e("RemoteConfigFragment", "Config update error: ${error.code}", error)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun displayFetchedValues() {
        val isPhotoHidden = remoteConfig.getBoolean("isPhotoHidden")
        val message = remoteConfig.getString("message")
        val year = remoteConfig.getLong("year")


        // Update UI components based on fetched values
        binding.imageView.visibility = if (isPhotoHidden) View.GONE else View.VISIBLE
        binding.tvMessage.text = message
        binding.tvYear.text = year.toString()
    }
}
