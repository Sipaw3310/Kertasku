package com.sipaw3310.kertasku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val auth: FirebaseAuth = Firebase.auth

        val backButton: ImageButton = findViewById(R.id.signUp_back)
        val progressBar: ProgressBar = findViewById(R.id.signUp_progressBar)
        val errortext: TextView = findViewById(R.id.signUp_errorText)

        val emailfield: TextInputEditText = findViewById(R.id.signUp_emailInput)
        val passwordfield: TextInputEditText = findViewById(R.id.signUp_passwordInput)
        val signUpButton: Button = findViewById(R.id.signUpButton)

        signUpButton.setOnClickListener {
            if(emailfield.text.toString() != "" && passwordfield.text.toString() != "") {
                signUpButton.isEnabled = false
                progressBar.visibility = View.VISIBLE

                auth.createUserWithEmailAndPassword(emailfield.text.toString(), passwordfield.text.toString())
                    .addOnCompleteListener(this) {
                        if(it.isSuccessful) {
                            Toast.makeText(this, getString(R.string.sign_up_success_toast), Toast.LENGTH_LONG).show()
                            val intent: Intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            signUpButton.isEnabled = true
                            progressBar.visibility = View.GONE
                            if(it.exception is FirebaseAuthWeakPasswordException) {
                                passwordfield.error = getString(R.string.password_weak_error)
                            } else if(it.exception is FirebaseAuthInvalidCredentialsException) {
                                errortext.visibility = View.VISIBLE
                                errortext.text = getString(R.string.sign_up_invalid_credential)
                            } else if(it.exception is FirebaseAuthUserCollisionException) {
                                errortext.visibility = View.VISIBLE
                                errortext.text = getString(R.string.sign_up_collision_error)
                            } else if(it.exception is FirebaseNetworkException) {
                                errortext.visibility = View.VISIBLE
                                errortext.text = getString(R.string.sign_network_error)
                            } else {
                                errortext.visibility = View.VISIBLE
                                errortext.text = getString(R.string.sign_in_failed)
                            }
                        }
                    }
            } else {
                if(emailfield.text.toString() == "") emailfield.error = getString(R.string.email_empty_error)
                if(passwordfield.text.toString() == "") passwordfield.error = getString(R.string.password_empty_error)
            }
        }

        backButton.setOnClickListener {
            finish()
        }

    }
}