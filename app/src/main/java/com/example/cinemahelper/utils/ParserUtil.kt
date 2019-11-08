package com.example.cinemahelper.utils

import android.os.StrictMode
import com.example.cinemahelper.Film
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder

object ParserUtil {

    private val parseMap: Map<String, Pair<String, String>> = mapOf(
        "name" to Pair("<h1 class=\"u_mb-xl\">", "</h1>"),
        "duration" to Pair("<nobr class=\"u_mr-m\">", "</nobr>"),
        "description" to Pair("itemProp=\"description\">", "</truncate-text>"),
        "genres" to Pair("<meta itemprop=\"genre\" content=\"", "\">"),
        "img" to Pair("<meta itemProp=\"image\" content=\"", "\"/>"),
        "tags" to Pair("<span class=\"film_label\">", "</span>"),
        "producer" to Pair("<span itemProp=\"name\">", "</span>"),
        "sessionTimes" to Pair(",\"time\":\"","\",\""),
        "sessionPrices" to Pair("\"price\":",","),
        "sessionDates" to Pair("{\"date\":\"","\"")
    )

    private val changersList: List<Pair<String, String>> = listOf(
        Pair("&#40;","("),
        Pair("&#41;",")")
    )


    fun getGenres(films: List<Film>): List<String> {
        var res: HashSet<String> = HashSet()
        films.forEach{ it.genres.forEach {res.add(it)}}
        return res.toList()
    }


    fun loadContent():List<Film> {
        val films: HashSet<Film> = HashSet();
        try {
            val mainPageHTML = loadHTML("https://cinemadelux.ru/", "utf-8")
            val ids: HashSet<String>? = mainPageHTML?.let { parseIds(it) }
            for(id in ids!!){
                val film: Film? = parseFilm("https://cinemadelux.ru/films/$id", id)
                film?.let { films.add(it) }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }

        return films.toList()
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
            e.printStackTrace()
        }

        return null
    }

    private fun parseIds(html: String, startPattern: String = "<a href=\"/films/"): HashSet<String> {
        val res: HashSet<String> = HashSet<String>()
        var beginIndex: Int = html.indexOf(startPattern)
        val limitIndex = html.indexOf("Скоро в кино") // не учитываем фильмы в разделе 'Скоро в кино'
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
        var res = text.substring(beginIndex, text.indexOf(stopWord, beginIndex))
        changersList.forEach{ res = res.replace(it.first, it.second)}
        return res
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
            val imgPath: String = parseOneString(html,  parseMap["img"]!!.first,  parseMap["img"]!!.second)
            val tags: List<String> = parseAllStrings(html, parseMap["tags"]!!.first, parseMap["tags"]!!.second)
            val genres: List<String> = parseOneString(html,  parseMap["genres"]!!.first, parseMap["genres"]!!.second).split(", ")
            val producer: String = parseOneString(html, parseMap["producer"]!!.first, parseMap["producer"]!!.second)

            val sessionDates: List<String> = parseAllStrings(html, parseMap["sessionDates"]!!.first, parseMap["sessionDates"]!!.second) // TODO: Проверить
            val sessionTimes: List<String> = parseAllStrings(html, parseMap["sessionTimes"]!!.first, parseMap["sessionTimes"]!!.second) // TODO: Проверить
            val sessionPrices: List<String> = parseAllStrings(html, parseMap["sessionPrices"]!!.first, parseMap["sessionPrices"]!!.second) // TODO: Проверить

            val sessions: List<Film.Session> = sessionDates.zip(sessionTimes).zip(sessionPrices) {(a,b), c -> listOf(a, b, c)}.map { Film.Session(it[0], it[1], it[2]) } // TODO: Проверить



            return Film(id, name, description, duration, genres, tags, imgPath, producer, sessions, null) // TODO: парсе сессии
        } catch (e: Exception){
            System.err.println("Parse error!  filmID: $id")
            return null
        }
    }

}