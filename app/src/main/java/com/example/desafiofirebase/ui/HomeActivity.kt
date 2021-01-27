package com.example.desafiofirebase.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiofirebase.R
import com.example.desafiofirebase.adapters.GamesListAdapter
import com.example.desafiofirebase.entities.Game
import com.example.desafiofirebase.models.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), GamesListAdapter.onGameClickListener {
    private lateinit var gamesListAdapter: GamesListAdapter
    private lateinit var gamesListLayoutManager: RecyclerView.LayoutManager

    //Realtime Database
    lateinit var database: FirebaseDatabase
    lateinit var reference: DatabaseReference

    //Variáveis
    var userId = ""
    var userName = ""

    val viewModel by viewModels<HomeViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return HomeViewModel() as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Recepção dos dados
        val extras = intent.extras
        userId = extras!!.getString("userId").toString()

        conectDatabase(userId)

        getGamesListInCloud()

        gamesListLayoutManager = GridLayoutManager(this, 2)
        rv_gamesListHome.layoutManager = gamesListLayoutManager
        gamesListAdapter = GamesListAdapter(this)
        rv_gamesListHome.adapter = gamesListAdapter

        fb_addGame.setOnClickListener {
            callRegisterGame()
        }

    }

    //Realtime Database

    fun conectDatabase(userId: String) {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference(userId) //Receber o id do usuário
    }

    fun getGamesListInCloud() {
        var gamesList = arrayListOf<Game>()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {

                    if (it.key == "games") {
                        it.children.forEach {

                            var game = Game(
                                it.child("gameId").value.toString(),
                                it.child("gameYearCreation").value.toString(),
                                it.child("gameName").value.toString(),
                                it.child("gameImage").value.toString(),
                                it.child("gameDescription").value.toString(),
                            )

                            gamesList.add(game)
                        }
                    }

                }

                gamesListAdapter.addList(gamesList)
                gamesList = arrayListOf()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun gameClick(position: Int) {
        var gamesList = gamesListAdapter.gamesList
        var game = gamesList.get(position)

        var intent = Intent(this, GameDetailsActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("game", game)
        startActivity(intent)
    }

    fun callRegisterGame() {
        var intent = Intent(this, RegisterGameActivity::class.java)
        intent.putExtra("isNew", true)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
}