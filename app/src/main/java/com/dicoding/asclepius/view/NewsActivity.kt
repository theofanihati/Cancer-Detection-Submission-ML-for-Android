package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var binding: ActivityNewsBinding
    private var originalEventList: List<ArticlesItem> = emptyList()

    companion object {
        private const val TAG = "NewsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)

        binding.rvNews.layoutManager = LinearLayoutManager(this)

        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            binding.searchBar.setText(binding.searchView.text)
            binding.searchView.hide()
            searchEvent(binding.searchView.text.toString())
            true
        }

        newsViewModel.events.observe(this) { listEvents ->
            setEventData(listEvents)
        }

        newsViewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        newsViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        newsViewModel.getNewsData()
    }

    private fun searchEvent(query: String) {
        val filteredList = originalEventList.filter { news ->
            news.title.contains(query, ignoreCase = true)
        }
        val adapter = NewsAdapter()
        adapter.submitList(filteredList)
        binding.rvNews.adapter = adapter

        adapter.setOnItemClickCallback(object : NewsAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ArticlesItem) {
                showSelectedEvent(data)
            }
        })
    }

    private fun setEventData(listEvents: List<ArticlesItem>) {
        originalEventList = listEvents
        val adapter = NewsAdapter()
        adapter.submitList(listEvents)
        binding.rvNews.adapter = adapter

        adapter.setOnItemClickCallback(object : NewsAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ArticlesItem) {
                showSelectedEvent(data)
            }
        })
    }

    private fun showSelectedEvent(news: ArticlesItem) {
        Log.d(TAG, "Title yang akan dikirim: ${news.title}")
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("news_title", news.title)
        }
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}