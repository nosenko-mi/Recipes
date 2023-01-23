package com.ltl.recipes.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ltl.recipes.AppConfig
import com.ltl.recipes.R
import com.ltl.recipes.data.user.UserModel
import com.ltl.recipes.databinding.FragmentLoginBinding
import java.util.*


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var account: GoogleSignInAccount
    private lateinit var mAuth: FirebaseAuth

    private var googleSignInLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                account = task.getResult(ApiException::class.java)

                firebaseAuthWithGoogle(account)
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

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnSuccessListener {

                val userModel = UserModel(
                    acct.displayName!!,
                    acct.email!!
                )

                updateUiWithUser(userModel)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Authentication failed.",Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUiWithUser(userModel: Any) {
        goToMainFragment()
    }

    private fun goToMainFragment() {
        view?.let { Navigation.findNavController(it).navigate(R.id.loginFragmentToMainFragment) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        mAuth = FirebaseAuth.getInstance()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            Log.d(TAG, currentUser.email.toString())

            val userModel = UserModel(currentUser.displayName.toString(), currentUser.email.toString())

            updateUiWithUser(userModel)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(
                Objects.requireNonNull(AppConfig.getConfigValue(context, "web_client_id"))
                .toString())
            .build()


        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.loginGoogleButton.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }



    companion object {
        private const val TAG = "Login"
    }
}