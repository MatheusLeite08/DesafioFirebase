package com.example.desafiofirebase.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiofirebase.R
import com.example.desafiofirebase.entities.Game
import com.squareup.picasso.Picasso

class GamesListAdapter(val listener: onGameClickListener): RecyclerView.Adapter<GamesListAdapter.GamesListViewHolder>() {
    var gamesList = ArrayList<Game>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GamesListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GamesListViewHolder, position: Int) {
        val currentItem: Game = gamesList[position]

        holder.gameName.setText(currentItem.gameName)
        holder.yearCreation.setText(currentItem.gameYearCreation)

        if (currentItem.gameImage.isNotEmpty())
            Picasso.get().load(currentItem.gameImage).into(holder.gameImage)

    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    interface onGameClickListener {
        fun gameClick(position: Int)
    }

    inner class GamesListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var gameImage: ImageView = itemView.findViewById(R.id.iv_gameImage)
        var gameName: TextView = itemView.findViewById(R.id.tv_gameName)
        var yearCreation: TextView = itemView.findViewById(R.id.tv_yearCreation)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (RecyclerView.NO_POSITION != position)
                listener.gameClick(position)
        }

    }

    fun addList(list: ArrayList<Game>) {
        gamesList = list
        notifyDataSetChanged()
    }

}