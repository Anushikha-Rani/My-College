package com.example.mycollegeucet.view

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mycollegeucet.R
import java.util.Locale


// ChatbotActivity.java
class CartActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendButton: ImageButton

    private val chatList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    private var state = "stream"
    private var userStream = ""
    private var userGoal = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        inputMessage = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.sendButton)

        chatAdapter = ChatAdapter(chatList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        addBotMessage("👋 Hi! I'm your Roadmap Guide.\nWhat is your stream? (e.g., B.Tech CSE, BCA, B.Sc IT)")

        sendButton.setOnClickListener {
            val userMessage = inputMessage.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                addUserMessage(userMessage)
                handleBotResponse(userMessage.lowercase())
                inputMessage.text.clear()
            }
        }
    }

    private fun addUserMessage(message: String) {
        chatList.add(ChatMessage(message, isUser = true))
        chatAdapter.notifyItemInserted(chatList.size - 1)
        chatRecyclerView.scrollToPosition(chatList.size - 1)
    }

    private fun addBotMessage(message: String) {
        chatList.add(ChatMessage(message, isUser = false))
        chatAdapter.notifyItemInserted(chatList.size - 1)
        chatRecyclerView.scrollToPosition(chatList.size - 1)
    }

    private fun handleBotResponse(input: String) {
        when (state) {
            "stream" -> {
                userStream = input
                state = "goal"
                addBotMessage("Great! What's your goal?\n👉 Web Development\n👉 GATE\n👉 AI/ML\n👉 Placements")
            }
            "goal" -> {
                userGoal = input
                state = "done"
                showRoadmap(userStream, userGoal)
            }
            else -> {
                if (input.contains("restart")) {
                    state = "stream"
                    addBotMessage("🔁 Restarted. Please enter your stream.")
                } else {
                    addBotMessage("Type 'restart' to begin again.")
                }
            }
        }
    }

    private fun showRoadmap(stream: String, goal: String) {
        when {
            "web" in goal -> addBotMessage("🧭 Web Dev Roadmap:\n1️⃣ Year 1: C/C++, DSA, Git\n2️⃣ Year 2: HTML, CSS, JS\n3️⃣ Year 3: React, Node.js\n4️⃣ Year 4: Projects + Internships")
            "gate" in goal -> addBotMessage("📘 GATE Roadmap:\n1️⃣ Year 2: Theory, DS, TOC\n2️⃣ Year 3: OS, DBMS, CN\n3️⃣ Year 4: PYQs, test series")
            "ml" in goal || "ai" in goal -> addBotMessage("🤖 AI/ML Roadmap:\n1️⃣ Year 2: Python, NumPy\n2️⃣ Year 3: ML Models, sklearn\n3️⃣ Year 4: DL + Projects")
            else -> addBotMessage("🚫 Roadmap not found. Type 'restart' to begin again.")
        }
    }
}
