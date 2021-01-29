package com.example.desafiofirebase.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.desafiofirebase.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.internal.userAgent

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Login automático
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            callHome(user.uid)
        }

        buttonLogin.setOnClickListener {
            getDataForLogin()
        }

        buttonCreateAccount.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    //Verificação dos campos e tratamento dos dados
    fun getDataForLogin() {
        var email = userEmail.text.toString()
        var password = userPassword.text.toString()

        var emailVerification = email.isNotEmpty()
        var passwordVerification = password.isNotEmpty()

        if (emailVerification && passwordVerification) {
            login(email, password)
        } else {
            Toast.makeText(this, "There are empty fields", Toast.LENGTH_SHORT).show()
        }
    }

    //Login com Firebase
    fun login(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result?.user!!
                    val idUser = firebaseUser.uid

                    callHome(idUser)
                } else {
                    Toast.makeText(
                        this,
                        "${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
    }

    fun callHome(idUser: String) {
        var intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("userId", idUser)
        intent.putExtra("isNewUser", false)
        startActivity(intent)
    }
}