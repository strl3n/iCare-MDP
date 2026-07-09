package com.istts.finalproject.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.istts.finalproject.R
import com.istts.finalproject.data.local.AppDatabase
import com.istts.finalproject.data.remote.repository.AuthRepository
import com.istts.finalproject.databinding.FragmentLoginBinding
import com.istts.finalproject.utils.Resource
import com.istts.finalproject.utils.SessionManager
import com.istts.finalproject.viewmodel.AuthViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    private val authViewModel: AuthViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getInstance(requireContext())
                val authRepository = AuthRepository(
                    apiService = com.istts.finalproject.data.remote.RetrofitClient.instance,
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        setupGoogleSignIn()
        setupListeners()
        observeViewModel()

        if (sessionManager.isLoggedIn()) {
            navigateToDashboard()
        }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (validateInput(email, password)) {
                authViewModel.login(email, password)
            }
        }

        binding.btnGoogleLogin.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }

        binding.tvRegisterLink.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        when {
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
            else -> return true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
                account?.let {
                    authViewModel.googleLogin(it.email ?: "", it.displayName ?: "User", it.photoUrl?.toString())
                }
            } catch (e: ApiException) {
                Toast.makeText(
                    requireContext(),
                    "Google Sign-In gagal (kode ${e.statusCode}): ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun observeViewModel() {
        authViewModel.authResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnLogin.isEnabled = false
                    binding.btnLogin.text = "Memproses..."
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Login"
                    binding.progressBar.visibility = View.GONE
                    resource.data?.let { (user, token) ->
                        // Clear session sebelumnya sebelum menyimpan yang baru
                        sessionManager.clearSession()
                        sessionManager.saveUserSession(user, token)
                        Toast.makeText(requireContext(), "Selamat datang, ${user.name}!", Toast.LENGTH_SHORT).show()
                        navigateToDashboard()
                    }
                }
                is Resource.Error -> {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Login"
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun navigateToDashboard() {
        findNavController().navigate(R.id.action_login_to_dashboard)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}