package com.ltl.recipes.ui.compose.user_profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ltl.recipes.R
import com.ltl.recipes.data.user.UserModel

private const val TAG = "UserProfileComposable"

@Composable
fun UserProfileScreen(
    userModel: State<UserModel>,
    onNavigate: (Int) -> Unit,
    onSignOut: () -> Unit
)
{
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar {
                IconButton(onClick = {
                    Log.d(TAG, "Pop back stack")
                    onNavigate(R.id.action_userProfileFragment_to_mainFragment)
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back button")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Text(
                    text = userModel.value.displayName.toString(),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                )
                Spacer(modifier = Modifier.height(64.dp))
                Text(
                    text = userModel.value.email,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                )

                if (userModel.value.profilePictureUrl == null){
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .align(Alignment.CenterEnd)
                    )
                } else{
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
                            .align(Alignment.CenterEnd)
                    )
                }

            }
            Spacer(modifier = Modifier.height(32.dp))

            CardButton(title = "Log out", leadingIcon = Icons.Outlined.Logout) {
                onSignOut()
            }

            Text(
                text = "app version",
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
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = 5.dp
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