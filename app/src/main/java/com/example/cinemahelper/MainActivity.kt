package com.example.cinemahelper

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.os.AsyncTask
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.cinemahelper.utils.ParserUtil


class MainActivity : AppCompatActivity() {

    @SuppressLint("StaticFieldLeak")
    inner class LoadInfoTask: AsyncTask<Unit, Unit, Unit>() {

        override fun doInBackground(vararg params: Unit?): Unit {
            films = ParserUtil.loadContent() // load information about films from cinemadelux.ru
        }


        override fun onPostExecute(result: Unit?): Unit {
            progressBar.isVisible = false
            if(films.isNullOrEmpty()){
                showErrorMessageTextView()
                return
            }
            showResultTextView()
            test()
        }

        override fun onPreExecute(): Unit {
            progressBar.isVisible = true
        }


        @Deprecated(message = "Функция для тестирования парсинга")
        private fun test():Unit {
            var checkStr: String = "";
            for(film in films){
                checkStr += "${film.toString()}\n\n"
            }
            tv_list.text = checkStr
        }

    }

    private lateinit var films: HashSet<Film>
    private lateinit var errorMsg: TextView
    private lateinit var result: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadIDs()


        LoadInfoTask().execute()

        // TODO: Оторвать ноги дизайнеру (change background, убрать header приложения)
        // TODO: Отрисовать базовое активити после загрузки данных
        // TODO: Реализовать передачу фильмов через интенты в детальное активити
        // TODO: Отрисовать детальное активити
    }

    fun loadIDs():Unit {
        this.errorMsg = findViewById(R.id.tv_error_message)
        this.result = findViewById(R.id.tv_list)
        this.progressBar = findViewById(R.id.pb_loading_films)
    }

    fun showResultTextView(): Unit {
        errorMsg.isVisible = false
        result.isVisible = true
    }

    fun showErrorMessageTextView(): Unit {
        result.isVisible = false
        errorMsg.isVisible = true
    }

    fun printErr(errorMsg:String){
        System.err.println(errorMsg)
    }
}


