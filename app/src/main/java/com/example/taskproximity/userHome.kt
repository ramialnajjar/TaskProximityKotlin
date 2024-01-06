package com.example.taskproximity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import com.google.firebase.firestore.FirebaseFirestore
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class userHome : AppCompatActivity() {

    val dataMap = mutableMapOf<String?, MutableSet<Pair<Double?, Double?>>>()

    var NotifyDist = 200.0

    private lateinit var tasksLayout: LinearLayout

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        tasksLayout = findViewById(R.id.tasksLayout)


        latitudeTv = findViewById(R.id.tv_lat)
        longitudeTv = findViewById(R.id.tv_long)
        startStopBtn = findViewById(R.id.btn_start_stop)
        startStopBtn.setOnClickListener {
            startStop()
        }



        // Retrieve data from Firestore and populate the LinearLayout with buttons for each document
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("tasks")

        val distBtn = findViewById<Button>(R.id.distanceConfitmBtn)

        distBtn.setOnClickListener{
            val Edit = findViewById<EditText>(R.id.editTextNumberDecimal)
            val inputText = Edit.text.toString()
            NotifyDist = inputText.toDouble()
        }


        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val description = document.getString("description")
                    val latitude = document.getDouble("latitude")
                    val longitude = document.getDouble("longitude")
                    val status = document.getString("status")
                    val taskId = document.getString("taskId")

                    val coordinatePair = Pair(latitude, longitude)

                    val coordinateSet = dataMap.getOrDefault(taskId, mutableSetOf())
                    coordinateSet.add(coordinatePair)

                    dataMap[taskId] = coordinateSet

                    // Create a new button for each document and set the field values
                    val taskButton = Button(this)
                    taskButton.text = "Description: $description\n" +
                            "Latitude: $latitude\n" +
                            "Longitude: $longitude\n" +
                            "Status: $status\n" +
                            "Task ID: $taskId\n"

                    // Set button layout parameters
                    val layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.setMargins(0, 0, 0, 16) // Optional: Add some margin between each button
                    taskButton.layoutParams = layoutParams

                    // Add a click listener to the button takes you to details of task
                    taskButton.setOnClickListener { //for now it copies to clip board, where u can use the long lat in google maps
                        // Copy latitude and longitude to clipboard
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                       val clip = ClipData.newPlainText("Coordinates", "Latitude: $latitude\nLongitude: $longitude")
                       clipboard.setPrimaryClip(clip)
//
//                        // Start the TaskDetailsActivity and pass the description as an extra
//                        val intent = Intent(this, taskDetails::class.java)
//                        intent.putExtra("description", description)
//                        intent.putExtra("latitude", latitude)
//                        intent.putExtra("longitude", longitude)
//                        intent.putExtra("status", status)
//                        intent.putExtra("taskId", taskId)
//                        startActivity(intent)
//
//                        // Show a toast indicating that the latitude and longitude are copied to the clipboard
//                        Toast.makeText(this, "Latitude and Longitude copied to clipboard", Toast.LENGTH_SHORT).show()


                    }

                    // Add the new button to the tasksLayout LinearLayout
                    tasksLayout.addView(taskButton)
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private lateinit var latitudeTv: TextView
    private lateinit var longitudeTv: TextView
    private lateinit var startStopBtn: Button

    private var tracker: LocationTracker = LocationTracker(
        minTimeBetweenUpdates = 500L, // 0.5 second
        minDistanceBetweenUpdates = 1F, // one meter
        shouldUseGPS = true,
        shouldUseNetwork = true,
        shouldUsePassive = true
    ).also {
        it.addListener(object : LocationTracker.Listener {
            @SuppressLint("SetTextI18n", "SuspiciousIndentation")
            override fun onLocationFound(location: Location) {
                val latT = location.latitude.toString()
                val lngT = location.longitude.toString()

                val lat = location.latitude
                val long = location.longitude

                val tLat = 4.0
                val tLong = 3.0

                dataMap.entries.forEach { entry ->
                    val taskId: String? = entry.key
                    val coordinateSet: Set<Pair<Double?, Double?>>? = entry.value

                    if (taskId != null && coordinateSet != null) {
                        coordinateSet.forEach { coordinatePair ->
                            val latitude: Double? = coordinatePair.first
                            val longitude: Double? = coordinatePair.second

                            if (latitude != null && longitude != null) {
                                // Perform operations with the taskId, latitude, and longitude
                                // For example, you can log or process the values here
                                val distance = calDistance(latitude,longitude,lat,long)

                                if(distance<=NotifyDist)
                                {
                                    runOnUiThread {
                                        //when you make toast, take description and id of task, add finish button under start/stop
                                        //when pressed -> changes status of task, and makes it done in db and in task in xml- Saleh
                                        Toast.makeText(applicationContext, "You are $distance away from the task", Toast.LENGTH_SHORT).show()

                                        // Play the sound file
                                        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.my_sound) // Replace "your_sound_file" with the actual name of your sound file
                                        mediaPlayer.start()
                                    }
                                }
                            }
                        }
                    }
                }



                //commented because they affect the correct distance that is displayed
                //val distance = calDistance(tLat, tLong, lat, long)


//                if(distance<=NotifyDist)
//                {
//                    Toast.makeText(applicationContext, "You are $distance away from the task", Toast.LENGTH_SHORT).show()
//
//                    // Play the sound file
//                    val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.my_sound) // Replace "your_sound_file" with the actual name of your sound file
//                    mediaPlayer.start()
//                }

                runOnUiThread {
                    latitudeTv.text = "Latitude: $latT"
                    longitudeTv.text = "Longitude: $lngT"
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
