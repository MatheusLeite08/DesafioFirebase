package com.example.desafiofirebase.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desafiofirebase.entities.Data
import com.example.desafiofirebase.entities.Game
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.coroutines.launch


class HomeViewModel() : ViewModel() {

    var returnFirebase = MutableLiveData<ArrayList<Game>>()

    fun getGamesListInCloud(database: FirebaseDatabase, reference: DatabaseReference) {
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

                            returnFirebase.value!!.add(game)
                            Log.i("gamesList", gamesList.toString())
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("HomeViewModel", "Failed to read value.", error.toException())
            }
        })


//        returnFirebase.value = gamesList
    }
}