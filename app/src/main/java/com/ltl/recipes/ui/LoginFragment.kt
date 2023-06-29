package com.ltl.recipes.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import com.google.android.gms.auth.api.identity.Identity
import com.ltl.recipes.R
import com.ltl.recipes.data.user.UserModel
import com.ltl.recipes.data.user.UserRegistrationRequest
import com.ltl.recipes.data.user.UserViewModel
import com.ltl.recipes.databinding.FragmentLoginBinding
import com.ltl.recipes.firebase.sign_in.FirebaseAuthUiClient
import com.ltl.recipes.firebase.sign_in.SignInViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class LoginFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentLoginBinding

    private val userViewModel: UserViewModel by navGraphViewModels(R.id.nav_graph)
    private val signInViewModel: SignInViewModel by navGraphViewModels(R.id.nav_graph)

    private val authUiClient by lazy {
        FirebaseAuthUiClient(
            context = requireContext(),
            oneTapClient = Identity.getSignInClient(requireContext())
        )
    }

    private var googleAuthLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch {
                val signInResult = authUiClient.signInWithIntent(
                    intent = result.data ?: return@launch
                )
                signInViewModel.onSignInResult(signInResult)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authUiClient.getSignedInUser()?.let {
            updateUiWithUser(it)
        }

        lifecycleScope.launch {
            signInViewModel.state.collectLatest { state ->
                if (state.isSignInSuccessful) {
                    Toast.makeText(
                        context,
                        "Sign in successful",
                        Toast.LENGTH_LONG
                    ).show()

                    authUiClient.getSignedInUser()?.let {
                        updateUiWithUser(it)
                        signInViewModel.resetState()
                    }
                }
            }
        }
        binding.loginGoogleButton.setOnClickListener(this)
        binding.loginButton.setOnClickListener(this)
        binding.registerTextView.setOnClickListener(this)
    }

    private fun updateUiWithUser(userModel: UserModel) {
        userViewModel.setNewUser(userModel)
        goToMainFragment()
    }

    private fun goToMainFragment() {
        view?.let { Navigation.findNavController(it).navigate(R.id.loginFragmentToMainFragment) }
    }

    private fun goToRegistrationFragment() {
        view?.let {
            Navigation.findNavController(it).navigate(R.id.loginFragmentToRegistrationFragment)
        }
    }

    companion object {
        private const val TAG = "LoginFragment"
    }

    override fun onClick(view: View) {
        when (view.id) {
            binding.loginButton.id -> {
                Log.d(TAG, "loginButton click")
                val user = UserRegistrationRequest(
                    binding.emailEditText.text.toString(),
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                )
                if (user.isValid()) {
                    lifecycleScope.launch {
                        val signInResult = authUiClient.signInWithEmail(user)
                        signInViewModel.onSignInResult(signInResult)

                    }
                } else {
                    Toast.makeText(context, "Bad credentials", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "AuthEmail: bad formatting/pattern")
                }
            }
            binding.loginGoogleButton.id -> {
                Log.d(TAG, "loginGoogleButton click")
                lifecycleScope.launch {
                    Log.d(TAG, "loginGoogleButton lifecycleScope.launch")
                    val signInIntentSender = authUiClient.signIn()
                    googleAuthLauncher.launch(
                        IntentSenderRequest.Builder(
                            signInIntentSender ?: return@launch
                        ).build()
                    )
                }
            }
            binding.registerTextView.id -> {
                goToRegistrationFragment()
            }
            else -> Toast.makeText(context, "else", Toast.LENGTH_SHORT).show()
        }
    }
}