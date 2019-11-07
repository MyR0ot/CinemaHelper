package com.example.cinemahelper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.cinemahelper.asyncTasks.DownloadImageTask

// TODO: Возможно хранить постер как Image

class Film(val id: String,               // Идентификатор фильма в https://cinemadelux.ru
           val name: String,             // Название фильма
           val description: String = "", // Описание фильма
           val duration: String,         // Продолжительность фильма
           val genres: List<String>,     // Жанры
           val tags: List<String>,       // Метки
           val imgPath: String,          // Адрес картинки
           val producer: String,         // режиссер
           val sessions: List<Session>,  // сеансы
           var poster: Bitmap?           // постер BMP
           ) {

    init {
        if(poster === null){
            var mIcon11: Bitmap? = null
            try {
                val inputStream = java.net.URL(imgPath).openStream()
                mIcon11 = BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                System.err.println("Ошибка передачи изображения")
                e.printStackTrace()
            }
            this.poster = mIcon11
        }
    }



    override fun toString(): String {
        var res: String = "id: $id\nname: $name\nproducer: $producer\nduration: $duration\ngenres: ${genres.toString()}\ntags: ${tags.toString()}\nimgLink: $imgPath\ndescription: $description\n"
        res+="sessions: {\n"
        sessions.forEach{ res += it.toString()+"\n"}
        res+="}"
        return res
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Film
        return id == other.id && name == other.name
    }

    fun getLink():String {
        return "https://cinemadelux.ru/films/$id"
    }


    class Session(val date: String, val day: String, val price: String){
        override fun toString(): String {
            return "date: $date, time: $day, price: $price"
        }
    }

    fun getGenresAsString(): String {
       return this.genres.joinToString { it -> it }
    }

}