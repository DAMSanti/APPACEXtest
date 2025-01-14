package com.example.appacex.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.appacex.components.BottomMenuBar
import com.example.appacex.components.TopMenu
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult

@Composable
fun ActividadesView(
    account: IAccount?,
    displayName: String?,
    profileImageUrl: String?,
    onLogoutClick: () -> Unit,
    navController: NavHostController,
    authenticationResult: IAuthenticationResult?
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize().background(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)),
                start = Offset(0f, 0f),
                end = Offset(1000f, 1000f)
            )
        )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawPattern()
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopMenu(displayName, profileImageUrl, { showDialog = true }, navController)
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Actiidades View",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black
                    )
                }
            }
            BottomMenuBar(modifier = Modifier.align(Alignment.CenterHorizontally), navController = navController)
        }
    }
}