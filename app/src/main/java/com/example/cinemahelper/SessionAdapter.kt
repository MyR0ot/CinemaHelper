package com.example.cinemahelper

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SessionAdapter(var items: List<Film.Session>, val callback: SessionAdapter.Callback): RecyclerView.Adapter<SessionAdapter.SessionViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = SessionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.session_list_item, parent, false))

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size


    inner class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val date = itemView.findViewById<TextView>(R.id.tv_date)
        private val time = itemView.findViewById<TextView>(R.id.tv_time)
        private val price = itemView.findViewById<TextView>(R.id.tv_price)


        @SuppressLint("SetTextI18n")
        fun bind(session: Film.Session) {
            date.text = "${session.getDay()} ${session.getMonth()}"
            time.text = session.time
            price.text = session.price + "₽"

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: Film.Session)
    }
}