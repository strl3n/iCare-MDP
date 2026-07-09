package com.istts.finalproject.ui.mood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.istts.finalproject.data.local.AppDatabase
import com.istts.finalproject.data.remote.repository.MoodRepository
import com.istts.finalproject.databinding.FragmentMoodHistoryBinding
import com.istts.finalproject.ui.mood.adapter.MoodHistoryAdapter
import com.istts.finalproject.utils.GenericViewModelFactory
import com.istts.finalproject.utils.Resource
import com.istts.finalproject.utils.SessionManager
import com.istts.finalproject.viewmodel.MoodViewModel

class MoodHistoryFragment : Fragment() {

    private var _binding: FragmentMoodHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: MoodHistoryAdapter

    private val moodViewModel: MoodViewModel by viewModels {
        GenericViewModelFactory {
            val database = AppDatabase.getInstance(requireContext())
            val moodRepository = MoodRepository(
                apiService = com.istts.finalproject.data.remote.RetrofitClient.instance,
                moodDao = database.moodDao()
            )
            MoodViewModel(moodRepository)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        setupRecyclerView()
        observeViewModel()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = MoodHistoryAdapter { mood ->
            moodViewModel.deleteMood(mood.id, sessionManager.getUserId())
        }
        binding.rvMoodHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMoodHistory.adapter = adapter
    }

    private fun loadData() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            moodViewModel.getMoodHistory(userId)
        }
    }

    private fun observeViewModel() {
        moodViewModel.moodHistory.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val moods = resource.data ?: emptyList()
                    if (moods.isEmpty()) {
                        binding.rvMoodHistory.visibility = View.GONE
                        binding.layoutEmpty.visibility = View.VISIBLE
                    } else {
                        binding.rvMoodHistory.visibility = View.VISIBLE
                        binding.layoutEmpty.visibility = View.GONE
                        adapter.submitList(moods)
                    }
                }
                is Resource.Error -> {
                    binding.rvMoodHistory.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}