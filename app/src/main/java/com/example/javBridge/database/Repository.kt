package com.example.javBridge.database

import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException


class BridgeRepository(private val bridgeDao: BridgeDao) {
    fun flowMovie(id: String) = bridgeDao.flowMovie(id)
    fun flowAllMovies() = bridgeDao.flowAllMovies()
    suspend fun addMovie(movie: Movie) = bridgeDao.addMovie(movie)
    suspend fun addUrl(url: Url) = bridgeDao.addUrl(url)
    fun liveUrl(name: String) = bridgeDao.liveUrl(name)
    fun liveUrls() = bridgeDao.liveUrls()
    suspend fun removeUrl(url: Url) = bridgeDao.removeUrl(url)
    suspend fun updateUrl(url: Url) = bridgeDao.updateUrl(url)
    fun moviesByDate(start: String, end: String) = bridgeDao.moviesByDate(start, end)
    fun moviesByActress(actress: String) = bridgeDao.moviesByActress(actress)
    fun moviesByStudio(studio: String) = bridgeDao.moviesByStudio(studio)
}

class RemoteRepository(private val bridgeDao: BridgeDao) {
    fun limitMovies() = bridgeDao.limitMovies()
    fun updateMovie(movie: Movie) = bridgeDao.updateMovie(movie)

    // TODO:moo,lib,db
    private object Bus {
        const val name = "javbus"
        const val url = "https://www.javbus.com/"
        val selector = mapOf(
            "studio" to "a[href*='studio']",
            "star" to "div.star-name>a"
        )
    }

    fun busJoinUrl(id: String): String {
        return Bus.url + id
    }

    fun dealBus(doc: Document): Map<String, Any> {
        val star = doc.select(Bus.selector["star"]!!)
        val actress = mutableSetOf<String>()
        for (a in star) {
            actress.add(a.text().trim())
        }
        val studio = doc.select(Bus.selector["studio"]!!).text().trim()
        return mapOf("actress" to actress, "studio" to studio)
    }

    fun getDoc(url: String): Document? {
        return try {
            Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0) Gecko/20100101 Firefox/91.0")
                .get()
        } catch (e: IOException) {
            Log.e("connect failed!", e.localizedMessage!!)
            throw e
        }
    }
}
