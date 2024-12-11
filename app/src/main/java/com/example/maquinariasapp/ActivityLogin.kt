package com.example.maquinariasapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityLogin : AppCompatActivity() {
    lateinit var buttonLogin: Button
    lateinit var editUser: EditText
    lateinit var editPass: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        /*enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        buttonLogin = findViewById(R.id.buttonLogin)
        editUser = findViewById(R.id.editUser)
        editPass = findViewById(R.id.editPass)

        buttonLogin.setOnClickListener {
            if (editUser.text.toString() == "gustavo" && editPass.text.toString() == "12345678") {
                val intent = Intent(this, MainActivity::class.java)
                startActivityForResult(intent, 1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Reinicia los campos de texto
            editUser.text.clear()
            editPass.text.clear()
        }
    }
}