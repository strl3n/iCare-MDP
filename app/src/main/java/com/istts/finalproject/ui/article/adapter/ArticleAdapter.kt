package com.istts.finalproject.ui.article.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.istts.finalproject.data.remote.repository.Article
import com.istts.finalproject.databinding.ItemArticleBinding

class ArticleAdapter(
    private val onItemClick: (Article) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private var articles: List<Article> = emptyList()

    fun submitList(newList: List<Article>) {
        articles = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    class ArticleViewHolder(
        private val binding: ItemArticleBinding,
        private val onItemClick: (Article) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.tvArticleTitle.text = article.title
            binding.tvArticleCategory.text = article.category
            binding.tvArticleDescription.text = article.description

            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }
    }
}