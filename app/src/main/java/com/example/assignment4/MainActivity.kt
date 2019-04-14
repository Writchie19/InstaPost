package com.example.assignment4

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


const val NAVCODE = 1
class MainActivity : AppCompatActivity() {

    private var currentUser : FirebaseUser? = null
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        sign_in.setOnClickListener{
            if (edit_email.text.isNotBlank() && edit_name.text.isNotBlank() && edit_nick_name.text.isNotBlank() && edit_password.text.isNotBlank()) {
                Log.i("WCR",edit_email.text.toString())
                Log.i("WCR",edit_password.text.toString())
                signIn(edit_email.text.toString(), edit_password.text.toString())
            }
            else {
                Toast.makeText(this, "Missing a Field", Toast.LENGTH_LONG).show()
            }
        }

        sign_up.setOnClickListener{
            if (edit_email.text.isNotBlank() && edit_name.text.isNotBlank() && edit_nick_name.text.isNotBlank() && edit_password.text.isNotBlank()) {
                createAccount(edit_email.text.toString(), edit_password.text.toString(), edit_name.text.toString(), edit_nick_name.text.toString())
            }
            else {
                Toast.makeText(this, "Missing a Field", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        currentUser = mAuth.currentUser
        if (currentUser != null) {
            Log.i("WCR", "ALREADY LOGGED IN")
            startNavActivity(currentUser?.email.toString())
        }
        //update ui
    }

    private fun createAccount(email: String, password: String, name: String, nickname: String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("WCR", "create user success")
                    currentUser = mAuth.currentUser
                    val db = FirebaseFirestore.getInstance()
                    val newUser = HashMap<String, String>()
                    newUser["name"] = name
                    newUser["email"] = email
                    newUser["nickname"] = nickname
                    Log.i("WCR", "HERE?")
                    db.collection("users").document(email).set(newUser)
                        .addOnSuccessListener { Log.i("WCR", "SUCCESS") }
                        .addOnFailureListener { Log.i("WCR", "FAIL") }
                    startNavActivity(email)
                    //update ui
                }
                else {
                    Log.i("WCR", "Create User Fail")
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show()
                    // update UI
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NAVCODE && resultCode == Activity.RESULT_OK) {
            currentUser = null
            mAuth.signOut()
        }
    }

    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("WCR", "sign in success")
                    currentUser = mAuth.currentUser
                    startNavActivity(email)
                    // update ui
                }
                else {
                    Log.i("WCR", "Sign in failure", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun startNavActivity(email: String) {
        val navIntent = Intent(this, NavigationActivity::class.java)
        navIntent.putExtra("email", email)
        startActivityForResult(navIntent, NAVCODE)
    }
}
