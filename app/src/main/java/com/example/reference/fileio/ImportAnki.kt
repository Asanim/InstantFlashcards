package com.google.mlkit.showcase.translate.fileio
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ImportAnki {

fun unzipFile(zipFile: File, destinationFolder: File) {
    val buffer = ByteArray(1024)

    try {
        // Create destination folder if it doesn't exist
        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs()
        }

        // Initialize ZipInputStream to read the zip file
        val zipInputStream = ZipInputStream(BufferedInputStream(FileInputStream(zipFile)))

        // Read each entry from the zip file
        var entry: ZipEntry? = zipInputStream.nextEntry
        while (entry != null) {
            val entryFile = File(destinationFolder, entry.name)
            val entryFolder = entryFile.parentFile

            // Create parent folders if they don't exist
            if (!entryFolder.exists()) {
                entryFolder.mkdirs()
            }

            // Write the extracted file
            val outputStream = FileOutputStream(entryFile)
            val bufferedOutputStream = BufferedOutputStream(outputStream)

            var readBytes: Int
            while (zipInputStream.read(buffer).also { readBytes = it } != -1) {
                bufferedOutputStream.write(buffer, 0, readBytes)
            }

            // Close the streams
            bufferedOutputStream.close()
            outputStream.close()

            // Move to the next entry
            zipInputStream.closeEntry()
            entry = zipInputStream.nextEntry
        }

        // Close the ZipInputStream
        zipInputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
//
//// Example usage
//val zipFile = File("/path/to/archive.zip")
//val destinationFolder = File("/path/to/destination/folder")
//
//unzipFile(zipFile, destinationFolder)
}