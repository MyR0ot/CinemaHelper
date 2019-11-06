package com.example.cinemahelper

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.os.AsyncTask
import com.example.cinemahelper.utils.ParserUtil


class MainActivity : AppCompatActivity() {

    @SuppressLint("StaticFieldLeak")
    inner class LoadInfoTask: AsyncTask<Unit, Unit, Unit>() {

        override fun doInBackground(vararg params: Unit?): Unit {
            films = ParserUtil.loadContent() // загрузка информации с cinemadelux.ru
        }


        override fun onPostExecute(result: Unit?) {
            test()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LoadInfoTask().execute()

        /*
        // TODO: Отрисовать базовое активити
        // TODO: Реализовать передачу фильмов через интенты
         */

    }

    fun printErr(errorMsg:String){
        System.err.println(errorMsg)
    }
}


