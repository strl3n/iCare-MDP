package com.istts.finalproject.ui.article

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.istts.finalproject.R
import com.istts.finalproject.data.remote.repository.ArticleRepository
import com.istts.finalproject.databinding.FragmentArticleListBinding
import com.istts.finalproject.ui.article.adapter.ArticleAdapter
import com.istts.finalproject.viewmodel.ArticleViewModel

class ArticleListFragment : Fragment() {

    private var _binding: FragmentArticleListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ArticleAdapter

    private val articleViewModel: ArticleViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val articleRepository = ArticleRepository(
                    apiService = com.istts.finalproject.data.remote.RetrofitClient.instance
                )
                return ArticleViewModel(articleRepository) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun setupSearch() {
        binding.etSearchArticle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                articleViewModel.searchArticles(s?.toString()?.trim() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = ArticleAdapter { article ->
            val action = ArticleListFragmentDirections
                .actionArticleListToArticleDetail(
                    articleId = article.id,
                    articleTitle = article.title,
                    articleCategory = article.category,
                    articleContent = article.content,
                    articleDate = article.date
                )
            findNavController().navigate(action)
        }
        binding.rvArticles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvArticles.adapter = adapter
    }

    private fun observeViewModel() {
        articleViewModel.articles.observe(viewLifecycleOwner) { articles ->
            adapter.submitList(articles)
            binding.tvArticleEmpty.visibility = if (articles.isEmpty()) View.VISIBLE else View.GONE
            binding.rvArticles.visibility = if (articles.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}