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
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {

    @SuppressLint("StaticFieldLeak")
    inner class LoadInfoTask : AsyncTask<List<Film>, Unit, Unit>() { // TODO: Перенести в asyncTasks

        override fun doInBackground(vararg params: List<Film>?): Unit {
            films = ParserUtil.loadContent() // load information about films from cinemadelux.ru
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
            progressBar.isVisible = false
            if (films.isNullOrEmpty()) {
                showErrorMessageTextView()
                return
            }
            showResultTextView()
        }

        override fun onPreExecute(): Unit {
            progressBar.isVisible = true
        }

        private fun configureRecyclerView(): Unit {
            val layoutManager: RecyclerView.LayoutManager =
                LinearLayoutManager(this@MainActivity) // последовательное отображение сверху вниз
            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = FilmsAdapter(films, object : FilmsAdapter.Callback {
                override fun onItemClicked(item: Film) {
                    openDetailedActivity(item)
                }
            })

            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        private fun configureSpinner(): Unit {
            genres = listOf("все").union(ParserUtil.getGenres(films)).filter { it.isNotEmpty() }
                .toMutableList().toList()
            val adapter = ArrayAdapter<String>(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                genres
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genreChooser.adapter = adapter
            genreChooser.prompt = "Жанр";
            genreChooser.setSelection(0); // default: all genres display
            genreChooser.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View,
                    position: Int, id: Long
                ) {
                    val genre: String = genreChooser.selectedItem.toString()
                    recyclerView.swapAdapter(
                        FilmsAdapter(
                            films.filter { it.hasGenre(genre) },
                            object : FilmsAdapter.Callback {
                                override fun onItemClicked(item: Film) {
                                    openDetailedActivity(item)
                                }
                            }), false
                    )
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {
                    genreChooser.setSelection(0);
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
    private lateinit var errorMsg: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var genreChooser: Spinner
    private lateinit var titleGenre: TextView
    private lateinit var title: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main)
        loadIDs()

        configChangeLanguage()
        LoadInfoTask().execute()


        // TODO: Отрисовать детальное активити
        // TODO: Реализовать подгрузку контента если подключение к интернету появилось после загрузки приложения
        // TODO: Создать интент на переход браузером по ссылке
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

    private fun loadIDs(): Unit {
        this.errorMsg = findViewById(R.id.tv_error_message)
        this.progressBar = findViewById(R.id.pb_loading_films)
        this.recyclerView = findViewById(R.id.rv_films)
        this.genreChooser = findViewById(R.id.sp_genre_chooser)
        this.titleGenre = findViewById(R.id.tv_title_genre)
        this.title = findViewById(R.id.tv_title)
    }

    fun showResultTextView(): Unit {
        errorMsg.isVisible = false
        titleGenre.isVisible = true
        title.isVisible = true
        genreChooser.isVisible = true
        recyclerView.isVisible = true
    }

    fun showErrorMessageTextView(): Unit {
        titleGenre.isVisible = false
        title.isVisible = false
        genreChooser.isVisible = false
        recyclerView.isVisible = false
        errorMsg.isVisible = true
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
        val currentLocale = ConfigurationCompat.getLocales(resources.configuration)[0]
        println(currentLocale.language)
        if(currentLocale.language == "ru"){
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


