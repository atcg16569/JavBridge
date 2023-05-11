package com.example.javBridge.getFrom

import android.content.ContentResolver
import android.net.Uri
import com.example.javBridge.database.Movie
import com.example.javBridge.database.Url
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONArray
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MovieText {
    fun db(txt: String): MutableList<Movie> {
        val list = mutableListOf<Movie>()
        val pages = JSONArray(txt)
        for (p in 0 until pages.length()) {
            val movies = pages.getJSONObject(p).getJSONArray("movies")
            for (m in 0 until movies.length()) {
                val json = movies.getJSONObject(m)
                val dateString = json.getString("date")
                val date = dateParse(dateString)
                val movie = Movie(
                    json.getString("id"),
                    date
                )
                list.add(movie)
            }
        }
        return list
    }

    fun bus(txt: String): MutableList<Movie> {
        val list = mutableListOf<Movie>()
        val movies = JSONArray(txt)
        for (m in 0 until movies.length()) {
            val json = movies.getJSONObject(m)
            val dateString = json.getString("date")
            val date = dateParse(dateString)
            val star = json.getString("star")
            val actress = star.split(",").toMutableSet()
            val movie = Movie(
                json.getString("id"),
                date,
                actress,
                json.getString("studio")
            )
            list.add(movie)
        }
        return list
    }

    fun restore(txt: String): List<Movie> {
        return Json.decodeFromString(txt)
    }
}

private fun dateParse(dateString: String): LocalDate {
    val pattern1 = Regex("\\d{2}/\\d{2}/\\d{4}")
//  val pattern2 = Regex("\\d{4}-\\d{2}-\\d{2}")
    val date = if (dateString.matches(pattern1)) {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        LocalDate.parse(dateString, formatter)
    } else {
        LocalDate.parse(dateString)
    }
    return date
}

// 读取字符串
fun readTextFromUri(uri: Uri, contentResolver: ContentResolver): String {
    val stringBuilder = StringBuilder()
    contentResolver.openInputStream(uri)?.use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String? = reader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = reader.readLine()
            }
        }
    }
    return stringBuilder.toString()
}

// 修改文档
fun writeDocument(uri: Uri, content: String, contentResolver: ContentResolver) {
    try {
        contentResolver.openFileDescriptor(uri, "w")?.use {
            FileOutputStream(it.fileDescriptor).use { output ->
                output.write(content.toByteArray())
            }
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun url(txt: String): MutableList<Url> {
    val list = mutableListOf<Url>()
    val urls = JSONArray(txt)
    for (u in 0 until urls.length()) {
        val json = urls.getJSONObject(u)
        val name = json.getString("name")
        val link = json.getString("link")
        val url = Url(name, link)
        list.add(url)
    }
    return list
}