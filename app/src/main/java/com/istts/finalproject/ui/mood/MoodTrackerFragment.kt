package com.istts.finalproject.ui.mood

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.istts.finalproject.data.local.AppDatabase
import com.istts.finalproject.data.remote.repository.MoodRepository
import com.istts.finalproject.databinding.FragmentMoodTrackerBinding
import com.istts.finalproject.utils.GenericViewModelFactory
import com.istts.finalproject.utils.Resource
import com.istts.finalproject.utils.SessionManager
import com.istts.finalproject.viewmodel.MoodViewModel

class MoodTrackerFragment : Fragment() {

    private var _binding: FragmentMoodTrackerBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private var currentMoodLevel = 5

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
        _binding = FragmentMoodTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.sliderMood.addOnChangeListener { _, value, _ ->
            currentMoodLevel = value.toInt()
            updateMoodDisplay(currentMoodLevel)
        }

        binding.btnSaveMood.setOnClickListener {
            val note = binding.etNote.text.toString().trim()
            val userId = sessionManager.getUserId()
            val token = sessionManager.getToken()

            Log.d("MoodTracker", "Saving mood - UserID: $userId, Level: $currentMoodLevel")

            if (userId != -1 && token != null) {
                moodViewModel.saveMood(token, userId, currentMoodLevel, note.takeIf { it.isNotEmpty() })
            } else {
                Toast.makeText(requireContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMoodDisplay(level: Int) {
        val emoji = getMoodEmoji(level)
        binding.tvMoodEmojiDisplay.text = emoji
        binding.tvMoodLevelDisplay.text = "Level $level"
        binding.sliderMood.value = level.toFloat()
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

    private fun observeViewModel() {
        moodViewModel.saveMoodResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnSaveMood.isEnabled = false
                    binding.btnSaveMood.text = "Menyimpan..."
                }
                is Resource.Success -> {
                    binding.btnSaveMood.isEnabled = true
                    binding.btnSaveMood.text = "Simpan Mood"
                    Toast.makeText(requireContext(), "Mood berhasil disimpan! 😊", Toast.LENGTH_SHORT).show()
                    binding.etNote.text?.clear()
                    binding.sliderMood.value = 5f
                    updateMoodDisplay(5)
                }
                is Resource.Error -> {
                    binding.btnSaveMood.isEnabled = true
                    binding.btnSaveMood.text = "Simpan Mood"
                    Toast.makeText(requireContext(), resource.message ?: "Gagal menyimpan mood", Toast.LENGTH_SHORT).show()
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