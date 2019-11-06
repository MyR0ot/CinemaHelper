package com.example.cinemahelper

// TODO: Возможно хранить постер как Image

class Film(val id: String,               // Идентификатор фильма в https://cinemadelux.ru
           val name: String,             // Название фильма
           val description: String = "", // Описание фильма
           val duration: String?,        // Продолжительность фильма
           val genres: List<String>,     // Жанры
           val tags: List<String>,       // Метки
           val img: String?              // Адрес картинки
           ) {


    override fun toString(): String {
        return "id: $id\nname: $name\nduration: $duration\ngenres: ${genres.toString()}\ntags: ${tags.toString()}\nimgLink: $img\ndescription: $description\n"
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

}