package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.PictureOfDayApi
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MainViewModel"

enum class AsteroidListFilter {
    WEEK,
    TODAY,
    SAVED
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val asteroidsDatabase = AsteroidsDatabase.getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(asteroidsDatabase)

    private val _filter = MutableLiveData<AsteroidListFilter>(AsteroidListFilter.WEEK)

    val asteroidList = Transformations.switchMap(_filter) {
        when (it) {
            AsteroidListFilter.WEEK -> asteroidsRepository.asteroidsByWeek
            AsteroidListFilter.TODAY -> asteroidsRepository.asteroidsOfDay
            else -> asteroidsRepository.savedAsteroids
        }
    }

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigateToDetailAsteroid = MutableLiveData<Asteroid>()
    val navigateToDetailAsteroid: LiveData<Asteroid>
        get() = _navigateToDetailAsteroid

    init {
//        _filter.value = AsteroidListFilter.WEEK
        viewModelScope.launch {
            refreshPictureOfDay()
            asteroidsRepository.refreshAsteroids()
        }
    }

    private suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO) {
            try {
                _pictureOfDay.postValue(PictureOfDayApi.retrofitService.getPictureOfDay())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onFilterChanged(filter: AsteroidListFilter) {
        Log.i(TAG, "onFilterChanged: $filter ")
        _filter.value = filter
    }

    fun displayDetailAsteroid(asteroid: Asteroid) {
        _navigateToDetailAsteroid.value = asteroid
    }

    fun displayDetailAsteroidComplete() {
        _navigateToDetailAsteroid.value = null
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct ViewModel")
        }
    }
}
