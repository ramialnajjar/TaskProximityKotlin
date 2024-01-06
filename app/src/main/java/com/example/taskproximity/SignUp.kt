package com.example.taskproximity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskproximity.R
import com.example.taskproximity.databinding.ActivitySignUpBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        fun signup(email: String, password: String) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user: FirebaseUser? = firebaseAuth.currentUser
                        Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
                        // Proceed to the next activity or perform other actions
                    } else {
                        Toast.makeText(this, "Signup failed! Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.button.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val user = hashMapOf(
                    "email" to username,
                    "password" to password
                )

                usersCollection.add(user)
                    .addOnSuccessListener {
                        // User data added successfully
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        // Error adding user data
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                firebaseAuth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, Login::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Empty Fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }

        //button2
        binding.button2.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val user = hashMapOf(
                    "email" to username,
                    "password" to password
                )

                usersCollection.add(user)
                    .addOnSuccessListener {
                        // User data added successfully
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        // Error adding user data
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                firebaseAuth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, Login::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Empty Fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }

    }
}