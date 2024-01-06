package com.example.taskproximity


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button1 = findViewById<Button>(R.id.loginButton)
        val button2 = findViewById<Button>(R.id.signupButton)
        button1.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener{
            val intent2 = Intent(this, SignUp::class.java)
            startActivity(intent2)
        }

    }
}