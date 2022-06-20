package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MainViewModel : ViewModel() {
    private val TAG = "MainViewModel"


    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _property = MutableLiveData<List<Asteroid>>()
    val property: LiveData<List<Asteroid>>
        get() = _property

    init {
        refreshPictureOfDay()
    }

    private fun refreshPictureOfDay() {
        Log.i(TAG, "getData: ")

        viewModelScope.launch {
            Log.i(TAG, "refreshPictureOfDay: Start")
            try {
                val value = AsteroidApi.retrofitService.getPictureOfDay()
                if (value.mediaType == "image") {
                    _pictureOfDay.value = value
                }
                Log.i(TAG, "refreshPictureOfDay: End")
            } catch (e: Exception) {

            }

        }
        Log.i(TAG, "getData: X ")

    }
}