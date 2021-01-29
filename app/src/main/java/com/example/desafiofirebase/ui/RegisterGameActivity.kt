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

    //Storage
    lateinit var alertDialog: AlertDialog
    lateinit var storageReference: StorageReference
    private val CODE_IMG = 1000

    //Realtime Database
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    //Variáveis
    var game = Game()
    var isNewGame = false
    var userId = ""
    var saveGame = false
    var updatedGame = Game()
    var gameId = ""
    var urlImage = ""
    var imgId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_game)

        //Recepção dos dados
        val extras = intent.extras
        isNewGame = extras!!.getBoolean("isNew")
        userId = extras.getString("userId").toString()

        //Realtime Database ------------------------------------------------------------------------

        //Cada usuário tem um "nó" do Banco de Dados
        conectDatabase(userId)

        //Aqui é verificado se o usuário quer add um novo game ou atualizar um já existente
        if (isNewGame == true) {
            createNewGameScope()
        }else{
            game = (intent.getSerializableExtra("game") as? Game)!!

            tv_registerGameName.setText(game.gameName)
            tv_registerGameCreated.setText(game.gameYearCreation)
            tv_registerGameDescription.setText(game.gameDescription)

            if(game.gameImage.isNotEmpty())
                showImage(game.gameImage)

            gameId = game.gameId
            urlImage = game.gameImage
            imgId = game.imgId
        }

        //Salvar as informações do game (única informação obrigatória é o Nome)
        buttonSaveGame.setOnClickListener {
            if (dataVerification()) {
                updateGameData()

                if(isNewGame == true) {
                    Toast.makeText(this, "Game saved successfully", Toast.LENGTH_LONG).show()
                    callHome()
                }else{
                    Toast.makeText(this, "Game updated successfully", Toast.LENGTH_LONG).show()
                    callGameDetailsPage()
                }
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
        var extensionId = reference.push().key.toString() //Criação da extension do ID da imagem

        imgId = "$userId/$gameId/$extensionId"

        //Sempre que o usuário muda a imagem do game é criado um ID exclusivo para essa imagem
        //a partir do ID do game, do ID do usuário e de uma extension.
        //Essa junção de ID's é feita para evitar conflitos e para que cada usuário tenha uma pasta
        //no Storage.
        storageReference = FirebaseStorage.getInstance().getReference(imgId)
    }

    fun getImg() {
        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Captura Imagem"), CODE_IMG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODE_IMG && data != null) {
            config(gameId)

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

                    if(urlImage.isNotEmpty())
                        showImage(urlImage)

                    alertDialog.dismiss()

                }
            }
        }
    }

    //Realtime Database

    fun conectDatabase(userId: String) {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference(userId)
    }

    fun createNewGameScope() {
        gameId = reference.push().key.toString() //Criação do ID do game
        var gameScope = Game(gameId = gameId)

        FirebaseDatabase.getInstance().reference
            .child(userId)
            .child("games")
            .child(gameId)
            .setValue(gameScope)
    }

    fun updateGameData() {
        updatedGame = Game(
            gameId,
            tv_registerGameCreated.text.toString(),
            tv_registerGameName.text.toString(),
            urlImage,
            tv_registerGameDescription.text.toString(),
            imgId
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

        //Informa ao usuário o que está inválido
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
        if (saveGame == false && isNewGame == true) {
            deleteGame()
        }
    }

    fun callHome() {
        var intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    fun callGameDetailsPage(){
        var intent = Intent(this, GameDetailsActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("game", updatedGame)

        startActivity(intent)
    }

}