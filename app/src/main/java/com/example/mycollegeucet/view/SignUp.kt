package com.example.mycollegeucet.view

import android.app.DatePickerDialog
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class SignUp : AppCompatActivity() {

    // UI Components
    private lateinit var nameEt: EditText
    private lateinit var emailEt: EditText

    private lateinit var phoneEt: EditText
    private lateinit var passEt: EditText
    private lateinit var signUpButton: TextView // Updated to match the TextView in XML

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize UI Components
        nameEt = findViewById(R.id.textInputEditNama)
        emailEt = findViewById(R.id.textInputEditEmail)

        phoneEt = findViewById(R.id.textInputEditPhone)
        passEt = findViewById(R.id.editTextPassword2)
        signUpButton = findViewById(R.id.buttonSignup)

        // Initialize Firebase Components
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        // Inside your activity or fragment


        // Handle Sign-Up Click
        signUpButton.setOnClickListener {
            val name = nameEt.text.toString()
            val email = emailEt.text.toString()
            val phone = phoneEt.text.toString()
            val pass = passEt.text.toString()

            if (validateInputs(name, email, phone, pass)) {
                registerUser(name, email, phone, pass)
            }
        }

        // Handle Sign-In Redirect
        setClickableSpan(
            findViewById(R.id.signin),
            "Already have an account? Sign in",
            24, 31
        ) {
            startActivity(Intent(this, SignIn::class.java))
        }
    }

    private fun validateInputs(name: String, email: String, phone: String, pass: String): Boolean {
        return when {
            name.isEmpty() -> {
                showNotification("Error", "Name cannot be empty")
                false
            }
            email.isEmpty() -> {
                showNotification("Error", "Email cannot be empty")
                false
            }
            !email.contains("@") -> {
                showNotification("Error", "Invalid email format")
                false
            }
            phone.isEmpty() -> {
                showNotification("Error", "Phone number cannot be empty")
                false
            }
            !phone.matches(Regex("\\d{10}")) -> {
                showNotification("Error", "Invalid phone number. Must be 10 digits.")
                false
            }
            pass.isEmpty() -> {
                showNotification("Error", "Password cannot be empty")
                false
            }
            pass.length < 8 -> {
                showNotification("Error", "Password must be at least 8 characters")
                false
            }
            else -> true
        }
    }

    private fun registerUser(name: String, email: String, phone: String, pass: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                user?.let {
                    val newUser = User(id = it.uid, name = name, phone = phone, role = "user")
                    saveUserToFirestore(newUser, it)
                }
            } else {
                handleRegistrationError(task.exception)
            }
        }
    }

    private fun handleRegistrationError(exception: Exception?) {
        if (exception is FirebaseAuthUserCollisionException) {
            showNotification("Error", "Email already in use. Please try a different email.")
        } else {
            showNotification("Error", "Registration failed. Please try again.")
            Log.e("RegistrationError", "Error: ", exception)
        }
    }

    private fun saveUserToFirestore(user: User, firebaseUser: FirebaseUser) {
        firestore.collection("users").document(user.id).set(user)
            .addOnSuccessListener {
                sendEmailVerification(firebaseUser)
            }
            .addOnFailureListener { e ->
                firebaseUser.delete()
                showNotification("Error", "Failed to save user data. Please try again.")
                Log.e("FirestoreError", "Error: ", e)
            }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Verification email sent. Please check your email.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, SignIn::class.java))
                finish()
            } else {
                val errorMessage = task.exception?.localizedMessage ?: "Failed to send verification email."
                showNotification("Error", errorMessage)
                Log.e("EmailVerificationError", errorMessage)
            }
        }
    }

    private fun setClickableSpan(textView: TextView, text: String, startIndex: Int, endIndex: Int, onClick: () -> Unit) {
        val spannableString = SpannableString(text)
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                onClick()
            }
        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun showNotification(title: String, message: String) {
        NotificationDialog.showDialog(this, title, message)
    }
}
