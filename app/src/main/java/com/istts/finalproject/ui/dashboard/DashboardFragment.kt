package com.istts.finalproject.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.istts.finalproject.R
import com.istts.finalproject.data.local.AppDatabase
import com.istts.finalproject.data.remote.repository.ArticleRepository
import com.istts.finalproject.data.remote.repository.MoodRepository
import com.istts.finalproject.databinding.FragmentDashboardBinding
import com.istts.finalproject.utils.Resource
import com.istts.finalproject.utils.SessionManager
import com.istts.finalproject.viewmodel.ArticleViewModel
import com.istts.finalproject.viewmodel.MoodViewModel
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    private val moodViewModel: MoodViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getInstance(requireContext())
                val moodRepository = MoodRepository(
                    apiService = com.istts.finalproject.data.remote.RetrofitClient.instance,
                    moodDao = database.moodDao()
                )
                return MoodViewModel(moodRepository) as T
            }
        }
    }

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
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        setupUI()
        observeViewModels()
        loadData()
    }

    private fun setupUI() {
        val userName = sessionManager.getUserName() ?: "User"
        val greeting = getGreeting()
        binding.tvGreeting.text = greeting
        binding.tvUserName.text = userName

        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile)
        }
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..4 -> "Selamat Malam"
            in 5..10 -> "Selamat Pagi"
            in 11..14 -> "Selamat Siang"
            in 15..17 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    private fun loadData() {
        val userId = sessionManager.getUserId()
        val token = sessionManager.getToken()

        // Log untuk debugging
        Log.d("DashboardFragment", "Current User ID: $userId")
        Log.d("DashboardFragment", "Token: ${token?.take(20)}...")

        if (userId != -1 && token != null) {
            moodViewModel.getLatestMood(userId)
            moodViewModel.getWeeklyAverage(userId)
            moodViewModel.getMoodStats(userId)
        } else {
            // Reset ke default jika belum login
            resetMoodUI()
        }

        articleViewModel.getRandomQuote()
    }

    private fun resetMoodUI() {
        binding.tvMoodEmoji.text = "😐"
        binding.tvMoodLevel.text = "Belum tercatat"
        binding.tvMoodNote.text = "Catat moodmu hari ini!"
        binding.tvMoodDate.text = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID")).format(Date())
        binding.tvWeeklyAverage.text = "-"
        binding.tvTotalMoods.text = "0"
    }

    private fun observeViewModels() {
        moodViewModel.latestMood.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { mood ->
                        Log.d("DashboardFragment", "Latest Mood: ${mood.moodLevel} for user ${mood.userId}")
                        binding.tvMoodEmoji.text = getMoodEmoji(mood.moodLevel)
                        binding.tvMoodLevel.text = "${mood.moodLevel}/10"
                        binding.tvMoodNote.text = mood.note ?: "Belum ada catatan"
                        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
                        binding.tvMoodDate.text = dateFormat.format(mood.date)
                    } ?: run {
                        resetMoodUI()
                    }
                }
                else -> {
                    resetMoodUI()
                }
            }
        }

        moodViewModel.weeklyAverage.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                val average = resource.data
                binding.tvWeeklyAverage.text = if (average != null && average > 0) {
                    String.format("%.1f", average)
                } else "-"
            } else {
                binding.tvWeeklyAverage.text = "-"
            }
        }

        moodViewModel.moodStats.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                binding.tvTotalMoods.text = resource.data?.totalEntries?.toString() ?: "0"
            } else {
                binding.tvTotalMoods.text = "0"
            }
        }

        articleViewModel.quote.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { quote ->
                        if (quote.quote.isNotEmpty() && quote.quote != "Test") {
                            binding.tvQuote.text = quote.quote
                            binding.tvQuoteAuthor.text = quote.author
                        }
                    }
                }
                else -> {
                    // Keep default quote
                }
            }
        }
    }

    private fun getMoodEmoji(level: Int): String {
        return when (level) {
            1 -> "😢"
            2 -> "😞"
            3 -> "😟"
            4 -> "😕"
            5 -> "😐"
            6 -> "🙂"
            7 -> "😊"
            8 -> "😄"
            9 -> "😁"
            10 -> "🥰"
            else -> "😐"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}