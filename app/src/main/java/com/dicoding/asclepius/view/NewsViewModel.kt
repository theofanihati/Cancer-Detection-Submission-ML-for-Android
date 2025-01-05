package com.dicoding.asclepius.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.data.response.NewsResponse
import com.dicoding.asclepius.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class NewsViewModel : ViewModel() {
    private val _news = MutableLiveData<List<ArticlesItem>>()
    val events: LiveData<List<ArticlesItem>> get() = _news

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> get() = _errorMessage

    companion object {
        private const val TAG = "NewsViewModel"
    }

    fun getNewsData() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getNews(
            "cancer",
            "health",
            "en",
            "1adfa2681e6947dbbdd6938fabfd198c")
        client.enqueue(object : Callback<NewsResponse> {

            override fun onResponse(
                call: Call<NewsResponse>,
                response: Response<NewsResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.d(TAG, "Data yang diterima: ${responseBody.articles}")
                        _news.value = responseBody.articles
                    } else {
                        Log.e(TAG, "Response body NULLLLLLLL")
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _errorMessage.value = when (response.code()) {
                        400 -> "Bad Request. Terdapat kesalahan penulisan"
                        401 -> "Unauthorized. Log in terlebih dahulu"
                        403 -> "Forbidden. Akses ditolak."
                        404 -> "Not Found. Halaman tidak ditemukan."
                        500 -> "Internal Server Error. Coba beberapa saat lagi."
                        else -> "Kesalahan tidak diketahui. Kode status: $response.code()"
                    }
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                if (t is IOException) {
                    _errorMessage.value = "koneksi internet terganggu"
                } else {
                    _errorMessage.value = "Error: ${t.message}"
                }
            }
        })
    }

}