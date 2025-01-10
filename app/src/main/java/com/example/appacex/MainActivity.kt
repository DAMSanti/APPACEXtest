package com.example.appacex

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.appacex.ui.theme.AppACEXTheme
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalClientException
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.exception.MsalServiceException
import com.microsoft.identity.client.exception.MsalUiRequiredException

class MainActivity : ComponentActivity() {
    private lateinit var msalApp: ISingleAccountPublicClientApplication
    private var isMsalAppInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppACEXTheme {
                SignInButton(onClick = {
                    Log.d("MainActivity", "SignIn button clicked")
                    if (isMsalAppInitialized) signIn() else showInitializationError()
                })
            }
        }

        PublicClientApplication.createSingleAccountPublicClientApplication(
            this,
            R.raw.auth_config_single_account,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    msalApp = application
                    isMsalAppInitialized = true
                    Log.d("MainActivity", "MSAL app initialized")
                }

                override fun onError(exception: MsalException) {
                    Log.e("MainActivity", "Error initializing MSAL app", exception)
                }
            }
        )
    }

    @Composable
    fun SignInButton(onClick: () -> Unit) {
        Button(onClick = onClick) {
            Text(text = "Sign In")
        }
    }

    private fun signIn() {
        Log.d("MainActivity", "signIn method called")
        msalApp.signIn(this, "", arrayOf("User.Read"), object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                Log.d("MainActivity", "Authentication successful")
                // Handle successful authentication
            }

            override fun onError(exception: MsalException) {
                Log.e("MainActivity", "Authentication error", exception)
                // Handle error
                when (exception) {
                    is MsalClientException -> {
                        // Exception inside MSAL, more info inside MsalError.java
                    }
                    is MsalServiceException -> {
                        // Exception when communicating with the STS, likely config issue
                    }
                    is MsalUiRequiredException -> {
                        // Tokens expired or no session, retry interactive auth
                    }
                    else -> {
                        // Other exceptions
                    }
                }
            }

            override fun onCancel() {
                Log.d("MainActivity", "Authentication canceled")
                // Handle cancel
            }
        })
    }

    private fun showInitializationError() {
        Log.e("MainActivity", "MSAL app not initialized")
        // Show an error message to the user indicating that MSAL is not initialized
    }
}