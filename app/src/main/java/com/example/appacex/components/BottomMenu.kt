package com.example.appacex.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.appacex.R
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomMenuBar(modifier: Modifier = Modifier, navController: NavHostController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination?.route

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            if (index == 0) {
                IconButton(onClick = {
                    if (currentDestination != "profile") {
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile",
                        tint = Color.White
                    )
                }
            }
            else if (index == 1) {
                IconButton(onClick = {
                    if (currentDestination != "map") {
                        navController.navigate("map") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.mapa),
                        contentDescription = "Mapa",
                        tint = Color.White
                    )
                }
            }
            else if (index == 2) {
                IconButton(onClick = {
                    if (currentDestination != "start") {
                        navController.navigate("start") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.home),
                        contentDescription = "Home",
                        tint = Color.White
                    )
                }
            }
            else if (index == 3) {
                IconButton(onClick = {
                    if (currentDestination != "actividades") {
                        navController.navigate("actividades") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.actividades),
                        contentDescription = "Actividades",
                        tint = Color.White
                    )
                }
            }
            else if (index == 4) {
                IconButton(onClick = {
                    if (currentDestination != "chatactividades") {
                        navController.navigate("chatactividades") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.chat),
                        contentDescription = "Chat",
                        tint = Color.White
                    )
                }
            } else {
                IconButton(onClick = { /* Handle other button clicks */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.microsoft), // Replace with your other icons
                        contentDescription = "Home",
                        tint = Color.White
                    )
                }
            }
            if (index < 4) {
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color.LightGray)
                )
            }
        }
    }
}