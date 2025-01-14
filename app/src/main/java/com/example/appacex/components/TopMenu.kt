package com.example.appacex.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.appacex.R
import com.example.appacex.views.getInitials


@Composable
fun TopMenu(
    displayName: String?,
    profileImagePath: String?,
    onLogoutClick: () -> Unit,
    navController: NavHostController
) {
    Log.d("TopMenu", "Profile Image Path: $profileImagePath")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.2f))
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { navController.navigate("profile") }
        ) {
            if (profileImagePath.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getInitials(displayName),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            } else {
                Image(
                    painter = rememberImagePainter(data = profileImagePath),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = displayName ?: "Unknown",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
        IconButton(onClick = { onLogoutClick() }) {
            Icon(
                painter = painterResource(id = R.drawable.logout),
                contentDescription = "Logout",
                tint = Color.White
            )
        }
    }
}

@Composable
fun LogoutConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Confirmar Desconexión") },
        text = { Text(text = "¿Estas seguro que quieres salir?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Yes", color = Color(0xFF007AFF)) // iPhone style color
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = Color(0xFF007AFF)) // iPhone style color
            }
        }
    )
}