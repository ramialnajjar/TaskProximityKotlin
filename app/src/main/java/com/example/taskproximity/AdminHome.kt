package com.example.taskproximity


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AdminHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminhome)
        val button1 = findViewById<Button>(R.id.addTask)
        val button2 = findViewById<Button>(R.id.editTask)

        button1.setOnClickListener{
            val intent = Intent(this, adminAddTask::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener{
            val intent = Intent(this, editTask::class.java)
            startActivity(intent)
        }

    }
}