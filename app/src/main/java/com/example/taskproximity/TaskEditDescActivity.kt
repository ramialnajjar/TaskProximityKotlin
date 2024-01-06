package com.example.taskproximity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class TaskEditDescActivity : AppCompatActivity() {

    private lateinit var taskId: String
    private lateinit var descriptionEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_edit_desc)

        taskId = intent.getStringExtra("taskId") ?: ""
        val description = intent.getStringExtra("description") ?: ""

        descriptionEditText = findViewById(R.id.editTextDescription)
        descriptionEditText.setText(description)

        val confirmButton: Button = findViewById(R.id.buttonConfirm)
        confirmButton.setOnClickListener {
            val updatedDescription = descriptionEditText.text.toString()

            val db = FirebaseFirestore.getInstance()
            val taskRef = db.collection("tasks").document(taskId)

            taskRef.update("description", updatedDescription)
                .addOnSuccessListener {
                    Toast.makeText(this, "Description updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}