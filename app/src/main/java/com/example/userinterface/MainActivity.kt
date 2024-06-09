package com.example.userinterface

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.userinterface.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        println("on create running")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Toast.makeText(this@MainActivity, "Welcome Our Application", Toast.LENGTH_LONG).show()

    }

    fun nextPage(view : View) {
        val alert = AlertDialog.Builder(this@MainActivity)
        alert.setTitle("Are You sure ?")
        alert.setMessage("We can still continue. Aren't you.")
        println("pressed on next page")
        alert.setPositiveButton("Yes"
        ) { dialog, which ->
            Toast.makeText(this@MainActivity, "SUCCESS", Toast.LENGTH_LONG).show()
        }

        alert.setNegativeButton("No") { dialog, which ->
            Toast.makeText(this@MainActivity, "ERROR", Toast.LENGTH_LONG).show()
        }

        alert.setNeutralButton("Maybe") { dialog, which ->
            Toast.makeText(this@MainActivity, "Maybe Continue", Toast.LENGTH_LONG).show()
        }

//        val intent = Intent(this, SecondActivity::class.java)
//        val editText = binding.editTextText.text.toString()
//        intent.putExtra("name", editText)
//        startActivity(intent)
    }
}