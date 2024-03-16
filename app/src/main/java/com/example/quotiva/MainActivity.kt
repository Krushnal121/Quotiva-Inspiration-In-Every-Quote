package com.example.quotiva

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import kotlin.random.Random
import java.nio.file.Files
import android.os.Build
import androidx.core.content.FileProvider
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import java.io.FileOutputStream
import android.net.Uri


class

MainActivity : AppCompatActivity() {

    companion object {
        private const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 101  // You can choose any unique value
    }


    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    lateinit var viewLikedImagesButton: Button
    private lateinit var vibrator: Vibrator
    private lateinit var shareButton: ImageButton
    lateinit var urltemp: String





    private lateinit var likeButton: ImageButton
    val tempFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Files.createTempFile("image_${System.currentTimeMillis()}", ".png").toFile()
    } else {
        TODO("VERSION.SDK_INT < O")
    }

    var likedImages = mutableListOf<String>()

    private fun loadLikedImagesFromPreferences() {
        val sharedPreferences = getSharedPreferences("my_app_prefs", MODE_PRIVATE)
        val savedLikedImages = sharedPreferences.getStringSet("liked_images", null)
        likedImages = savedLikedImages?.toMutableList() ?: mutableListOf()
    }

    private fun saveLikedImagesToPreferences() {
        val sharedPreferences = getSharedPreferences("my_app_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("liked_images", likedImages.map { it.toString() }.toSet())
        editor.apply()
    }




    override

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main)

        loadLikedImagesFromPreferences() // Load saved liked images



        imageView = findViewById(R.id.imageViewId)
        previousButton = findViewById(R.id.prevBtn)
        nextButton = findViewById(R.id.nextBtn)
        likeButton= findViewById(R.id.likeBtn)
        viewLikedImagesButton=findViewById(R.id.likedQuotes)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        shareButton=findViewById(R.id.shareBtn)

        likeButton.setImageResource(R.drawable.heartempty)

        likeButton.setOnClickListener {
            vibrator?.vibrate(10)
            if (urltemp !in likedImages) {
                likedImages.add(urltemp)
                likeButton.setImageResource(R.drawable.heartfilled) // Update button icon
            } else {
                likedImages.remove(urltemp)
                likeButton.setImageResource(R.drawable.heartempty) // Update button icon
            }
        }


        displayImage()

        previousButton.setOnClickListener {
            vibrator?.vibrate(10)
            displayImage()
        }

        nextButton.setOnClickListener {
            vibrator?.vibrate(10)
            displayImage()
        }

        viewLikedImagesButton.setOnClickListener {
            vibrator?.vibrate(10)
            val intent = Intent(this, LikedImagesActivity::class.java)
            intent.putStringArrayListExtra("likedImages", ArrayList(likedImages))
            startActivity(intent)
        }


        shareButton.setOnClickListener {
            shareImage()
        }


    }

    override fun onPause() {
        super.onPause()
        saveLikedImagesToPreferences()
    }

    /*private fun displayImage() {
        imageView.setImageResource(images[imageIndex])

        // Check if the current image is liked and update the button icon accordingly
        if (images[imageIndex] in likedImages) {
            likeButton.setImageResource(R.drawable.heartfilled)
        } else {
            likeButton.setImageResource(R.drawable.heartempty)
        }
    }*/

    private fun displayImage() {

        // Access Firestore and get quotes collection
        val db = FirebaseFirestore.getInstance()
        val quotesCollection = db.collection("quotes")

        // Fetch a random quote document (you can use queries for filtering)
        quotesCollection.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documents = task.result
                if (documents != null && !documents.isEmpty()) {
                    val randomIndex = Random.nextInt(documents.size())
                    val randomQuote = documents.documents[randomIndex]

                    // Extract quote text, author (optional), and imageUrl
                    val quoteText = randomQuote.getString("quote") ?: ""
                    val author = randomQuote.getString("author")
                    val imageUrl = randomQuote.getString("image") ?: ""

                    // Update UI elements with quote text and author (if available)
                    urltemp=imageUrl
                    downloadAndDisplayImage(imageUrl)
                    if (urltemp in likedImages) {
                        likeButton.setImageResource(R.drawable.heartfilled)
                    } else {
                        likeButton.setImageResource(R.drawable.heartempty)
                    }
                } else {
                    Log.w("TAG", "Firestore: No quotes found")
                    // Handle the case where no quotes are retrieved
                }
            } else {
                Log.w("TAG", "Firestore: Error getting documents.", task.exception)
                // Handle potential errors during data retrieval
            }
        }
    }

    private fun downloadAndDisplayImage(imageUrl: String) {

        // Check if imageUrl is valid
        if (imageUrl.isNotEmpty()) {
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

            // Asynchronously retrieve the download URL
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                // Download successful, display the image using Glide
                Glide.with(this).load(uri).into(imageView)
            }.addOnFailureListener {
                // Handle any errors
                Log.w("tag", "Error downloading image from Firebase Storage", it)
                // Display a placeholder or error message if needed
            }
        } else {
            // Handle the case where imageUrl is empty
            // Display a placeholder or error message
        }
    }


    private fun shareImage() {
        // Get the bitmap of the currently displayed image
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        Log.d("bitmap","$bitmap")

        // Create a temporary file to store the image
        val imageFile = createTempImageFile(bitmap)
        Log.e("imagefile","$imageFile")
        imageUri = FileProvider.getUriForFile(this, "com.example.quotiva.fileprovider", imageFile)
        Log.d("imageURI","$imageUri")
        // Create a share intent with the image URI
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/*"
        }

        // Start the share activity
        startActivity(Intent.createChooser(shareIntent, "Share image using"))
    }

    // Helper function to create a temporary image file
    private fun createTempImageFile(bitmap: Bitmap): File {
        val cacheDir = applicationContext.cacheDir
        val tempFile = File.createTempFile("temp_image_", ".jpg", cacheDir)
        tempFile.deleteOnExit() // Delete the file when the app exits

        val outputStream = FileOutputStream(tempFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.close()

        return tempFile
    }


}