/*
William Ritchie
CS 646
Assignment 4
4/18/19
 */
package com.example.assignment4

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

// The different Login states
enum class LoginState {
    SignIn,
    SignUp,
    Limbo
}

const val NAVCODE = 1 // The response code for the result from the navigation activity
class MainActivity : AppCompatActivity() {

    private var currentUser : FirebaseUser? = null
    private lateinit var mAuth: FirebaseAuth
    private var currentLoginState = LoginState.Limbo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        sign_in.setOnClickListener{
            // Only the Sign in button, back button, email option, and password option should be visible
            if (currentLoginState == LoginState.Limbo) {
                currentLoginState = LoginState.SignIn
                sign_up.visibility = View.INVISIBLE
                back_button.visibility = View.VISIBLE
                email.visibility = View.VISIBLE
                password.visibility = View.VISIBLE
                edit_email.visibility = View.VISIBLE
                edit_password.visibility = View.VISIBLE
            }
            else if (currentLoginState == LoginState.SignIn){
                if (edit_email.text.isNotBlank() && edit_password.text.isNotBlank()) {
                    signIn(edit_email.text.toString(), edit_password.text.toString())
                }
                else {
                    Toast.makeText(this, "Missing a Field", Toast.LENGTH_LONG).show()
                }
            }
        }

        sign_up.setOnClickListener{
            // Only the sign up button, back button, name option, nickname option, email option, and password option should be visible
            if (currentLoginState == LoginState.Limbo) {
                currentLoginState = LoginState.SignUp
                sign_in.visibility = View.INVISIBLE
                back_button.visibility = View.VISIBLE
                email.visibility = View.VISIBLE
                password.visibility = View.VISIBLE
                edit_email.visibility = View.VISIBLE
                edit_password.visibility = View.VISIBLE
                name.visibility = View.VISIBLE
                nickname.visibility = View.VISIBLE
                edit_name.visibility = View.VISIBLE
                edit_nick_name.visibility = View.VISIBLE
            }
            else {
                if (edit_email.text.isNotBlank() && edit_name.text.isNotBlank() && edit_nick_name.text.isNotBlank() && edit_password.text.isNotBlank()) {
                    createAccount(edit_email.text.toString(), edit_password.text.toString(), edit_name.text.toString(), edit_nick_name.text.toString())
                }
                else {
                    Toast.makeText(this, "Missing a Field", Toast.LENGTH_LONG).show()
                }
            }
        }

        // reset the state back to the Limbo state (ie reverse the changes made by what ever the previous state was
        back_button.setOnClickListener {
            if (currentLoginState == LoginState.SignIn) {
                currentLoginState = LoginState.Limbo
                sign_up.visibility = View.VISIBLE
                back_button.visibility = View.INVISIBLE
                email.visibility = View.INVISIBLE
                password.visibility = View.INVISIBLE
                edit_email.visibility = View.INVISIBLE
                edit_password.visibility = View.INVISIBLE
            }
            else if (currentLoginState == LoginState.SignUp) {
                currentLoginState = LoginState.Limbo
                sign_in.visibility = View.VISIBLE
                back_button.visibility = View.INVISIBLE
                email.visibility = View.INVISIBLE
                password.visibility = View.INVISIBLE
                edit_email.visibility = View.INVISIBLE
                edit_password.visibility = View.INVISIBLE
                name.visibility = View.INVISIBLE
                nickname.visibility = View.INVISIBLE
                edit_name.visibility = View.INVISIBLE
                edit_nick_name.visibility = View.INVISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Check if the user is already logged in
        currentUser = mAuth.currentUser
        if (currentUser != null) {
            startNavActivity(currentUser?.email.toString())
        }
    }

    // This will create an account with firebase authentication
    private fun createAccount(email: String, password: String, name: String, nickname: String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUser = mAuth.currentUser
                    val db = FirebaseFirestore.getInstance()
                    val newUser = HashMap<String, String>()
                    newUser["name"] = name
                    newUser["email"] = email
                    newUser["nickname"] = nickname
                    db.collection("users").document(email).set(newUser)
                        .addOnSuccessListener { Log.i("WCR", "SUCCESS") }
                        .addOnFailureListener { Log.i("WCR", "FAIL") }
                    startNavActivity(email)
                }
                else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    // The only result that should be returned is if the user has pressed the logout button
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
