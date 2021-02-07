package com.udacity.asteroidradarapp.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradarapp.model.Asteroid
import com.udacity.asteroidradarapp.model.PictureOfDay
import com.udacity.asteroidradarapp.api.AsteroidApiService
import com.udacity.asteroidradarapp.database.getDatabase
import com.udacity.asteroidradarapp.repository.AsteroidRepository
import com.udacity.asteroidradarapp.util.AsteroidItem
import com.udacity.asteroidradarapp.util.Constants.NASA_API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay> get() = _pictureOfDay

    private val _navigateToDetailAsteroid = MutableLiveData<Asteroid>()
    val navigateToDetailAsteroid: LiveData<Asteroid> get() = _navigateToDetailAsteroid

    private var _filterAsteroid = MutableLiveData(AsteroidItem.ALL)

    val asteroidList = Transformations.switchMap(_filterAsteroid) {
        when (it!!) {
            AsteroidItem.WEEK -> {
                asteroidRepository.weekAsteroidsList
            }
            AsteroidItem.TODAY -> {
                asteroidRepository.todayAsteroidsList
            }
            else -> {
                asteroidRepository.allAsteroidsList
            }
        }
    }

    init {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
            refreshPictureOfDay()
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetailAsteroid.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToDetailAsteroid.value = null
    }

    fun onChangeFilter(filter: AsteroidItem) {
        _filterAsteroid.postValue(filter)
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct the ViewModel")
        }
    }

    private suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO) {
            try {
                _pictureOfDay.postValue(
                    AsteroidApiService.AsteroidApi.retrofitService.getPictureOfTheDay(NASA_API_KEY)
                )
            } catch (err: Exception) {
                Log.e("refreshPictureOfDay", err.printStackTrace().toString())
            }
        }
    }
}