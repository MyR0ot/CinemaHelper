package com.example.cinemahelper

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import android.provider.MediaStore.Images.Media.getBitmap
import android.graphics.drawable.BitmapDrawable







class MainActivity : AppCompatActivity() {

    private val films: HashSet<Film> = HashSet();

    private val parseMap: Map<String, Pair<String, String>> = mapOf(
        "name" to Pair("<h1 class=\"u_mb-xl\">", "</h1>"),
        "duration" to Pair("<nobr class=\"u_mr-m\">", "</nobr>"),
        "description" to Pair("itemProp=\"description\">", "</truncate-text>"),
        "genres" to Pair("<meta itemprop=\"genre\" content=\"", "\">"),
        "img" to Pair("<meta itemProp=\"image\" content=\"", "\"/>"),
        "tags" to Pair("<span class=\"film_label\">", "</span>")
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        loadContent() // TODO: Убрать в отдельный поток и поставить спинер, пока загружается контент

        // TODO: Отрисовать базовое активити
        // TODO: Реализовать передачу фильмов через интенты

        test()

        printErr("happy end")
    }

    private fun loadContent():Unit {
        val mainPageHTML = loadHTML("https://cinemadelux.ru/", "utf-8")
        val ids: HashSet<String>? = mainPageHTML?.let { parseIds(it) }
        for(id in ids!!){
            val film: Film? = parseFilm("https://cinemadelux.ru/films/$id", id)
            film?.let { films.add(it) }
        }
    }

    fun loadImage(url: String, imgView: ImageView): Unit {
        Glide.with(this)
            .load(url).into(imgView)
    }

    @Deprecated(message = "Функция для тестирования парсинга")
    private fun test():Unit {
        var checkStr: String = "";
        for(film in films){
            checkStr += "${film.toString()}\n\n"
        }

        tv_list.text = checkStr
    }


    private fun loadHTML(url: String, charsetName: String = "windows-1251"): String?  {
        val sb: StringBuilder = StringBuilder()
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val httpClient: HttpClient = DefaultHttpClient()
        try {
            val response: HttpResponse = httpClient.execute(HttpGet(url))
            val inputStream: InputStream = response.entity.content
            val reader: BufferedReader = BufferedReader(InputStreamReader(inputStream, charsetName), 8)
            do {
                val line = reader.readLine() ?: break
                sb.append(line + "\n")
            } while (true)
            return sb.toString()
        }catch(e: Exception)
        {
            // text.setText("Нет подключения к интернету")
        }

        return null
    }


    private fun parseIds(html: String, startPattern: String = "<a href=\"/films/"): HashSet<String> {
        val res: HashSet<String> = HashSet<String>()
        var beginIndex: Int = html.indexOf(startPattern)
        val limitIndex = html.indexOf("Скоро в кино") // не учитываем фильмы в разделе 'Скоро в кино'
        printErr("limitIndex: $limitIndex")
        while(beginIndex != -1 && beginIndex < limitIndex) {
            var length = 0;
            while(html[beginIndex + length + startPattern.length].isDigit()){
                length++
            }
            try {
                res.add(html.substring(beginIndex + startPattern.length, beginIndex + startPattern.length + length))
            } catch (e: Exception){
            }
            beginIndex = html.indexOf(startPattern, beginIndex + startPattern.length)
        }
        return res
    }


    private fun parseOneString(text: String, startWord: String, stopWord: String): String {
        val beginIndex: Int = text.indexOf(startWord) + startWord.length
        return text.substring(beginIndex, text.indexOf(stopWord, beginIndex))
    }

    private fun parseAllStrings(text: String, startWord: String, stopWord: String): List<String> {
        val res: MutableList<String> = mutableListOf()
        var beginIndex: Int = text.indexOf(startWord)

        while(beginIndex != -1){
            beginIndex += startWord.length
            val endIndex = text.indexOf(stopWord, beginIndex)
            if(endIndex == -1) break
            res.add(text.substring(beginIndex, endIndex))
            beginIndex = text.indexOf(startWord, beginIndex)
        }

        return res.toList()
    }

    private fun parseFilm(url: String, id: String): Film? {
        val html: String? = loadHTML(url, "utf-8")
        html ?: return null

        try {
            val name: String = parseOneString(html,  parseMap["name"]!!.first,  parseMap["name"]!!.second)
            val duration: String = parseOneString(html,  parseMap["duration"]!!.first,  parseMap["duration"]!!.second)
            val description: String = parseOneString(html,  parseMap["description"]!!.first,  parseMap["description"]!!.second)
            val img: String = parseOneString(html,  parseMap["img"]!!.first,  parseMap["img"]!!.second)
            val tags: List<String> = parseAllStrings(html, parseMap["tags"]!!.first, parseMap["tags"]!!.second)
            val genres: List<String> = parseOneString(html,  parseMap["genres"]!!.first, parseMap["genres"]!!.second).split(", ") // TODO: Проверить

            return Film(id, name, description, duration, genres, tags, img)
        } catch (e: Exception){
            printErr("Parse error!  filmID: $id")
            return null
        }
    }

    // TODO: Перенести в отдельный класс/пакет/объект?
    fun printErr(errorMsg:String){
        System.err.println(errorMsg)
    }
}


