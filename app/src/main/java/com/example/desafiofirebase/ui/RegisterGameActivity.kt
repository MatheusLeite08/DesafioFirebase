package com.example.desafiofirebase.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.desafiofirebase.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_register_game.*

class RegisterGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_game)

        buttonRegisterGameImage.setOnClickListener {
            Toast.makeText(this, "Clicou", Toast.LENGTH_SHORT).show()
            Picasso.get().load(R.drawable.splash_img).into(buttonRegisterGameImage)
        }
    }
}