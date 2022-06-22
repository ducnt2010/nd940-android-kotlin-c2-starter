package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository

import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MainViewModel"

    private val asteroidsDatabase = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(asteroidsDatabase)

    val asteroidList=asteroidsRepository.asteroids

    private val _navigateToDetailAsteroid = MutableLiveData<Asteroid>()
    val navigateToDetailAsteroid: LiveData<Asteroid>
        get() = _navigateToDetailAsteroid

    init {
        viewModelScope.launch { asteroidsRepository.refreshAsteroids() }
    }


    fun displayDetailAsteroid(asteroid: Asteroid) {
        _navigateToDetailAsteroid.value = asteroid
    }

    fun displayDetailAsteroidComplete() {
        _navigateToDetailAsteroid.value = null
    }

    class Factory(val app:Application):ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)){
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct ViewModel")
        }
    }
}
