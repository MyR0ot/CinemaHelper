package com.example.cinemahelper

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemahelper.utils.ParserUtil
import android.view.View
import android.widget.AdapterView.OnItemSelectedListener
import androidx.recyclerview.widget.DividerItemDecoration
import android.graphics.Bitmap
import android.os.Build
import android.widget.*
import androidx.core.os.ConfigurationCompat
import com.example.cinemahelper.utils.LocaleChecker
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {

    @SuppressLint("StaticFieldLeak")
    inner class LoadInfoTask : AsyncTask<List<Film>, Unit, Unit>() {

        override fun doInBackground(vararg params: List<Film>?): Unit {
            films = ParserUtil.loadContent(LocaleChecker.isRussianLocale(this@MainActivity)) // load information about films from cinemadelux.ru
            films.forEach { it ->
                it.poster?.let { poster ->
                    this@MainActivity.createImageFromBitmap(
                        poster,
                        it.getFileName()
                    )
                }
            }
            if (films.isNullOrEmpty()) return
            this@MainActivity.runOnUiThread {
                configureRecyclerView()
                configureSpinner()
            }
        }

        override fun onPostExecute(result: Unit?): Unit {
            pb_loading_films.isVisible = false
            if (films.isNullOrEmpty()) {
                showErrorMessageTextView()
                return
            }
            showResultTextView()
        }

        override fun onPreExecute(): Unit {
            pb_loading_films.isVisible = true
        }

        private fun configureRecyclerView(): Unit {
            val layoutManager: RecyclerView.LayoutManager =
                LinearLayoutManager(this@MainActivity) // последовательное отображение сверху вниз
            rv_films.layoutManager = layoutManager
            rv_films.setHasFixedSize(true)
            rv_films.adapter = FilmsAdapter(films, LocaleChecker.isRussianLocale(this@MainActivity),  object : FilmsAdapter.Callback {
                override fun onItemClicked(item: Film) {
                    openDetailedActivity(item)
                }
            })

            rv_films.addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        private fun configureSpinner(): Unit {
            if(LocaleChecker.isRussianLocale(this@MainActivity)){
                genres = listOf("все").union(ParserUtil.getGenres(films)).filter { it.isNotEmpty() }.toMutableList().toList()
            } else {
                genres = listOf("all").union(ParserUtil.getGenres(films)).filter { it.isNotEmpty() }.toMutableList().toList()
            }

            val adapter = ArrayAdapter<String>(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                genres
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sp_genre_chooser.adapter = adapter
            sp_genre_chooser.setSelection(0) // default: 'all' genres display
            sp_genre_chooser.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View,
                    position: Int, id: Long
                ) {
                    val genre: String = sp_genre_chooser.selectedItem.toString()
                    rv_films.swapAdapter(
                        FilmsAdapter(
                            films.filter { it.hasGenre(genre) }, LocaleChecker.isRussianLocale(this@MainActivity),
                            object : FilmsAdapter.Callback {
                                override fun onItemClicked(item: Film) {
                                    openDetailedActivity(item)
                                }
                            }), false
                    )
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {
                    sp_genre_chooser.setSelection(0)
                }
            }
        }

        private fun openDetailedActivity(film: Film): Unit {

            Intent(this@MainActivity, FilmActivity::class.java).also {
                it.putExtra(Film::class.java.canonicalName, film as Parcelable)
                it.putExtra("posterFileName", film.getFileName())
                startActivity(it)
            }
        }
    }

    private var films: List<Film> = listOf()
    private var genres: List<String> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        configChangeLanguage()
        LoadInfoTask().execute()

    }

    fun createImageFromBitmap(bitmap: Bitmap, fileName: String): Boolean {
        try {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val fo = openFileOutput(fileName, Context.MODE_PRIVATE)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }


    fun showResultTextView(): Unit {
        tv_error_message.isVisible = false
        tv_title_genre.isVisible = true
        tv_title.isVisible = true
        sp_genre_chooser.isVisible = true
        rv_films.isVisible = true
    }

    fun showErrorMessageTextView(): Unit {
        tv_title_genre.isVisible = false
        tv_title.isVisible = false
        sp_genre_chooser.isVisible = false
        rv_films.isVisible = false
        tv_error_message.isVisible = true
    }



    private fun configChangeLanguage(): Unit {
        val currentLocale = ConfigurationCompat.getLocales(resources.configuration)[0]
        if(currentLocale.language == "ru"){
            iv_language_icon.setImageResource(R.drawable.ru)
        } else {
            iv_language_icon.setImageResource(R.drawable.us)
        }

        iv_language_icon.setOnClickListener{ changeLanguage() }
        tv_language.setOnClickListener{ changeLanguage() }
    }

    private fun changeLanguage(): Unit {
        if(LocaleChecker.isRussianLocale(this)){
            setLocale("en")
        } else setLocale("ru")
        recreate()
    }

    private fun setLocale(lang: String):Unit {
        val locale: Locale = Locale(lang)
        Locale.setDefault(locale)
        val conf = Configuration()
        conf.locale = locale
        baseContext.resources.updateConfiguration(conf, baseContext.resources.displayMetrics)
        val editor: SharedPreferences.Editor = getSharedPreferences("Settings", MODE_PRIVATE).edit()
        editor.putString("language", lang)
        editor.apply()
    }

    private fun loadLocale(){
        val prefs: SharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val language: String? = prefs.getString("language", "ru")
        language?.let { setLocale(it) }
    }

}


