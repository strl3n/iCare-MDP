package com.istts.finalproject.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.istts.finalproject.databinding.FragmentEditProfileBinding
import com.istts.finalproject.utils.SessionManager

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val name = sessionManager.getUserName() ?: "User"
        val email = sessionManager.getUserEmail() ?: ""

        binding.etEditName.setText(name)
        binding.etEditEmail.setText(email)

        // Set avatar initial
        val initial = name.firstOrNull()?.toString()?.uppercase() ?: "U"
        binding.tvAvatarInitial.text = initial
    }

    private fun setupListeners() {
        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.etEditName.text.toString().trim()
            if (newName.isEmpty()) {
                binding.etEditName.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }

            // Update SharedPreferences
            val editor = sessionManager.getSharedPreferences().edit()
            editor.putString(SessionManager.KEY_USER_NAME, newName)
            editor.apply()

            Toast.makeText(requireContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        binding.btnCancelEdit.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}