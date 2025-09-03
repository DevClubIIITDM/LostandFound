package com.institute.lostandfound.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.institute.lostandfound.R
import com.institute.lostandfound.databinding.FragmentAuthBinding
import com.institute.lostandfound.viewmodel.AuthViewModel

class AuthFragment : Fragment() {
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()
    
    private lateinit var googleSignInClient: GoogleSignInClient
    
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleGoogleSignInResult(task)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupGoogleSignIn()
        setupClickListeners()
        observeAuthState()
    }
    
    private fun setupGoogleSignIn() {
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            
            googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        } catch (e: Exception) {
            // Handle missing client ID gracefully
            Toast.makeText(context, "Google Sign-In not configured. Please check Firebase setup.", Toast.LENGTH_LONG).show()
            binding.buttonGoogleSignIn.isEnabled = false
            binding.buttonGoogleSignIn.text = "Google Sign-In Not Available"
        }
    }

    private fun setupClickListeners() {
        binding.buttonGoogleSignIn.setOnClickListener {
            if (::googleSignInClient.isInitialized) {
                signInWithGoogle()
            } else {
                Toast.makeText(context, "Google Sign-In not configured. Please check Firebase setup.", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun signInWithGoogle() {
        try {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                // Validate email domain
                val email = account.email
                if (email != null && email.endsWith("@iiitdm.ac.in")) {
                    viewModel.signInWithGoogle(idToken)
                } else {
                    Toast.makeText(context, "Only @iiitdm.ac.in emails are allowed", Toast.LENGTH_LONG).show()
                    googleSignInClient.signOut()
                }
            } else {
                Toast.makeText(context, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google Sign-In failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeAuthState() {
        viewModel.authState.observe(viewLifecycleOwner) { authState ->
            when (authState) {
                is AuthViewModel.AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonGoogleSignIn.isEnabled = false
                }
                is AuthViewModel.AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonGoogleSignIn.isEnabled = true
                    
                    Toast.makeText(context, "Authentication successful!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_auth_to_home)
                }
                is AuthViewModel.AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonGoogleSignIn.isEnabled = true
                    
                    Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonGoogleSignIn.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
