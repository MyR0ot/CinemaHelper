package com.example.cinemahelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.graphics.BitmapFactory
import android.widget.ImageView


class FilmActivity : AppCompatActivity() {

    private lateinit var film: Film

    private lateinit var poster: ImageView
    private lateinit var description: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film)
        loadIDs()
        getInfoFromIntent()

        description.text = film.description
        poster.setImageBitmap(film.poster)
    }

    private fun getInfoFromIntent(): Unit {
        film = intent.getParcelableExtra(Film::class.java.canonicalName)
        if (intent.hasExtra("posterFileName")) {
            film.poster = BitmapFactory.decodeStream(this.openFileInput(intent.getStringExtra("posterFileName")))
        }
    }


    private fun loadIDs(): Unit {
        this.description = findViewById(R.id.tv_description)
        this.poster = findViewById(R.id.iv_main_poster)
    }
}
