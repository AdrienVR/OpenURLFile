package com.urlopener.data

import android.content.Context
import android.net.Uri
import com.urlopener.domain.UrlParser
import com.urlopener.util.Logger

class UrlFileRepository(private val context: Context) {

    fun extractUrl(uri: Uri): Result<String> {
        Logger.d("Extracting URL from URI: $uri")

        if (uri.scheme == null) {
            Logger.e("URI has no scheme")
            return Result.failure(InvalidUriException("Invalid URI: missing scheme"))
        }

        if (uri.scheme == "http" || uri.scheme == "https") {
            val url = uri.toString()
            Logger.d("Direct URL received: $url")
            return Result.success(url)
        }

        if (uri.scheme == "content") {
            Logger.d("Content URI detected (authority: ${uri.authority}), attempting to read file")
            return readFromContentUri(uri)
        }

        if (uri.scheme == "file") {
            Logger.d("File URI detected, attempting to read file")
            return readFromContentUri(uri)
        }

        Logger.e("Unsupported URI scheme: ${uri.scheme}")
        return Result.failure(UnsupportedSchemeException("Unsupported URI scheme: ${uri.scheme}"))
    }

    private fun readFromContentUri(uri: Uri): Result<String> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: run {
                    Logger.e("Failed to open input stream for URI")
                    return Result.failure(UriAccessException("Cannot open file"))
                }

            inputStream.bufferedReader().use { reader ->
                UrlParser.parseUrlFile(reader)
            }
        } catch (e: SecurityException) {
            Logger.e("Permission denied accessing content URI", e)
            Result.failure(PermissionDeniedException("Permission denied to read file", e))
        } catch (e: NullPointerException) {
            Logger.e("Null pointer when reading content URI", e)
            Result.failure(UriAccessException("Failed to read file", e))
        } catch (e: Exception) {
            Logger.e("Error reading content URI", e)
            Result.failure(UriAccessException("Error reading file: ${e.message}", e))
        }
    }

    class InvalidUriException(message: String) : Exception(message)
    class UnsupportedSchemeException(message: String) : Exception(message)
    class UriAccessException(message: String, cause: Throwable? = null) : Exception(message, cause)
    class PermissionDeniedException(message: String, cause: Throwable? = null) : Exception(message, cause)
}
