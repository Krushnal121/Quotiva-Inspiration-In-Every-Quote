package com.example.quotiva

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.ImageButton
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class LikedImagesActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var homeBtn: Button
    private lateinit var vibrator: Vibrator
    private lateinit var webBtn:ImageButton
    private lateinit var instaBtn: ImageButton
    private lateinit var linkedinBtn: ImageButton
    private lateinit var githubBtn: ImageButton
    lateinit var imageUrl:String


    private var imageIndex = 0
    private var likedImages = emptyList<String>()
    private val placeholderImageResId = R.drawable.nothing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_liked_images)

        imageView = findViewById(R.id.imageViewIdLiked)
        previousButton = findViewById(R.id.prevBtnLiked)
        nextButton = findViewById(R.id.nextBtnLiked)
        homeBtn = findViewById(R.id.home)
        webBtn =findViewById(R.id.webBtn)
        instaBtn = findViewById(R.id.instagramBtn)
        linkedinBtn = findViewById(R.id.linkedinBtn)
        githubBtn = findViewById(R.id.githubBtn)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        likedImages =
            intent.getStringArrayListExtra("likedImages") ?: emptyList()
        imageIndex = 0
        Log.w("imageurl","$likedImages")
        if (likedImages.isEmpty()) {
            previousButton.visibility = View.INVISIBLE
            previousButton.isEnabled = false
            nextButton.visibility = View.INVISIBLE
            nextButton.isEnabled = false
            imageView.setImageResource(placeholderImageResId)
        } else {
            // Display the initial image
            imageIndex = 0
            displayImage()
        }

        previousButton.setOnClickListener {
            vibrator?.vibrate(10)
            imageIndex = (imageIndex - 1 + likedImages.size) % likedImages.size
            displayImage()
        }

        nextButton.setOnClickListener {
            vibrator?.vibrate(10)
            imageIndex = (imageIndex + 1) % likedImages.size
            displayImage()
        }

        homeBtn.setOnClickListener {
            vibrator?.vibrate(10)
            finish()
        }

        webBtn.setOnClickListener {
            val websiteUrl = "https://bento.me/krushnalpatil"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
            startActivity(intent)
        }

        instaBtn.setOnClickListener {
            val websiteUrl =
                "https://www.instagram.com/krushnal_patil_111"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
            startActivity(intent)
        }

        linkedinBtn.setOnClickListener {
            val websiteUrl =
                "https://www.linkedin.com/in/krushnal-jagannath-patil/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
            startActivity(intent)
        }

        githubBtn.setOnClickListener {
            val websiteUrl =
                "https://github.com/Krushnal121"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
            startActivity(intent)
        }
    }

    private fun displayImage() {
        if (likedImages.isNotEmpty()) {
            val imageUrl = likedImages[imageIndex]
            Log.w("imageurl","$imageUrl")
            try {
                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this).load(uri).into(imageView)
                }.addOnFailureListener {
                    Log.w("LikedImagesActivity", "Error downloading image from Firebase Storage", it)
                    // Display a placeholder or error message
                }
            } catch (e: IllegalArgumentException) {
                Log.e("LikedImagesActivity", "Invalid image URL: $imageUrl", e)
                // Handle the error:
                // - Display a placeholder error image
                // - Skip to the next valid image
                // - Notify the user about the invalid URL
            }
        } else {
            imageView.setImageResource(placeholderImageResId)
        }
    }
}