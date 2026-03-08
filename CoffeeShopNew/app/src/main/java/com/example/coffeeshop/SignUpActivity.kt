package com.example.coffeeshop

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignUpActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var passwordToggle: ImageView
    private lateinit var confirmPasswordToggle: ImageView
    private lateinit var signUpButton: Button
    private lateinit var loginText: TextView

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        phoneInput = findViewById(R.id.phoneInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        passwordToggle = findViewById(R.id.passwordToggle)
        confirmPasswordToggle = findViewById(R.id.confirmPasswordToggle)
        signUpButton = findViewById(R.id.signUpButton)
        loginText = findViewById(R.id.loginText)

        setupPasswordToggles()
        setupSignUpButton()
        setupLoginText()
    }

    private fun setupPasswordToggles() {
        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            passwordInput.inputType =
                if (isPasswordVisible) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            passwordInput.setSelection(passwordInput.text.length)
            passwordToggle.setImageResource(if (isPasswordVisible) R.drawable.eye_icon else R.drawable.eye_off_icon)
        }

        confirmPasswordToggle.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            confirmPasswordInput.inputType =
                if (isConfirmPasswordVisible) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            confirmPasswordInput.setSelection(confirmPasswordInput.text.length)
            confirmPasswordToggle.setImageResource(if (isConfirmPasswordVisible) R.drawable.eye_icon else R.drawable.eye_off_icon)
        }
    }

    private fun setupSignUpButton() {
        signUpButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            // Validation
            when {
                name.isEmpty() -> { nameInput.error = "Please enter your name"; return@setOnClickListener }
                email.isEmpty() -> { emailInput.error = "Please enter your email"; return@setOnClickListener }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> { emailInput.error = "Invalid email"; return@setOnClickListener }
                phone.isEmpty() -> { phoneInput.error = "Please enter phone number"; return@setOnClickListener }
                phone.length < 10 -> { phoneInput.error = "Phone must be at least 10 digits"; return@setOnClickListener }
                password.isEmpty() -> { passwordInput.error = "Please enter password"; return@setOnClickListener }
                password.length < 6 -> { passwordInput.error = "Password too short"; return@setOnClickListener }
                confirmPassword.isEmpty() -> { confirmPasswordInput.error = "Confirm password"; return@setOnClickListener }
                password != confirmPassword -> { confirmPasswordInput.error = "Passwords do not match"; return@setOnClickListener }
            }

            // Disable button to prevent double-click
            signUpButton.isEnabled = false

            FirebaseHelper.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    signUpButton.isEnabled = true

                    if (task.isSuccessful) {
                        val userId = FirebaseHelper.auth.currentUser?.uid ?: ""

                        val userMap = mapOf(
                            "name" to name,
                            "email" to email,
                            "phone" to phone
                        )

                        FirebaseHelper.firestore.collection("users")
                            .document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                clearForm()
                                Toast.makeText(this, "Signup successful!", Toast.LENGTH_LONG).show()



                                // Delay 700ms before navigating so toast can show properly
                                Handler(Looper.getMainLooper()).postDelayed({
                                    FirebaseHelper.auth.signOut()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                }, 700)
                            }

                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Firestore error: ${e.message}", Toast.LENGTH_LONG).show()
                            }

                    } else {
                        val msg = when (val e = task.exception) {
                            is FirebaseAuthUserCollisionException -> "Email already registered"
                            is FirebaseAuthWeakPasswordException -> "Weak password"
                            else -> e?.message ?: "Signup failed"
                        }
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun setupLoginText() {
        loginText.setOnClickListener { finish() }
    }

    private fun clearForm() {
        nameInput.text.clear()
        emailInput.text.clear()
        phoneInput.text.clear()
        passwordInput.text.clear()
        confirmPasswordInput.text.clear()

        passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        confirmPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        isPasswordVisible = false
        isConfirmPasswordVisible = false

        passwordToggle.setImageResource(R.drawable.eye_off_icon)
        confirmPasswordToggle.setImageResource(R.drawable.eye_off_icon)
    }
}
