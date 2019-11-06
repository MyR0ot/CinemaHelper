package com.example.cinemahelper.utils

import android.net.Uri
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object NetworkUtils {

    private const val VK_API_BASE_URL = "https://api.vk.com"
    private const val VK_USER_GET = "/method/users.get"
    private const val PARAM_USER_ID = "user_ids"
    private const val PARAM_VERSION = "v"
    private const val PARAM_ACCESS_TOKEN = "access_token"
    private const val ACCESS_TOKEN = "a4021ebfa4021ebfa4021ebfa6a46fcbf8aa402a4021ebff9c0af8458d7dc95541b0952"

    public fun generateURL(userId: String): URL? {
        val buildUri: Uri = Uri.parse(VK_API_BASE_URL + VK_USER_GET)
            .buildUpon()
            .appendQueryParameter(PARAM_ACCESS_TOKEN, ACCESS_TOKEN)
            .appendQueryParameter(PARAM_USER_ID, userId)
            .appendQueryParameter(PARAM_VERSION, "5.8")
            .build()

        return URL(buildUri.toString())
    }


    @Throws(IOException::class)
    public fun getResponseFromURL(url: URL): String?{
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

        try {
            val inStream: InputStream = urlConnection.inputStream
            val scanner: Scanner = Scanner(inStream)
            scanner.useDelimiter("\\A")

            val hasInput: Boolean = scanner.hasNext()

            return if(hasInput) {
                scanner.next();
            } else {
                null;
            }
        } finally {
            urlConnection.disconnect()
        }
    }
}