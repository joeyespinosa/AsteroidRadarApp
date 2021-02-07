package com.udacity.asteroidradarapp.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradarapp.model.Asteroid
import com.udacity.asteroidradarapp.api.AsteroidApiService
import com.udacity.asteroidradarapp.api.parseAsteroidsJsonResult
import com.udacity.asteroidradarapp.database.*
import com.udacity.asteroidradarapp.util.Constants.NASA_API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AsteroidRepository(private val database: AsteroidDatabase) {

    @RequiresApi(Build.VERSION_CODES.O)
    private val startDate = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val endDate = LocalDateTime.now().minusDays(7)

    val allAsteroidsList: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModelItem()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    val todayAsteroidsList: LiveData<List<Asteroid>> =
        Transformations.map(
            database.asteroidDao.getAsteroidsDay(
                startDate.format(DateTimeFormatter.ISO_DATE))) {
            it.asDomainModelItem()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    val weekAsteroidsList: LiveData<List<Asteroid>> =
        Transformations.map(
            database.asteroidDao.getAsteroidsDate(
                startDate.format(DateTimeFormatter.ISO_DATE),
                endDate.format(DateTimeFormatter.ISO_DATE)
            )
        ) {
            it.asDomainModelItem()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroids = AsteroidApiService.AsteroidApi.retrofitService.getAsteroids(NASA_API_KEY)
                val result = parseAsteroidsJsonResult(JSONObject(asteroids))
                database.asteroidDao.insertAll(*result.asDatabaseModelItem())
                Log.d("Refreshing Asteroids...", "Success")
            } catch (err: Exception) {
                Log.e("Failed: ", err.message.toString())
            }
        }
    }
}