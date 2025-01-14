package com.example.appacex.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.appacex.components.LogoutConfirmationDialog
import com.example.appacex.components.TopMenu
import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ActividadesListView(displayName: String?,
                        profileImageUrl: String?,
                        navController: NavHostController,
                        onLogoutClick: () -> Unit
) {
    val actividades = remember { mutableStateListOf<ActividadResponse>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                if (response.isSuccessful) {
                    val approvedActividades = response.body()?.filter { it.estado == "APROBADA" } ?: emptyList()
                    actividades.addAll(approvedActividades)
                } else {
                    errorMessage.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Exception: ${e.message}"
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
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
            TopMenu(displayName = displayName, profileImagePath = profileImageUrl, onLogoutClick = { showDialog = true }, navController = navController)
            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading.value) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage.value != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage.value ?: "Unknown error", color = Color.Red)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(actividades) { actividad ->
                        ActividadCard(actividad, navController)
                    }
                }
            }
            BottomMenuBar(modifier = Modifier.align(Alignment.CenterHorizontally), navController = navController)
        }
    }

    if (showDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showDialog = false
                onLogoutClick()
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun ActividadCard(actividad: ActividadResponse, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("chat/${actividad.id}") },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = actividad.titulo, style = MaterialTheme.typography.titleLarge)
            Text(text = actividad.descripcion, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Fecha: ${actividad.fini} - ${actividad.ffin}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Estado: ${actividad.estado}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}