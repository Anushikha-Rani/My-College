package com.example.mycollegeucet.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mycollegeucet.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Activity to display and manage user profile.
 */
class Profile : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var cameraButton: ImageView
    private lateinit var nameProfile: TextView
    private lateinit var emailProfile: TextView
    private lateinit var phoneProfile: TextView
    private lateinit var aluminibox: TextView
    private lateinit var hostelbox: TextView
    private lateinit var canteenbox: TextView
    private lateinit var librarybox: TextView
    private lateinit var consellingbox: TextView
    private lateinit var internshipbox: TextView
    private lateinit var placementbox: TextView
    private lateinit var aboutusbox: TextView
    private lateinit var editProfileBox: TextView
    private lateinit var logoutBox: TextView

    companion object {
        const val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize UI components
        profileImageView = findViewById(R.id.imagePhoto)
        cameraButton = findViewById(R.id.cameraButton)
        nameProfile = findViewById(R.id.NameProfile)
        emailProfile = findViewById(R.id.emailProfile)
        phoneProfile = findViewById(R.id.phoneProfile)
        aluminibox = findViewById(R.id.aluminitext)
         hostelbox= findViewById(R.id.hosteltext)
        canteenbox= findViewById(R.id.canteentext)
        librarybox= findViewById(R.id.librarytext)
        consellingbox= findViewById(R.id.consellingtext)
        internshipbox= findViewById(R.id.internshiptext)
        placementbox= findViewById(R.id.placementtext)
        aboutusbox=findViewById(R.id.aboutustext)
        editProfileBox = findViewById(R.id.editProfileText)
        logoutBox = findViewById(R.id.logoutText)

        // Set up click listeners
        cameraButton.setOnClickListener {
            pickImage()
        }

        aluminibox.setOnClickListener {
            navigateToAlumini()
        }

        hostelbox.setOnClickListener {
            navigateToHostel()
        }
        canteenbox.setOnClickListener {
            navigateToCanteen()
        }
        librarybox.setOnClickListener {
            navigateToLibrary()
        }
        consellingbox.setOnClickListener {
            navigateToConselling()
        }
        internshipbox.setOnClickListener {
            navigateToInternship()
        }
        placementbox.setOnClickListener {
            navigateToPlacement()
        }
        aboutusbox.setOnClickListener {
            navigateToAboutus()
        }


        editProfileBox.setOnClickListener {
            navigateToEditProfile()
        }

        logoutBox.setOnClickListener {
            logout()
        }

        // Fetch profile data
        fetchProfileData()
    }

    // Function to pick an image
    private fun pickImage() {
        ImagePicker.with(this)
            .galleryOnly()
            .crop()
            .start(IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                profileImageView.setImageURI(uri)
                uploadImageToFirebase(uri)
            }
        }
    }

    // Function to upload the image to Firebase
    private fun uploadImageToFirebase(uri: Uri) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val storageReference = FirebaseStorage.getInstance().reference.child("profile/$userId.jpg")

            storageReference.putFile(uri)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                        updateProfileImage(downloadUri.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("FirebaseStorage", "Error uploading image", exception)
                }
        }
    }

    // Function to update the profile image URL in Firestore
    private fun updateProfileImage(imageUrl: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(userId)
                .update("profileImage", imageUrl)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile image updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to update profile image: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Firestore", "Error updating profile image", exception)
                }
        }
    }

    // Function to fetch profile data from Firestore
    private fun fetchProfileData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val db = FirebaseFirestore.getInstance()
            val email = currentUser.email
            emailProfile.text = email

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name")
                        val phone = document.getString("phone")
                        val profileImage = document.getString("profileImage")

                        nameProfile.text = name
                        phoneProfile.text = phone
                        if (!profileImage.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(profileImage)
                                .into(profileImageView)
                        }
                    } else {
                        Toast.makeText(this, "Profile document does not exist.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to fetch profile data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to navigate to Cart
    private fun navigateToAlumini() {
        Toast.makeText(this, "Navigate to Alumini", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Alumini::class.java)
        startActivity(intent)
    }
    private fun navigateToHostel() {
        Toast.makeText(this, "Navigate to Hostel", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Hostel::class.java)
        startActivity(intent)
    }
    private fun navigateToCanteen() {
        Toast.makeText(this, "Navigate to Canteen", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Canteen::class.java)
        startActivity(intent)
    }
    private fun navigateToLibrary() {
        Toast.makeText(this, "Navigate to Library", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Library::class.java)
        startActivity(intent)
    }
    private fun navigateToConselling() {
        Toast.makeText(this, "Navigate to Conselling", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Conselling::class.java)
        startActivity(intent)
    }
    private fun navigateToInternship() {
        Toast.makeText(this, "Navigate to Internship", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Internship::class.java)
        startActivity(intent)
    }
    private fun navigateToPlacement() {
        Toast.makeText(this, "Navigate to Placement", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Placement::class.java)
        startActivity(intent)
    }
    private fun navigateToAboutus() {
        Toast.makeText(this, "Navigate to Aboutus", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Aboutus::class.java)
        startActivity(intent)
    }


    // Function to navigate to Edit Profile
    private fun navigateToEditProfile() {
        val intent = Intent(this, EditProfile::class.java)
        startActivity(intent)
    }

    // Function to logout
    private fun logout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, _ ->
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, Home::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }
}
