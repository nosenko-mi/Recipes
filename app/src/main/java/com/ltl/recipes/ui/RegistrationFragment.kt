package com.ltl.recipes.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ltl.recipes.R
import com.ltl.recipes.data.user.UserRegistrationRequest
import com.ltl.recipes.data.user.UserRepository
import com.ltl.recipes.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {

    companion object {
        private const val TAG = "RegistrationFragment"
    }

    private lateinit var binding: FragmentRegistrationBinding

    private var firebaseAuth = Firebase.auth
    private var userRepository = UserRepository()

    private val registerListener = OnClickListener {
        Toast.makeText(context, "register clicked", Toast.LENGTH_SHORT).show()
        registerUserSequence()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerButton.setOnClickListener(registerListener)
        binding.toolbar.backButton.setOnClickListener {
            popBackStack()
        }
    }

    private fun registerUserSequence(){
        val request = collectRegistrationData()
        Log.d(TAG, "RegisterSequence: request $request")
        if (!request.isValid()){
            return
        }
        createUser(request)
    }

    private fun createUser(request: UserRegistrationRequest){
        firebaseAuth.createUserWithEmailAndPassword(request.email, request.password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail: success")
                    val user = firebaseAuth.currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(request.displayName)
                        .build()

                    user?.updateProfile(profileUpdates)
                    userRepository.addUser(request)
                    goToMainFragment()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail: failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun collectRegistrationData(): UserRegistrationRequest{
        // TODO validation
        val email = binding.emailRegEditText.editText?.text.toString()
        val password = binding.passwordRegEditText.text.toString()
        val repeatedPassword = binding.repeatPasswordRegEditText.text.toString()
        Log.d(TAG, "email=$email; password=$password; repeated=$repeatedPassword")
        return UserRegistrationRequest(email, email, password)
    }

    private fun goToMainFragment(){
        view?.let { Navigation.findNavController(it).navigate(R.id.registrationFragmentToMainFragment) }
    }

    private fun popBackStack(){
        view?.let {Navigation.findNavController(it).popBackStack()}
    }
}