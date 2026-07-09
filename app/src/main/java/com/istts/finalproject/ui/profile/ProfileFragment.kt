package com.istts.finalproject.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.istts.finalproject.R
import com.istts.finalproject.databinding.FragmentProfileBinding
import com.istts.finalproject.utils.SessionManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
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
        val email = sessionManager.getUserEmail() ?: "email@example.com"
        val isGoogleLogin = sessionManager.isGoogleLogin()

        binding.tvProfileName.text = name
        binding.tvProfileEmail.text = email
        binding.tvLoginType.text = if (isGoogleLogin) "Login dengan Google" else "Login dengan Email"

        val initial = name.firstOrNull()?.toString()?.uppercase() ?: "U"
        binding.tvAvatarInitial.text = initial
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            // Cukup clear session saja. JANGAN hapus data mood lokal —
            // supaya history mood user tetap ada saat mereka login lagi nanti.
            sessionManager.clearSession()
            Toast.makeText(requireContext(), "Berhasil logout", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_profile_to_login)
        }

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_edit_profile)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
