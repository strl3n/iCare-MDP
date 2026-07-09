package com.istts.finalproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istts.finalproject.data.remote.repository.Article
import com.istts.finalproject.data.remote.repository.ArticleRepository
import com.istts.finalproject.data.remote.model.Quote
import com.istts.finalproject.utils.Resource
import kotlinx.coroutines.launch

class ArticleViewModel(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    private val _quote = MutableLiveData<Resource<Quote>>()
    val quote: LiveData<Resource<Quote>> = _quote

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles

    private val _isLoadingArticles = MutableLiveData<Boolean>()
    val isLoadingArticles: LiveData<Boolean> = _isLoadingArticles

    // Cache gabungan (lokal + BoredAPI) dipakai untuk search, supaya tidak fetch ulang tiap ketik
    private var cachedArticles: List<Article> = emptyList()

    init {
        _quote.value = Resource.Success(
            Quote(
                quote = "Kesehatan mental bukanlah tujuan, tapi proses. Mari jaga kesehatan mental kita bersama.",
                author = "iCare"
            )
        )
        loadArticles()
    }

    fun getRandomQuote() {
        viewModelScope.launch {
            _quote.value = Resource.Loading()
            val result = articleRepository.getRandomQuote()
            _quote.value = result
        }
    }

    // Tampilkan artikel lokal dulu (langsung ada), lalu tambahkan artikel dari BoredAPI
    // begitu selesai di-fetch. Kalau BoredAPI gagal/timeout, artikel lokal tetap tampil.
    fun loadArticles() {
        val local = articleRepository.getLocalArticles()
        cachedArticles = local
        _articles.value = local

        viewModelScope.launch {
            _isLoadingArticles.value = true
            val remoteResult = articleRepository.getRemoteArticles()
            if (remoteResult is Resource.Success && !remoteResult.data.isNullOrEmpty()) {
                val combined = local + remoteResult.data
                cachedArticles = combined
                _articles.value = combined
            }
            _isLoadingArticles.value = false
        }
    }

    fun searchArticles(query: String) {
        if (query.isNotEmpty()) {
            _articles.value = cachedArticles.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
            }
        } else {
            _articles.value = cachedArticles
        }
    }

    fun getArticleById(articleId: Int): Article? {
        return cachedArticles.find { it.id == articleId }
    }
}
