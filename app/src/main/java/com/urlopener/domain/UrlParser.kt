package com.urlopener.domain

import com.urlopener.util.Logger
import java.io.BufferedReader

object UrlParser {
    private const val MAX_FILE_SIZE_BYTES = 1_048_576 // 1 MB limit
    private val urlPattern = Regex("""^URL=(.+)$""", RegexOption.IGNORE_CASE)

    fun parseUrlFile(reader: BufferedReader): Result<String> {
        return try {
            val lines = mutableListOf<String>()
            var bytesRead = 0
            var lineCount = 0
            val maxLines = 10_000

            reader.use { r ->
                while (true) {
                    if (bytesRead >= MAX_FILE_SIZE_BYTES) {
                        Logger.w("File exceeds max size limit of $MAX_FILE_SIZE_BYTES bytes")
                        return Result.failure(FileTooLargeException("URL file exceeds maximum allowed size"))
                    }
                    if (lineCount >= maxLines) {
                        Logger.w("File exceeds max line count of $maxLines")
                        return Result.failure(FileTooLargeException("URL file has too many lines"))
                    }

                    val line = r.readLine() ?: break
                    bytesRead += line.length + 1
                    lineCount++

                    if (line.trim().isNotEmpty()) {
                        lines.add(line)
                    }
                }
            }

            if (lines.isEmpty()) {
                Logger.e("File is empty")
                return Result.failure(EmptyFileException("URL file is empty"))
            }

            for (line in lines) {
                urlPattern.find(line.trim())?.let { match ->
                    val url = match.groupValues[1].trim()
                    if (url.isNotBlank() && isValidUrl(url)) {
                        Logger.d("Found URL: $url")
                        return Result.success(url)
                    }
                }
            }

            Logger.e("No valid URL found in file")
            Result.failure(NoUrlFoundException("No valid URL found in file"))
        } catch (e: SecurityException) {
            Logger.e("Permission denied reading file", e)
            Result.failure(e)
        } catch (e: Exception) {
            Logger.e("Error parsing URL file", e)
            Result.failure(e)
        }
    }

    private fun isValidUrl(url: String): Boolean {
        val lowerUrl = url.lowercase()
        return lowerUrl.startsWith("http://") || lowerUrl.startsWith("https://")
    }

    class FileTooLargeException(message: String) : Exception(message)
    class EmptyFileException(message: String) : Exception(message)
    class NoUrlFoundException(message: String) : Exception(message)
}
