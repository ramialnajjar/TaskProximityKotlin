package com.example.taskproximity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.RequiresPermission


class LocationTracker constructor(
    val minTimeBetweenUpdates: Long =  5000.toLong(),
    val minDistanceBetweenUpdates: Float = 10f,
    val shouldUseGPS: Boolean = true,
    val shouldUseNetwork: Boolean = true,
    val shouldUsePassive: Boolean = true
) {
    // Android LocationManager
    private lateinit var locationManager: LocationManager

    // Last known location
    private var lastKnownLocation: Location? = null

    // Custom Listener for the LocationManager
    private val listener = object : LocationListener {
        override fun onLocationChanged(p0: Location) {
            Location(p0).let { currentLocation ->
                lastKnownLocation = currentLocation
                hasLocationFound = true
                listeners.forEach { l -> l.onLocationFound(currentLocation) }
            }
        }

        override fun onProviderDisabled(provider: String) {
            behaviorListener.forEach { l -> l.onProviderDisabled(provider) }
        }

        override fun onProviderEnabled(provider: String) {
            behaviorListener.forEach { l -> l.onProviderEnabled(provider) }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            behaviorListener.forEach { l -> l.onStatusChanged(provider, status, extras) }
        }

    }

    // List used to register the listeners to notify
    private val listeners: MutableSet<Listener> = mutableSetOf()


    // List used to register the behavior listeners to notify
    private val behaviorListener: MutableSet<BehaviorListener> = mutableSetOf()

    var isListening = false
        private set

    var hasLocationFound = false
        private set

    fun addListener(listener: Listener): Boolean = listeners.add(listener)

    fun removeListener(listener: Listener): Boolean = listeners.remove(listener)
    fun addBehaviorListener(listener: BehaviorListener): Boolean = behaviorListener.add(listener)
    fun removeBehaviorListener(listener: BehaviorListener): Boolean = behaviorListener.remove(listener)

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun startListening(context: Context) {
        initManagerAndUpdateLastKnownLocation(context)
        if (!isListening) {
            // Listen for GPS Updates
            if (shouldUseGPS) {
                registerForLocationUpdates(LocationManager.GPS_PROVIDER)
            }
            // Listen for Network Updates
            if (shouldUseNetwork) {
                registerForLocationUpdates(LocationManager.NETWORK_PROVIDER)
            }
            // Listen for Passive Updates
            if (shouldUseNetwork) {
                registerForLocationUpdates(LocationManager.PASSIVE_PROVIDER)
            }
            isListening = true
        }
    }

    fun stopListening(clearListeners: Boolean = false) {
        if (isListening) {
            locationManager.removeUpdates(listener)
            isListening = false
            if (clearListeners) {
                listeners.clear()
            }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun quickFix(@NonNull context: Context) {
        initManagerAndUpdateLastKnownLocation(context)
        lastKnownLocation?.let { lastLocation ->
            listeners.forEach { l -> l.onLocationFound(lastLocation) }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initManagerAndUpdateLastKnownLocation(context: Context) {
        // Init the manager
        locationManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getSystemService(LocationManager::class.java)
        } else {
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        // Update the lastKnownLocation
        if (lastKnownLocation == null && shouldUseGPS) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        if (lastKnownLocation == null && shouldUseNetwork) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        if (lastKnownLocation == null && shouldUsePassive) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        }
    }

    @SuppressLint("MissingPermission")
    private fun registerForLocationUpdates(provider: String) {
        if (locationManager.isProviderEnabled(provider)) {
            locationManager.requestLocationUpdates(provider, minTimeBetweenUpdates, minDistanceBetweenUpdates, listener)
        } else {
           //nothing
        }
    }

    interface Listener {
        fun onLocationFound(location: Location)
       //nothing fun onProviderError(providerError: ProviderError)
    }

    interface BehaviorListener {
        fun onProviderDisabled(provider: String)
        fun onProviderEnabled(provider: String)
        fun onStatusChanged(provider: String, status: Int, extras: Bundle)
    }

}
