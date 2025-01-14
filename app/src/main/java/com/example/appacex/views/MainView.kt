package com.example.appacex.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appacex.R
import com.example.appacex.ui.theme.AppACEXTheme

@Composable
fun MainView(onSignInClick: () -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    val insets = WindowInsets.statusBars
    val topPadding = with(LocalDensity.current) { insets.asPaddingValues().calculateTopPadding() }

    AppACEXTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawPattern()
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    var login by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var rememberMe by remember { mutableStateOf(false) }

                    TextField(
                        value = login,
                        onValueChange = { login = it },
                        label = { Text("Login") },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(0.8f)
                    )

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(0.8f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it }
                        )
                        Text(text = "Remember Me")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    LogInButton(onClick = {
                        isLoading = true
                        onSignInClick()
                    })
                    Spacer(modifier = Modifier.height(16.dp))
                    SignInButton(onClick = {
                        isLoading = true
                        onSignInClick()
                    })
                }
            }
        }
    }
}

fun DrawScope.drawPattern() {
    val baseStep = 50f
    val amplitude = 20f
    val frequency = 0.1f

    for (x in 0..size.width.toInt() step baseStep.toInt()) {
        for (y in 0..size.height.toInt() step baseStep.toInt()) {
            val offsetX = x.toFloat() + amplitude * kotlin.math.sin(frequency * y)
            val offsetY = y.toFloat() + amplitude * kotlin.math.sin(frequency * x)

            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = 5f,
                center = Offset(offsetX, offsetY)
            )
        }
    }
}

@Composable
fun LogInButton(onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3867F3)
        ),
        modifier = Modifier
            .width(175.dp)
            .height(55.dp)
    ) {
        Text(text = "Log In", fontSize = 18.sp, color = Color.LightGray)
    }
}

@Composable
fun SignInButton(onClick: () -> Unit = {}) {
    Button(onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3867F3)
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.microsoft),
            contentDescription = "Microsoft Icon",
            modifier = Modifier.padding(end = 8.dp)
                .width(120.dp)
                .height(40.dp)
        )
    }
}