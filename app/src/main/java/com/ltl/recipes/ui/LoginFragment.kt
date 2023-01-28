package com.ltl.recipes.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ltl.recipes.AppConfig
import com.ltl.recipes.R
import com.ltl.recipes.data.user.UserModel
import com.ltl.recipes.data.user.UserRegistrationRequest
import com.ltl.recipes.data.user.UserRepository
import com.ltl.recipes.data.user.UserViewModel
import com.ltl.recipes.databinding.FragmentLoginBinding
import java.util.*


class LoginFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentLoginBinding

    private val userRepository = UserRepository()

    private lateinit var account: GoogleSignInAccount
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val userViewModel: UserViewModel by navGraphViewModels(R.id.nav_graph)

    private var googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                account = task.getResult(ApiException::class.java)

                authWithGoogle(account)
                Log.e(TAG, "GoogleSignIn: success")

            } catch (e: ApiException) {
                Log.e(TAG, "GoogleSignIn: error $e")
            }
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, change activity.
            val userModel = UserModel(
                account.displayName!!,
                account.email!!
            )

            updateUiWithUser(userModel)
        } catch (e: ApiException) {
            Log.e(TAG, "GoogleSignIn: error $e")
        }
    }

    private fun authWithEmail(user: UserRegistrationRequest){
        firebaseAuth.signInWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val currentUser = UserModel(
                        firebaseAuth.currentUser?.displayName.toString(),
                        firebaseAuth.currentUser?.email.toString()
                    )
                    updateUiWithUser(currentUser)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun authWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                val userModel = UserModel(acct.displayName!!, acct.email!!)
                userRepository.addUser(userModel)
                updateUiWithUser(userModel)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Authentication: failed.",Toast.LENGTH_SHORT).show()
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

        firebaseAuth = FirebaseAuth.getInstance()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            Log.d(TAG, currentUser.email.toString())
            val userModel = UserModel(currentUser.displayName.toString(), currentUser.email.toString())
            updateUiWithUser(userModel)
        }

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(
                Objects.requireNonNull(AppConfig.getConfigValue(context, "web_client_id"))
                .toString())
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

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

    private fun goToRegistrationFragment(){
        view?.let { Navigation.findNavController(it).navigate(R.id.loginFragmentToRegistrationFragment) }
    }

    companion object {
        private const val TAG = "LoginFragment"
    }

    override fun onClick(view: View) {
        when (view.id) {
            binding.loginButton.id -> {
                val user = UserRegistrationRequest(
                    binding.emailEditText.text.toString(),
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                )
                if (user.isValid()){
                    authWithEmail(user)
                } else{
//                    show errors
                    Toast.makeText(context, "Bad credentials", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "AuthEmail: bad formatting/pattern")
                }
            }
            binding.loginGoogleButton.id -> {
                val signInIntent = mGoogleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
            binding.registerTextView.id -> {
                goToRegistrationFragment()
            }
            else -> Toast.makeText(context, "else", Toast.LENGTH_SHORT).show()
        }
    }
}