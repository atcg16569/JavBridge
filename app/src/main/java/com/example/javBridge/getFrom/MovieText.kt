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
                val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                val date = LocalDate.parse(json.getString("date"), formatter)
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
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val date = LocalDate.parse(json.getString("date"), formatter)
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