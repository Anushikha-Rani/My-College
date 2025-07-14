package com.example.mycollegeucet.view

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mycollegeucet.R
import com.example.cart.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity for Sign In
 */
class SignIn : AppCompatActivity() {

    // UI Variables
    private lateinit var emailEt: EditText
    private lateinit var passEt: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Initialize UI Variables
        emailEt = findViewById(R.id.textInputEditText)
        passEt = findViewById(R.id.editTextPassword)
        val signInTextView = findViewById<TextView>(R.id.button6) // Updated to match TextView

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Set Listener for "Sign In"
        signInTextView.setOnClickListener {
            val email = emailEt.text.toString()
            val pass = passEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                signInUser(email, pass)
            } else {
                showToast("Error: Empty Fields Are Not Allowed!")
            }
        }

        // Set Clickable Span for "Forgot Password?"
        setClickableSpan(
            findViewById(R.id.textViewForgot),
            "Forgot Password?",
            onClick = { startActivity(Intent(this, ForgotPassword::class.java)) }
        )

        // Set Clickable Span for "Register Now!"
        setClickableSpan(
            findViewById(R.id.textView7),
            "Don’t have accounts? Register now!",
            onClick = { startActivity(Intent(this, SignUp::class.java)) },
            startIndex = 21,
            endIndex = 34
        )
    }

    private fun signInUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                user?.let {
                    if (it.isEmailVerified) {
                        fetchUserData(it.uid)
                    } else {
                        showNotification("Email not verified", "Please verify your email.")
                    }
                }
            } else {
                showNotification("Login Failed", "Invalid email/password. Please try again.")
            }
        }
    }

    private fun fetchUserData(userId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userData = document.toObject(User::class.java)
                    if (userData?.role == "user") {
                        navigateToHome(userData.name)
                    } else {
                        showNotification("Access Denied", "Only 'user' role can log in.")
                        firebaseAuth.signOut()
                    }
                } else {
                    showNotification("Error", "User data not found.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error fetching user data", e)
                showNotification("Error", "Could not retrieve user data.")
            }
    }

    private fun navigateToHome(name: String?) {
        showToast("Login Success")
        val intent = Intent(this, Home::class.java).apply {
            putExtra("USER_NAME", name)
        }
        startActivity(intent)
    }

    private fun showNotification(title: String, message: String) {
        NotificationDialog.showDialog(this, title, message)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setClickableSpan(
        textView: TextView,
        fullText: String,
        onClick: () -> Unit,
        startIndex: Int = 0,
        endIndex: Int = fullText.length
    ) {
        val spannableString = SpannableString(fullText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                onClick()
            }
        }
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}
