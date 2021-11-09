package com.example.javBridge

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.javBridge.database.DatabaseApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class InfoWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val repository = DatabaseApplication().remoteRepository
        val movies = repository.limitMovies()
        if (movies.isNullOrEmpty()) {
//            val output = workDataOf("update result" to "no null movies\n$movies")
            Log.d("update result","no null movies$movies")
        } else {
            for (m in movies) {
                val url = repository.busJoinUrl(m.id)
                val time = (0..25).random().toLong()
                runBlocking {
                    delay(time * 1000)
                }
                val doc = repository.getDoc(url)
                if (doc != null) {
                    val result = repository.dealBus(doc)
                    m.actress = result["actress"] as MutableSet<String>
                    m.studio = result["studio"] as String
                    repository.updateMovie(m)
                    Log.d("update result", "${m.id}\ndelay $time seconds")
                }
            }
//            val output = workDataOf("update result" to Json.encodeToString(movies))
        }
        return Result.success()
    }
}

private val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.UNMETERED)
    .setRequiresBatteryNotLow(true)
    .setRequiresStorageNotLow(true)
    .build()

fun javRequest(): PeriodicWorkRequest {
//    val data = workDataOf("movies" to movies)
    return PeriodicWorkRequestBuilder<InfoWorker>(1, TimeUnit.DAYS)
        .setConstraints(constraints)//.setInputData(data)
        .build()
}