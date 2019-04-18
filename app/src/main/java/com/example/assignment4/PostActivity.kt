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

class PostActivity : AppCompatActivity() {

    private var file: Uri? = null
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
            val imagePath = "users/${intent.extras?.getString("email")}/${abs(file?.path.hashCode())}.jpg"
            val imageRef = mStorageRef.child(imagePath)

            imageRef.putFile(file as Uri)
                .addOnSuccessListener{ taskSnapshot ->
                    // Get a URL to the uploaded content
                    val downloadUrl = taskSnapshot.totalByteCount
                    Log.i("WCR", downloadUrl.toString())
                }
                .addOnFailureListener{
                    // Handle unsuccessful uploads
                    // ...
                    Log.i("WCR", "Upload FAIL")
                }

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


            val result = intent
            result.putExtra("description", edit_description.text.toString())
            Log.i("WCR", "IMAGEPATH POST" + imagePath)
            result.putExtra("imagePath", imagePath)
            setResult(Activity.RESULT_OK, result)
            finish()
        }
        else {
            Toast.makeText(this, "Please select a photo", Toast.LENGTH_LONG).show()
        }
    }

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
        //super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == Activity.RESULT_OK) {
            file = data?.data as Uri
            imageView.setImageURI(file)
            Log.i("WCR", file?.path)

            //val hardFile = Uri.fromFile(File(file.path))
            //Log.i("WCR", hardFile.path)
            // Do work with full size photo saved at fullPhotoUri


        }
    }
}
