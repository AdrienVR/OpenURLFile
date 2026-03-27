package com.urlopener

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.urlopener.data.UrlFileRepository
import com.urlopener.data.UrlFileRepository.InvalidUriException
import com.urlopener.data.UrlFileRepository.PermissionDeniedException
import com.urlopener.data.UrlFileRepository.UnsupportedSchemeException
import com.urlopener.data.UrlFileRepository.UriAccessException
import com.urlopener.domain.UrlParser.EmptyFileException
import com.urlopener.domain.UrlParser.FileTooLargeException
import com.urlopener.domain.UrlParser.NoUrlFoundException
import com.urlopener.ui.theme.URLFileOpenerTheme
import com.urlopener.util.Logger

class MainActivity : ComponentActivity() {

    private lateinit var repository: UrlFileRepository
    private var extractedUrl by mutableStateOf<UrlState>(UrlState.Loading)

    sealed class UrlState {
        data object Loading : UrlState()
        data class Success(val url: String) : UrlState()
        data class Error(val message: String) : UrlState()
        data object Empty : UrlState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.i("onCreate called")
        enableEdgeToEdge()

        repository = UrlFileRepository(this)

        extractUrlFromIntent()
        handleIntentSafe()

        setContent {
            URLFileOpenerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UrlDisplayScreen(
                        urlState = extractedUrl,
                        onOpenBrowser = { url -> safeOpenBrowser(url) },
                        onCopyToClipboard = { url -> safeCopyToClipboard(url) }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Logger.i("onNewIntent called")
        this.intent = intent
        extractUrlFromIntent()
        handleIntentSafe()
    }

    private fun extractUrlFromIntent() {
        Logger.d("Processing intent: $intent")
        Logger.d("Intent action: ${intent?.action}")
        Logger.d("Intent data: ${intent?.data}")

        if (intent == null) {
            Logger.d("Intent is null")
            extractedUrl = UrlState.Empty
            return
        }

        if (intent.action == Intent.ACTION_MAIN) {
            Logger.d("Main action detected, showing empty state")
            extractedUrl = UrlState.Empty
            return
        }

        val uri = intent.data
        if (uri == null) {
            Logger.d("No data in intent")
            extractedUrl = UrlState.Empty
            return
        }

        extractedUrl = UrlState.Loading
    }

    private fun handleIntentSafe() {
        val uri = intent?.data
        if (uri == null) return

        Logger.d("Full URI: $uri")
        Logger.d("Scheme: ${uri.scheme}")
        Logger.d("Authority: ${uri.authority}")
        Logger.d("Path: ${uri.path}")

        try {
            val result = repository.extractUrl(uri)
            extractedUrl = result.fold(
                onSuccess = { url ->
                    Logger.d("Successfully extracted URL: $url")
                    UrlState.Success(url)
                },
                onFailure = { error ->
                    Logger.e("Failed to extract URL", error)
                    when (error) {
                        is PermissionDeniedException -> UrlState.Error("Permission denied. Please grant file access.")
                        is FileTooLargeException -> UrlState.Error("File is too large to process.")
                        is EmptyFileException -> UrlState.Error("File is empty.")
                        is NoUrlFoundException -> UrlState.Error("No valid URL found in file.")
                        is UriAccessException -> UrlState.Error("Cannot read file: ${error.message}")
                        is InvalidUriException, is UnsupportedSchemeException -> UrlState.Error("Unsupported file format.")
                        else -> UrlState.Error("Failed to read URL: ${error.message ?: "Unknown error"}")
                    }
                }
            )
        } catch (e: Exception) {
            Logger.e("Unexpected error processing intent", e)
            extractedUrl = UrlState.Error("An unexpected error occurred")
        }
    }

    private fun safeOpenBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No browser app found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Logger.e("Error opening browser", e)
            Toast.makeText(this, "Cannot open URL", Toast.LENGTH_SHORT).show()
        }
    }

    private fun safeCopyToClipboard(url: String) {
        try {
            val clipboard = getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("URL", url)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "URL copied to clipboard", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Logger.e("Error copying to clipboard", e)
            Toast.makeText(this, "Failed to copy URL", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun UrlDisplayScreen(
    urlState: MainActivity.UrlState,
    onOpenBrowser: (String) -> Unit,
    onCopyToClipboard: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "URL File Opener",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        when (urlState) {
            is MainActivity.UrlState.Loading -> {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            is MainActivity.UrlState.Success -> {
                Text(
                    text = urlState.url,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = { onOpenBrowser(urlState.url) }) {
                    Text("Open in Browser")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(onClick = { onCopyToClipboard(urlState.url) }) {
                    Text("Copy URL")
                }
            }

            is MainActivity.UrlState.Error -> {
                Text(
                    text = urlState.message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is MainActivity.UrlState.Empty -> {
                Text(
                    text = "No URL file detected.\nOpen a .url file with this app.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
