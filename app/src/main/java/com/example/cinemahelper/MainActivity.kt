package com.example.cinemahelper

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemahelper.utils.ParserUtil
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.recyclerview.widget.DividerItemDecoration


class MainActivity : AppCompatActivity() {

    @SuppressLint("StaticFieldLeak")
    inner class LoadInfoTask: AsyncTask<List<Film>, Unit, Unit>() { // TODO: Перенести в asyncTasks

        override fun doInBackground(vararg params: List<Film>?): Unit {
            println("do in background")
            films = ParserUtil.loadContent() // load information about films from cinemadelux.ru
            if(films.isNullOrEmpty()) return
            this@MainActivity.runOnUiThread {
                configureRecyclerView()
                configureSpinner()
            }
        }

        override fun onPostExecute(result: Unit?): Unit {
            progressBar.isVisible = false
            if(films.isNullOrEmpty()){
                showErrorMessageTextView()
                return
            }
            showResultTextView()
        }

        override fun onPreExecute(): Unit {
            progressBar.isVisible = true
        }

        private fun configureRecyclerView(): Unit {
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@MainActivity) // последовательное отображение сверху вниз
            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = FilmsAdapter(films, object : FilmsAdapter.Callback {
                override fun onItemClicked(item: Film) { openDetailedActivity(item) }
            })

            recyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
        }

        private fun configureSpinner(): Unit {
            genres = listOf("все").union(ParserUtil.getGenres(films)).filter { it.isNotEmpty() }.toMutableList().toList()
            val adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_spinner_item, genres)
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
                    recyclerView.swapAdapter(FilmsAdapter(films.filter { it.hasGenre(genre) }, object : FilmsAdapter.Callback {
                        override fun onItemClicked(item: Film) { openDetailedActivity(item) }
                    }), false)
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {
                    genreChooser.setSelection(0);
                }
            }
        }

        private fun openDetailedActivity(film: Film): Unit {
            println(film.toString())
            // TODO:
        }



    }

    private var films: List<Film> = listOf()
    private var genres: List<String> = listOf()
    private lateinit var errorMsg: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var genreChooser: Spinner
    private lateinit var titleGenre: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main)
        loadIDs()
        LoadInfoTask().execute()


        // TODO: Оторвать ноги дизайнеру (change background)
        // TODO: Отрисовать детальное активити
        // TODO: Реализовать передачу фильмов через интенты в детальное активити
        // TODO: Реализовать подгрузку контента если подключение к интернету появилось после загрузки приложения
        // TODO: Выбор языка
    }


    private fun loadIDs(): Unit {
        this.errorMsg = findViewById(R.id.tv_error_message)
        this.progressBar = findViewById(R.id.pb_loading_films)
        this.recyclerView = findViewById(R.id.rv_films)
        this.genreChooser = findViewById(R.id.sp_genre_chooser)
        this.titleGenre = findViewById(R.id.tv_title_genre)
    }

    fun showResultTextView(): Unit {
        errorMsg.isVisible = false
        titleGenre.isVisible = true
        genreChooser.isVisible = true
        recyclerView.isVisible = true
    }

    fun showErrorMessageTextView(): Unit {
        titleGenre.isVisible = false
        genreChooser.isVisible = false
        recyclerView.isVisible = false
        errorMsg.isVisible = true
    }
}


