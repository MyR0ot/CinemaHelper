package com.example.cinemahelper.asyncTasks

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import androidx.core.graphics.scale

@SuppressLint("StaticFieldLeak")
@Deprecated("Загрузка картинки происходит внутри конструктора film")
class DownloadImageTask(private val bmpImage: ImageView) : AsyncTask<String, Void, Bitmap>() {

    @SuppressLint("WrongThread")
    override fun doInBackground(vararg params: String?): Bitmap? {
        var mIcon11: Bitmap? = null
        try {
            val inputStream = java.net.URL(params[0]).openStream()
            mIcon11 = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            System.err.println("Ошибка передачи изображения")
            e.printStackTrace()
        }

        return mIcon11
    }

    override fun onPostExecute(result: Bitmap?) {
        this.bmpImage.setImageBitmap(result);
    }


    override fun onPreExecute() {
        super.onPreExecute()
        // TODO: Добавить спинер на подгрузку картинок
    }


}