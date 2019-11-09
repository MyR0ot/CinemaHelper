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
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import android.widget.Toast




class FilmActivity : GestureDetector.OnGestureListener, AppCompatActivity() {
    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return true
    }

    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100

    private lateinit var  gd: GestureDetectorCompat

    fun onSwipeTop() {
    }

    fun onSwipeRight() {
        finish()
    }

    fun onSwipeLeft() {
    }

    fun onSwipeBottom() {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        var result = false
        try {
            val diffY = e2!!.getY() - e1!!.getY()
            val diffX = e2!!.getX() - e1!!.getX()
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                    result = true
                }
            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom()
                } else {
                    onSwipeTop()
                }
                result = true
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        return result
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        this.gd.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private lateinit var film: Film

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film)
        getInfoFromIntent()
        gd = GestureDetectorCompat(this, this)
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
