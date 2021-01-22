package com.example.desafiofirebase.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desafiofirebase.entities.Game
import kotlinx.coroutines.launch

class HomeViewModel(): ViewModel() {

    var returnFirebase = MutableLiveData<ArrayList<Game>>()

    fun getGamesListInCloud(){
        viewModelScope.launch {
            returnFirebase.value = arrayListOf(
                Game(1, "https://image.tmdb.org/t/p/original/pHcNHYPg0c2vg7qay6wjJoApUgS.jpg", "God of war", "2020", "ggg"),
                Game(1, "https://image.tmdb.org/t/p/original/pHcNHYPg0c2vg7qay6wjJoApUgS.jpg", "The Sims", "2010", "ggg"),
                Game(1, "https://image.tmdb.org/t/p/original/pHcNHYPg0c2vg7qay6wjJoApUgS.jpg", "PES 2009", "2009", "ggg"),
                Game(1, "https://image.tmdb.org/t/p/original/pHcNHYPg0c2vg7qay6wjJoApUgS.jpg", "God of war", "2020", "ggg"),
                Game(1, "https://image.tmdb.org/t/p/original/pHcNHYPg0c2vg7qay6wjJoApUgS.jpg", "God of war", "2020", "ggg"),
                Game(1, "https://image.tmdb.org/t/p/original/pHcNHYPg0c2vg7qay6wjJoApUgS.jpg", "God of war", "2020", "ggg"))
        }
    }
}