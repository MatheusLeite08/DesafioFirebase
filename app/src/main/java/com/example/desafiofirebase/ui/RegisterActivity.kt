package com.example.desafiofirebase.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.desafiofirebase.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        buttonSave.setOnClickListener {
            getDataForRegister()
        }
    }

    //Verificação dos campos e tratamento dos dados
    fun getDataForRegister() {
        var name = registerUserName.text.toString()
        var email = registerUserEmail.text.toString()
        var password = registerUserPassword.text.toString()
        var repeatePassword = repeateUserPassword.text.toString()

        var nameVerification = name.isNotEmpty()
        var emailVerification = email.isNotEmpty()
        var passwordVerification = password.isNotEmpty()
        var repeatePasswordVerification = repeatePassword.isNotEmpty()

        if (nameVerification && emailVerification && passwordVerification && repeatePasswordVerification) {
            if (password != repeatePassword) {
                Toast.makeText(this, "Different passwords", Toast.LENGTH_SHORT).show()
            } else {
                registerNewUser(name, email, password)
            }
        } else {
            Toast.makeText(this, "There are empty fields", Toast.LENGTH_SHORT).show()
        }
    }

    //Cadastro de novos usuários no Firebase
    fun registerNewUser(name: String, email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result?.user!!
                    val idUser = firebaseUser.uid
                    Toast.makeText(this, "Successful registration", Toast.LENGTH_SHORT).show()
                    callHome(idUser, name)
                } else {
                    Toast.makeText(
                        this,
                        "${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
    }

    fun callHome(idUser: String, username: String) {
        var intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("userId", idUser)
        intent.putExtra("isNewUser", true)
        intent.putExtra("username", username)
        startActivity(intent)
    }
}