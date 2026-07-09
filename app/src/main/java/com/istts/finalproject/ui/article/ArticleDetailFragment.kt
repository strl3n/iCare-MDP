package com.istts.finalproject.ui.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.istts.finalproject.databinding.FragmentArticleDetailBinding

// Fragment ini murni tampilkan data yang dikirim lewat Safe Args dari ArticleListFragment.
// Tidak perlu ViewModel/Repository lagi di sini, karena semua data artikel
// (baik dari mock lokal maupun dari BoredAPI) sudah lengkap dikirim lewat argument.
class ArticleDetailFragment : Fragment() {

    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ArticleDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvArticleTitle.text = args.articleTitle
        binding.tvArticleCategory.text = args.articleCategory
        binding.tvArticleContent.text = args.articleContent
        binding.tvArticleDate.text = args.articleDate
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
