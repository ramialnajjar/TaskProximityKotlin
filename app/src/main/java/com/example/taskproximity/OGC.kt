package com.example.taskproximity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class OGC : AppCompatActivity() {

    private lateinit var latitudeTv: TextView
    private lateinit var longitudeTv: TextView
    private lateinit var startStopBtn: Button
    lateinit var listViewAdapter: ArrayAdapter<String>
    private var latLngStrings = ArrayList<String>()

    private var tracker: LocationTracker = LocationTracker(
        minTimeBetweenUpdates = 500L, // 0.5 second
        minDistanceBetweenUpdates = 1F, // one meter
        shouldUseGPS = true,
        shouldUseNetwork = true,
        shouldUsePassive = true
    ).also {
        it.addListener(object : LocationTracker.Listener {
            @SuppressLint("SetTextI18n")
            override fun onLocationFound(location: Location) {
                val latT = location.latitude.toString()
                val lngT = location.longitude.toString()

                val lat = location.latitude
                val long = location.longitude

                val tLat = 31.783089
                val tLong = 35.804489


                val distance = calDistance(tLat, tLong, lat, long)

                if(distance<=200)
                {
                Toast.makeText(applicationContext, "You are $distance away from the task", Toast.LENGTH_SHORT).show()
                listViewAdapter.add("$latT, $lngT")
                }

                runOnUiThread {
                    latitudeTv.text = latT
                    longitudeTv.text = lngT
                }
            }
        })
    }

    private fun calDistance(latT: Double, longT: Double, lat: Double, long: Double): Double {
        // Radius of the Earth in meters
        val R = 6371000.0

        // Convert coordinates from degrees to radians
        val lat1Rad = Math.toRadians(latT)
        val lng1Rad = Math.toRadians(longT)
        val lat2Rad = Math.toRadians(lat)
        val lng2Rad = Math.toRadians(long)

        // Calculate differences
        val dlat = lat2Rad - lat1Rad
        val dlng = lng2Rad - lng1Rad

        // Haversine formula
        val a = sin(dlat / 2) * sin(dlat / 2) +
                cos(lat1Rad) * cos(lat2Rad) * sin(dlng / 2) * sin(dlng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = R * c

        return distance
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ogc)
        latitudeTv = findViewById(R.id.tv_lat)
        longitudeTv = findViewById(R.id.tv_long)
        startStopBtn = findViewById(R.id.btn_start_stop)
        listViewAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, latLngStrings)
        // list_view.adapter = listViewAdapter

        startStopBtn.setOnClickListener {
            startStop()
        }
    }

    override fun onStart() {
        super.onStart()
        initTracker()
    }

    override fun onDestroy() {
        super.onDestroy()
        tracker.stopListening(clearListeners = true)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        initTracker()
    }

    private fun initTracker() {
        val hasFineLocation =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasFineLocation || !hasCoarseLocation) {
            return ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1337
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun startStop() {
        if (tracker.isListening) {
            startStopBtn.text = "Start"
            tracker.stopListening()
        } else {
            startStopBtn.text = "Stop"
            tracker.startListening(context = baseContext)
        }
    }
}
