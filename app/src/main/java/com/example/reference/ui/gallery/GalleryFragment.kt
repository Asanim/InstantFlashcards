package com.example.reference.ui.gallery

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reference.R
import com.example.reference.databinding.FragmentGalleryBinding
import java.io.File
import java.io.InputStream


class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textGallery
//        galleryViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        val pickFileButton: Button = binding.openFile
        pickFileButton.setOnClickListener {
            openFilePicker()
        }

        val recyclerView: RecyclerView =  binding.recyclerView

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL ,false)

        val imagePaths: List<String> = context?.let { getImagePathsFromDirectory(it, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) }!!
        val imageAdapter = ImageAdapter(imagePaths)
        recyclerView.setAdapter(imageAdapter)

        return root
    }
    companion object {
        private const val REQUEST_CODE_PICK_FILE = 1001
    }

    private fun getImagePathsFromDirectory(context: Context, directoryUri: Uri): List<String>? {
        val imagePaths: MutableList<String> = ArrayList()

        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val resolver: ContentResolver = context.contentResolver

        val cursor: Cursor? = resolver.query(directoryUri, projection, null, null, null)

        cursor?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (cursor.moveToNext()) {
                val imagePath = cursor.getString(columnIndex)
                imagePaths.add(imagePath)
            }
        }

        return imagePaths
    }


    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*" // Set the MIME type filter to restrict the file types if needed
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "image/*")) // Example filter for PDF and images

        // Optionally, specify the starting directory
        val downloadsUri = Uri.parse("content://com.android.providers.downloads.documents/document/msf%3A150")
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, downloadsUri)

        startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                handlePickedFile(uri)
            }
        }
    }

    private fun handlePickedFile(uri: Uri) {
        val documentFile = DocumentFile.fromSingleUri(requireContext(), uri)
        val file = getFileFromDocument(documentFile)
        // Use the selected file as needed
    }

    private fun getFileFromDocument(documentFile: DocumentFile?): File? {
        documentFile?.let { document ->
            val fileName = document.name ?: return null
            val filePath = "${requireContext().cacheDir.absolutePath}/$fileName"

//            val inputStream = requireContext().contentResolver.openInputStream(document.uri)
//            val outputStream = FileOutputStream(filePath)
//
//            inputStream?.use { input ->
//                outputStream.use { output ->
//                    val buffer = ByteArray(4 * 1024) // Adjust buffer size as per your needs
//                    var bytesRead: Int
//                    while (input.read(buffer).also { bytesRead = it } != -1) {
//                        output.write(buffer, 0, bytesRead)
//                    }
//                    output.flush()
//                }
//            }


//
//            // here we are actually reading the image back from file...
//            // TODO read the actual image not the save file OR move this to gallery to be read later...
//            val imageUri: Uri? = filePath
//
//            val inputStream: InputStream? = imageUri?.let {
//                requireContext().contentResolver.openInputStream(
//                    it
//                )
//            }
//            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
////            recognizeTextOnDevice(bitmap)
//
//
//            if (imgFile.exists()) {
//                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
//                val myImage = findViewById(com.example.reference.R.id.imageviewTest) as ImageView
//                myImage.setImageBitmap(myBitmap)
//            }

//            inputStream?.close()


            return File(filePath)
        }

        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}