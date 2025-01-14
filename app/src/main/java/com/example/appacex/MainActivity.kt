package com.example.appacex

import LoginDialogFragment
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appacex.MsalAppHolder.msalApp
import com.example.appacex.model.ProfesorResponse
import com.example.appacex.model.RetrofitClient
import com.example.appacex.ui.theme.AppACEXTheme
import com.example.appacex.views.*
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.microsoft.graph.http.GraphServiceException
import com.microsoft.graph.requests.GraphServiceClient
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalClientException
import com.microsoft.identity.client.exception.MsalException
import fetchCalendarEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import com.microsoft.graph.models.Event
import java.util.concurrent.CompletableFuture

object MsalAppHolder {
    lateinit var msalApp: ISingleAccountPublicClientApplication
}

class MainActivity : AppCompatActivity(), LoginDialogFragment.LoginDialogListener {
    private lateinit var navController: NavHostController
    private var isMsalAppInitialized = false
    private var authenticationResult: IAuthenticationResult? = null
    private var displayName: String? = null
    private var profileImageUrl: String? = null
    private var isLoggedIn by mutableStateOf(false)
    private var isLoading by mutableStateOf(false)
    private var isAccessDeniedDialogShown = false
    private lateinit var db: FirebaseFirestore


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()
        setContent {
            AppACEXTheme {
                navController = rememberNavController()
                MainScreen()
            }
        }

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )

        initializeMsalApp()
        db.collection("chats").get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    Log.d("MainActivity", "DB has data: ${result.documents.size} documents found")
                } else {
                    Log.d("MainActivity", "DB is empty")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error checking DB", exception)
            }
        // Add this block to register FCM token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and send token to your server
            Log.d("FCM", "FCM Token: $token")
            sendTokenToServer(token)
        }
    }

    private fun sendTokenToServer(token: String) {
        val client = OkHttpClient()
        val json = """
            {
                "token": "$token"
            }
        """.trimIndent()

        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://your-server-url.com/register-token") // Replace with your server URL
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("MainActivity", "Failed to send token to server", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    Log.d("MainActivity", "Token sent to server successfully")
                } else {
                    Log.e("MainActivity", "Failed to send token to server: ${response.message}")
                }
            }
        })
    }
    /*private fun initializeFirestoreWithServiceAccount() {
        try {
            val serviceAccount: InputStream = assets.open("acexchat-firebase-adminsdk.json")
            val credentials = GoogleCredentials.fromStream(serviceAccount)
            val options = FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId("acexchat")
                .build()
            FirebaseApp.initializeApp(this, options)
            db = FirebaseFirestore.getInstance(FirebaseApp.getInstance())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    private fun initializeMsalApp() {
        PublicClientApplication.createSingleAccountPublicClientApplication(
            this,
            R.raw.auth_config_single_account,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    MsalAppHolder.msalApp = application
                    isMsalAppInitialized = true
                    Log.d("MainActivity", "MSAL app initialized")
                }

                override fun onError(exception: MsalException) {
                    Log.e("MainActivity", "Error initializing MSAL app", exception)
                }
            }
        )
    }

    //override fun onLoginSuccess(authenticationResult: IAuthenticationResult, displayName: String, photoPath: String) {
    //this.isLoggedIn = true
    //this.authenticationResult = authenticationResult
    //this.displayName = displayName
    //this.profileImageUrl = photoPath
    //}

    override fun onLoginSuccess(
        authenticationResult: IAuthenticationResult,
        displayName: String,
        photoPath: String
    ) {
        val email = authenticationResult.account?.username?.lowercase() ?: return
        isLoading = true

        RetrofitClient.instance.getProfesores().enqueue(object : Callback<List<ProfesorResponse>> {
            override fun onResponse(
                call: Call<List<ProfesorResponse>>,
                response: Response<List<ProfesorResponse>>
            ) {
                if (response.isSuccessful) {
                    val profesores = response.body() ?: emptyList()
                    val profesor = profesores.find { it.correo.lowercase() == email && it.activo == 1 }
                    if (profesor != null) {
                        this@MainActivity.isLoggedIn = true
                        this@MainActivity.authenticationResult = authenticationResult
                        this@MainActivity.displayName = displayName
                        this@MainActivity.profileImageUrl = photoPath
                    } else {
                        if (!isAccessDeniedDialogShown) {
                            isAccessDeniedDialogShown = true
                            showAccessDeniedDialog()
                        }
                    }
                } else {
                    Log.e("MainActivity", "API response error: ${response.code()}")
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<ProfesorResponse>>, t: Throwable) {
                Log.e("MainActivity", "API call failed", t)
                isLoading = false
            }
        })
    }

    private fun showAccessDeniedDialog() {
        isLoading = false
        AlertDialog.Builder(this)
            .setTitle("Access Denied")
            .setMessage("Lo sentimos, esta cuenta no tiene acceso a la aplicación")
            .setPositiveButton("Aceptar") { _, _ ->
                logoutAndRedirectToLogin()
                isAccessDeniedDialogShown = false
            }
            .setCancelable(false)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun MainScreen() {
        val accessToken = authenticationResult?.accessToken ?: ""
        var calendarId by remember { mutableStateOf<String?>(null) }
        var events by remember { mutableStateOf<List<Event>?>(null) }
        var isFetchingEvents by remember { mutableStateOf(false) }
        var hasFetchedEvents by remember { mutableStateOf(false) }
        Log.e("MainActivity", "accessToken: $accessToken - calendarId: $calendarId")

        LaunchedEffect(authenticationResult) {
            if (isLoggedIn && authenticationResult != null && accessToken.isNotEmpty() && !isFetchingEvents && !hasFetchedEvents) {
                isFetchingEvents = true
                calendarId = fetchCalendarId(accessToken, "ACEX")
                if (!calendarId.isNullOrEmpty()) {
                    Log.e("fetchCalendar", "Fetching events for calendar ID: $calendarId")
                    events = fetchCalendarEvents(accessToken, calendarId!!)
                    hasFetchedEvents = true
                } else {
                    Log.e("MainScreen", "Calendar ID is null or empty")
                }
                isFetchingEvents = false
            }
        }

        Log.e("MainActivity", "accessToken: $accessToken - calendarId: $calendarId")

        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "start" else "main"
        ) {
            composable("main") {
                MainView(onSignInClick = { showLoginDialog() })
            }
            composable("start") {
                StartView(
                    account = authenticationResult?.account,
                    displayName = displayName,
                    profileImageUrl = profileImageUrl,
                    onLogoutClick = { logout() },
                    navController = navController,
                    authenticationResult = authenticationResult,
                    accessToken = accessToken,
                    calendarId = calendarId ?: ""
                )
            }
            composable("profile") {
                ProfileView(
                    account = authenticationResult?.account,
                    displayName = displayName,
                    profileImageUrl = profileImageUrl,
                    onLogoutClick = { logout() },
                    navController = navController,
                    authenticationResult = authenticationResult
                )
            }
            composable("chat") {
                ChatView(
                    account = authenticationResult?.account,
                    displayName = displayName,
                    profileImageUrl = profileImageUrl,
                    onLogoutClick = { logout() },
                    navController = navController,
                    authenticationResult = authenticationResult,
                    activityId = "yourActivityId",
                    userId = displayName ?: "Unknown"
                )
            }
            composable("map") {
                MapView(
                    account = authenticationResult?.account,
                    displayName = displayName,
                    profileImageUrl = profileImageUrl,
                    onLogoutClick = { logout() },
                    navController = navController,
                    authenticationResult = authenticationResult
                )
            }
            composable("actividades") {
                ActividadesView(
                    account = authenticationResult?.account,
                    displayName = displayName,
                    profileImageUrl = profileImageUrl,
                    onLogoutClick = { logout() },
                    navController = navController,
                    authenticationResult = authenticationResult
                )
            }
            composable("chatactividades") {
                ActividadesListView(
                    displayName = displayName,
                    profileImageUrl = profileImageUrl,
                    onLogoutClick = { logout() },
                    navController = navController
                )
            }
            composable("chat/{activityId}") { backStackEntry ->
                val activityId = backStackEntry.arguments?.getString("activityId") ?: return@composable
                ChatView(
                    account = authenticationResult?.account,
                    displayName = displayName,
                    profileImageUrl = profileImageUrl,
                    onLogoutClick = { logout() },
                    navController = navController,
                    authenticationResult = authenticationResult,
                    activityId = activityId,
                    userId = displayName ?: "Unknown"
                )
            }
        }
    }

    suspend fun fetchCalendarId(accessToken: String, calendarName: String): String? {
        if (accessToken.isEmpty()) {
            Log.e("fetchCalendarId", "Access token is empty")
            return null
        }

        Log.d("fetchCalendarId", "Starting fetch with token: $accessToken")
        return try {
            withContext(Dispatchers.IO) {
                val graphClient = GraphServiceClient
                    .builder()
                    .authenticationProvider { CompletableFuture.completedFuture(accessToken) }
                    .buildClient()

                val calendars = graphClient.me().calendars().buildRequest()?.get()?.currentPage
                Log.d("fetchCalendarId", "Calendars: $calendars")

                val calendar = calendars?.find { it.name == calendarName }
                if (calendar != null) {
                    Log.d("fetchCalendarId", "Found calendar: ${calendar.name} with ID: ${calendar.id}")
                    calendar.id
                } else {
                    Log.e("fetchCalendarId", "Calendar with name $calendarName not found")
                    null
                }
            }
        } catch (e: GraphServiceException) {
            Log.e("fetchCalendarId", "Error fetching calendar ID", e)
            null
        } catch (e: Exception) {
            Log.e("fetchCalendarId", "Unexpected error", e)
            null
        }
    }

    private fun showLoginDialog() {
        val loginDialogFragment =
            LoginDialogFragment { authenticationResult, displayName, photoUrl ->
                onLoginSuccess(authenticationResult, displayName, photoUrl)
            }
        loginDialogFragment.show(supportFragmentManager, "loginDialog")
    }

    private fun logout() {
        if (isMsalAppInitialized) {
            try {
                msalApp.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                    override fun onSignOut() {
                        authenticationResult = null
                        isLoggedIn = false
                        profileImageUrl = null
                        isLoading = false
                        Log.d("MainActivity", "User logged out successfully")
                    }

                    override fun onError(exception: MsalException) {
                        Log.e("MainActivity", "Error during sign out", exception)
                    }
                })
            } catch (e: MsalClientException) {
                Log.e("MainActivity", "MSAL client exception during sign out", e)
            } catch (e: Exception) {
                Log.e("MainActivity", "Unexpected error during sign out", e)
            }
        } else {
            Log.e("MainActivity", "MSAL app is not initialized")
        }
    }

    private fun logoutAndRedirectToLogin() {
        if (isMsalAppInitialized) {
            try {
                msalApp.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                    override fun onSignOut() {
                        authenticationResult = null
                        isLoggedIn = false
                        profileImageUrl = null
                        isLoading = false
                        Log.d("MainActivity", "User logged out successfully")
                        navController.navigate("main")
                    }

                    override fun onError(exception: MsalException) {
                        Log.e("MainActivity", "Error during sign out", exception)
                        isLoading = false
                    }
                })
            } catch (e: MsalClientException) {
                Log.e("MainActivity", "MSAL client exception during sign out", e)
                isLoading = false
            } catch (e: Exception) {
                Log.e("MainActivity", "Unexpected error during sign out", e)
                isLoading = false
            }
        } else {
            Log.e("MainActivity", "MSAL app is not initialized")
            isLoading = false
        }
    }

}