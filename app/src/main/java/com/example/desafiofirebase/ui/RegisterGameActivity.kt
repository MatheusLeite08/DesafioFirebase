package com.example.desafiofirebase.ui

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.desafiofirebase.R
import com.example.desafiofirebase.entities.Game
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_register_game.*
import kotlinx.android.synthetic.main.item_game.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterGameActivity : AppCompatActivity() {
    val scope = CoroutineScope(Dispatchers.Main)

    //Storage
    lateinit var alertDialog: AlertDialog
    lateinit var storageReference: StorageReference
    private val CODE_IMG = 1000
    private var authorizedShipping = false

    //Realtime Database
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    //Variáveis
    var isNewGame = false
    var userId = ""
    var gameId = ""
    var saveGame = false
    var urlImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_game)

        //Recepção dos dados
        val extras = intent.extras
        isNewGame = extras!!.getBoolean("isNew")
        userId = extras.getString("userId").toString()

        //Realtime Database ------------------------------------------------------------------------

        //Cada usuário tem um "nó" do banco de dados
        conectDatabase(userId)

        //Aqui é verificado se o usuário quer add um novo game ou atualizar um já existente
        if (isNewGame == true) {
            createNewGameScope()
        }

        //Salvar as informações do game (unica informação obrigatório é o Nome)
        buttonSaveGame.setOnClickListener {
            if (dataVerification()) {
                updateGameData()
                Toast.makeText(this, "Game saved successfully", Toast.LENGTH_SHORT).show()
            }
        }

        //Storage ----------------------------------------------------------------------------------

        config(gameId)

        iv_registerGameImage.setOnClickListener {
            getImg()
        }
    }

    fun config(gameId: String) {
        alertDialog = SpotsDialog.Builder().setContext(this).build()

        var imgId = "$userId/$gameId"

        Log.i("userId", userId)
        Log.i("gameId", gameId)
        Log.i("imagId", imgId)

        //Receber o id do game gerado pela criação da "reserva" no Realtime Database mais o userId
        //Essa junção de id's é feita para evitar conflitos e cada user ter uma pasta no Storage
        storageReference = FirebaseStorage.getInstance().getReference(imgId)
    }

    fun getImg() {
        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT //VER SE É ESSA ACTION MESMO
        startActivityForResult(Intent.createChooser(intent, "Captura Imagem"), CODE_IMG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODE_IMG) {
            alertDialog.show()
            var uploadFile = storageReference.putFile(data!!.data!!)
            var task = uploadFile.continueWithTask { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT)
                        .show()
                }
                storageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var downloadUri = task.result
                    urlImage = downloadUri!!.toString()
                        .substring(0, downloadUri.toString().indexOf("&token"))

                    showImage(urlImage)
                    Log.i("urlImage", urlImage)
                    alertDialog.dismiss()

                }
            }
        }
    }

    //Realtime Database

    fun conectDatabase(userId: String) {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference(userId) //Receber o id do usuário
    }

    fun createNewGameScope() {
        gameId = reference.push().key.toString()
        var gameScope = Game(gameId = gameId)

        FirebaseDatabase.getInstance().reference
            .child(userId)
            .child("games")
            .child(gameId)
            .setValue(gameScope)
    }

    fun updateGameData() {
        var updatedGame = Game(
            gameId,
            tv_registerGameCreated.text.toString(),
            tv_registerGameName.text.toString(),
            urlImage,
            tv_registerGameDescription.text.toString()
        )

        FirebaseDatabase.getInstance().reference
            .child(userId)
            .child("games")
            .child(gameId)
            .setValue(updatedGame)

        saveGame = true
    }

    fun deleteGame() {
        FirebaseDatabase.getInstance().reference
            .child(userId)
            .child("games")
            .child(gameId)
            .removeValue()
    }

    //Funções complementares

    fun dataVerification(): Boolean {
        var name = tv_registerGameName.text.toString()
        var yearCreation = tv_registerGameCreated.text.toString()

        var nameVerification = name.isNotEmpty()
        var yearCreationVerification = true

        if (yearCreation.length != 0)
            yearCreationVerification = (yearCreation.length == 4)

        //Informar ao usuário o que está invalido
        if (nameVerification == false)
            Toast.makeText(this, "Game name is required", Toast.LENGTH_LONG).show()

        if (yearCreationVerification == false)
            Toast.makeText(this, "Year of Creation invalid", Toast.LENGTH_LONG).show()

        return nameVerification && yearCreationVerification
    }

    fun showImage(url: String) {
        Picasso.get().load(url).into(iv_registerGameImage)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        //Como a metodologia adotada é: Criar uma "reserva" para o novo game no Firebase Realtime
        //antes do usuário preencher os campos, caso ele não salve o novo game, é preciso excluir
        //a "reserva" feita.
        if (saveGame == false) {
            deleteGame()
        }

    }

}