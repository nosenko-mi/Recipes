package com.ltl.recipes.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.gms.auth.api.identity.Identity
import com.ltl.recipes.R
import com.ltl.recipes.data.user.UserViewModel
import com.ltl.recipes.firebase.sign_in.FirebaseAuthUiClient
import com.ltl.recipes.ui.compose.user_profile.UserProfileScreen
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment() {

    private val viewModel: UserViewModel by navGraphViewModels(R.id.nav_graph)

    private val authUiClient by lazy {
        FirebaseAuthUiClient(
            context = requireContext(),
            oneTapClient = Identity.getSignInClient(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                UserProfileScreen(
                    userModel = viewModel.currentUser.collectAsState(),
                    onNavigate = { dest -> findNavController().navigate(dest) },
                    onSignOut = {
                        lifecycleScope.launch {
                            Log.d("FirebaseAuthUiClient", "Log out coroutine launched")
                            authUiClient.signOut()
                            findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToLoginFragment())
                        }
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
//        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    companion object {
        const val TAG = "UserProfileFragment"
    }
}
