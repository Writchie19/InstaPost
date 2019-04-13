package com.example.assignment4

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

const val REQUEST_IMAGE_OPEN = 1
const val REQUEST_IMAGE_CREATE = 2
class MainActivity : AppCompatActivity(), DatabaseReference.CompletionListener {
    private val posts = arrayListOf<Post>()
    private val users = arrayListOf<String>()
    private val hashtags = arrayListOf<String>()
    private var postAdapter : PostRecycleAdapter = PostRecycleAdapter(posts, this)
    private val userAdapter = ListRecycleAdapter(users, this)

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
//                message.setText(R.string.title_home)
                // Can load a fragment in this section
                @Suppress("UNCHECKED_CAST")
                loadFrag(postAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
//                message.setText(R.string.title_dashboard)
                @Suppress("UNCHECKED_CAST")
                loadFrag(userAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
//                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    fun selectImage2() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
        startActivityForResult(intent, REQUEST_IMAGE_OPEN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == Activity.RESULT_OK) {
            val file: Uri = data?.data as Uri
            Log.i("WCR", file.path)

            //val hardFile = Uri.fromFile(File(file.path))
            //Log.i("WCR", hardFile.path)
            // Do work with full size photo saved at fullPhotoUri
            val mStorageRef = FirebaseStorage.getInstance().reference
            val riversRef = mStorageRef.child("images/image")

            riversRef.putFile(file)
                .addOnSuccessListener{ taskSnapshot ->
                    // Get a URL to the uploaded content
                    val downloadUrl = taskSnapshot.totalByteCount
                    Log.i("WCR", downloadUrl.toString())
                }
                .addOnFailureListener{
                    // Handle unsuccessful uploads
                    // ...
                    Log.i("WCR", "FAIL")
                }
        }
    }

    fun getPosts() {
        FirebaseApp.initializeApp(this)
        val db = FirebaseFirestore.getInstance()
        db.collection("posts")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("WCR", "${document.id} => ${document.data}")
                        posts.add(Post(document.data["username"].toString(), document.data["imagePath"].toString(), document.data["description"].toString()))
                        if (!users.contains(document.data["username"].toString())) {
                            users.add(document.data["username"].toString())
                        }

                        for (hashtag in document.data["hashtag"] as ArrayList<String>) {
                            if (!hashtags.contains(hashtag)) {
                                hashtags.add(hashtag)
                            }
                        }
                        Log.i("WCR", hashtags.toString())

                    }

                    for (post in posts) {
                        downLoadPost(post)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("WCR", "Error getting documents.", exception)
                }
                .addOnCompleteListener {
                    textView2.postDelayed({loadFrag(postAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)}, 1000) // can make the delay an equation based on the number of posts
                }
    }

    fun loadFrag(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        var fragments = supportFragmentManager
        var fragmentTransaction = fragments.beginTransaction()
        val fragment = RecyclerFragment.create() // Static constructor
        fragment.setView(adapter , this)
        fragmentTransaction.replace(R.id.recycle_fragment, fragment)
        fragmentTransaction.commit()
    }

    fun downLoadPost(post : Post) {
        val mStorageRef = FirebaseStorage.getInstance().reference
        Log.i("WCR", post.imagePath)
        val riversRef = mStorageRef.child(post.imagePath as String)
        val localFile = File.createTempFile( "image",".jpg")
        post.setImage(Uri.fromFile(localFile))
        riversRef.getFile(localFile)
            .addOnSuccessListener{ taskSnapshot ->
                Log.i("WCR", taskSnapshot.bytesTransferred.toString())

            }
            .addOnFailureListener{
                Log.i("WCR", "FAIL")
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPosts()

        button.setOnClickListener {selectImage2()}


//        try {
////            FirebaseApp.initializeApp(this)
////            val database = FirebaseDatabase.getInstance()
////            val myRef = database.getReference("message")
////            myRef.setValue("Hello, World!")
//
//            val database = FirebaseDatabase.getInstance()
//            val personTable = database.getReference("person")
//            val daniel = Person("daniel", "ritchie", 1998)
//            val will = Person("daniel", "ritchie", 1994)
//            personTable.child("daniel").setValue(daniel)
//            personTable.child("will").setValue(will)
//            val alanTable = database.getReference("person/cs/famous/alan")
//            val alan = Person("Alan", "Turing", 2001)
//            alanTable.setValue(alan)
//            val donald = Person("Donald", "Knuth", 2000)
//            val toplevel = database.reference
//            toplevel.child("person").child("cs").child("famous/donald").setValue(donald)
//
//            val db = FirebaseFirestore.getInstance()
//            val city = HashMap<String, Any>()
//            city["name"] = "Los Angeles"
//            city["state"] = "CA"
//            city["country"] = "USA"
//            db.collection("cities").document("LA")
//                .set(city)
//                .addOnSuccessListener { Log.d("WCR", "DocumentSnapshot successfully written!") }
//                .addOnFailureListener { e -> Log.w("WCR", "Error writing document", e) }
//
//            var user = HashMap<String, Any>()
//            user["firstName"] = "Ada"
//            user["lastName"] = "Lovelace"
//            user["birthYear"] = 1815
//            user["text"] = "this is a super long text that i am writing ashdfl kjasbflaskjnglKGnslkgqreiljk"
//            db.collection("users").document("Ada")
//                .set(user)
//                .addOnSuccessListener { Log.d("WCR", "DocumentSnapshot successfully written!") }
//                .addOnFailureListener { e -> Log.w("WCR", "Error adding document", e) }
//            db.collection("users").document("Daniel")
//                .set(daniel)
//                .addOnSuccessListener { Log.d("WCR", "DocumentSnapshot successfully written!") }
//                .addOnFailureListener { e -> Log.w("WCR", "Error adding document", e) }
//            db.collection("users")
//                .get()
//                .addOnSuccessListener { result ->
//                    for (document in result) {
//                        Log.d("WCR", "${document.id} => ${document.data}")
//                        var somePerson = Person(document.data["firstName"].toString(), document.data["lastName"].toString(), document.data["birthYear"].toString().toInt())
//                        Log.i("WCR", somePerson.firstName)
//                        Log.i("WCR", somePerson.lastName)
//                        Log.i("WCR", somePerson.birthYear.toString())
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Log.w("WCR", "Error getting documents.", exception)
//                }
//            db.collection("hashtags").document("WillisAwsome")
//                .get()
//                .addOnSuccessListener { result ->
//                    val data = result.data
//                    Log.i("WCR", data?.get("email").toString())
//                }
//
//        }
//        catch (e : Exception){
//            Log.i("WCR", e.toString())
//        }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }


    override fun onComplete(error: DatabaseError?, db: DatabaseReference) {
        if (error == null)
        // no error database received data
            Log.i("WCR", "Success!")
        else {
            Log.e("WCR", error.getMessage())
        }
    }
}
