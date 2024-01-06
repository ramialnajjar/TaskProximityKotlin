package com.example.taskproximity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class editTask : AppCompatActivity() {

    private lateinit var editTaskLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        editTaskLayout = findViewById(R.id.editTaskLayout)

        // Retrieve data from Firestore and populate the LinearLayout with buttons for each document
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("tasks")

        collectionRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val description = document.getString("description")
                    val latitude = document.getDouble("latitude")
                    val longitude = document.getDouble("longitude")
                    val status = document.getString("status")
                    val taskId = document.id // Use document ID instead of getString("taskId")

                    // Create a new button for each document and set the field values
                    val editTaskButton = Button(this)
                    editTaskButton.text = "Description: $description\n" +
                            "Latitude: $latitude\n" +
                            "Longitude: $longitude\n" +
                            "Status: $status\n" +
                            "Task ID: $taskId\n"

                    // Set button layout parameters
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.setMargins(0, 0, 0, 16) // Optional: Add some margin between each button
                    editTaskButton.layoutParams = layoutParams

                    // Add a click listener to the button
                    editTaskButton.setOnClickListener {
                        val intent = Intent(this, TaskEditDescActivity::class.java)
                        intent.putExtra("taskId", taskId)
                        intent.putExtra("description", description)
                        intent.putExtra("latitude", latitude)
                        intent.putExtra("longitude", longitude)
                        intent.putExtra("status", status)
                        startActivity(intent)
                    }

                    // Add the new button to the tasksLayout LinearLayout
                    editTaskLayout.addView(editTaskButton)
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}