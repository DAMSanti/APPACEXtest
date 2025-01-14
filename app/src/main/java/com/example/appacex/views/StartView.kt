package com.example.appacex.views

import Calendario
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.appacex.components.BottomMenuBar
import com.example.appacex.components.LogoutConfirmationDialog
import com.example.appacex.components.TopMenu
import com.microsoft.graph.requests.GraphServiceClient
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.appacex.components.MapScreen
import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.RetrofitClient
import com.microsoft.graph.models.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StartView(
    account: IAccount?,
    displayName: String?,
    profileImageUrl: String?,
    onLogoutClick: () -> Unit,
    navController: NavHostController,
    authenticationResult: IAuthenticationResult?,
    accessToken: String,
    calendarId: String
) {
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var actividades by remember { mutableStateOf<List<ActividadResponse>>(emptyList()) }

    if (showDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showDialog = false
                onLogoutClick()
            },
            onDismiss = { showDialog = false }
        )
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                if (response.isSuccessful) {
                    val today = LocalDate.now()
                    val upcomingActividades = response.body()?.filter {
                        val activityDate = LocalDate.parse(it.fini, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        activityDate.isAfter(today.minusDays(1)) && activityDate.isBefore(today.plusDays(30))
                    } ?: emptyList()
                    actividades = upcomingActividades
                }
            } catch (e: Exception) {
                Log.e("StartView", "Error fetching activities", e)
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
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
                TopMenu(displayName, profileImageUrl, { showDialog = true }, navController)
                Spacer(modifier = Modifier.height(16.dp))
                ActividadSlider(actividades)
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        MapScreen(modifier = Modifier.weight(1f))
                        Calendario(accessToken, calendarId)
                    }
                }
                BottomMenuBar(modifier = Modifier.align(Alignment.CenterHorizontally), navController = navController)
            }
        }
    }
}

@Composable
fun ActividadSlider(actividades: List<ActividadResponse>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        items(actividades.size) { index ->
            val actividad = actividades[index]
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .width(200.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = actividad.titulo,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = actividad.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun EventSlider(events: List<Event>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        items(events.size) { index ->
            val event = events[index]
            Card(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .width(200.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = event.subject ?: "No Title",
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = event.start?.dateTime ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
fun getInitials(name: String?): String {
    return name?.split(" ")?.mapNotNull { it.firstOrNull()?.toString() }?.take(2)?.joinToString("")?.uppercase() ?: "?"
}

