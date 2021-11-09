package com.example.javBridge.getFrom

import com.example.javBridge.database.Movie
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONArray
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
                val pattern1 = Regex("\\d{2}/\\d{2}/\\d{4}")
//                val pattern2 = Regex("\\d{4}-\\d{2}-\\d{2}")
                val date = if (dateString.matches(pattern1)) {
                    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                    LocalDate.parse(dateString, formatter)
                } else {
                    LocalDate.parse(json.getString("date"))
                }
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
            val pattern1 = Regex("\\d{2}/\\d{2}/\\d{4}")
            val date = if (dateString.matches(pattern1)) {
                val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                LocalDate.parse(dateString, formatter)
            } else {
                LocalDate.parse(json.getString("date"))
            }
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