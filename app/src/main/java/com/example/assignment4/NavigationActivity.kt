package com.example.assignment4

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_navigation.*
import java.io.File

const val REQUEST_IMAGE_OPEN = 1
const val REQUEST_IMAGE_CREATE = 2
const val POST = 3
const val USEREMAIL = "useremail"
const val HASHTAG = "hashtag"
class NavigationActivity: AppCompatActivity(), DatabaseReference.CompletionListener {
    private val posts = arrayListOf<Post>()
    private val users = arrayListOf<User>()
    private val hashtags = arrayListOf<HashTag>()
    private val userPosts = HashMap<String, ArrayList<Post>>()
    private val userPostsHashTag = HashMap<String, ArrayList<Post>>()
    private var currentUserEmail: String? = null
    private var currentUserNickName: String? = null
    private var currentUserName: String? = null
    private var postAdapter: PostRecycleAdapter? = null
    private val userAdapter = ListRecycleAdapter(users, userPosts, this)
    private var hashtagAdapter: ListRecycleAdapter? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_my_posts -> {
//                message.setText(R.string.title_home)
                // Can load a fragment in this section
                logout.visibility = View.VISIBLE
                post.visibility = View.VISIBLE
                @Suppress("UNCHECKED_CAST")
                loadFrag(postAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_all_posts -> {
//                message.setText(R.string.title_home)
                // Can load a fragment in this section
                logout.visibility = View.VISIBLE
                post.visibility = View.VISIBLE
                @Suppress("UNCHECKED_CAST")
                loadFrag(postAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_users -> {
//                message.setText(R.string.title_dashboard)
                logout.visibility = View.INVISIBLE
                post.visibility = View.INVISIBLE
                @Suppress("UNCHECKED_CAST")
                loadFrag(userAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_hashtags -> {
//                message.setText(R.string.title_notifications)
                logout.visibility = View.INVISIBLE
                post.visibility = View.INVISIBLE
                Log.i("WCR", "HERE")
                for (item in hashtags) {
                    Log.i("WCR", "HASHTAGLIST: ${item.getHashTag()}")
                }

                @Suppress("UNCHECKED_CAST")
                loadFrag(hashtagAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun makePost() {
        val postIntent = Intent(this, PostActivity::class.java)
        postIntent.putExtra("email", currentUserEmail)
        Log.i("WCR", "NICKNAME  " + currentUserNickName)
        postIntent.putExtra("nickname", currentUserNickName)
        startActivityForResult(postIntent, POST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == POST) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i("WCR","IMAGEPATH OAR " + data?.extras?.getString("imagePath").toString() )
                val newPost = Post(currentUserNickName as String, data?.extras?.getString("imagePath").toString(),data?.extras?.getString("description").toString())
                userPosts[currentUserEmail]?.add(0,newPost)


                downLoadPostImage(newPost)
                userposts.postDelayed({
                    @Suppress("UNCHECKED_CAST")
                    loadFrag(postAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                }, 3000)
            }
            else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    private fun getUserInfo(email: String){
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(email)
            .get()
            .addOnSuccessListener { result ->
                currentUserName = result.data?.get("name").toString()
                currentUserNickName = result.data?.get("nickname").toString()
            }
    }

    private fun constructUserList() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("WCR", "${document.id} => ${document.data}")
                    users.add(User(document.data["name"].toString(), document.data["nickname"].toString(), document.data["email"].toString()))
                    //users.add(document.data["nickname"].toString())
                }
            }
            .addOnFailureListener { exception ->
                Log.w("WCR", "Error getting documents.", exception)
            }
    }
// .whereEqualTo(field, filter)
    private fun constructPostList() {
        val db = FirebaseFirestore.getInstance()
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("WCR", "${document.id} => ${document.data}")
                    val post = Post(document.data["username"].toString(), document.data["imagePath"].toString(), document.data["description"].toString())
                    if (!userPosts.containsKey(document.data[USEREMAIL].toString())) {
                        val userPostList = arrayListOf<Post>()
                        userPostList.add(post)
                        userPosts[document.data[USEREMAIL].toString()] = userPostList
                    }
                    else {
                        userPosts[document.data[USEREMAIL].toString()]?.add(post)
                    }

                    @Suppress("UNCHECKED_CAST")
                    for (hashtag in document.data[HASHTAG] as ArrayList<String>) {
                        Log.i("WCR", "hastag: $hashtag")
                        if (hashtag.isNotBlank()) {
                            if (hashtags.isNotEmpty()) {
                                var exists = false
                                for (i in  0 until hashtags.size) {
                                    Log.i("WCR", "inner hashtag ${hashtags[i].getHashTag()}")
                                    if (hashtags[i].getHashTag().compareTo(hashtag) == 0) {
                                        exists = true
                                    }
                                }
                                if (!exists) {
                                    hashtags.add(HashTag(hashtag, document.data[USEREMAIL].toString()))
                                    val hashtagPostList = arrayListOf<Post>()
                                    userPostsHashTag[hashtag] = hashtagPostList
                                }
                                userPostsHashTag[hashtag]?.add(post)
                            }
                            else {
                                hashtags.add(HashTag(hashtag, document.data[USEREMAIL].toString()))
                                val hashtagPostList = arrayListOf<Post>()
                                hashtagPostList.add(post)
                                userPostsHashTag[hashtag] = hashtagPostList
                            }
                        }
                    }
                    downLoadPostImage(post)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("WCR", "Error getting documents.", exception)
            }
            .addOnCompleteListener {
                userposts.postDelayed({
                    postAdapter = PostRecycleAdapter(userPosts[currentUserEmail] as ArrayList<Post>, this)
                    hashtagAdapter = ListRecycleAdapter(hashtags, userPostsHashTag, this)
                    @Suppress("UNCHECKED_CAST")
                    loadFrag(postAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                }, 2000) // can make the delay an equation based on the number of posts
            }
    }

    fun loadFrag(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        val fragments = supportFragmentManager
        val fragmentTransaction = fragments.beginTransaction()
        val fragment = RecyclerFragment.create() // Static constructor
        fragment.setView(adapter , this)
        fragmentTransaction.replace(R.id.recycle_fragment, fragment)
        fragmentTransaction.commit()
    }

    private fun downLoadPostImage(post : Post) {
        val mStorageRef = FirebaseStorage.getInstance().reference
        val imageRef = mStorageRef.child(post.imagePath as String)
        val localFile = File.createTempFile( "image",".jpg")
        post.setImageUri(Uri.fromFile(localFile))
        imageRef.getFile(localFile)
            .addOnSuccessListener{ taskSnapshot ->
                Log.i("WCR", "BYTES" + taskSnapshot.bytesTransferred.toString())

            }
            .addOnFailureListener{
                Log.i("WCR", "FAIL")
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        FirebaseApp.initializeApp(this)
        currentUserEmail = intent.extras?.getString("email").toString()
        getUserInfo(currentUserEmail as String)
        constructUserList()
        constructPostList()
        post.setOnClickListener {makePost()}
        logout.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
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
