package com.example.cinemahelper

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemahelper.utils.ParserUtil


class MainActivity : AppCompatActivity() {

    @SuppressLint("StaticFieldLeak")
    inner class LoadInfoTask: AsyncTask<List<Film>, Unit, Unit>() {

        override fun doInBackground(vararg params: List<Film>?): Unit {
            films = ParserUtil.loadContent() // load information about films from cinemadelux.ru
            // TODO: Как-то прикрутить адаптер (см main)
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
    }

    private var films: List<Film> = listOf()
    private lateinit var errorMsg: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var filmsList: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadIDs()

        // LoadInfoTask().execute()
        helpLoader()


        // TODO: Спарсить сеансы для каждого фильма
        // TODO: Добавить адаптер ПОСЛЕ загрузки фильмов с сайта (в отдельном потоке нельзя?)
        // TODO: Разобраться с загрузкой картинки (Получить Bitmap для класса Film)
        // TODO: Оторвать ноги дизайнеру (change background, убрать header приложения)
        // TODO: Отрисовать базовое активити после загрузки данных
        // TODO: Отрисовать детальное активити
        // TODO: Реализовать передачу фильмов через интенты в детальное активити
        // TODO: Реализовать подгрузку контента если подключение к интернету появилось после загрузки приложения
    }


    @Deprecated("Переписать выполнение в другом потоке")
    fun helpLoader(): Unit {
        films = ParserUtil.loadContent()
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this) // последовательное отображение сверху вниз
        filmsList.layoutManager = layoutManager
        filmsList.setHasFixedSize(true)
        filmsList.adapter = FilmsAdapter(films, object : FilmsAdapter.Callback {
            override fun onItemClicked(item: Film) {
                // TODO: Открытие детального активити
                println(item.toString())
            }
        })
    }



    fun loadIDs():Unit {
        this.errorMsg = findViewById(R.id.tv_error_message)
        this.progressBar = findViewById(R.id.pb_loading_films)
        this.filmsList = findViewById(R.id.rv_films)
    }

    fun showResultTextView(): Unit {
        errorMsg.isVisible = false
        filmsList.isVisible = true
    }

    fun showErrorMessageTextView(): Unit {
        filmsList.isVisible = false
        errorMsg.isVisible = true
    }

    fun printErr(errorMsg:String){
        System.err.println(errorMsg)
    }
}


