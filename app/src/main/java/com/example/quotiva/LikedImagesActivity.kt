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
import android.widget.ImageButton

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


    private var imageIndex = 0
    private var likedImages = emptyList<Int>()
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
            intent.getIntegerArrayListExtra("likedImages") ?: emptyList()
        imageIndex = 0

        if (likedImages.isEmpty()) {
            previousButton.visibility = View.INVISIBLE
            previousButton.isEnabled = false
            nextButton.visibility = View.INVISIBLE
            nextButton.isEnabled = false
        } else {
            // Display the initial image
            imageIndex = 0
            displayImage()
        }

        if (likedImages.isEmpty()) {
            imageView.setImageResource(placeholderImageResId)
        } else {
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
        imageView.setImageResource(likedImages[imageIndex])
    }
}