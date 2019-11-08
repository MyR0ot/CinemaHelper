package com.example.cinemahelper

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import android.graphics.Bitmap as Bitmap


class Film(val id: String,               // Идентификатор фильма в https://cinemadelux.ru
           val name: String,             // Название фильма
           val description: String,      // Описание фильма
           val duration: String,         // Продолжительность фильма
           var genres: List<String>,     // Жанры
           val tags: List<String>,       // Метки
           val imgPath: String,          // Адрес картинки
           val producer: String,         // режиссер
           val sessions: List<Session>,  // сеансы
           var poster: Bitmap?           // постер BMP
           ): Serializable, Parcelable {

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

        this.genres = genres.map { it.toLowerCase() }
    }


    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!,
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.createTypedArrayList(Session)!!,
        null
    ) {
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


    fun getFileName():String {
        return this.hashCode().toString() + ".jpj"
    }



    class Session(private val date: String, private val day: String, private val price: String): Serializable, Parcelable{
        constructor(parcel: Parcel) : this(
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString()
        ) {
        }

        override fun toString(): String {
            return "date: $date, time: $day, price: $price"
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(date)
            parcel.writeString(day)
            parcel.writeString(price)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Session> {
            override fun createFromParcel(parcel: Parcel): Session {
                return Session(parcel)
            }

            override fun newArray(size: Int): Array<Session?> {
                return arrayOfNulls(size)
            }
        }
    }

    fun getGenresAsString(): String {
       return this.genres.joinToString { it }
    }

    @SuppressLint("DefaultLocale")
    fun hasGenre(genre: String): Boolean {
        if(genre == "все") return true
        val g = genre.toLowerCase()
        return !this.genres.find { it == g }.isNullOrEmpty()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(duration)
        parcel.writeStringList(genres)
        parcel.writeStringList(tags)
        parcel.writeString(imgPath)
        parcel.writeString(producer)
        parcel.writeTypedList(sessions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Film> {
        override fun createFromParcel(parcel: Parcel): Film {
            return Film(parcel)
        }

        override fun newArray(size: Int): Array<Film?> {
            return arrayOfNulls(size)
        }
    }

}