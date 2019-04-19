/*
William Ritchie
CS 646
Assignment 4
4/18/19
 */
package com.example.assignment4

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_post.*
import java.lang.Math.abs

const val REQUEST_IMAGE_OPEN = 1 // Used for the result from the implicit intent, accessing the gallery for posting an image

class PostActivity : AppCompatActivity() {

    private var file: Uri? = null // Keeps track of URI information of the image the user wishes to post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        back.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        imageView.setOnClickListener{getPhoto()}
        post.setOnClickListener { post() }
        nicknameView.text = intent.extras?.getString("nickname")
    }

    private fun getPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
        startActivityForResult(intent, REQUEST_IMAGE_OPEN)
    }

    private fun post() {
        if (file != null) {
            val mStorageRef = FirebaseStorage.getInstance().reference
            // This will store the new image in firebase storage with a mostly unique number based on the paths hashcode
            // Of course this isnt perfect and would need to be replaced with something a little more perfect like for
            // Example the next prime number or something
            val imagePath = "users/${intent.extras?.getString("email")}/${abs(file?.path.hashCode())}.jpg"
            val imageRef = mStorageRef.child(imagePath)

            // Upload the image to firebase storage
            imageRef.putFile(file as Uri)
                .addOnSuccessListener{ taskSnapshot ->
                    // Get a URL to the uploaded content
                    val downloadUrl = taskSnapshot.totalByteCount
                    Log.i("WCR", "upload post success")
                }
                .addOnFailureListener{
                    // Handle unsuccessful uploads
                    // ...
                    Log.i("WCR", "Upload FAIL")
                }

            // Update firebase firestore with the new post information
            val db = FirebaseFirestore.getInstance()
            val post = HashMap<String, Any>()
            post["description"] = edit_description.text.toString()
            post["imagePath"] = imagePath
            post["useremail"] = intent.extras?.getString("email") as String
            post["username"] = intent.extras?.getString("nickname") as String
            post["hashtag"] = getHashTags()
            db.collection("posts")
                .document()
                .set(post)

            // Notify the navigation activity of the results from the post
            val result = intent
            result.putExtra("description", edit_description.text.toString())
            Log.i("WCR", "IMAGEPATH POST" + imagePath)
            result.putExtra("imagePath", imagePath)
            result.putExtra("hashtag", getHashTags())
            setResult(Activity.RESULT_OK, result)
            finish()
        }
        else {
            Toast.makeText(this, "Please select a photo", Toast.LENGTH_LONG).show()
        }
    }

    // Parses through the description the user has written for hashtags
    private fun getHashTags(): ArrayList<String> {
        val hashtags = arrayListOf<String>()
        val tokens = edit_description.text.toString().split(" ")
        for (token in tokens) {
            if (token.isNotBlank()) {
                if (token.startsWith("#")) {
                    hashtags.add(token)
                }
            }
        }
        return hashtags
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == Activity.RESULT_OK) {
            file = data?.data as Uri
            imageView.setImageURI(file)
        }
    }
}
