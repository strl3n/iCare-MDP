package com.istts.finalproject.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.istts.finalproject.R
import com.istts.finalproject.data.local.AppDatabase
import com.istts.finalproject.data.remote.RetrofitClient
import com.istts.finalproject.data.remote.repository.AuthRepository
import com.istts.finalproject.databinding.FragmentRegisterBinding
import com.istts.finalproject.utils.Resource
import com.istts.finalproject.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getInstance(requireContext())
                val authRepository = AuthRepository(
                    apiService = RetrofitClient.instance,
                    userDao = database.userDao()
                )
                return AuthViewModel(authRepository) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInput(name, email, password, confirmPassword)) {
                authViewModel.register(name, email, password)
            }
        }

        binding.tvLoginLink.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }
    }

    private fun validateInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        when {
            name.isEmpty() -> {
                binding.etName.error = "Nama tidak boleh kosong"
                return false
            }
            email.isEmpty() -> {
                binding.etEmail.error = "Email tidak boleh kosong"
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = "Format email tidak valid"
                return false
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Password tidak boleh kosong"
                return false
            }
            password.length < 6 -> {
                binding.etPassword.error = "Password minimal 6 karakter"
                return false
            }
            confirmPassword != password -> {
                binding.etConfirmPassword.error = "Konfirmasi password tidak cocok"
                return false
            }
            else -> return true
        }
    }

    private fun observeViewModel() {
        authViewModel.registerResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnRegister.isEnabled = false
                    binding.btnRegister.text = "Memproses..."
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "Daftar"
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Registrasi berhasil! Silakan login.",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_register_to_login)
                }
                is Resource.Error -> {
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "Daftar"
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
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
