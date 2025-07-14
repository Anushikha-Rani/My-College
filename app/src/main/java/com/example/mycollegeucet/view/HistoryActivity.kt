package com.example.mycollegeucet.view

import android.os.Bundle
import android.widget.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycollegeucet.R
import com.example.cart.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryActivity : AppCompatActivity() {
    private lateinit var nameInput: EditText
    private lateinit var rollInput: EditText
    private lateinit var classInput: EditText
    private lateinit var addButton: Button
    private lateinit var listView: ListView
    private val studentList = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Initialize views
        nameInput = findViewById(R.id.editTextName)
        rollInput = findViewById(R.id.editTextRoll)
        classInput = findViewById(R.id.editTextClass)
        addButton = findViewById(R.id.buttonAdd)
        listView = findViewById(R.id.listViewStudents)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, studentList)
        listView.adapter = adapter

        addButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val roll = rollInput.text.toString().trim()
            val className = classInput.text.toString().trim()

            if (name.isNotEmpty() && roll.isNotEmpty() && className.isNotEmpty()) {
                val record = "Name: $name\nRoll: $roll\nClass: $className"
                studentList.add(record)
                adapter.notifyDataSetChanged()

                nameInput.text.clear()
                rollInput.text.clear()
                classInput.text.clear()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

