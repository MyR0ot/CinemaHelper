package com.example.cinemahelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_film.*
import android.content.Intent
import android.net.Uri


class FilmActivity : AppCompatActivity() {

    private lateinit var film: Film

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film)
        getInfoFromIntent()


        inflate()
    }

    private fun getInfoFromIntent(): Unit {
        film = intent.getParcelableExtra(Film::class.java.canonicalName)
        if (intent.hasExtra("posterFileName")) {
            film.poster = BitmapFactory.decodeStream(this.openFileInput(intent.getStringExtra("posterFileName")))
        }
    }

    private fun inflate(){
        iv_main_poster.setImageBitmap(film.poster)
        tv_description.text = film.description
        configureRecyclerView()
    }


    private fun configureRecyclerView(): Unit {

        rv_sessions.layoutManager = LinearLayoutManager(this@FilmActivity, LinearLayoutManager.VERTICAL, true)
        rv_sessions.setHasFixedSize(true)
        rv_sessions.adapter = SessionAdapter(film.sessions, object : SessionAdapter.Callback {
            override fun onItemClicked(item: Film.Session) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(film.getLink()))
                startActivity(browserIntent)
            }
        })

        rv_sessions.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }
}
