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
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_navigation.*
import java.io.File

const val POST = 3 // The request code to see the result from the user posting an image
const val USEREMAIL = "useremail"
const val HASHTAG = "hashtag"

class NavigationActivity: AppCompatActivity() {
    private val posts = arrayListOf<Post>() // A List of all posts
    private val users = arrayListOf<User>() // A list of all users
    private val hashtags = arrayListOf<HashTag>() // A List of all unique hashtags (no duplicates)
    private val userPosts = HashMap<String, ArrayList<Post>>() // Maps the posts associated with a specific user (by email)
    private val userPostsHashTag = HashMap<String, ArrayList<Post>>() // Maps the posts associated with a specific hashtag
    private var currentUserEmail: String? = null
    private var currentUserNickName: String? = null
    private var currentUserName: String? = null
    private var postAdapter: PostRecycleAdapter? = null // Used with the recycle view in a fragment to display specific posts
    private val allPostsAdapter = PostRecycleAdapter(posts, this) // Used with the recycle view in a fragment to display all posts
    private val userAdapter = ListRecycleAdapter(users, userPosts, this) // Used with the recycle view in a fragment to display a list of all users
    private var hashtagAdapter: ListRecycleAdapter? = null // User with the recycel view in a fragment to display a list of all hashtags

    // These all load a fragment with the corresponding adapter
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_my_posts -> {
                selection.text = currentUserNickName
                logout.visibility = View.VISIBLE
                post.visibility = View.VISIBLE
                @Suppress("UNCHECKED_CAST")
                loadFrag(postAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_all_posts -> {
                selection.text = getString(R.string.everyone)
                logout.visibility = View.INVISIBLE
                post.visibility = View.INVISIBLE
                @Suppress("UNCHECKED_CAST")
                loadFrag(allPostsAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_users -> {
                selection.text = getString(R.string.list_of_users)
                logout.visibility = View.INVISIBLE
                post.visibility = View.INVISIBLE
                @Suppress("UNCHECKED_CAST")
                loadFrag(userAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_hashtags -> {
                selection.text = getString(R.string.list_of_hashtags)
                logout.visibility = View.INVISIBLE
                post.visibility = View.INVISIBLE
                @Suppress("UNCHECKED_CAST")
                loadFrag(hashtagAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    // Called when the user clicks the post button
    private fun makePost() {
        val postIntent = Intent(this, PostActivity::class.java)
        postIntent.putExtra("email", currentUserEmail)
        postIntent.putExtra("nickname", currentUserNickName)
        startActivityForResult(postIntent, POST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == POST) {
            if (resultCode == Activity.RESULT_OK) {
                val newPost = Post(currentUserNickName as String, data?.extras?.getString("imagePath").toString(),data?.extras?.getString("description").toString())

                // Handles an edge case where the user has not posted anything yet so the arraylist in the hashmap is actually null
                if (userPosts[currentUserEmail] == null) {
                    val userPostList = arrayListOf<Post>()
                    userPosts[currentUserEmail as String] = userPostList
                }
                userPosts[currentUserEmail]?.add(0,newPost)
                posts.add(0, newPost) // Update all posts with the new post

                // The following code handles updating the hashtag list with possible new hashtags and post associated with them
                for (hashtag in data?.extras?.getStringArrayList("hashtag") as ArrayList<String>) {
                    if (hashtags.isNotEmpty()) {
                        var exists = false // Used to keep track of if a hashtag is already in the list
                        for (i in  0 until hashtags.size) {
                            Log.i("WCR", "inner hashtag ${hashtags[i].getHashTag()}")
                            if (hashtags[i].getHashTag().compareTo(hashtag) == 0) {
                                exists = true
                            }
                        }

                        if (!exists) {
                            hashtags.add(HashTag(hashtag, currentUserEmail as String))
                            val hashtagPostList = arrayListOf<Post>()
                            userPostsHashTag[hashtag] = hashtagPostList
                        }
                        userPostsHashTag[hashtag]?.add(newPost)
                    }
                    else {
                        hashtags.add(HashTag(hashtag, currentUserEmail as String))
                        val hashtagPostList = arrayListOf<Post>()
                        hashtagPostList.add(newPost)
                        userPostsHashTag[hashtag] = hashtagPostList
                    }
                }

                // Post delayed is used here to avoid downloading the image before it is finished being uploaded to firebase
                // and to prevent updateing the ui until the image downloaded from firebase
                selection.postDelayed({downLoadPostImage(newPost)},3000)
                selection.postDelayed({
                    postAdapter = PostRecycleAdapter(userPosts[currentUserEmail], this)
                    @Suppress("UNCHECKED_CAST")
                    loadFrag(postAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                }, 4000)
            }
            else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    // Get current user info
    private fun getUserInfo(email: String){
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(email)
            .get()
            .addOnSuccessListener { result ->
                Log.i("WCR", "Successful getUserInfo")
                currentUserName = result.data?.get("name").toString()
                currentUserNickName = result.data?.get("nickname").toString()
                selection.text = currentUserNickName
            }
            .addOnFailureListener {
                Log.i("WCR", "Fail getUserInfo")
            }
    }

    private fun constructUserList() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                Log.i("WCR", "SUCCESSFUL USERLIST CONSTRUCTION")
                for (document in result) {
                    users.add(User(document.data["name"].toString(), document.data["nickname"].toString(), document.data["email"].toString()))
                }
            }
            .addOnFailureListener { exception ->
                Log.w("WCR", "Error getting documents in constructUserList", exception)
            }
    }

    private fun constructPostList() {
        val db = FirebaseFirestore.getInstance()
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                Log.d("WCR", "Successful constructUserList")
                for (document in result) {
                    val post = Post(document.data["username"].toString(), document.data["imagePath"].toString(), document.data["description"].toString())
                    posts.add(post) // Add the new post to all posts list

                    // Handle Specific user posts for the user list
                    if (!userPosts.containsKey(document.data[USEREMAIL].toString())) {
                        val userPostList = arrayListOf<Post>()
                        userPostList.add(post)
                        userPosts[document.data[USEREMAIL].toString()] = userPostList
                    }
                    else {
                        userPosts[document.data[USEREMAIL].toString()]?.add(post)
                    }

                    // Build hashmap of arraylists of posts, to handle user selecting a hashtag and seeing all relevent posts
                    @Suppress("UNCHECKED_CAST")
                    for (hashtag in document.data[HASHTAG] as ArrayList<String>) {
                        // Checking for blank hashtag handles edge case where hashtag is the empty string
                        if (hashtag.isNotBlank()) {
                            if (hashtags.isNotEmpty()) {
                                var exists = false // Used to determine if the arraylist already contains the hashtag
                                for (i in  0 until hashtags.size) {
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
                Log.w("WCR", "Error getting documents. in constructUserList", exception)
            }
            .addOnCompleteListener {
                // Use post delayed to avoid updating the UI before the necessary post images are finished downloading
                selection.postDelayed({
                    postAdapter = PostRecycleAdapter(userPosts[currentUserEmail], this)
                    hashtagAdapter = ListRecycleAdapter(hashtags, userPostsHashTag, this)
                    @Suppress("UNCHECKED_CAST")
                    loadFrag(postAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                }, 2000) // can make the delay an equation based on the number of posts
            }
    }

    // Replaces the current fragment with a new one, note this does not add backstack
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
                Log.i("WCR", "Success in downloadpostimage")

            }
            .addOnFailureListener{
                Log.i("WCR", "FAIL in downloadpostimage")
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        FirebaseApp.initializeApp(this)
        currentUserEmail = intent.extras?.getString("email").toString() // Passed in from the main login activity
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
}
