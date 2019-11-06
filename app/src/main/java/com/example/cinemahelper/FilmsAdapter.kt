package com.example.cinemahelper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilmsAdapter(var items: List<Film>, val callback: Callback): RecyclerView.Adapter<FilmsAdapter.FilmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = FilmViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.film_list_item, parent, false))

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size



    inner class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name = itemView.findViewById<TextView>(R.id.tv_film_name)
        private val description = itemView.findViewById<TextView>(R.id.tv_film_description)


        fun bind(film: Film) {
            name.text = film.name
            description.text = film.description
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }

        }
    }

    interface Callback {
        fun onItemClicked(item: Film)
    }

}