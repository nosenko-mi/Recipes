package com.ltl.recipes.ui.compose.user_profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
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
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
//    IconButton(onClick = {
//        Log.d(TAG, "Pop back stack")
//        onNavigate(R.id.action_userProfileFragment_to_mainFragment)
//    }) {
//        Icon(
//            imageVector = Icons.Default.ArrowBack,
//            contentDescription = "Back button")
//    }
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

//                Text(
//                    text = userModel.value.displayName.toString(),
//                    fontSize = 30.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier
//                        .align(Alignment.TopEnd)
//                )
//                Spacer(modifier = Modifier.height(64.dp))
//                Text(
//                    text = userModel.value.email,
//                    modifier = Modifier
//                        .align(Alignment.BottomEnd)
//                )

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
        
            Text(
                text = BuildConfig.VERSION_NAME,
                fontSize = 10.sp,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(0.dp, 32.dp)
            )

        }
    }
}

@Composable
fun CardButton(
    title: String,
    leadingIcon: ImageVector?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 20.sp,
            )
        }
    }
}