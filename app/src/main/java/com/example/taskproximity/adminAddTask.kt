package com.example.taskproximity


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore

class adminAddTask : AppCompatActivity() {
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var etDescription: EditText
    private lateinit var btnLocation: Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitudeString: String = ""
    private var longitudeString: String = ""
    private var statusString: String = "active"
    private var descriptionString: String = ""
    private var taskId: String = ""
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_task)

        tvLatitude = findViewById(R.id.tv_latitude)
        tvLongitude = findViewById(R.id.tv_longitude)
        etDescription = findViewById(R.id.etDescription)
        btnLocation = findViewById(R.id.btn_location)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnLocation.setOnClickListener {
            checkLocationPermission()
            saveDescription()
            generateRandomTaskId()
            //createTask()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        } else {
            getLastKnownLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    latitudeString = latitude.toString()
                    longitudeString = longitude.toString()
                    tvLatitude.text = latitudeString
                    tvLongitude.text = longitudeString
                    createTask(latitude, longitude)
                }
            }
    }

    private fun saveDescription() {
        descriptionString = etDescription.text.toString()
    }

    private fun generateRandomTaskId() {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        taskId = (1..10)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")

        println("Random Task ID: $taskId")
    }

    private fun createTask(latitude: Double, longitude: Double) {
        val task = Task(taskId, latitude, longitude, statusString, descriptionString)
        saveTaskToFirestore(task)
    }

    private fun saveTaskToFirestore(task: Task) {
        firestore.collection("tasks")
            .add(task)
            .addOnSuccessListener { documentReference ->
                // Task saved successfully
                Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                // Error occurred while saving the task
                Toast.makeText(this, "Error adding task: $e", Toast.LENGTH_SHORT).show()
            }
    }



    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100
    }
}

data class Task(
    val taskId: String,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val description: String
)