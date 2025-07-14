package com.example.mycollegeucet.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mycollegeucet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfile : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var editTextDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        editTextDate = findViewById(R.id.editTextDate)
        editTextDate.setOnClickListener{
            showDatePickerDialog()
        }


        // Initialize UI components
        val nameInput = findViewById<EditText>(R.id.textInputEditName)
        val radioGroupGender = findViewById<RadioGroup>(R.id.radioGroupGender)
        val phoneInput = findViewById<EditText>(R.id.textInputEditPhone)
        val addressInput = findViewById<EditText>(R.id.textInputEditAddress)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        if (nameInput == null || radioGroupGender == null || phoneInput == null || addressInput == null || saveButton == null) {
            Log.e("EditProfile", "One or more views are null!")
            return
        }

        val currentUser = auth.currentUser
        currentUser?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    document?.let {
                        nameInput.setText(it.getString("name") ?: "")
                        val gender = it.getString("gender")
                        if (gender == "Male") {
                            radioGroupGender.check(R.id.radioMale)
                        } else if (gender == "Female") {
                            radioGroupGender.check(R.id.radioFemale)
                        }
                        phoneInput.setText(it.getString("phone") ?: "")
                        addressInput.setText(it.getString("address") ?: "")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("EditProfile", "Error fetching user data: ${exception.message}")
                }
        }


        saveButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val selectedGenderId = radioGroupGender.checkedRadioButtonId
            val gender = if (selectedGenderId == R.id.radioMale) "Male" else "Female"
            val phone = phoneInput.text.toString().trim()
            val address = addressInput.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || selectedGenderId == -1) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentUser?.uid?.let { userId ->
                val profileData = mapOf(
                    "name" to name,
                    "gender" to gender,
                    "phone" to phone,
                    "address" to address
                )
                db.collection("users").document(userId).update(profileData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("EditProfile", "Error updating profile: ${exception.message}")
                    }
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,
            {_, selectedYear, selectedMonth, selectedDay ->
                val dob = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
                editTextDate.setText(dob)
            }, year, month, day)

        datePickerDialog.show()
    }
}
