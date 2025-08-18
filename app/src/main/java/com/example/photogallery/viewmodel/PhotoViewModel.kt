package com.example.photogallery.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photogallery.api.RetrofitInstance
import com.example.photogallery.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class PhotoViewModel : ViewModel() {
    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos

    init {
        fetchPhotos()
    }

    private fun fetchPhotos() {
        viewModelScope.launch {
            val response = try {
                RetrofitInstance.api.getPhotos(5, "")
            } catch (e: IOException) {
                Log.e("TAG", "IOException, could not fetch photos", e)
                return@launch
            } catch (e: HttpException) {
                Log.e("TAG", "HttpException, unsuccessful response", e)
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                _photos.value = response.body()!!
            } else {
                Log.e("TAG", "Response was not successful or body was null")
            }
        }
    }
}