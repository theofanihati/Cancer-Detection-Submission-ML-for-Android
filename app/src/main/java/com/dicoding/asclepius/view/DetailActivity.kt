package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.data.response.NewsResponse
import com.dicoding.asclepius.data.retrofit.ApiConfig
import com.dicoding.asclepius.databinding.ActivityDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsTitle = intent.getStringExtra("news_title")
        Log.d(TAG, "title news yang diterima: $newsTitle")
        if(!newsTitle.isNullOrEmpty()) {
            showLoading(true)
            fetchEvents(newsTitle)
        }
    }

    private fun fetchEvents(newsTitle:String) {
        val client = ApiConfig.getApiService().getNews(
            query = "cancer",
            category = "health",
            language = "en",
            apiKey = "1adfa2681e6947dbbdd6938fabfd198c"
        )

        client.enqueue(object : Callback<NewsResponse> {

            override fun onResponse(
                call: Call<NewsResponse>,
                response: Response<NewsResponse>
            ) {
                showLoading(false)
                Log.d(TAG, "Response Code: ${response.code()}")
                Log.d(TAG, "Raw Response Body: ${response.errorBody()?.string()}")
                Log.d(TAG, "Response Body: ${response.body()}")
                if (response.isSuccessful) {
                    val articles = response.body()?.articles ?: emptyList()
                    val Article = articles.find { it.title.equals(newsTitle, ignoreCase = true) }
                    if (Article != null) {
                        setEventData(Article)
                    } else {
                        Toast.makeText(this@DetailActivity, "News not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    Toast.makeText(this@DetailActivity, "Gagal Mengambil Data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(this@DetailActivity, "Gagal Mengakses API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setEventData(news: ArticlesItem) {

        Log.d(TAG, "Received Event: $news")

        Glide.with(this)
            .load(news.urlToImage)
            .into(binding.ivNewsImage)
        binding.tvNewsTitle.text = news.title
        binding.tvPublishedAt.text = news.publishedAt
        binding.tvAuthor.text = news.author
        binding.tvDescription.text = news.description

        binding.btnLink.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(news.url)))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "DetailActivity"
    }
}

