package com.example.desafiofirebase.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.desafiofirebase.R
import com.example.desafiofirebase.entities.Game
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_game_details.*

class GameDetailsActivity : AppCompatActivity() {
    //Variáveis
    var userId = ""
    var gameId: String = ""
    var gameYearCreation: String = ""
    var gameName: String = ""
    var gameImage: String = ""
    var gameDescription: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_details)

        setSupportActionBar(toolbarDetailsGamePage)

        toolbarDetailsGamePage.setNavigationOnClickListener {
            callHome()
        }

        //Recepção dos dados
        val extras = intent.extras
        userId = extras!!.getString("userId").toString()

        val game = intent.getSerializableExtra("game") as? Game
        gameId = game!!.gameId
        gameYearCreation = game.gameYearCreation
        gameName = game.gameName
        gameImage = game.gameImage
        gameDescription = game.gameDescription

        //Personalização da página
        if (gameImage.isNotEmpty())
            Picasso.get().load(gameImage).into(iv_gameImageDetailsGamePage)

        tv_gameTitleDetailsGamePage.text = gameName
        tv_gameTitle2DetailsGamePage.text = gameName
        tv_gameYearCreationDetailsGamePage.text = gameYearCreation
        tv_gameDescriptionDetailsGamePage.text = gameDescription

        if(gameYearCreation.isEmpty())
            gameYearCreationLabel.visibility = View.INVISIBLE

    }

    fun callHome() {
        var intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
}