package com.example.mycollegeucet.view

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mycollegeucet.R
import com.google.firebase.auth.FirebaseAuth

/**
 * Activity to manage the forgot password feature.
 */
class ForgotPassword : AppCompatActivity() {

    private lateinit var emailEt: EditText // EditText for email input
    private lateinit var resetPasswordTextView: TextView // TextView for resetting the password
    private lateinit var alreadyHaveAccountTextView: TextView // TextView for redirecting to SignIn
    private lateinit var firebaseAuth: FirebaseAuth // FirebaseAuth instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Get references to UI components
        emailEt = findViewById(R.id.textInputEditText)
        resetPasswordTextView = findViewById(R.id.button6)
        alreadyHaveAccountTextView = findViewById(R.id.textView3) // Assuming this is the ID

        // Handle reset password text click
        resetPasswordTextView.setOnClickListener {
            val email = emailEt.text.toString()
            if (validateEmail(email)) {
                sendPasswordResetEmail(email)
            }
        }

        // Handle "Already have an account? Login" click
        setClickableSpan(
            findViewById(R.id.textView7),
            "Already have an account? Sign in",
            24, 31
        ) {
            startActivity(Intent(this, SignIn::class.java))
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

    private fun validateEmail(email: String): Boolean {
        return if (email.isNotEmpty()) {
            true
        } else {
            Toast.makeText(this, "Please enter your email to reset your password.", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Password reset email has been sent. Please check your email.",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToSignIn()
                } else {
                    Toast.makeText(
                        this,
                        "Failed to send password reset email. Check your email and try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigateToSignIn() {
        val intent = Intent(this, SignIn::class.java)
        startActivity(Intent(this, SignIn::class.java))
        finish()
    }
}
