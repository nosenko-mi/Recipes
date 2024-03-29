package com.ltl.recipes.ui.compose.user_profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ltl.recipes.BuildConfig
import com.ltl.recipes.R
import com.ltl.recipes.data.user.UserModel

private const val TAG = "UserProfileComposable"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userModel: State<UserModel>,
    onNavigate: (Int) -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        Log.d("UserProfileScreen", "$userModel")
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { stringResource(R.string.profile) },
                navigationIcon = {
                    IconButton(onClick = {
                        Log.d(TAG, "Pop back stack")
                        onNavigate(R.id.action_userProfileFragment_to_mainFragment)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back button"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .background(color = MaterialTheme.colorScheme.background)

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                if (userModel.value.profilePictureUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(userModel.value.profilePictureUrl)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column(
                    modifier = Modifier
                ) {
                    Text(
                        text = userModel.value.displayName.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        text = userModel.value.email,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1

                    )
                }

            }
            Spacer(modifier = Modifier.height(32.dp))
            Divider()
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = { onSignOut() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(imageVector = Icons.Outlined.Logout, contentDescription = "log out")
                Text(text = stringResource(R.string.log_out))
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    showDeleteDialog = showDeleteDialog.not()
//                onDeleteAccount()
                }) {
                Text(text = stringResource(R.string.delete_account))
            }

            Text(
                text = BuildConfig.VERSION_NAME,
                fontSize = 10.sp,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(0.dp, 8.dp)
            )

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(stringResource(R.string.are_you_sure_you_want_to_delete_account)) },
                    text = { Text(stringResource(R.string.this_action_cannot_be_undone)) },
                    confirmButton = {
                        TextButton(onClick = {
                            Log.d("UserProfileScreen", "delete account start")
                            onDeleteAccount()
                            showDeleteDialog = false
                        }) {
                            Text(stringResource(R.string.delete).uppercase())
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(stringResource(R.string.cancel).uppercase())
                        }
                    },
                )
            }

        }
    }
}