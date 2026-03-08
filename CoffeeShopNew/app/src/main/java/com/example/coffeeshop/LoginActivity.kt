package com.example.coffeeshop

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var passwordToggle: ImageView
    private lateinit var loginButton: Button
    private lateinit var signUpText: TextView
    private lateinit var skipText: TextView
    private lateinit var forgotPasswordText: TextView

    private var isPasswordVisible = false
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        passwordToggle = findViewById(R.id.passwordToggle)
        loginButton = findViewById(R.id.loginButton)
        signUpText = findViewById(R.id.signUpText)
        skipText = findViewById(R.id.skipText)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)

        prefs = getSharedPreferences("CoffeeShopPrefs", MODE_PRIVATE)

        setupPasswordToggle()
        setupLoginButton()
        setupSignUpText()
        setupSkipText()
        setupForgotPassword()
    }

    private fun setupPasswordToggle() {
        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            passwordInput.inputType =
                if (isPasswordVisible) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordInput.setSelection(passwordInput.text.length)
            passwordToggle.setImageResource(if (isPasswordVisible) R.drawable.eye_icon else R.drawable.eye_off_icon)
        }
    }

    private fun setupLoginButton() {
        loginButton.setOnClickListener {
            val email = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable login button to prevent double tap
            loginButton.isEnabled = false

            FirebaseHelper.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    loginButton.isEnabled = true

                    if (task.isSuccessful) {
                        val userId = FirebaseHelper.auth.currentUser?.uid ?: ""

                        FirebaseHelper.firestore.collection("users").document(userId).get()
                            .addOnSuccessListener { doc ->
                                val username = doc.getString("name") ?: "User"
                                prefs.edit().putString("username", username).apply()


                                Toast.makeText(this, "Welcome back, $username!", Toast.LENGTH_SHORT).show()
                                navigateToMain()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                                navigateToMain()
                            }
                    } else {
                        // Friendly error messages
                        val errorMsg = when (task.exception?.message) {
                            null -> "Login failed. Please try again."
                            else -> task.exception!!.message!!
                                .replace(
                                    "The password is invalid or the user does not have a password.",
                                    "Incorrect password"
                                )
                                .replace(
                                    "There is no user record corresponding to this identifier",
                                    "No account found with this email"
                                )
                        }
                        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun setupSignUpText() {
        signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun setupSkipText() {
        skipText.setOnClickListener {
            Toast.makeText(this, "Skipped login", Toast.LENGTH_SHORT).show()
            navigateToMain()
        }
    }

    private fun setupForgotPassword() {
        forgotPasswordText.setOnClickListener {
            val email = usernameInput.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email to reset password", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseHelper.auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { e ->
                        val msg = e.message ?: "Failed to send reset email"
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
