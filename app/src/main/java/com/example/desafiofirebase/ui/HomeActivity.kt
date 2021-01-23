package com.example.desafiofirebase.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiofirebase.R
import com.example.desafiofirebase.adapters.GamesListAdapter
import com.example.desafiofirebase.models.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), GamesListAdapter.onGameClickListener {
    private lateinit var gamesListAdapter: GamesListAdapter
    private lateinit var gamesListLayoutManager: RecyclerView.LayoutManager

    val viewModel by viewModels<HomeViewModel>{
        object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return HomeViewModel() as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        gamesListLayoutManager = GridLayoutManager(this, 2)
        rv_gamesListHome.layoutManager = gamesListLayoutManager
        gamesListAdapter = GamesListAdapter(this)
        rv_gamesListHome.adapter = gamesListAdapter

        viewModel.returnFirebase.observe(this){
            gamesListAdapter.addList(it)
        }

        viewModel.getGamesListInCloud()

        fb_addGame.setOnClickListener {
            callRegisterGame()
        }
    }

    override fun gameClick(position: Int) {
        viewModel.returnFirebase.observe(this){
            var game = it.get(position)

            Toast.makeText(this, game.name, Toast.LENGTH_LONG).show()
        }
    }

    fun callRegisterGame(){
        var intent = Intent(this, RegisterGameActivity::class.java)
        startActivity(intent)
    }
}