package com.example.quotiva

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import kotlin.random.Random
import java.nio.file.Files
import androidx.core.graphics.drawable.toBitmap
import java.io.FileOutputStream
import android.graphics.Bitmap
import android.os.Build
import androidx.core.content.FileProvider
import androidx.core.app.ActivityCompat
import android.Manifest
import java.io.File











class

MainActivity : AppCompatActivity() {

    companion object {
        private const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 101  // You can choose any unique value
    }


    private lateinit var imageView: ImageView

    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    lateinit var viewLikedImagesButton: Button
    private lateinit var vibrator: Vibrator
    private lateinit var shareButton: ImageButton





    private lateinit var likeButton: ImageButton
    val tempFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Files.createTempFile("image_${System.currentTimeMillis()}", ".png").toFile()
    } else {
        TODO("VERSION.SDK_INT < O")
    }

    private var imageIndex = 0
    private val images = arrayOf(R.drawable.one, R.drawable.two, R.drawable.three,R.drawable.four,R.drawable.five,R.drawable.six,R.drawable.seven,R.drawable.eight,R.drawable.nine,R.drawable.ten,R.drawable.eleven,R.drawable.twelve,R.drawable.thirteen,R.drawable.fourteen,R.drawable.fifteen,R.drawable.sixteen,R.drawable.seventeen, R.drawable.eighteen,R.drawable.nineteen,R.drawable.twenty)
    var likedImages = mutableListOf<Int>()

    private fun loadLikedImagesFromPreferences() {
        val sharedPreferences = getSharedPreferences("my_app_prefs", MODE_PRIVATE)
        val savedLikedImages = sharedPreferences.getStringSet("liked_images", null)
        likedImages = savedLikedImages?.toList()?.map { it.toInt() }?.toMutableList() ?: mutableListOf()


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
            if (images[imageIndex] !in likedImages) {
                likedImages.add(images[imageIndex])
                likeButton.setImageResource(R.drawable.heartfilled) // Update button icon
            } else {
                likedImages.remove(images[imageIndex])
                likeButton.setImageResource(R.drawable.heartempty) // Update button icon
            }
        }


        imageIndex = Random.nextInt(images.size)
        displayImage()

        previousButton.setOnClickListener {
            vibrator?.vibrate(10)
            imageIndex = (imageIndex - 1 + images.size) % images.size
            displayImage()
        }

        nextButton.setOnClickListener {
            vibrator?.vibrate(10)
            imageIndex = (imageIndex + 1) % images.size
            displayImage()
        }

        viewLikedImagesButton.setOnClickListener {
            vibrator?.vibrate(10)
            val intent = Intent(this, LikedImagesActivity::class.java)
            intent.putIntegerArrayListExtra("likedImages", ArrayList(likedImages))
            startActivity(intent)
        }


        shareButton.setOnClickListener {
            //shareCurrentImage()
        }


    }

    override fun onPause() {
        super.onPause()
        saveLikedImagesToPreferences()
    }

    private fun displayImage() {
        imageView.setImageResource(images[imageIndex])

        // Check if the current image is liked and update the button icon accordingly
        if (images[imageIndex] in likedImages) {
            likeButton.setImageResource(R.drawable.heartfilled)
        } else {
            likeButton.setImageResource(R.drawable.heartempty)
        }
    }

    private fun shareCurrentImage() {
        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_REQUEST_CODE)
        //val imageView = findViewById<ImageView>(R.id.imageViewId)
        //val bitmap = imageView.drawable.toBitmap()
        //val outputStream = FileOutputStream(tempFile)
        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        //outputStream.close()
        //val contentUri = FileProvider.getUriForFile(this, "com.example.quotiva.provider", tempFile)
        //this.grantUriPermission(this.packageName, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //val imageUri = FileProvider.getUriForFile(this, "${packageName}.provider", tempFile)
        //val shareIntent = Intent(Intent.ACTION_SEND)
        //shareIntent.setType("image/*")
        //shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        //startActivity(Intent.createChooser(shareIntent, "Share Image"))
        //tempFile.delete()
        //fun shareImageButtonClicked() {
            val file = File("android.resource://com.example.quotiva/res/drawable/background.png")
            val contentUri = FileProvider.getUriForFile(this, "com.example.quotiva.fileprovider", file)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("image/png")
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(Intent.createChooser(shareIntent, "Share Image"))


    }


}