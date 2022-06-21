package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidsApi
import com.udacity.asteroidradar.api.PictureOfDayApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult

import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MainViewModel : ViewModel() {
    private val TAG = "MainViewModel"


    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _asteroidList = MutableLiveData<ArrayList<Asteroid> >()
    val asteroidList: LiveData<ArrayList<Asteroid> >
        get() = _asteroidList

    init {
        refreshPictureOfDay()
    }

    private fun refreshPictureOfDay() {
        Log.i(TAG, "getData: ")

        viewModelScope.launch {
            Log.i(TAG, "refreshPictureOfDay: Start")


            try {
                val value = PictureOfDayApi.retrofitService.getPictureOfDay()
                if (value.mediaType == "image") {
                    _pictureOfDay.value = value
                }
                val feed = AsteroidsApi.retrofitService.getAsteroids()
                Log.i(TAG, "refreshPictureOfDay: End $feed")
                val obj = JSONObject(feed)
                Log.i(TAG, "obj: End $obj")

                _asteroidList.value = parseAsteroidsJsonResult(JSONObject(feed))

//                for (asteroid in list) {
//                    Log.i(TAG, "Listasteroid: $asteroid")
//                }
                Log.i(TAG, "refreshPictureOfDay: End")
            } catch (e: Exception) {

            }

        }
        Log.i(TAG, "getData: X ")

    }
}