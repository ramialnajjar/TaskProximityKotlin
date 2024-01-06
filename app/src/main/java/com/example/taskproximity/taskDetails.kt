package com.example.taskproximity



import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt


class taskDetails : AppCompatActivity() {

    private lateinit var descriptionTextView: TextView
    private lateinit var latitudeTextView: TextView
    private lateinit var longitudeTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var taskIdTextView: TextView
    lateinit var listViewAdapter: ArrayAdapter<String>
    var latitude: Double = 0.0
    var latitudeT: Double = 0.0
    var longitude: Double = 0.0
    var longitudeT: Double = 0.0

    private val CHANNEL_ID = "TASK_PROXIMITY_CHANNEL"

    private val locationUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            // Update the UI with the new location
            latitudeTextView.text = "Latitude: $latitude"
            longitudeTextView.text = "Longitude: $longitude"
            //checkTaskProximity(latitude, longitude)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        descriptionTextView = findViewById(R.id.descriptionTextView)
        latitudeTextView = findViewById(R.id.latitudeTextView)
        longitudeTextView = findViewById(R.id.longitudeTextView)
        statusTextView = findViewById(R.id.statusTextView)
        taskIdTextView = findViewById(R.id.taskIdTextView)

        // Start the location tracker service
        val serviceIntent = Intent(this, locationTrack::class.java)
        startService(serviceIntent)

        // Register the location update receiver
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(locationUpdateReceiver, IntentFilter("location_updated"))

        // Retrieve the task information from the intent extras
        val description = intent.getStringExtra("description")
        latitudeT = intent.getDoubleExtra("latitude", 0.0)
        longitudeT = intent.getDoubleExtra("longitude", 0.0)
        val status = intent.getStringExtra("status")
        val taskId = intent.getStringExtra("taskId")

        // Set the task information in the respective TextView elements
        descriptionTextView.text = "Description: $description"
        latitudeTextView.text = "Latitude: $latitudeT"
        longitudeTextView.text = "longitude: $longitudeT"
        statusTextView.text = "Status: $status"
        taskIdTextView.text = "Task Id: $taskId"

        // Create the notification channel
        createNotificationChannel()

        //push long and lat here

        val openActivityButton = findViewById<Button>(R.id.openActivityButton)
        openActivityButton.setOnClickListener {
            val intent = Intent(this, OGC::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the location tracker service
        val serviceIntent = Intent(this, locationTrack::class.java)
        stopService(serviceIntent)

        // Unregister the location update receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationUpdateReceiver)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Proximity Channel"
            val descriptionText = "Channel for Task Proximity Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
