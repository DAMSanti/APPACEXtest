import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.DialogFragment
import com.example.appacex.MsalAppHolder
import com.example.appacex.R
import com.microsoft.graph.requests.GraphServiceClient
import com.microsoft.identity.client.AcquireTokenParameters
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.Prompt
import com.microsoft.identity.client.exception.MsalException
import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.microsoft.graph.http.GraphErrorResponse
import com.microsoft.graph.http.GraphServiceException
import java.io.File
import java.io.FileOutputStream

class LoginDialogFragment(private val onSuccess: (IAuthenticationResult, String, String) -> Unit) : DialogFragment() {

    private var isSuccessCalled = false
    private lateinit var listener: LoginDialogListener
    private var isLoading = mutableStateOf(false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as LoginDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Theme_AppACEX_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parameters = AcquireTokenParameters.Builder()
            .startAuthorizationFromActivity(requireActivity())
            .withScopes(listOf("User.Read", "Calendars.Read")) // Add the required permissions
            .withPrompt(Prompt.SELECT_ACCOUNT)
            .withCallback(object : AuthenticationCallback {
                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    if (!isSuccessCalled) {
                        isSuccessCalled = true
                        isLoading.value = true
                        Log.d("LoginDialogFragment", "Authentication successful")
                        fetchUserProfile(requireContext(), authenticationResult) { displayName, photoPath ->
                            listener.onLoginSuccess(authenticationResult, displayName, photoPath)
                            onSuccess(authenticationResult, displayName, photoPath)
                            isLoading.value = false
                        }
                        dismissAllowingStateLoss()
                    }
                }

                override fun onError(exception: MsalException) {
                    Log.e("LoginDialogFragment", "Authentication error", exception)
                    dismissAllowingStateLoss()
                }

                override fun onCancel() {
                    Log.d("LoginDialogFragment", "Authentication canceled")
                    dismissAllowingStateLoss()
                }
            })
            .build()

        MsalAppHolder.msalApp.acquireToken(parameters)
    }

    @Composable
    fun LoadingIndicator() {
        if (isLoading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }

    private fun fetchUserProfile(context: Context, authenticationResult: IAuthenticationResult, callback: (String, String) -> Unit) {
        val accessToken = authenticationResult.accessToken

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val graphClient = GraphServiceClient
                    .builder()
                    .authenticationProvider { CompletableFuture.completedFuture(accessToken) }
                    .buildClient()

                val user = graphClient.me().buildRequest().get()
                val displayName = user?.displayName ?: "Unknown"

                val inputStream = try {
                    graphClient.me().photo().content().buildRequest().get()
                } catch (e: GraphServiceException) {
                    val errorResponse = e.error as GraphErrorResponse
                    if (errorResponse.error?.code == "ImageNotFound") {
                        null
                    } else {
                        throw e
                    }
                }

                val photoPath = if (inputStream != null) {
                    saveImageToFile(context, inputStream.readBytes())
                } else {
                    ""
                }

                withContext(Dispatchers.Main) {
                    callback(displayName, photoPath)
                }
            } catch (e: Exception) {
                Log.e("LoginDialogFragment", "Error fetching user profile", e)
                withContext(Dispatchers.Main) {
                    callback("Unknown", "")
                }
            }
        }
    }

    private fun saveImageToFile(context: Context, imageBytes: ByteArray): String {
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val file = File(context.filesDir, "profile_image.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return file.absolutePath
    }


    interface LoginDialogListener {
        fun onLoginSuccess(authenticationResult: IAuthenticationResult, displayName: String, photoPath: String)
    }
}