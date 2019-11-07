package com.example.cinemahelper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemahelper.asyncTasks.DownloadImageTask

class FilmsAdapter(var items: List<Film>, val callback: Callback): RecyclerView.Adapter<FilmsAdapter.FilmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = FilmViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.film_list_item, parent, false))

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size



    inner class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name = itemView.findViewById<TextView>(R.id.tv_name)
        private val duration = itemView.findViewById<TextView>(R.id.tv_duration)
        private val genres = itemView.findViewById<TextView>(R.id.tv_genres)
        private val poster = itemView.findViewById<ImageView>(R.id.iv_poster)
        private val producer = itemView.findViewById<TextView>(R.id.tv_producer)


        fun bind(film: Film) {
            poster.setImageBitmap(film.poster)
            name.text = film.name
            setTextView(producer, "Режиссер", film.producer) // TODO: Что делать с переводом
            setTextView(duration, "Продолжительность", film.duration)
            setTextView(genres, "Жанры", film.getGenresAsString())


            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }

        private fun setTextView(tv: TextView, title:String, text: String): Unit {
            if(!text.isEmpty()){
                tv.text = "$title: $text"
            } else {
                tv.text = ""
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: Film)
    }

}